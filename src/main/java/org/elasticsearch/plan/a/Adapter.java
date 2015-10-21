package org.elasticsearch.plan.a;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.elasticsearch.plan.a.Caster.*;
import static org.elasticsearch.plan.a.Default.*;
import static org.elasticsearch.plan.a.Definition.*;
import static org.elasticsearch.plan.a.PlanAParser.*;

class Adapter {
    static class Variable {
        final String name;
        final Type type;
        final int slot;

        private Variable(final String name, final Type type, final int slot) {
            this.name = name;
            this.type = type;
            this.slot = slot;
        }
    }

    static class StatementMetadata {
        final ParserRuleContext source;

        boolean allExit;
        boolean allReturn;
        boolean anyReturn;
        boolean allBreak;
        boolean anyBreak;
        boolean allContinue;
        boolean anyContinue;

        private StatementMetadata(final ParserRuleContext source) {
            this.source = source;

            allExit = false;
            allReturn = false;
            anyReturn = false;
            allBreak = false;
            anyBreak = false;
            allContinue = false;
            anyContinue = false;
        }
    }

    static class ExpressionMetadata {
        final ParserRuleContext source;

        boolean statement;

        Object preConst;
        Object postConst;
        boolean isNull;

        Type to;
        Type from;
        Promotion promotion;
        boolean explicit;

        Cast cast;

        private ExpressionMetadata(final ParserRuleContext source) {
            this.source = source;

            statement = false;

            preConst = null;
            postConst = null;
            isNull = false;

            to = null;
            from = null;
            promotion = null;
            explicit = false;

            cast = null;
        }
    }

    static class Branch {
        final ParserRuleContext source;

        Label begin;
        Label end;
        Label tru;
        Label fals;

        private Branch(final ParserRuleContext source) {
            this.source = source;

            begin = null;
            end = null;
            tru = null;
            fals = null;
        }
    }

    static String error(final ParserRuleContext ctx) {
        return "Error [" + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + "]: ";
    }

    final Definition definition;
    final Standard standard;
    final Caster caster;
    final String source;
    final ParserRuleContext root;

    private final Deque<Integer> scopes;
    private final Deque<Variable> variables;

    private final Map<ParserRuleContext, StatementMetadata> statementMetadata;
    private final Map<ParserRuleContext, ExpressionMetadata> expressionMetadata;
    private final Map<ParserRuleContext, External> externals;

    private final Map<ParserRuleContext, Branch> branches;
    private final Deque<Branch> jumps;
    private final Set<ParserRuleContext> strings;

    Adapter(final Definition definition, final Standard standard, final Caster caster,
            final String source, final ParserRuleContext root) {
        this.definition = definition;
        this.standard = standard;
        this.caster = caster;
        this.source = source;
        this.root = root;

        scopes = new ArrayDeque<>();
        variables = new ArrayDeque<>();

        statementMetadata = new HashMap<>();
        expressionMetadata = new HashMap<>();
        externals = new HashMap<>();

        branches = new HashMap<>();
        jumps = new ArrayDeque<>();
        strings = new HashSet<>();
    }

    void incrementScope() {
        scopes.push(0);
    }

    void decrementScope() {
        int remove = scopes.pop();

        while (remove > 0) {
            variables.pop();
            --remove;
        }
    }

    Variable getVariable(final String name) {
        final Iterator<Variable> itr = variables.iterator();

        while (itr.hasNext()) {
            final Variable variable = itr.next();

            if (variable.name.equals(name)) {
                return variable;
            }
        }

        return null;
    }

     Variable addVariable(final ParserRuleContext source, final String name, final Type type) {
         if (getVariable(name) != null) {
             if (source == null) {
                 throw new IllegalArgumentException("Argument name [" + name + "] already defined within the scope.");
             } else {
                 throw new IllegalArgumentException(
                         error(source) + "Variable name [" + name + "] already defined within the scope.");
             }
         }

         final Variable previous = variables.peekFirst();
         int slot = 0;

         if (previous != null) {
             slot += previous.slot + previous.type.metadata.size;
         }

         final Variable variable = new Variable(name, type, slot);
         variables.push(variable);

         final int update = scopes.pop() + 1;
         scopes.push(update);

         return variable;
    }

    StatementMetadata createStatementMetadata(final ParserRuleContext source) {
        final StatementMetadata sourcesmd = new StatementMetadata(source);
        statementMetadata.put(source, sourcesmd);

        return sourcesmd;
    }

    StatementMetadata getStatementMetadata(final ParserRuleContext source) {
        final StatementMetadata sourcesmd = statementMetadata.get(source);

        if (sourcesmd == null) {
            throw new IllegalStateException(error(source) + "Statement metadata does not exist at" +
                    " the parse node with text [" + source.getText() + "].");
        }

        return sourcesmd;
    }

    ExpressionContext getExpressionContext(ExpressionContext source) {
        if (source instanceof PrecedenceContext) {
            final ParserRuleContext parent = source.getParent();
            int index = 0;

            for (final ParseTree child : parent.children) {
                if (child == source) {
                    break;
                }

                ++index;
            }

            while (source instanceof PrecedenceContext) {
                source = ((PrecedenceContext)source).expression();
            }

            parent.children.set(index, source);
        }

        return source;
    }

    ExpressionMetadata createExpressionMetadata(ParserRuleContext source) {
        final ExpressionMetadata sourceemd = new ExpressionMetadata(source);
        expressionMetadata.put(source, sourceemd);

        return sourceemd;
    }
    
    ExpressionMetadata getExpressionMetadata(final ParserRuleContext source) {
        final ExpressionMetadata sourceemd = expressionMetadata.get(source);

        if (sourceemd == null) {
            throw new IllegalStateException(error(source) + "Expression metadata does not exist at" +
                    " the parse node with text [" + source.getText() + "].");
        }

        return sourceemd;
    }

    void putExternal(final ParserRuleContext source, final External external) {
        externals.put(source, external);
    }

    External getExternal(final ParserRuleContext source) {
        final External external = externals.get(source);

        if (external == null) {
            throw new IllegalStateException(error(source) + "External data does not exist at" +
                    " the parse node with text [" + source.getText() + "].");
        }

        return external;
    }

    Branch markBranch(final ParserRuleContext source, final ParserRuleContext... nodes) {
        final Branch branch = new Branch(source);

        for (final ParserRuleContext node : nodes) {
            branches.put(node, branch);
        }

        return branch;
    }

    Branch getBranch(final ParserRuleContext source) {
        return branches.get(source);
    }

    void checkWriteBranch(final MethodVisitor visitor, final ParserRuleContext source) {
        final Branch branch = getBranch(source);

        if (branch != null) {
            if (branch.tru != null) {
                visitor.visitJumpInsn(Opcodes.IFNE, branch.tru);
            } else if (branch.fals != null) {
                visitor.visitJumpInsn(Opcodes.IFEQ, branch.fals);
            }
        }
    }

    void pushJump(final Branch branch) {
        jumps.push(branch);
    }

    Branch peekJump() {
        return jumps.peek();
    }

    void popJump() {
        jumps.pop();
    }

    void markStrings(final ParserRuleContext node) {
        strings.add(node);
    }

    void unmarkStrings(final ParserRuleContext node) {
        strings.remove(node);
    }

    boolean getStrings(final ParserRuleContext node) {
        return strings.contains(node);
    }
}

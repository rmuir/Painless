package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static painless.Caster.*;
import static painless.Default.*;
import static painless.Definition.*;

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
        final ParseTree source;

        boolean allExit;
        boolean allReturn;
        boolean anyReturn;
        boolean allBreak;
        boolean anyBreak;
        boolean allContinue;
        boolean anyContinue;

        private StatementMetadata(final ParseTree source) {
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
        ParseTree source;

        boolean statement;

        Object preConst;
        Object postConst;
        boolean isNull;

        Type to;
        Type from;
        Promotion promotion;
        boolean explicit;

        Cast cast;

        private ExpressionMetadata() {
            source = null;

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
        final ParseTree source;

        Label begin;
        Label end;
        Label tru;
        Label fals;

        private Branch(final ParseTree source) {
            this.source = source;

            begin = null;
            end = null;
            tru = null;
            fals = null;
        }
    }

    final Definition definition;
    final Standard standard;
    final Caster caster;
    final String source;
    final ParseTree root;

    private final Deque<Integer> scopes;
    private final Deque<Variable> variables;

    private final Map<ParseTree, StatementMetadata> statementMetadata;
    private final Map<ParseTree, ExpressionMetadata> expressionMetadata;
    private final Map<ParseTree, External> externals;

    private final Map<ParseTree, Branch> branches;
    private final Deque<Branch> jumps;
    private final Set<ParseTree> strings;

    Adapter(final Definition definition, final Standard standard, final Caster caster,
            final String source, final ParseTree root) {
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

     Variable addVariable(final String name, final Type ptype) {
         if (getVariable(name) != null) {
             throw new IllegalArgumentException(); // TODO: message
         }

         final Variable previous = variables.peekFirst();
         int slot = 0;

         if (previous != null) {
             slot += previous.slot + previous.type.metadata.size;
         }

         final Variable variable = new Variable(name, ptype, slot);
         variables.push(variable);

         final int update = scopes.pop() + 1;
         scopes.push(update);

         return variable;
    }

    StatementMetadata createStatementMetadata(final ParseTree source) {
        final StatementMetadata sourcesmd = new StatementMetadata(source);
        statementMetadata.put(source, sourcesmd);

        return sourcesmd;
    }

    StatementMetadata getStatementMetadata(final ParseTree source) {
        final StatementMetadata sourcesmd = statementMetadata.get(source);

        if (sourcesmd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourcesmd;
    }

    ExpressionMetadata createExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceemd = new ExpressionMetadata();
        sourceemd.source = source;
        expressionMetadata.put(source, sourceemd);

        return sourceemd;
    }

    void updateExpressionMetadata(final ParseTree source, final ExpressionMetadata expremd) {
        expressionMetadata.remove(expremd.source);
        expremd.source = source;
        expressionMetadata.put(source, expremd);
    }
    
    ExpressionMetadata getExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceemd = expressionMetadata.get(source);

        if (sourceemd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourceemd;
    }

    void putExternal(final ParseTree source, final External external) {
        externals.put(source, external);
    }

    External getExternal(final ParseTree source) {
        final External external = externals.get(source);

        if (external == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return external;
    }

    Branch markBranch(final ParseTree source, final ParseTree node) {
        Branch branch = getBranch(source);

        if (branch == null) {
            branch = new Branch(source);
        }

        if (node != null) {
            branches.put(node, branch);
        }

        return branch;
    }

    Branch getBranch(final ParseTree source) {
        return branches.get(source);
    }

    void checkWriteBranch(final MethodVisitor visitor, final ParseTree source) {
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

    void markStrings(final ParseTree node) {
        strings.add(node);
    }

    void unmarkStrings(final ParseTree node) {
        strings.remove(node);
    }

    boolean getStrings(final ParseTree node) {
        return strings.contains(node);
    }
}

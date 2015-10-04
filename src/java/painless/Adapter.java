package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Label;

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

        Promotions promotions;
        Type to;
        Type from;
        boolean explicit;

        Cast cast;
        Transform transform;

        private ExpressionMetadata() {
            source = null;

            statement = false;

            preConst = null;
            postConst = null;
            isNull = false;

            promotions = null;
            to = null;
            from = null;
            explicit = false;

            cast = null;
            transform = null;
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

    private final HashMap<ParseTree, Branch> branches;
    private final Deque<Branch> loops;

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

        this.branches = new HashMap<>();
        this.loops = new ArrayDeque<>();
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
        final Iterator<Variable> itr = variables.descendingIterator();

        while (itr.hasNext()) {
            final Variable variable = itr.next();

            if (variable.name.equals(name)) {
                return variable;
            }
        }

        return null;
    }

     void addVariable(final String name, final Type ptype) {
        if (getVariable(name) != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final Variable previous = variables.peekLast();
        int aslot = 0;

        if (previous != null) {
            aslot += previous.slot + previous.type.metadata.size;
        }

        final Variable pvariable = new Variable(name, ptype, aslot);
        variables.add(pvariable);

        final int update = scopes.pop() + 1;
         scopes.push(update);
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

    void updateExpressionMetadata(final ParseTree source, final ExpressionMetadata exprmd) {
        expressionMetadata.remove(exprmd.source);
        exprmd.source = source;
        expressionMetadata.put(source, exprmd);
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

    Branch createBranch(final ParseTree source, boolean loop) {
        final Branch branch = new Branch(source);
        branches.put(source, branch);

        return branch;
    }

    Branch propagateBranch(final ParseTree node, Branch branch) {
        branches.put(source, branch);
    }

    Branch getBranch(final ParseTree source) {
        final Branch branch = branches.get(source);

        if (branch == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return branch;
    }
}

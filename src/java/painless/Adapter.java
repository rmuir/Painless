package painless;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Types.*;

public class Adapter {
    static class Variable {
        final String name;
        final Type type;
        final int slot;

        Variable(final String name, final Type type, final int slot) {
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

        StatementMetadata(final ParseTree source) {
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
        final ParseTree source;

        Object preConst;
        Object postConst;
        boolean isNull;

        Type to;
        Type from;
        boolean anyNumeric;
        boolean anyType;
        boolean explicit;

        Cast cast;
        Transform transform;

        ExpressionMetadata(final ParseTree source) {
            this.source = source;

            preConst = null;
            postConst = null;
            isNull = false;

            to = null;
            from = null;
            anyNumeric = false;
            anyType = false;
            explicit = false;

            cast = null;
            transform = null;
        }
    }

    final Types types;
    final ParseTree root;

    private final Deque<Integer> scopes;
    private final Deque<Variable> variables;

    private final Map<ParseTree, StatementMetadata> statementMetadata;
    private final Map<ParseTree, ExpressionMetadata> expressionMetadata;

    Adapter(final Types types, final ParseTree root) {
        this.types = types;
        this.root = root;

        this.scopes = new ArrayDeque<>();
        this.variables = new ArrayDeque<>();

        statementMetadata = new HashMap<>();
        expressionMetadata = new HashMap<>();
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

     Variable addVariable(final String name, final Type ptype) {
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

        return pvariable;
    }

    StatementMetadata createStatementMetadata(final ParseTree source) {
        final StatementMetadata sourceSMD = new StatementMetadata(source);
        statementMetadata.put(source, sourceSMD);

        return sourceSMD;
    }

    StatementMetadata getStatementMetadata(final ParseTree source) {
        final StatementMetadata sourceSMD = statementMetadata.get(source);

        if (sourceSMD == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourceSMD;
    }

    ExpressionMetadata createExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceSMD = new ExpressionMetadata(source);
        expressionMetadata.put(source, sourceSMD);

        return sourceSMD;
    }

    ExpressionMetadata getExpressionMetadata(final ParseTree source) {
        final ExpressionMetadata sourceSMD = expressionMetadata.get(source);

        if (sourceSMD == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourceSMD;
    }
}

package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Type;

class PainlessAdapter {
    private final Map<ParseTree, PainlessCast> casts;

    private final Map<ParseTree, List<PainlessVariable> > contexts;
    private final List<List<PainlessVariable> > scopes;

    private int scope;
    private int slot;

    PainlessAdapter(final ParseTree context) {
        casts = new HashMap<>();

        contexts = new HashMap<>();
        scopes = new ArrayList<>();

        final List<PainlessVariable> variables = new ArrayList<>();
        contexts.put(context, variables);
        scopes.add(variables);

        scope = 0;
        slot = 0;
    }

    void markCast(final ParseTree node, final Type from, final Type to, final boolean explicit) {
        if (!PainlessCast.isLegalCast(from, to, explicit)) {
            throw new IllegalArgumentException();
        }

        if (!from.equals(to)) {
            final PainlessCast cast = new PainlessCast(from, to);
            casts.put(node, cast);
        }
    }

    void incrementScope(final ParseTree context) {
        ++scope;

        final List<PainlessVariable> variables = new ArrayList<>();
        contexts.put(context, variables);
        scopes.add(variables);
    }

    void decrementScope() {
        final List<PainlessVariable> variables = scopes.get(scope);
        final ListIterator<PainlessVariable> iterator = variables.listIterator(variables.size());

        while (iterator.hasPrevious()) {
            final PainlessVariable variable = iterator.previous();

            if (variable.getScope() == scope) {
                final Type type = variable.getType();
                slot = type.getSort() == type.LONG || type.getSort() == type.DOUBLE ? slot - 2 : slot - 1;
            } else {
                break;
            }
        }

        scopes.remove(scope);
        --scope;
    }

    PainlessVariable getVariable(final String name) {
        for (int scount = scope; scount >= 0; --scount) {
            final List<PainlessVariable> variables = scopes.get(scount);
            final ListIterator<PainlessVariable> iterator = variables.listIterator(variables.size());

            while (iterator.hasPrevious()) {
                final PainlessVariable variable = iterator.previous();

                if (variable.getName().equals(name)) {
                    return variable;
                }
            }
        }

        return null;
    }

    PainlessVariable newVariable(final String name, final Type type) {
        if (getVariable(name) != null) {
            throw new IllegalArgumentException();
        }

        final PainlessVariable variable = new PainlessVariable(name, type, scope, slot);
        final List<PainlessVariable> variables = scopes.get(scope);
        variables.add(variable);
        slot = type.getSort() == type.LONG || type.getSort() == type.DOUBLE ? slot + 2 : slot + 1;

        return variable;
    }
}

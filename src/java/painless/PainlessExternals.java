package painless;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Type;

import painless.PainlessParser.*;

public class PainlessExternals {
    private static final int START       = 0;
    private static final int PRECEDENCE0 = 1;
    private static final int PRECEDENCE1 = 2;
    private static final int CAST        = 3;
    private static final int ARRAY       = 4;
    private static final int DOT         = 5;
    private static final int CALL        = 6;
    private static final int VARIABLE    = 7;

    private static final HashMap<Integer, HashSet<Integer> > legal;

    static {
        legal = new HashMap<>();
        HashSet<Integer> states;

        states = new HashSet<>();
        states.add(PRECEDENCE0);
        states.add(CAST);
        states.add(CALL);
        states.add(VARIABLE);
        legal.put(START, states);
        legal.put(PRECEDENCE0, states);

        states = new HashSet<>();
        states.add(ARRAY);
        states.add(DOT);
        legal.put(PRECEDENCE1, states);
        legal.put(ARRAY, states);
        legal.put(CALL, states);
        legal.put(VARIABLE, states);

        states = new HashSet<>();
        states.add(CALL);
        states.add(VARIABLE);
        legal.put(CAST, states);
        legal.put(DOT, states);
    }

    static void parse(PainlessValidator validator, ExternalContext ctx, Type type, Set<Integer> legal) {
        if (legal == null) {
            legal = PainlessExternals.legal.get(START);
        }

        if (ctx.decltype() != null) {
            if (!legal.contains(CAST)) {
                throw new IllegalArgumentException();
            }

            parse(validator, ctx.external(0), null, PainlessExternals.legal.get(CAST));
        } else if (ctx.DOT() != null) {
            if (!legal.contains(DOT)) {
                throw new IllegalArgumentException();
            }

            parse(validator, ctx.external(0), null, PainlessExternals.legal.get(DOT));
        } else if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            if (!legal.contains(ARRAY)) {
                throw new IllegalArgumentException();
            }

            if (ctx.external(0) != null) {
                parse(validator, ctx.external(0), null, PainlessExternals.legal.get(ARRAY));
            }
        } else if (ctx.arguments() != null) {
            if (!legal.contains(CALL)) {
                throw new IllegalArgumentException();
            }

            if (ctx.external(0) != null) {
                parse(validator, ctx.external(0), null, PainlessExternals.legal.get(ARRAY));
            }
        } else if (ctx.ID() != null) {
            if (!legal.contains(VARIABLE)) {
                throw new IllegalArgumentException();
            }

            if (ctx.external(0) != null) {
                parse(validator, ctx.external(0), null, PainlessExternals.legal.get(ARRAY));
            }
        } else if (ctx.LP() != null && ctx.RP() != null) {
            if (!legal.contains(PRECEDENCE0)) {
                throw new IllegalArgumentException();
            }

            parse(validator, ctx.external(0), null, PainlessExternals.legal.get(PRECEDENCE0));

            if (ctx.external(1) != null) {
                parse(validator, ctx.external(1), null, PainlessExternals.legal.get(PRECEDENCE1));
            }
        } else {
            throw new IllegalStateException();
        }
    }
}

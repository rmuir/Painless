package painless;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import static painless.PainlessExternal.*;
import static painless.PainlessParser.*;
import static painless.PainlessTypes.*;

class PainlessAnalyzer extends PainlessBaseVisitor<Void> {
    static class PArgument {
        private final String pname;
        private final PType ptype;

        PArgument(final String pname, final PType ptype) {
            this.pname = pname;
            this.ptype = ptype;
        }

        String getPName() {
            return pname;
        }

        PType getPType() {
            return ptype;
        }
    }

    static class PVariable {
        private final String pname;
        private final PType ptype;
        private final int aslot;

        PVariable(final String pname, final PType ptype, final int aslot) {
            this.pname = pname;
            this.ptype = ptype;
            this.aslot = aslot;
        }

        String getPName() {
            return pname;
        }

        PType getPType() {
            return ptype;
        }

        int getASlot() {
            return aslot;
        }
    }

    static class PMetadata {
        private ParseTree source;

        private boolean close;
        private boolean statement;

        private boolean allexit;
        private boolean allrtn;
        private boolean anyrtn;
        private boolean allbreak;
        private boolean anybreak;
        private boolean allcontinue;
        private boolean anycontinue;

        private boolean pop;
        private PExternal pexternal;

        private Object constpre;
        private Object constpost;
        private boolean isnull;

        private PType toptype;
        private boolean anypnumeric;
        private boolean anyptype;
        private boolean explicit;
        private PType fromptype;
        private PCast pcast;
        private PTransform ptransform;

        PMetadata(final ParseTree source) {
            this.source = source;

            close = false;
            statement = false;

            allexit = false;
            allrtn = false;
            anyrtn = false;
            allbreak = false;
            anybreak = false;
            allcontinue = false;
            anycontinue = false;

            pop = false;
            pexternal = null;

            constpre = null;
            constpost = null;
            isnull = false;

            toptype = null;
            anypnumeric = false;
            anyptype = false;
            explicit = false;
            fromptype = null;
            pcast = null;
            ptransform = null;
        }

        ParseTree getSource() {
            return source;
        }

        boolean getAllExit() {
            return allexit;
        }

        boolean getAllReturn() {
            return allrtn;
        }

        boolean getAnyReturn() {
            return anyrtn;
        }

        boolean getAllBreak() {
            return allbreak;
        }

        boolean getAnyBreak() {
            return anybreak;
        }

        boolean getAllContinue() {
            return allcontinue;
        }

        boolean getAnyContinue() {
            return anycontinue;
        }

        boolean getPop() {
            return pop;
        }

        PExternal getPExternal() {
            return pexternal;
        }

        Object getConstPre() {
            return constpre;
        }

        Object getConstPost() {
            return constpost;
        }

        boolean getIsNull() {
            return isnull;
        }

        PType getToPType() {
            return toptype;
        }

        boolean getAnyPNumeric() {
            return anypnumeric;
        }

        boolean getAnyPType() {
            return anyptype;
        }

        boolean getExplicit() {
            return explicit;
        }

        PType getFromPType() {
            return fromptype;
        }

        PCast getPCast() {
            return pcast;
        }

        PTransform getPTransform() {
            return ptransform;
        }
    }

    static Map<ParseTree, PMetadata> analyze(
            final PTypes ptypes, final ParseTree root, final Deque<PArgument> arguments) {
        return new PainlessAnalyzer(ptypes, root, arguments).pmetadata;
    }

    private final PTypes ptypes;
    private final PStandard pstandard;

    private final Deque<Integer> ascopes;
    private final Deque<PVariable> pvariables;

    private final Map<ParseTree, PMetadata> pmetadata;

    private PainlessAnalyzer(final PTypes ptypes, final ParseTree root, final Deque<PArgument> parguments) {
        this.ptypes = ptypes;
        pstandard = ptypes.getPStandard();

        ascopes = new ArrayDeque<>();
        pvariables = new ArrayDeque<>();

        pmetadata = new HashMap<>();

        incrementScope();

        for (final PArgument argument : parguments) {
            addPVariable(argument.pname, argument.ptype);
        }

        createPMetadata(root);
        visit(root);

        decrementScope();
    }

    private void incrementScope() {
        ascopes.push(0);
    }

    private void decrementScope() {
        int remove = ascopes.pop();

        while (remove > 0) {
            pvariables.pop();
            --remove;
        }
    }

    private PVariable getPVariable(final String name) {
        final Iterator<PVariable> itr = pvariables.descendingIterator();

        while (itr.hasNext()) {
            final PVariable variable = itr.next();

            if (variable.pname.equals(name)) {
                return variable;
            }
        }

        return null;
    }

    private PVariable addPVariable(final String name, final PType ptype) {
        if (getPVariable(name) != null) {
            throw new IllegalArgumentException();
        }

        final PVariable previous = pvariables.peekLast();
        int aslot = 0;

        if (previous != null) {
            aslot += previous.aslot + previous.ptype.getPSort().getASize();
        }

        final PVariable pvariable = new PVariable(name, ptype, aslot);
        pvariables.add(pvariable);

        final int update = ascopes.pop() + 1;
        ascopes.push(update);

        return pvariable;
    }

    private PMetadata createPMetadata(final ParseTree source) {
        final PMetadata sourcemd = new PMetadata(source);
        pmetadata.put(source, sourcemd);

        return sourcemd;
    }

    private void passPMetadata(final ParseTree source, final PMetadata sourcemd) {
        sourcemd.source = source;
        pmetadata.put(source, sourcemd);
    }

    private PMetadata getPMetadata(final ParseTree source) {
        final PMetadata sourcemd = pmetadata.get(source);

        if (sourcemd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        return sourcemd;
    }

    private void markCast(final PMetadata pmetadata) {
        if (pmetadata.fromptype == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (pmetadata.toptype != null) {
            final Object object = getLegalCast(pmetadata.fromptype, pmetadata.toptype, pmetadata.explicit);

            if (object instanceof PCast) {
                pmetadata.pcast = (PCast)object;
            } else if (object instanceof PTransform) {
                pmetadata.ptransform = (PTransform)object;
            }

            if (pmetadata.toptype.getPSort().isPBasic()) {
                constCast(pmetadata);
            }
        } else if (!pmetadata.anypnumeric && !pmetadata.anyptype) {
            throw new IllegalStateException(); // TODO: message
        }
    }

    private Object getLegalCast(final PType pfrom, final PType pto, final boolean explicit) {
        if (pfrom.equals(pto)) {
            return null;
        }

        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.isPDisallowed(pcast)) {
            throw new ClassCastException(); // TODO: message
        }

        final PTransform pexplicit = ptypes.getPExplicit(pcast);

        if (explicit && pexplicit != null) {
            return pexplicit;
        }

        final PTransform pimplicit = ptypes.getPImplicit(pcast);

        if (pimplicit != null) {
            return pimplicit;
        }

        final PSort fpsort = pfrom.getPSort();
        final PSort tpsort = pto.getPSort();

        if (fpsort.isPNumeric() && tpsort.isPNumeric()) {
            switch (fpsort) {
                case BYTE:
                    switch (tpsort) {
                        case CHAR:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case SHORT:
                        case INT:
                        case LONG:
                        case FLOAT:
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case SHORT:
                    switch (tpsort) {
                        case BYTE:
                        case CHAR:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case INT:
                        case LONG:
                        case FLOAT:
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case CHAR:
                    switch (tpsort) {
                        case BYTE:
                        case SHORT:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case INT:
                        case LONG:
                        case FLOAT:
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case INT:
                    switch (tpsort) {
                        case BYTE:
                        case SHORT:
                        case CHAR:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case LONG:
                        case FLOAT:
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case LONG:
                    switch (tpsort) {
                        case BYTE:
                        case SHORT:
                        case CHAR:
                        case INT:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case FLOAT:
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case FLOAT:
                    switch (tpsort) {
                        case BYTE:
                        case SHORT:
                        case CHAR:
                        case INT:
                        case LONG:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        case DOUBLE:
                            return pcast;
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                case DOUBLE:
                    switch (tpsort) {
                        case BYTE:
                        case SHORT:
                        case CHAR:
                        case INT:
                        case LONG:
                        case FLOAT:
                            if (explicit) {
                                return pcast;
                            } else {
                                throw new ClassCastException(); // TODO : message
                            }
                        default:
                            throw new IllegalStateException(); // TODO: message
                    }
                    
                default:
                    throw new IllegalStateException(); // TODO: message
            }
        }

        try {
            pfrom.getJClass().asSubclass(pto.getJClass());

            return null;
        } catch (ClassCastException cce0) {
            try {
                if (explicit) {
                    pto.getJClass().asSubclass(pfrom.getJClass());

                    return pcast;
                } else {
                    throw new ClassCastException(); // TODO: message
                }
            } catch (ClassCastException cce1) {
                throw new ClassCastException(); // TODO: message
            }
        }
    }

    private void constCast(final PMetadata pmetadata) {
        if (pmetadata.constpre != null) {
            if (pmetadata.ptransform != null) {
                pmetadata.constpost = invokeTransform(pmetadata.ptransform, pmetadata.constpre);
            } else if (pmetadata.pcast != null) {
                final PSort pfsort = pmetadata.pcast.getPFrom().getPSort();
                final PSort ptsort = pmetadata.pcast.getPTo().getPSort();

                switch (pfsort) {
                    case BYTE:
                        switch (ptsort) {
                            case SHORT:
                                pmetadata.constpost = (short)(byte)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(byte)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(byte)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(byte)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(byte)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(byte)pmetadata.constpre;
                                break;
                            case BYTE:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                    case SHORT:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(short)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(short)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(short)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(short)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(short)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(short)pmetadata.constpre;
                                break;
                            case SHORT:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case CHAR:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(char)pmetadata.constpre;
                                break;
                            case SHORT:
                                pmetadata.constpost = (short)(char)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(char)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(char)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(char)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(char)pmetadata.constpre;
                                break;
                            case CHAR:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case INT:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(int)pmetadata.constpre;
                                break;
                            case SHORT:
                                pmetadata.constpost = (short)(int)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(int)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(int)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(int)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(int)pmetadata.constpre;
                                break;
                            case INT:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case LONG:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(long)pmetadata.constpre;
                                break;
                            case SHORT:
                                pmetadata.constpost = (short)(long)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(long)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(long)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(long)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(long)pmetadata.constpre;
                                break;
                            case LONG:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case FLOAT:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(float)pmetadata.constpre;
                                break;
                            case SHORT:
                                pmetadata.constpost = (short)(float)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(float)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(float)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(float)pmetadata.constpre;
                                break;
                            case DOUBLE:
                                pmetadata.constpost = (double)(float)pmetadata.constpre;
                                break;
                            case FLOAT:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case DOUBLE:
                        switch (ptsort) {
                            case BYTE:
                                pmetadata.constpost = (byte)(double)pmetadata.constpre;
                                break;
                            case SHORT:
                                pmetadata.constpost = (short)(double)pmetadata.constpre;
                                break;
                            case CHAR:
                                pmetadata.constpost = (char)(double)pmetadata.constpre;
                                break;
                            case INT:
                                pmetadata.constpost = (int)(double)pmetadata.constpre;
                                break;
                            case LONG:
                                pmetadata.constpost = (long)(double)pmetadata.constpre;
                                break;
                            case FLOAT:
                                pmetadata.constpost = (float)(double)pmetadata.constpre;
                                break;
                            case DOUBLE:
                            case BOOL:
                            case STRING:
                            default:
                                throw new IllegalStateException(); // TODO: message
                        }
                        break;
                    case BOOL:
                    case STRING:
                    default:
                        throw new IllegalStateException(); // TODO: message
                }
            } else {
                pmetadata.constpost = pmetadata.constpre;
            }
        }
    }

    private Object invokeTransform(final PTransform ptransform, final Object object) {
        final PMethod pmethod = ptransform.getPMethod();
        final Method jmethod = pmethod.getJMethod();
        final int modifiers = jmethod.getModifiers();

        try {
            if (Modifier.isStatic(modifiers)) {
                return jmethod.invoke(null, object);
            } else {
                return jmethod.invoke(object);
            }
        } catch (IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | NullPointerException |
                ExceptionInInitializerError exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    private PType getUnaryNumericPromotion(final PType pfrom, boolean decimal) {
        return getBinaryNumericPromotion(pfrom, null , decimal);
    }

    private PType getBinaryNumericPromotion(final PType pfrom0, final PType pfrom1, boolean decimal) {
        final Deque<PType> upcast = new ArrayDeque<>();
        final Deque<PType> downcast = new ArrayDeque<>();

        if (decimal) {
            upcast.push(pstandard.pdouble);
            upcast.push(pstandard.pfloat);
        } else {
            downcast.push(pstandard.pdouble);
            downcast.push(pstandard.pfloat);
        }

        upcast.push(pstandard.plong);
        upcast.push(pstandard.pint);

        while (!upcast.isEmpty()) {
            final PType pto = upcast.pop();
            final PCast pcast0 = new PCast(pfrom0, pto);
            boolean promote;

            promote = ptypes.isPDisallowed(pcast0); if (promote) continue;
            promote = upcast.contains(pfrom0); if (promote) continue;
            promote = downcast.contains(pfrom0) && ptypes.getPImplicit(pcast0) == null; if (promote) continue;
            promote = !pfrom0.getPSort().isPNumeric() && ptypes.getPImplicit(pcast0) == null; if (promote) continue;

            if (pfrom1 != null) {
                final PCast pcast1 = new PCast(pfrom1, pto);

                promote = ptypes.isPDisallowed(pcast1); if (promote) continue;
                promote = upcast.contains(pfrom1); if (promote) continue;
                promote = downcast.contains(pfrom1) && ptypes.getPImplicit(pcast1) == null; if (promote) continue;
                promote = !pfrom1.getPSort().isPNumeric() && ptypes.getPImplicit(pcast1) == null; if (promote) continue;
            }

            return pto;
        }

        return null;
    }

    private PType getBinaryAnyPromotion(final PType pfrom0, final PType pfrom1) {
        if (pfrom0.equals(pfrom1)) {
            return pfrom0;
        }

        final PSort pfsort0 = pfrom0.getPSort();
        final PSort pfsort1 = pfrom1.getPSort();

        if (pfsort0 == PSort.BOOL || pfsort1 == PSort.BOOL) {
            final PCast pcast = new PCast(pfsort0 == PSort.BOOL ? pfrom1 : pfrom0, pstandard.pbool);

            if (!ptypes.isPDisallowed(pcast) && ptypes.getPImplicit(pcast) != null) {
                return pstandard.pbool;
            }
        }

        if (pfsort0.isPNumeric() || pfsort1.isPNumeric()) {
            PType ptype = getBinaryNumericPromotion(pfrom0, pfrom1, true);

            if (ptype != null) {
                return ptype;
            }
        }

        if (pfrom0.getJClass().equals(pfrom1.getJClass())) {
            PCast pcast0 = null;
            PCast pcast1 = null;

            if (pfrom0.getPClass().isPGeneric() && !pfrom1.getPClass().isPGeneric()) {
                pcast0 = new PCast(pfrom0, pfrom1);
                pcast1 = new PCast(pfrom1, pfrom0);
            } else if (!pfrom0.getPClass().isPGeneric() && pfrom1.getPClass().isPGeneric()) {
                pcast0 = new PCast(pfrom1, pfrom0);
                pcast1 = new PCast(pfrom0, pfrom1);
            }

            if (pcast0 != null && pcast1 != null) {
                if (!ptypes.isPDisallowed(pcast0) && ptypes.getPImplicit(pcast0) != null) {
                    return pcast0.getPTo();
                }

                if (!ptypes.isPDisallowed(pcast1) && ptypes.getPImplicit(pcast1) != null) {
                    return pcast1.getPTo();
                }
            }

            return pstandard.pobject;
        }

        try {
            pfrom0.getJClass().asSubclass(pfrom1.getJClass());
            final PCast pcast = new PCast(pfrom0, pfrom1);

            if (!ptypes.isPDisallowed(pcast)) {
                return pfrom1;
            }
        } catch (ClassCastException cce0) {
            // Do nothing.
        }

        try {
            pfrom1.getJClass().asSubclass(pfrom0.getJClass());
            final PCast pcast = new PCast(pfrom1, pfrom0);

            if (!ptypes.isPDisallowed(pcast)) {
                return pfrom0;
            }
        } catch (ClassCastException cce0) {
            // Do nothing.
        }

        if (pfsort0.isJObject() && pfsort1.isJObject()) {
            return pstandard.pobject;
        }

        return null;
    }

    @Override
    public Void visitSource(final SourceContext ctx) {
        final PMetadata sourcemd = pmetadata.get(ctx);
        PMetadata statementmd = null;

        incrementScope();

        for (final StatementContext sctx : ctx.statement()) {
            if (sourcemd.close) {
                throw new IllegalArgumentException(); // TODO: message
            }

            statementmd = createPMetadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalArgumentException(); // TODO: message
            }

            sourcemd.close = statementmd.close;

            if (statementmd.anybreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (statementmd.anycontinue) {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        if (statementmd == null) {
            throw new IllegalStateException(); // TODO: message
        }

        sourcemd.allrtn = statementmd.allrtn;

        decrementScope();

        return null;
    }

    @Override
    public Void visitIf(final IfContext ctx) {
        final PMetadata ifmd = getPMetadata(ctx);

        incrementScope();

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = pstandard.pbool;
        visit(ectx);

        if (expressionmd.constpost != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final BlockContext bctx0 = ctx.block(0);
        final PMetadata blockmd0 = createPMetadata(bctx0);
        visit(bctx0);
        ifmd.anyrtn = blockmd0.anyrtn;
        ifmd.anybreak = blockmd0.anybreak;
        ifmd.anycontinue = blockmd0.anycontinue;

        if (ctx.ELSE() != null) {
            final BlockContext bctx1 = ctx.block(1);
            final PMetadata blockmd1 = createPMetadata(bctx1);
            visit(bctx1);
            ifmd.close = blockmd0.close && blockmd1.close;
            ifmd.allexit = blockmd0.allexit && blockmd1.allexit;
            ifmd.allrtn = blockmd0.allrtn && blockmd1.allrtn;
            ifmd.anyrtn |= blockmd1.anyrtn;
            ifmd.allbreak = blockmd0.allbreak && blockmd1.allbreak;
            ifmd.anybreak |= blockmd1.anybreak;
            ifmd.allcontinue = blockmd0.allcontinue && blockmd1.allcontinue;
            ifmd.anycontinue |= blockmd1.anycontinue;
        }

        ifmd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitWhile(final WhileContext ctx) {
        final PMetadata whilemd = getPMetadata(ctx);

        incrementScope();

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = pstandard.pbool;
        visit(ectx);

        final boolean emptyallowed = expressionmd.statement;
        boolean exitrequired = false;

        if (expressionmd.constpost != null) {
            boolean constant = (boolean)expressionmd.constpost;

            if (!constant) {
                throw new IllegalArgumentException(); // TODO: message
            }

            exitrequired = true;
            whilemd.constpost = true;
        }

        final BlockContext bctx = ctx.block();

        if (bctx != null) {
            PMetadata blockmd = createPMetadata(bctx);
            visit(bctx);

            if (blockmd.allrtn) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (blockmd.allbreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && !blockmd.anyrtn && !blockmd.anybreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && blockmd.anyrtn && !blockmd.anybreak) {
                whilemd.close = true;
            }
        } else if (!emptyallowed) {
            throw new IllegalArgumentException(); // TODO: message
        }

        whilemd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitDo(final DoContext ctx) {
        final PMetadata domd = getPMetadata(ctx);

        incrementScope();

        final BlockContext bctx = ctx.block();
        final PMetadata blockmd = createPMetadata(bctx);
        visit(bctx);

        if (blockmd.allrtn) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (blockmd.allbreak) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (blockmd.allcontinue) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = pstandard.pbool;
        visit(ectx);

        if (expressionmd.constpost != null) {
            boolean constant = (boolean)expressionmd.constpost;

            if (constant && !blockmd.anyrtn && !blockmd.anybreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (constant && blockmd.anyrtn && !blockmd.anybreak) {
                domd.close = true;
            }

            if (!constant && !blockmd.anycontinue) {
                throw new IllegalArgumentException(); // TODO: message
            }

            domd.constpost = true;
        }

        domd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitFor(final ForContext ctx) {
        final PMetadata formd = getPMetadata(ctx);
        boolean emptyallowed = false;
        boolean exitrequired = false;

        incrementScope();

        final DeclarationContext dctx = ctx.declaration();

        if (dctx != null) {
            final PMetadata declarationmd = createPMetadata(dctx);
            visit(dctx);

            if (!declarationmd.statement) {
                throw new IllegalStateException(); // TODO: message
            }
        }

        final ExpressionContext ectx0 = ctx.expression(0);

        if (ectx0 != null) {
            final PMetadata expressionmd0 = createPMetadata(ectx0);
            expressionmd0.toptype = pstandard.pbool;
            visit(ectx0);

            emptyallowed = expressionmd0.statement;

            if (expressionmd0.constpost != null) {
                boolean constant = (boolean)expressionmd0.constpost;

                if (!constant) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                exitrequired = true;
                formd.constpost = true;
            }
        } else {
            exitrequired = true;
            formd.constpost = true;
        }

        final ExpressionContext ectx1 = ctx.expression(1);

        if (ectx1 != null) {
            final PMetadata expressionmd1 = createPMetadata(ectx1);
            expressionmd1.toptype = pstandard.pvoid;
            expressionmd1.pop = true;
            visit(ectx1);

            if (!expressionmd1.statement) {
                throw new IllegalStateException(); // TODO: message
            }

            emptyallowed = true;
        }

        final BlockContext bctx = ctx.block();

        if (bctx != null) {
            final PMetadata blockmd = createPMetadata(bctx);
            visit(bctx);

            if (blockmd.allrtn) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (blockmd.allbreak) {
                throw new IllegalArgumentException(); //TODO: message
            }

            if (exitrequired && !blockmd.anyrtn && !blockmd.anybreak) {
                throw new IllegalArgumentException(); // TODO: message
            }

            if (exitrequired && blockmd.anyrtn && !blockmd.anybreak) {
                formd.close = true;
            }
        } else if (exitrequired) {
            throw new IllegalArgumentException(); // TODO: message
        } else if (!emptyallowed) {
            throw new IllegalArgumentException(); // TODO: message
        }

        formd.statement = true;

        decrementScope();

        return null;
    }

    @Override
    public Void visitDecl(final DeclContext ctx) {
        final PMetadata declmd = getPMetadata(ctx);

        final DeclarationContext dctx = ctx.declaration();
        final PMetadata declarationmd = createPMetadata(dctx);
        visit(ctx.declaration());

        declmd.statement = declarationmd.statement;

        return null;
    }

    @Override
    public Void visitContinue(final ContinueContext ctx) {
        final PMetadata continuemd = getPMetadata(ctx);

        continuemd.statement = true;
        continuemd.close = true;

        continuemd.allexit = true;
        continuemd.allcontinue = true;
        continuemd.anycontinue = true;

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final PMetadata breakmd = getPMetadata(ctx);

        breakmd.statement = true;
        breakmd.close = true;

        breakmd.allexit = true;
        breakmd.allbreak = true;
        breakmd.anybreak = true;

        return null;
    }

    @Override
    public Void visitReturn(final ReturnContext ctx) {
        final PMetadata returnmd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = pstandard.pobject;
        visit(ectx);

        returnmd.statement = true;
        returnmd.close = true;

        returnmd.allexit = true;
        returnmd.allrtn = true;
        returnmd.anyrtn = true;

        return null;
    }

    @Override
    public Void visitExpr(final ExprContext ctx) {
        final PMetadata exprmd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = pstandard.pvoid;
        expressionmd.pop = true;
        visit(ectx);

        exprmd.statement = expressionmd.statement;

        return null;
    }

    @Override
    public Void visitMultiple(final MultipleContext ctx) {
        final PMetadata multiplemd = getPMetadata(ctx);

        for (StatementContext sctx : ctx.statement()) {
            if (multiplemd.close) {
                throw new IllegalStateException();  // TODO: message
            }

            final PMetadata statementmd = createPMetadata(sctx);
            visit(sctx);

            if (!statementmd.statement) {
                throw new IllegalStateException(); // TODO: message
            }

            multiplemd.close = statementmd.close;

            multiplemd.allexit = statementmd.allexit;
            multiplemd.allrtn = statementmd.allrtn && !statementmd.anybreak && !statementmd.anycontinue;
            multiplemd.anyrtn |= statementmd.anyrtn;
            multiplemd.allbreak = !statementmd.anyrtn && statementmd.allbreak && !statementmd.anycontinue;
            multiplemd.anybreak |= statementmd.anybreak;
            multiplemd.allcontinue = !statementmd.anyrtn && !statementmd.anybreak && !statementmd.allcontinue;
            multiplemd.anycontinue |= statementmd.anycontinue;
        }

        multiplemd.statement = true;

        return null;
    }

    @Override
    public Void visitSingle(final SingleContext ctx) {
        final PMetadata singlemd = getPMetadata(ctx);

        final StatementContext sctx = ctx.statement();
        final PMetadata statementmd = createPMetadata(sctx);
        visit(sctx);

        if (!statementmd.statement) {
            throw new IllegalStateException(); // TODO: message
        }

        singlemd.statement = true;
        singlemd.close = statementmd.close;

        singlemd.allexit = statementmd.allexit;
        singlemd.allrtn = statementmd.allrtn;
        singlemd.anyrtn = statementmd.anyrtn;
        singlemd.allbreak = statementmd.allbreak;
        singlemd.anybreak = statementmd.anybreak;
        singlemd.allcontinue = statementmd.allcontinue;
        singlemd.anycontinue = statementmd.anycontinue;

        return null;
    }

    @Override
    public Void visitEmpty(final EmptyContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }

    @Override
    public Void visitDeclaration(final DeclarationContext ctx) {
        final PMetadata declarationmd = getPMetadata(ctx);

        final DecltypeContext dctx0 = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx0);
        decltypemd.anyptype = true;
        visit(dctx0);

        for (final DeclvarContext dctx1 : ctx.declvar()) {
            PMetadata declvarmd = createPMetadata(dctx1);
            declvarmd.toptype = decltypemd.fromptype;
            visit(dctx1);
        }

        declarationmd.statement = true;

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final PMetadata decltypemd = getPMetadata(ctx);

        if (!decltypemd.anyptype) {
            throw new IllegalStateException(); // TODO: message
        }

        final String pnamestr = ctx.getText();
        decltypemd.fromptype = getPTypeFromCanonicalPName(ptypes, pnamestr);

        return null;
    }

    @Override
    public Void visitDeclvar(final DeclvarContext ctx) {
        final PMetadata declvarmd = getPMetadata(ctx);

        final String name = ctx.ID().getText();
        declvarmd.constpost = addPVariable(name, declvarmd.toptype);

        final ExpressionContext ectx = ctx.expression();

        if (ectx != null) {
            final PMetadata expressionmd = createPMetadata(ectx);
            expressionmd.toptype = declvarmd.toptype;
            visit(ectx);
        }

        return null;
    }

    @Override
    public Void visitPrecedence(final PrecedenceContext ctx) {
        final PMetadata precedencemd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        passPMetadata(ectx, precedencemd);
        visit(ectx);

        return null;
    }

    @Override
    public Void visitNumeric(final NumericContext ctx) {
        final PMetadata numericmd = getPMetadata(ctx);

        if (ctx.DECIMAL() != null) {
            final String svalue = ctx.DECIMAL().getText();

            if (svalue.endsWith("f") || svalue.endsWith("F")) {
                try {
                    numericmd.fromptype = pstandard.pfloat;
                    numericmd.constpre = Float.parseFloat(svalue.substring(0, svalue.length() - 1));
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericmd.fromptype = pstandard.pdouble;
                    numericmd.constpre = Double.parseDouble(svalue);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        } else {
            String svalue;
            int radix;

            if (ctx.OCTAL() != null) {
                svalue = ctx.OCTAL().getText();
                radix = 8;
            } else if (ctx.INTEGER() != null) {
                svalue = ctx.INTEGER().getText();
                radix = 10;
            } else if (ctx.HEX() != null) {
                svalue = ctx.HEX().getText();
                radix = 16;
            } else {
                throw new IllegalStateException(); // TODO: message
            }

            if (svalue.endsWith("l") || svalue.endsWith("L")) {
                try {
                    numericmd.fromptype = pstandard.plong;
                    numericmd.constpre = Long.parseLong(svalue.substring(0, svalue.length() - 1), radix);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                try {
                    numericmd.fromptype = pstandard.pint;
                    numericmd.constpre = Integer.parseInt(svalue, radix);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }
        }

        markCast(numericmd);

        return null;
    }

    @Override
    public Void visitString(final StringContext ctx) {
        final PMetadata stringmd = getPMetadata(ctx);

        if (ctx.STRING() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        final int length = ctx.STRING().getText().length();
        stringmd.constpre = ctx.STRING().getText().substring(1, length - 1);
        stringmd.fromptype = pstandard.pstring;

        markCast(stringmd);

        return null;
    }

    @Override
    public Void visitChar(final CharContext ctx) {
        final PMetadata charmd = getPMetadata(ctx);

        if (ctx.CHAR() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (ctx.CHAR().getText().length() != 3) {
            throw new IllegalStateException(); // TODO: message
        }

        charmd.constpre = ctx.CHAR().getText().charAt(1);
        charmd.fromptype = pstandard.pchar;

        markCast(charmd);

        return null;
    }

    @Override
    public Void visitTrue(final TrueContext ctx) {
        final PMetadata truemd = getPMetadata(ctx);

        if (ctx.TRUE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        truemd.constpre = true;
        truemd.fromptype = pstandard.pbool;

        markCast(truemd);

        return null;
    }

    @Override
    public Void visitFalse(final FalseContext ctx) {
        final PMetadata falsemd = getPMetadata(ctx);

        if (ctx.FALSE() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        falsemd.constpre = false;
        falsemd.fromptype = pstandard.pbool;

        markCast(falsemd);

        return null;
    }

    @Override
    public Void visitNull(final NullContext ctx) {
        final PMetadata nullmd = getPMetadata(ctx);

        if (ctx.NULL() == null) {
            throw new IllegalStateException(); // TODO: message
        }

        nullmd.isnull = true;
        nullmd.fromptype = pstandard.pobject;

        markCast(nullmd);

        return null;
    }

    @Override
    public Void visitExt(final ExtContext ctx) {
        final PMetadata extmd = getPMetadata(ctx);

        final ExtstartContext ectx = ctx.extstart();
        passPMetadata(ectx, extmd);
        visit(ectx);

        return null;
    }

    @Override
    public Void visitUnary(final UnaryContext ctx) {
        final PMetadata unarymd = getPMetadata(ctx);

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);

        if (ctx.BOOLNOT() != null) {
            expressionmd.toptype = pstandard.pbool;
            visit(ectx);

            if (expressionmd.constpost != null) {
                unarymd.constpre = !(boolean)expressionmd.constpost;
            }

            unarymd.fromptype = pstandard.pbool;
        } else if (ctx.BWNOT() != null || ctx.ADD() != null || ctx.SUB() != null) {
            expressionmd.anypnumeric = true;
            visit(ectx);

            final boolean decimal = ctx.ADD() != null || ctx.SUB() != null;
            final PType promoteptype = getUnaryNumericPromotion(expressionmd.fromptype, decimal);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd.toptype = promoteptype;
            markCast(expressionmd);

            if (expressionmd.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.BWNOT() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = ~(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = ~(long)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.SUB() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = -(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = -(long)expressionmd.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constpre = -(float)expressionmd.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constpre = -(double)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.ADD() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constpre = +(int)expressionmd.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constpre = +(long)expressionmd.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constpre = +(float)expressionmd.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constpre = +(double)expressionmd.constpost;
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }

            unarymd.fromptype = promoteptype;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        markCast(unarymd);

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final PMetadata castmd = getPMetadata(ctx);

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        decltypemd.anyptype = true;
        visit(dctx);

        final PType declptype = decltypemd.fromptype;
        castmd.fromptype = declptype;

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        expressionmd.toptype = declptype;
        expressionmd.explicit = true;
        visit(ectx);

        if (expressionmd.constpost != null) {
            castmd.constpre = expressionmd.constpost;
        }

        markCast(castmd);

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final PMetadata binarymd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.anypnumeric = true;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.anypnumeric = true;
        visit(ectx1);

        final boolean decimal = ctx.ADD() != null || ctx.SUB() != null ||
                ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null;
        final PType promoteptype = getBinaryNumericPromotion(expressionmd0.fromptype, expressionmd1.fromptype, decimal);

        if (promoteptype == null) {
            throw new ClassCastException(); // TODO: message
        }

        expressionmd0.toptype = promoteptype;
        markCast(expressionmd0);
        expressionmd1.toptype = promoteptype;
        markCast(expressionmd1);

        if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
            final PSort promotepsort = promoteptype.getPSort();
            
            if (ctx.MUL() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost * (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost * (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost * (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost * (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.DIV() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost / (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost / (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost / (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost / (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.REM() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost % (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost % (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost % (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost % (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.ADD() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost + (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost + (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost + (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost + (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.SUB() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost - (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost - (long)expressionmd1.constpost;
                } else if (promotepsort == PSort.FLOAT) {
                    binarymd.constpre = (float)expressionmd0.constpost - (float)expressionmd1.constpost;
                } else if (promotepsort == PSort.DOUBLE) {
                    binarymd.constpre = (double)expressionmd0.constpost - (double)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.LSH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost << (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost << (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.RSH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost >> (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost >> (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.USH() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost >>> (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost >>> (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWAND() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost & (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost & (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWXOR() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost ^ (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost ^ (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWOR() != null) {
                if (promotepsort == PSort.INT) {
                    binarymd.constpre = (int)expressionmd0.constpost | (int)expressionmd1.constpost;
                } else if (promotepsort == PSort.LONG) {
                    binarymd.constpre = (long)expressionmd0.constpost | (long)expressionmd1.constpost;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        binarymd.fromptype = promoteptype;
        markCast(binarymd);

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);

        if (expressionmd0.isnull && expressionmd1.isnull) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (ctx.EQ() != null || ctx.NE() != null) {
            expressionmd0.anyptype = true;
            visit(ectx0);
            expressionmd1.anyptype = true;
            visit(ectx1);

            final PType promoteptype = getBinaryAnyPromotion(expressionmd0.fromptype, expressionmd1.fromptype);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd0);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd1);

            if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.EQ() != null) {
                    if (promotepsort == PSort.BOOL) {
                        compmd.constpre = (boolean)expressionmd0.constpost == (boolean)expressionmd1.constpost;
                    } else if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost == (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost == (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost == (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost == (double)expressionmd1.constpost;
                    } else {
                        compmd.constpre = expressionmd0.constpost == expressionmd1.constpost;
                    }
                } else if (ctx.NE() != null) {
                    if (promotepsort == PSort.BOOL) {
                        compmd.constpre = (boolean)expressionmd0.constpost != (boolean)expressionmd1.constpost;
                    } else if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost != (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost != (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost != (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost != (double)expressionmd1.constpost;
                    } else {
                        compmd.constpre = expressionmd0.constpost != expressionmd1.constpost;
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else if (ctx.GT() != null || ctx.GTE() != null || ctx.LT() != null || ctx.LTE() != null) {
            expressionmd0.anypnumeric = true;
            visit(ectx0);
            expressionmd1.anypnumeric = true;
            visit(ectx1);

            final PType promoteptype = getBinaryNumericPromotion(expressionmd0.fromptype, expressionmd1.fromptype, true);

            if (promoteptype == null) {
                throw new ClassCastException(); // TODO: message
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd0);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd1);

            if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.GTE() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost >= (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost >= (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost >= (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost >= (double)expressionmd1.constpost;
                    }
                } else if (ctx.GT() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost > (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost > (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost > (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost > (double)expressionmd1.constpost;
                    }
                } else if (ctx.LTE() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost <= (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost <= (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost <= (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost <= (double)expressionmd1.constpost;
                    }
                } else if (ctx.LT() != null) {
                    if (promotepsort == PSort.INT) {
                        compmd.constpre = (int)expressionmd0.constpost < (int)expressionmd1.constpost;
                    } else if (promotepsort == PSort.LONG) {
                        compmd.constpre = (long)expressionmd0.constpost < (long)expressionmd1.constpost;
                    } else if (promotepsort == PSort.FLOAT) {
                        compmd.constpre = (float)expressionmd0.constpost < (float)expressionmd1.constpost;
                    } else if (promotepsort == PSort.DOUBLE) {
                        compmd.constpre = (double)expressionmd0.constpost < (double)expressionmd1.constpost;
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            throw new IllegalStateException();
        }

        compmd.fromptype = pstandard.pbool;
        markCast(compmd);

        return null;
    }

    @Override
    public Void visitBool(final BoolContext ctx) {
        final PMetadata boolmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.toptype = pstandard.pbool;
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.toptype = pstandard.pbool;
        visit(ectx1);

        if (expressionmd0.constpost != null && expressionmd1.constpost != null) {
            if (ctx.BOOLAND() != null) {
                boolmd.constpre = (boolean)expressionmd0.constpost && (boolean)expressionmd1.constpost;
            } else if (ctx.BOOLOR() != null) {
                boolmd.constpre = (boolean)expressionmd0.constpost || (boolean)expressionmd1.constpost;
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        }

        boolmd.fromptype = pstandard.pbool;
        markCast(boolmd);

        return null;
    }

    @Override
    public Void visitConditional(final ConditionalContext ctx) {
        final PMetadata conditionalmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        expressionmd0.toptype = pstandard.pbool;
        visit(ectx0);

        if (expressionmd0.constpost != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        expressionmd1.toptype = conditionalmd.toptype;
        expressionmd1.anyptype = conditionalmd.anyptype;
        expressionmd1.anypnumeric = conditionalmd.anypnumeric;
        visit(ectx1);

        final ExpressionContext ectx2 = ctx.expression(2);
        final PMetadata expressionmd2 = createPMetadata(ectx2);
        expressionmd2.toptype = conditionalmd.toptype;
        expressionmd1.anyptype = conditionalmd.anyptype;
        expressionmd1.anypnumeric = conditionalmd.anypnumeric;
        visit(ectx2);

        if (conditionalmd.toptype != null) {
            conditionalmd.fromptype = conditionalmd.toptype;
        } else if (conditionalmd.anyptype || conditionalmd.anypnumeric) {
            PType promoteptype = conditionalmd.anyptype ?
                    getBinaryAnyPromotion(expressionmd1.fromptype, expressionmd2.fromptype) :
                    getBinaryNumericPromotion(expressionmd1.fromptype, expressionmd2.fromptype, true);

            if (promoteptype == null) {
                throw new ClassCastException();
            }

            expressionmd0.toptype = promoteptype;
            markCast(expressionmd1);
            expressionmd1.toptype = promoteptype;
            markCast(expressionmd2);

            conditionalmd.fromptype = promoteptype;
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        markCast(conditionalmd);

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);

        final ExtstartContext ectx0 = ctx.extstart();
        final PMetadata extstartmd = createPMetadata(ectx0);
        extstartmd.anyptype = true;
        extstartmd.pop = assignmentmd.pop;
        visit(ectx0);

        if (extstartmd.pexternal == null) {
            throw new IllegalStateException(); // TODO: message
        }

        if (extstartmd.pexternal.isReadOnly()) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext ectx1 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx1);
        expressionmd.toptype = extstartmd.pexternal.getPType();
        visit(ectx1);

        extstartmd.pexternal.addSegment(SType.WRITE, ectx1);

        assignmentmd.fromptype = extstartmd.fromptype;
        assignmentmd.statement = true;
        markCast(assignmentmd);

        return null;
    }

    @Override
    public Void visitExtstart(final ExtstartContext ctx) {
        final PMetadata extstartmd = getPMetadata(ctx);
        final boolean pop = extstartmd.pop;
        extstartmd.pexternal = new PExternal();

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd = createPMetadata(ectx0);
            extprecmd.pexternal = extstartmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd = createPMetadata(ectx1);
            extcastmd.pexternal = extstartmd.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extstartmd.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extstartmd.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        extstartmd.statement = extstartmd.pexternal.isCall();
        extstartmd.fromptype = pop ? pstandard.pvoid : extstartmd.pexternal.getPType();
        markCast(extstartmd);

        return null;
    }

    @Override
    public Void visitExtprec(final ExtprecContext ctx) {
        final PMetadata extprecmd0 = getPMetadata(ctx);

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd1 = createPMetadata(ectx0);
            extprecmd1.pexternal = extprecmd0.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd = createPMetadata(ectx1);
            extcastmd.pexternal = extprecmd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extprecmd0.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extprecmd0.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final ExtdotContext ectx4 = ctx.extdot();
        final ExtarrayContext ectx5 = ctx.extarray();

        if (ectx4 != null) {
            final PMetadata extdotmd = createPMetadata(ectx4);
            extdotmd.pexternal = extprecmd0.pexternal;
            visit(ectx4);
        } else if (ectx5 != null) {
            final PMetadata extarraymd = createPMetadata(ectx5);
            extarraymd.pexternal = extprecmd0.pexternal;
            visit(ectx5);
        }

        return null;
    }

    @Override
    public Void visitExtcast(final ExtcastContext ctx) {
        final PMetadata extcastmd0 = getPMetadata(ctx);

        final ExtprecContext ectx0 = ctx.extprec();
        final ExtcastContext ectx1 = ctx.extcast();
        final ExttypeContext ectx2 = ctx.exttype();
        final ExtmemberContext ectx3 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extprecmd1 = createPMetadata(ectx0);
            extprecmd1.pexternal = extcastmd0.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extcastmd1 = createPMetadata(ectx1);
            extcastmd1.pexternal = extcastmd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata exttypemd = createPMetadata(ectx2);
            exttypemd.pexternal = extcastmd0.pexternal;
            visit(ectx2);
        } else if (ectx3 != null) {
            final PMetadata extmembermd = createPMetadata(ectx3);
            extmembermd.pexternal = extcastmd0.pexternal;
            visit(ectx3);
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        decltypemd.anyptype = true;
        visit(dctx);

        final PType pfrom = extcastmd0.pexternal.getPType();
        final PType pto = decltypemd.fromptype;

        final Object object = getLegalCast(pfrom, pto, true);

        if (object instanceof PCast) {
            extcastmd0.pexternal.addSegment(SType.CAST, object);
        } else if (object instanceof PTransform) {
            extcastmd0.pexternal.addSegment(SType.TRANSFORM, object);
        }

        return null;
    }

    @Override
    public Void visitExtarray(final ExtarrayContext ctx) {
        final PMetadata extarraymd0 = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx0);
        expressionmd.toptype = pstandard.pint;
        visit(ectx0);

        extarraymd0.pexternal.addSegment(SType.NODE, ectx0);
        extarraymd0.pexternal.addSegment(SType.ARRAY, null);

        final ExtdotContext ectx1 = ctx.extdot();
        final ExtarrayContext ectx2 = ctx.extarray();

        if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extarraymd0.pexternal;
            visit(ectx1);
        } else if (ectx2 != null) {
            final PMetadata extarraymd1 = createPMetadata(ectx2);
            extarraymd1.pexternal = extarraymd0.pexternal;
            visit(ectx2);
        }

        return null;
    }

    @Override
    public Void visitExtdot(final ExtdotContext ctx) {
        final PMetadata extdotmd = getPMetadata(ctx);

        final ExtcallContext ectx0 = ctx.extcall();
        final ExtmemberContext ectx1 = ctx.extmember();

        if (ectx0 != null) {
            final PMetadata extcallmd = createPMetadata(ectx0);
            extcallmd.pexternal = extdotmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extmembermd = createPMetadata(ectx1);
            extmembermd.pexternal = extdotmd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitExttype(final ExttypeContext ctx) {
        final PMetadata exttypemd = getPMetadata(ctx);
        final String ptypestr = ctx.TYPE().getText();
        final PType ptype = getPTypeFromCanonicalPName(ptypes, ptypestr);

        if (ptype == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        exttypemd.pexternal.addSegment(SType.TYPE, ptype);

        final ExtdotContext ectx = ctx.extdot();
        final PMetadata extdotmd = createPMetadata(ectx);
        extdotmd.pexternal = exttypemd.pexternal;
        visit(ectx);

        return null;
    }

    @Override
    public Void visitExtcall(final ExtcallContext ctx) {
        final PMetadata extcallmd = getPMetadata(ctx);
        final PType declptype = extcallmd.pexternal.getPType();
        final PClass pclass = declptype.getPClass();

        if (pclass == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final String pname = ctx.ID().getText();
        final boolean statik = extcallmd.pexternal.isStatic();
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        SType stype;
        Object svalue;
        PType[] argumentsptypes;

        if (statik && "makearray".equals(pname)) {
            stype = SType.AMAKE;
            svalue = arguments.size();
            argumentsptypes = new PType[arguments.size()];
            Arrays.fill(argumentsptypes, pstandard.pint);
        } else {
            final PConstructor pconstructor = statik ? pclass.getPConstructor(pname) : null;
            final PMethod pmethod = statik ? pclass.getPFunction(pname) : pclass.getPMethod(pname);

            if (pconstructor != null) {
                stype = SType.CONSTRUCTOR;
                svalue = pconstructor;
                argumentsptypes = new PType[pconstructor.getPArguments().size()];
                pconstructor.getPArguments().toArray(argumentsptypes);
            } else if (pmethod != null) {
                stype = SType.METHOD;
                svalue = pmethod;
                argumentsptypes = new PType[pmethod.getPArguments().size()];
                pmethod.getPArguments().toArray(argumentsptypes);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        if (arguments.size() != argumentsptypes.length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int argument = 0; argument < arguments.size(); ++argument) {
            final ParseTree ectx = arguments.get(argument);
            final PMetadata expressionmd = createPMetadata(ectx);
            expressionmd.toptype = argumentsptypes[argument];
            visit(ectx);

            extcallmd.pexternal.addSegment(SType.NODE, ectx);
        }

        extcallmd.pexternal.addSegment(stype, svalue);

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtarrayContext ectx1 = ctx.extarray();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extcallmd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitExtmember(final ExtmemberContext ctx) {
        final PMetadata extmembermd = getPMetadata(ctx);
        final PType ptype = extmembermd.pexternal.getPType();
        final String pname = ctx.ID().getText();

        if (ptype == null) {
            final PVariable pvariable = getPVariable(pname);

            if (pvariable == null) {
                throw new IllegalArgumentException(); // TODO: message
            }

            extmembermd.pexternal.addSegment(SType.VARIABLE, pvariable);
        } else {
            if (ptype.getPSort() == PSort.ARRAY) {
                if ("length".equals(pname)) {
                    extmembermd.pexternal.addSegment(SType.ALENGTH, null);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                final PClass pclass = ptype.getPClass();

                if (pclass == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                final boolean statik = extmembermd.pexternal.isStatic();
                final PField pmember = statik ? pclass.getPStatic(pname) : pclass.getPMember(pname);

                if (pmember == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                extmembermd.pexternal.addSegment(SType.FIELD, pmember);
            }
        }

        final ExtdotContext ectx0 = ctx.extdot();
        final ExtarrayContext ectx1 = ctx.extarray();

        if (ectx0 != null) {
            final PMetadata extdotmd = createPMetadata(ectx0);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx0);
        } else if (ectx1 != null) {
            final PMetadata extdotmd = createPMetadata(ectx1);
            extdotmd.pexternal = extmembermd.pexternal;
            visit(ectx1);
        }

        return null;
    }

    @Override
    public Void visitArguments(final ArgumentsContext ctx) {
        throw new UnsupportedOperationException(); // TODO: message
    }
}

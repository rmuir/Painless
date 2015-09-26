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
import org.antlr.v4.runtime.tree.TerminalNode;

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

        private boolean allrtn;
        private boolean anyrtn;
        private boolean allbreak;
        private boolean anybreak;
        private boolean allcontinue;
        private boolean anycontinue;

        private boolean write;
        private boolean pop;
        private PExternal pexternal;

        private Object constpre;
        private Object constpost;
        private boolean isnull;

        private PType toptype;
        private boolean pnumeric;
        private boolean anyptype;
        private boolean explicit;
        private PType fromptype;
        private PCast pcast;
        private PTransform ptransform;

        PMetadata(final ParseTree source) {
            this.source = source;

            close = false;
            statement = false;

            allrtn = false;
            anyrtn = false;
            allbreak = false;
            anybreak = false;
            allcontinue = false;
            anycontinue = false;

            write = false;
            pop = false;
            pexternal = null;

            constpre = null;
            constpost = null;
            isnull = false;

            toptype = null;
            pnumeric = false;
            anyptype = false;
            explicit = false;
            fromptype = null;
            pcast = null;
            ptransform = null;
        }

        ParseTree getSource() {
            return source;
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

        boolean getWrite() {
            return write;
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

        boolean getPNumeric() {
            return pnumeric;
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

    private void addPVariable(final String name, final PType ptype) {
        if (getPVariable(name) != null) {
            throw new IllegalArgumentException();
        }

        final PVariable previous = pvariables.peek();
        int aslot = 0;

        if (previous != null) {
            aslot += previous.aslot + previous.ptype.getPSort().getASize();
        }

        final PVariable pvariable = new PVariable(name, ptype, aslot);
        pvariables.push(pvariable);

        final int update = ascopes.pop() + 1;
        ascopes.push(update);
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
        } else if (!pmetadata.pnumeric && !pmetadata.anyptype) {
            throw new IllegalStateException(); // TODO: message
        }
    }

    private Object getLegalCast(final PType pfrom, final PType pto, final boolean explicit) {
        if (pfrom.equals(pto)) {
            return null;
        }

        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.isPDisallowed(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
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

    private Object invokeTransform(final PType pfrom, final PType pto, final Object object, final boolean explicit) {
        final PCast pcast = new PCast(pfrom, pto);

        if (ptypes.isPDisallowed(pcast)) {
            throw new IllegalArgumentException(); // TODO: message
        }

        PTransform ptransform = null;

        if (explicit) {
            ptransform = ptypes.getPExplicit(pcast);
        }

        if (ptransform == null) {
            ptransform = ptypes.getPImplicit(pcast);
        }

        if (ptransform == null) {
            return null;
        }

        return invokeTransform(ptransform, object);
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

    private PType getNumericPromotion(final PType pfrom0, final PType pfrom1) {
        final Deque<PType> ptypes = new ArrayDeque<>();

        ptypes.push(pstandard.pdouble);
        ptypes.push(pstandard.pfloat);
        ptypes.push(pstandard.plong);
        ptypes.push(pstandard.pint);

        while (!ptypes.isEmpty()) {
            final PType pto = ptypes.pop();
            final PCast pcast0 = new PCast(pfrom0, pto);
            final PCast pcast1 = new PCast(pfrom1, pto);
            boolean promote;

            promote = this.ptypes.isPDisallowed(pcast0); if (promote) continue;
            promote = this.ptypes.isPDisallowed(pcast1); if (promote) continue;
            promote = ptypes.contains(pfrom0); if (promote) continue;
            promote = ptypes.contains(pfrom1); if (promote) continue;
            promote = !pfrom0.getPSort().isPNumeric() && this.ptypes.getPImplicit(pcast0) == null; if (promote) continue;
            promote = !pfrom1.getPSort().isPNumeric() && this.ptypes.getPImplicit(pcast1) == null; if (promote) continue;

            return pto;
        }

        return null;
    }

    private PType getAnyPromotion(final PType pfrom0, final PType pfrom1) {
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
            PType ptype = getNumericPromotion(pfrom0, pfrom1);

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
    public Void visitIf(IfContext ctx) {
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
        visit(ctx.block(0));
        ifmd.anyrtn = blockmd0.anyrtn;
        ifmd.anybreak = blockmd0.anybreak;
        ifmd.anycontinue = blockmd0.anycontinue;

        if (ctx.ELSE() != null) {
            final BlockContext bctx1 = ctx.block(1);
            final PMetadata blockmd1 = createPMetadata(bctx1);
            visit(ctx.block(1));

            ifmd.close = blockmd0.close && blockmd1.close;
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

        continuemd.allcontinue = true;
        continuemd.anycontinue = true;

        return null;
    }

    @Override
    public Void visitBreak(final BreakContext ctx) {
        final PMetadata breakmd = getPMetadata(ctx);

        breakmd.statement = true;
        breakmd.close = true;

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
        multiplemd.allrtn = true;
        multiplemd.allbreak = true;
        multiplemd.allcontinue = true;

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

            multiplemd.allrtn &= statementmd.allrtn && !statementmd.anybreak && !statementmd.anycontinue;
            multiplemd.anyrtn |= statementmd.anyrtn;
            multiplemd.allbreak &= !statementmd.anyrtn && statementmd.allbreak && !statementmd.anycontinue;
            multiplemd.anybreak |= statementmd.anybreak;
            multiplemd.allcontinue &= !statementmd.anyrtn && !statementmd.anybreak && !statementmd.allcontinue;
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

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        visit(dctx);

        final PType declptype = decltypemd.fromptype;

        if (declptype == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        for (int child = 0; child < ctx.getChildCount(); ++child) {
            final ParseTree cctx = ctx.getChild(child);

            if (cctx instanceof TerminalNode) {
                final TerminalNode tctx = (TerminalNode)cctx;

                if (tctx.getSymbol().getType() == PainlessLexer.ID) {
                    final String name = tctx.getText();
                    addPVariable(name, declptype);
                }
            } else if (cctx instanceof ExpressionContext) {
                final ExpressionContext ectx = (ExpressionContext)cctx;
                final PMetadata expressionmd = createPMetadata(ectx);
                expressionmd.toptype = declptype;
                visit(ectx);
            }
        }

        declarationmd.statement = true;

        return null;
    }

    @Override
    public Void visitDecltype(final DecltypeContext ctx) {
        final PMetadata decltypemd = getPMetadata(ctx);

        final String pnamestr = ctx.getText();
        decltypemd.fromptype = getPTypeFromCanonicalPName(ptypes, pnamestr);

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
        unarymd.castnodes = new ParseTree[] {ctx};

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        visit(ectx);

        if (ctx.BOOLNOT() != null) {
            unarymd.castptypes = new PType[] {boolptype};

            if (expressionmd.constant != null) {
                final PType pfrom = expressionmd.castptypes[0];
                final Object constant = invokeTransform(pfrom, boolptype, expressionmd.constant, false);

                if (constant != null) {
                    unarymd.constant = !((boolean)constant);
                } else if (expressionmd.constant instanceof Boolean) {
                    unarymd.constant = !((boolean)expressionmd.constant);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                markCast(expressionmd, boolptype, false);
            }
        } else if (ctx.BWNOT() != null || ctx.ADD() != null || ctx.SUB() != null) {
            final boolean decimal = ctx.ADD() != null || ctx.SUB() != null;
            final PType promoteptype = getPromotion(new PMetadata[] {expressionmd}, decimal);
            unarymd.castptypes = new PType[] {promoteptype};

            if (expressionmd.constant != null) {
                final PType pfrom = expressionmd.castptypes[0];
                final Object object = invokeTransform(pfrom, promoteptype, expressionmd.constant, false);
                Number number;

                if (object != null) {
                    if (object instanceof Number) {
                        number = (Number)object;
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                } else {
                    if (expressionmd.constant instanceof Number) {
                        number = (Number)expressionmd.constant;
                    } else if (expressionmd.constant instanceof Character) {
                        number = getNumericFromChar((char)expressionmd.constant, promoteptype);
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                }

                if (number == null) {
                    throw new IllegalStateException(); // TODO: message
                }

                final PSort promotepsort = promoteptype.getPSort();

                if (ctx.BWNOT() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constant = ~number.intValue();
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constant = ~number.longValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.SUB() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constant = -number.intValue();
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constant = -number.longValue();
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constant = -number.floatValue();
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constant = -number.doubleValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else if (ctx.ADD() != null) {
                    if (promotepsort == PSort.INT) {
                        unarymd.constant = -number.intValue();
                    } else if (promotepsort == PSort.LONG) {
                        unarymd.constant = -number.longValue();
                    } else if (promotepsort == PSort.FLOAT) {
                        unarymd.constant = -number.floatValue();
                    } else if (promotepsort == PSort.DOUBLE) {
                        unarymd.constant = -number.doubleValue();
                    } else {
                        throw new IllegalStateException(); // TODO: message
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                markCast(expressionmd, promoteptype, false);
            }
        } else {
            throw new IllegalStateException(); // TODO: message
        }

        return null;
    }

    @Override
    public Void visitCast(final CastContext ctx) {
        final PMetadata castmd = getPMetadata(ctx);
        castmd.castnodes = new ParseTree[] {ctx};

        final DecltypeContext dctx = ctx.decltype();
        final PMetadata decltypemd = createPMetadata(dctx);
        visit(dctx);

        final PType declptype = decltypemd.declptype;
        final PSort declpsort = declptype.getPSort();
        castmd.castptypes = new PType[] {declptype};

        final ExpressionContext ectx = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx);
        visit(ectx);

        if (declpsort.isPBasic() && expressionmd.constant != null) {
            PType pfrom = expressionmd.castptypes[0];
            PSort fpsort = pfrom.getPSort();
            castmd.constant = invokeTransform(pfrom, declptype, expressionmd.constant, true);

            if (castmd.constant == null) {
                if (fpsort == PSort.BOOL) {
                    if (declpsort == PSort.BOOL) {
                        castmd.constant = expressionmd.constant;
                    }
                } else if (expressionmd.constant instanceof Number) {
                    if (declpsort == PSort.BYTE) {
                        castmd.constant = invokeTransform(byteptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).byteValue();
                        }
                    } else if (declpsort == PSort.SHORT) {
                        castmd.constant = invokeTransform(shortptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).shortValue();
                        }
                    } else if (declpsort == PSort.CHAR) {
                        castmd.constant = invokeTransform(charptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            final PType promote = getPromotion(new PMetadata[] {expressionmd}, true);

                            if (promote.equals(intptype)) {
                                castmd.constant = (char)((Number)expressionmd.constant).intValue();
                            } else if (promote.equals(longptype)) {
                                castmd.constant = (char)((Number)expressionmd.constant).longValue();
                            } else if (promote.equals(floatptype)) {
                                castmd.constant = (char)((Number)expressionmd.constant).floatValue();
                            } else if (promote.equals(doubleptype)) {
                                castmd.constant = (char)((Number)expressionmd.constant).doubleValue();
                            }
                        }
                    } else if (declpsort == PSort.INT) {
                        castmd.constant = invokeTransform(intptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).intValue();
                        }
                    } else if (declpsort == PSort.LONG) {
                        castmd.constant = invokeTransform(longptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).longValue();
                        }
                    } else if (declpsort == PSort.FLOAT) {
                        castmd.constant = invokeTransform(floatptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).floatValue();
                        }
                    } else if (declpsort == PSort.DOUBLE) {
                        castmd.constant = invokeTransform(doubleptype, declptype, expressionmd.constant, true);

                        if (castmd.constant == null) {
                            castmd.constant = ((Number)expressionmd.constant).doubleValue();
                        }
                    }
                } else if (expressionmd.constant instanceof Character) {
                    castmd.constant = invokeTransform(charptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null) {
                        if (declpsort == PSort.BYTE) {
                            castmd.constant = (byte)(char)expressionmd.constant;
                        } else if (declpsort == PSort.SHORT) {
                            castmd.constant = (short)(char)expressionmd.constant;
                        } else if (declpsort == PSort.CHAR) {
                            castmd.constant = expressionmd.constant;
                        } else if (declpsort == PSort.INT) {
                            castmd.constant = (int)(char)expressionmd.constant;
                        } else if (declpsort == PSort.LONG) {
                            castmd.constant = (long)(char)expressionmd.constant;
                        } else if (declpsort == PSort.FLOAT) {
                            castmd.constant = (float)(char)expressionmd.constant;
                        } else if (declpsort == PSort.DOUBLE) {
                            castmd.constant = (double)(char)expressionmd.constant;
                        }
                    }
                } else if (expressionmd.constant instanceof String) {
                    castmd.constant = invokeTransform(stringptype, declptype, expressionmd.constant, true);

                    if (castmd.constant == null && declpsort == PSort.STRING) {
                        castmd.constant = expressionmd.constant;
                    }
                }
            }
        }

        if (castmd.constant == null) {
            markCast(expressionmd, declptype, true);
        }

        return null;
    }

    @Override
    public Void visitBinary(final BinaryContext ctx) {
        final PMetadata binarymd = getPMetadata(ctx);
        binarymd.castnodes = new ParseTree[] {ctx};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        final boolean decimal = ctx.ADD() != null || ctx.SUB() != null ||
                ctx.DIV() != null || ctx.MUL() != null || ctx.REM() != null;
        final PType promoteptype = getPromotion(new PMetadata[] {expressionmd0, expressionmd1}, decimal);
        final PSort psort = promoteptype.getPSort();
        binarymd.castptypes = new PType[] {promoteptype};

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            PType ptype0 = expressionmd0.castptypes[0];
            PType ptype1 = expressionmd0.castptypes[0];
            final Object object0 = invokeTransform(ptype0, promoteptype, expressionmd0.constant, false);
            final Object object1 = invokeTransform(ptype1, promoteptype, expressionmd0.constant, false);
            Number number0;
            Number number1;

            if (object0 != null) {
                if (object0 instanceof Number) {
                    number0 = (Number)object0;
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                if (expressionmd0.constant instanceof Number) {
                    number0 = (Number)expressionmd0.constant;
                } else if (expressionmd0.constant instanceof Character) {
                    number0 = getNumericFromChar((char)expressionmd0.constant, promoteptype);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            if (object1 != null) {
                if (object1 instanceof Number) {
                    number1 = (Number)object1;
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else {
                if (expressionmd1.constant instanceof Number) {
                    number1 = (Number)expressionmd1.constant;
                } else if (expressionmd1.constant instanceof Character) {
                    number1 = getNumericFromChar((char)expressionmd1.constant, promoteptype);
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            }

            if (number0 == null || number1 == null) {
                throw new IllegalStateException(); // TODO: message
            }

            if (ctx.MUL() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() * number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() * number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() * number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() * number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.DIV() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() / number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() / number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() / number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() / number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.REM() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() % number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() % number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() % number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() % number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.ADD() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() + number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() + number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() + number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() + number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.SUB() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() - number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() - number1.longValue();
                } else if (psort == PSort.FLOAT) {
                    binarymd.constant = number0.floatValue() - number1.floatValue();
                } else if (psort == PSort.DOUBLE) {
                    binarymd.constant = number0.doubleValue() - number1.doubleValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.LSH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() << number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() << number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.RSH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() >> number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() >> number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.USH() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() >>> number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() >>> number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWAND() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() & number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() & number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWXOR() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() ^ number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() ^ number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else if (ctx.BWOR() != null) {
                if (psort == PSort.INT) {
                    binarymd.constant = number0.intValue() | number1.intValue();
                } else if (psort == PSort.LONG) {
                    binarymd.constant = number0.longValue() | number1.longValue();
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        } else {
            markCast(expressionmd0, promoteptype, false);
            markCast(expressionmd1, promoteptype, false);
        }

        return null;
    }

    @Override
    public Void visitComp(final CompContext ctx) {
        final PMetadata compmd = getPMetadata(ctx);
        compmd.castnodes = new ParseTree[] {ctx};
        compmd.castptypes = new PType[] {boolptype};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        if (expressionmd0.isnull && expressionmd1.isnull) {
            throw new IllegalArgumentException(); // TODO: message
        }

        boolean anybool = isAnyPType(expressionmd0, boolptype) || isAnyPType(expressionmd1, boolptype);
        boolean anynumeric = isAnyPNumeric(expressionmd0) && isAnyPNumeric(expressionmd1);

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            final PType pfrom0 = expressionmd0.castptypes[0];
            final PType pfrom1 = expressionmd1.castptypes[0];

            if (anybool) {
                Object constant0 = invokeTransform(pfrom0, boolptype, expressionmd0.constant, false);
                Object constant1 = invokeTransform(pfrom1, boolptype, expressionmd1.constant, false);

                if (constant0 == null && expressionmd0.constant instanceof Boolean) {
                    constant0 = expressionmd0.constant;
                }

                if (constant1 == null && expressionmd1.constant instanceof Boolean) {
                    constant1 = expressionmd1.constant;
                }

                if (constant0 == null || constant1 == null) {
                    throw new IllegalArgumentException(); // TODO: message
                }

                if (ctx.EQ() != null) {
                    compmd.constant = (boolean)constant0 == (boolean)constant1;
                } else if (ctx.NE() != null) {
                    compmd.constant = (boolean)constant0 != (boolean)constant1;
                } else {
                    throw new IllegalArgumentException(); // TODO: message
                }
            } else if (anynumeric) {
                final PType promoteptype = getPromotion(new PMetadata[]{expressionmd0, expressionmd1}, true);
                final PSort psort = promoteptype.getPSort();

                final Object object0 = invokeTransform(pfrom0, promoteptype, expressionmd0.constant, false);
                final Object object1 = invokeTransform(pfrom1, promoteptype, expressionmd0.constant, false);
                Number number0;
                Number number1;

                if (object0 != null) {
                    if (object0 instanceof Number) {
                        number0 = (Number)object0;
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                } else {
                    if (expressionmd0.constant instanceof Number) {
                        number0 = (Number)expressionmd0.constant;
                    } else if (expressionmd0.constant instanceof Character) {
                        number0 = getNumericFromChar((char)expressionmd0.constant, promoteptype);
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                }

                if (object1 != null) {
                    if (object1 instanceof Number) {
                        number1 = (Number)object1;
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                } else {
                    if (expressionmd1.constant instanceof Number) {
                        number1 = (Number)expressionmd0.constant;
                    } else if (expressionmd1.constant instanceof Character) {
                        number1 = getNumericFromChar((char)expressionmd1.constant, promoteptype);
                    } else {
                        throw new IllegalArgumentException(); // TODO: message
                    }
                }

                if (number0 == null || number1 == null) {
                    throw new IllegalStateException(); // TODO: message
                }

                if (ctx.EQ() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() == number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() == number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() == number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() == number1.doubleValue();
                    }
                } else if (ctx.NE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() != number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() != number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() != number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() != number1.doubleValue();
                    }
                } else if (ctx.GTE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() >= number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() >= number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() >= number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() >= number1.doubleValue();
                    }
                } else if (ctx.GT() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() > number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() > number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() > number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() > number1.doubleValue();
                    }
                } else if (ctx.LTE() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() <= number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() <= number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() <= number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() <= number1.doubleValue();
                    }
                } else if (ctx.LT() != null) {
                    if (psort == PSort.INT) {
                        compmd.constant = number0.intValue() < number1.intValue();
                    } else if (psort == PSort.LONG) {
                        compmd.constant = number0.longValue() < number1.longValue();
                    } else if (psort == PSort.FLOAT) {
                        compmd.constant = number0.floatValue() < number1.floatValue();
                    } else if (psort == PSort.DOUBLE) {
                        compmd.constant = number0.doubleValue() < number1.doubleValue();
                    }
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            } else {
                if (ctx.EQ() != null) {
                    compmd.constant = expressionmd0.constant == expressionmd1.constant;
                } else if (ctx.NE() != null) {
                    compmd.constant = expressionmd0.constant != expressionmd1.constant;
                } else {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        } else {
            if (anybool && (ctx.EQ() != null || ctx.NE() != null)) {
                markCast(expressionmd0, boolptype, false);
                markCast(expressionmd1, boolptype, false);
            } else if (!anynumeric && (ctx.EQ() != null || ctx.NE() != null)) {
                markCast(expressionmd0, objectptype, false);
                markCast(expressionmd1, objectptype, false);
            } else if (anynumeric) {
                final PType promoteptype = getPromotion(new PMetadata[] {expressionmd0, expressionmd1}, true);
                markCast(expressionmd0, promoteptype, false);
                markCast(expressionmd1, promoteptype, false);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }
        }

        return null;
    }

    @Override
    public Void visitBool(BoolContext ctx) {
        final PMetadata boolmd = getPMetadata(ctx);
        boolmd.castnodes = new ParseTree[] {ctx};
        boolmd.castptypes = new PType[] {boolptype};

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        if (expressionmd0.constant != null && expressionmd1.constant != null) {
            Object constant0 = invokeTransform(expressionmd0.castptypes[0], boolptype, expressionmd0.constant, false);
            Object constant1 = invokeTransform(expressionmd1.castptypes[0], boolptype, expressionmd1.constant, false);

            if (constant0 == null) {
                constant0 = expressionmd0.constant;
            }

            if (constant1 == null) {
                constant1 = expressionmd1.constant;
            }

            if (ctx.BOOLAND() != null) {
                boolmd.constant = (boolean)constant0 && (boolean)constant1;
            } else if (ctx.BOOLOR() != null) {
                boolmd.constant = (boolean)constant0 || (boolean)constant1;
            } else {
                throw new IllegalStateException(); // TODO: message
            }
        } else {
            markCast(expressionmd0, boolptype, false);
            markCast(expressionmd1, boolptype, false);
        }

        return null;
    }

    @Override
    public Void visitConditional(ConditionalContext ctx) {
        final PMetadata conditionalmd = getPMetadata(ctx);

        final ExpressionContext ectx0 = ctx.expression(0);
        final PMetadata expressionmd0 = createPMetadata(ectx0);
        visit(ectx0);
        markCast(expressionmd0, boolptype, false);

        if (expressionmd0.constant != null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final ExpressionContext ectx1 = ctx.expression(1);
        final PMetadata expressionmd1 = createPMetadata(ectx1);
        visit(ectx1);

        final ExpressionContext ectx2 = ctx.expression(2);
        final PMetadata expressionmd2 = createPMetadata(ectx2);
        visit(ectx2);

        final int nodeslen1 = expressionmd1.castnodes.length;
        final int nodeslen2 = expressionmd2.castnodes.length;
        conditionalmd.castnodes = new ParseTree[nodeslen1 + nodeslen2];
        System.arraycopy(expressionmd1.castnodes, 0, conditionalmd.castnodes, 0, nodeslen1);
        System.arraycopy(expressionmd2.castnodes, 0, conditionalmd.castnodes, nodeslen1, nodeslen2);

        final int castslen1 = expressionmd1.castptypes.length;
        final int castslen2 = expressionmd2.castptypes.length;
        conditionalmd.castnodes = new ParseTree[castslen1 + castslen2];
        System.arraycopy(expressionmd1.castptypes, 0, conditionalmd.castptypes, 0, castslen1);
        System.arraycopy(expressionmd2.castptypes, 0, conditionalmd.castptypes, castslen1, castslen2);

        return null;
    }

    @Override
    public Void visitAssignment(final AssignmentContext ctx) {
        final PMetadata assignmentmd = getPMetadata(ctx);

        final ExtstartContext ectx0 = ctx.extstart();
        PMetadata extstartmd = createPMetadata(ectx0);
        extstartmd.write = true;
        extstartmd.pop = assignmentmd.pop;
        visit(ectx0);

        if (extstartmd.pexternal == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (extstartmd.pexternal.isReadOnly()) {
            throw new IllegalArgumentException(); // TODO: message
        }

        assignmentmd.castnodes = new ParseTree[] {ctx};
        assignmentmd.castptypes = new PType[] {extstartmd.castptypes[0]};

        final ExpressionContext ectx1 = ctx.expression();
        final PMetadata expressionmd = createPMetadata(ectx1);
        visit(ectx1);
        markCast(expressionmd, extstartmd.pexternal.getPType(), false);

        assignmentmd.statement = true;

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
        extstartmd.castnodes = new ParseTree[] {ctx};
        extstartmd.castptypes = new PType[] {pop ? voidptype : extstartmd.pexternal.getPType()};

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
        visit(dctx);

        final PType pfrom = extcastmd0.pexternal.getPType();
        final PType pto = decltypemd.declptype;

        final Object object = checkLegalCast(pfrom, pto, true);

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
        visit(ectx0);
        markCast(expressionmd, intptype, false);

        extarraymd0.pexternal.addSegment(SType.ARRAY, ectx0);

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

        final ArgumentsContext actx = ctx.arguments();
        final PMetadata argumentsmd = createPMetadata(actx);
        final List<ExpressionContext> arguments = ctx.arguments().expression();
        argumentsmd.pexternal = extcallmd.pexternal;
        argumentsmd.castnodes = new ParseTree[arguments.size()];
        arguments.toArray(argumentsmd.castnodes);

        if (statik && "makearray".equals(pname)) {
            extcallmd.pexternal.addSegment(SType.AMAKE, arguments.size());

            PType[] ptypes = new PType[arguments.size()];
            Arrays.fill(ptypes, intptype);
            argumentsmd.castptypes = ptypes;
        } else {
            final PConstructor pconstructor = statik ? pclass.getPConstructor(pname) : null;
            final PMethod pmethod = statik ? pclass.getPFunction(pname) : pclass.getPMethod(pname);

            if (pconstructor != null) {
                extcallmd.pexternal.addSegment(SType.CONSTRUCTOR, pconstructor);
                argumentsmd.castptypes = new PType[arguments.size()];
                pconstructor.getPArguments().toArray(argumentsmd.castptypes);
            } else if (pmethod != null) {
                extcallmd.pexternal.addSegment(SType.METHOD, pmethod);
                argumentsmd.castptypes = new PType[arguments.size()];
                pmethod.getPArguments().toArray(argumentsmd.castptypes);
            } else {
                throw new IllegalArgumentException(); // TODO: message
            }

            visit(actx);
        }

        visit(actx);

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
                    extmembermd.pexternal.addSegment(SType.ALENGTH, intptype);
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
        final PMetadata argumentsmd = getPMetadata(ctx);
        final ParseTree[] nodes = argumentsmd.castnodes;
        final PType[] ptypes = argumentsmd.castptypes;

        if (nodes == null || ptypes == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (nodes.length != ptypes.length) {
            throw new IllegalArgumentException(); // TODO: message
        }

        final int arguments = nodes.length;

        for (int argument = 0; argument < arguments; ++argument) {
            final ParseTree ectx = nodes[argument];
            final PMetadata nodemd = createPMetadata(ectx);
            visit(ectx);
            markCast(nodemd, ptypes[argument], false);

            argumentsmd.pexternal.addSegment(SType.ARGUMENT, ectx);
        }

        return null;
    }
}

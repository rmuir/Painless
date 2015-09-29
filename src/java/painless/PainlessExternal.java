package painless;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.antlr.v4.runtime.tree.ParseTree;

import static painless.PainlessAnalyzer.*;
import static painless.PainlessTypes.*;

public class PainlessExternal {
    enum SType {
        TYPE,
        VARIABLE,
        CONSTRUCTOR,
        METHOD,
        FIELD,
        ARRAY,
        NODE,
        WRITE,
        CAST,
        TRANSFORM,
        AMAKE,
        ALENGTH
    }

    static class PSegment {
        private final SType stype;
        private final Object svalue;

        private PSegment(final SType stype, final Object svalue) {
            this.stype = stype;
            this.svalue = svalue;
        }

        SType getSType() {
            return stype;
        }

        Object getSValue() {
            return svalue;
        }
    }

    static class PExternal {
        private final Deque<PSegment> psegments;

        private boolean statik;
        private boolean call;
        private boolean member;
        private boolean readonly;
        private boolean write;

        private PType ptype;

        PExternal() {
            psegments = new ArrayDeque<>();

            statik = false;
            call = false;
            member = false;
            readonly = false;
            write = false;

            ptype = null;
        }

        void addSegment(final SType stype, final Object svalue) {
            switch (stype) {
                case TYPE:
                    if (!(svalue instanceof PType)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    statik = true;

                    ptype = (PType)svalue;

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case VARIABLE:
                    if (!(svalue instanceof PVariable)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    statik = false;
                    call  = false;
                    member = true;
                    readonly = false;

                    ptype = ((PVariable)svalue).getPType();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case CONSTRUCTOR:
                    if (!(svalue instanceof PConstructor)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    ptype = getPTypeWithArrayDimensions(((PConstructor)svalue).getPOwner(), 0);

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case METHOD:
                    if (!(svalue instanceof PMethod)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    ptype = ((PMethod)svalue).getPReturn();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case FIELD:
                    if (!(svalue instanceof PField)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    member = true;
                    readonly = Modifier.isFinal(((PField)svalue).getJField().getModifiers());

                    ptype = ((PField)svalue).getPType();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case ARRAY: {
                    if (svalue != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() == 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    member = true;
                    readonly = false;

                    ptype = getPTypeWithArrayDimensions(ptype.getPClass(), ptype.getPDimensions() - 1);

                    psegments.add(new PSegment(stype, ptype));

                    break;
                } case NODE: {
                    if (!(svalue instanceof ParseTree)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    psegments.add(new PSegment(stype, svalue));

                    break;
                } case WRITE: {
                    if (!(svalue instanceof ParseTree)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    PSegment psegment = psegments.removeLast();

                    if (psegment.stype != SType.VARIABLE &&
                            psegment.stype != SType.FIELD && psegment.stype != SType.ARRAY) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    psegments.add(new PSegment(stype, svalue));
                    psegments.add(psegment);
                    write = true;

                    break;
                } case CAST:
                    if (!(svalue instanceof PCast)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    readonly = true;

                    ptype = ((PCast)svalue).getPTo();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case TRANSFORM:
                    if (!(svalue instanceof PTransform)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    readonly = true;

                    ptype = ((PTransform)svalue).getPCast().getPTo();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case AMAKE: {
                    if (!(svalue instanceof Integer)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() > 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    ptype = getPTypeWithArrayDimensions(ptype.getPClass(), (int)svalue);

                    psegments.add(new PSegment(stype, ptype));

                    break;
                } case ALENGTH:
                    if (svalue != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() == 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    member = true;
                    readonly = true;

                    psegments.add(new PSegment(stype, null));
            }
        }

        Iterator<PSegment> getIterator() {
            return psegments.iterator();
        }

        PSegment getWriteSegment() {
            return write ? psegments.peekLast() : null;
        }

        boolean isStatic() {
            return statik;
        }

        boolean isCall() {
            return call;
        }

        boolean isMember() {
            return member;
        }

        boolean isReadOnly() {
            return readonly;
        }

        boolean isWrite() {
            return write;
        }

        PType getPType() {
            return ptype;
        }
    }
}

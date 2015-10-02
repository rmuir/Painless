package painless;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Analyzer.*;
import static painless.Types.*;

public class External {
    /*enum SType {
        TYPE,
        VARIABLE,
        CONSTRUCTOR,
        METHOD,
        FIELD,
        ARRAY,
        SHORTCUT,
        NODE,
        WRITE,
        DUP,
        POP,
        CAST,
        TRANSFORM,
        AMAKE,
        ALENGTH
    }

    static class PSegment {
        private final SType stype;
        private final Object svalue0;
        private final Object svalue1;

        private PSegment(final SType stype, final Object svalue0, final Object svalue1) {
            this.stype = stype;
            this.svalue0 = svalue0;
            this.svalue1 = svalue1;
        }

        SType getSType() {
            return stype;
        }

        Object getSValue0() {
            return svalue0;
        }

        Object getSValue1() {
            return svalue1;
        }
    }

    static class PExternal {
        private final PTypes ptypes;

        private final List<PSegment> psegments;
        private int writeable;

        private boolean statik;
        private boolean call;
        private boolean readonly;

        private PType ptype;

        PExternal(final PTypes ptypes) {
            this.ptypes = ptypes;

            psegments = new ArrayList<>();
            writeable = -1;

            statik = false;
            call = false;
            readonly = false;

            ptype = null;
        }

        void addSegment(final SType stype, final Object svalue0, final Object svalue1) {
            switch (stype) {
                case TYPE: {
                    if (!(svalue0 instanceof PType)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    statik = true;

                    ptype = (PType)svalue0;
                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                }
                case VARIABLE: {
                    if (!(svalue0 instanceof PVariable)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (!(svalue1 instanceof Boolean)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    statik = false;
                    call = false;
                    readonly = false;

                    ptype = ((PVariable)svalue0).getPType();

                    writeable = psegments.size();
                    psegments.add(new PSegment(stype, svalue0, svalue1));

                    break;
                }
                case CONSTRUCTOR: {
                    if (!(svalue0 instanceof Constructor)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    readonly = true;

                    ptype = getPTypeWithArrayDimensions(((Constructor)svalue0).getPOwner(), 0);
                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                }
                case METHOD: {
                    if (!(svalue0 instanceof PMethod)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    readonly = true;

                    ptype = ((PMethod)svalue0).getPReturn();
                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                }
                case FIELD: {
                    if (!(svalue0 instanceof PField)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (!(svalue1 instanceof Boolean)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    readonly = Modifier.isFinal(((PField)svalue0).getJField().getModifiers());

                    ptype = ((PField)svalue0).getPType();

                    if (!readonly) {
                        writeable = psegments.size();
                    }

                    psegments.add(new PSegment(stype, svalue0, svalue1));

                    break;
                }
                case ARRAY: {
                    if (svalue0 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (!(svalue1 instanceof Boolean)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() == 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    readonly = false;

                    ptype = getPTypeWithArrayDimensions(ptype.getPClass(), ptype.getPDimensions() - 1);

                    writeable = psegments.size();
                    psegments.add(new PSegment(stype, ptype, svalue1));

                    break;
                }
                case SHORTCUT: {
                    if (!(svalue0 instanceof PMethod)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (!(svalue1 instanceof PMethod)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    readonly = false;

                    ptype = ptypes.getPStandard().pobject;

                    writeable = psegments.size();
                    psegments.add(new PSegment(stype, svalue0, svalue1));

                    break;
                }
                case NODE: {
                    if (!(svalue0 instanceof ParseTree)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                } case WRITE: {
                    if (svalue0 != null && !(svalue0 instanceof ParseTree)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (writeable == -1) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue0 != null) {
                        final PSegment write = psegments.remove(writeable);
                        psegments.add(new PSegment(stype, svalue0, null));

                        if (write.stype == SType.VARIABLE || write.stype == SType.FIELD
                                || write.stype == SType.ARRAY) {
                            psegments.add(new PSegment(write.stype, write.svalue0, true));
                        } else if (write.stype == SType.SHORTCUT) {
                            psegments.add(new PSegment(write.stype, write.svalue1, null));
                        }
                    } else {
                        final PSegment write = psegments.get(writeable);

                        if (write.stype == SType.VARIABLE || write.stype == SType.FIELD
                                || write.stype == SType.ARRAY) {
                            psegments.add(new PSegment(write.stype, write.svalue0, true));
                        } else if (write.stype == SType.SHORTCUT) {
                            psegments.add(new PSegment(write.stype, write.svalue1, write.svalue0));
                        }
                    }

                    break;
                } case DUP: {
                    if (!(svalue0 instanceof Boolean)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (!(svalue1 instanceof PType)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    final int index = (boolean)svalue0 ? writeable - 1 : writeable;
                    final SType dupstype = psegments.get(writeable).stype;

                    psegments.add(index, new PSegment(stype, dupstype, svalue1));
                } case POP: {
                    if (!(svalue0 instanceof PType)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                } case CAST: {
                    if (!(svalue0 instanceof PCast)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    readonly = true;

                    ptype = ((PCast)svalue0).getPTo();
                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                } case TRANSFORM: {
                    if (!(svalue0 instanceof PTransform)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    readonly = true;

                    ptype = ((PTransform)svalue0).getPCast().getPTo();
                    psegments.add(new PSegment(stype, svalue0, null));

                    break;
                } case AMAKE: {
                    if (!(svalue0 instanceof Integer)) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() > 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = true;
                    readonly = true;

                    ptype = getPTypeWithArrayDimensions(ptype.getPClass(), (int)svalue0);
                    psegments.add(new PSegment(stype, ptype, null));

                    break;
                } case ALENGTH: {
                    if (svalue0 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (svalue1 != null) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    if (ptype.getPDimensions() == 0) {
                        throw new IllegalArgumentException(); // TODO: message
                    }

                    call = false;
                    readonly = true;

                    psegments.add(new PSegment(stype, null, null));
                } default: {
                    throw new IllegalStateException(); // TODO: message
                }
            }
        }

        Iterator<PSegment> getIterator() {
            return psegments.iterator();
        }

        boolean isStatic() {
            return statik;
        }

        boolean isCall() {
            return call;
        }

        boolean isReadOnly() {
            return readonly;
        }

        PType getPType() {
            return ptype;
        }
    }*/
}

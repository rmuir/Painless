package painless;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Type.*;

import static painless.PainlessAnalyzer.*;
import static painless.PainlessTypes.*;

public class PainlessExternal {
    final static int TYPE = 0;
    final static int VARIABLE = 1;
    final static int CONSTRUCTOR = 2;
    final static int METHOD = 3;
    final static int FIELD = 4;
    final static int ARRAY = 5;
    final static int ARGUMENT = 6;
    final static int CAST = 7;

    final static int AMAKE = 8;
    final static int ALENGTH = 9;

    static class PSegment {
        final int stype;
        final Object svalue;

        PSegment(final int stype, final Object svalue) {
            this.stype = stype;
            this.svalue = svalue;
        }
    }

    static class PExternal {
        private final Deque<PSegment> psegments;

        private boolean statik;
        private boolean call;
        private boolean member;
        private boolean readonly;

        private Type atype;

        PExternal() {
            psegments = new ArrayDeque<>();

            statik = false;
            call = false;
            member = false;
            readonly = false;

            atype = null;
        }

        void addSegment(final int stype, final Object svalue) {
            switch (stype) {
                case TYPE:
                    if (!(svalue instanceof Type)) {
                        throw new IllegalArgumentException();
                    }

                    if (!psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    statik = true;

                    atype = (Type)svalue;

                    psegments.push(new PSegment(stype, svalue));

                    break;
                case VARIABLE:
                    if (!(svalue instanceof Variable)) {
                        throw new IllegalArgumentException();
                    }

                    if (!psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    statik = false;
                    call  = false;
                    member = true;
                    readonly = false;

                    atype = ((Variable)svalue).atype;

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case CONSTRUCTOR:
                    if (!(svalue instanceof PConstructor)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case METHOD:
                    if (!(svalue instanceof PMethod)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    atype = ((PMethod)svalue).amethod.getReturnType();

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case FIELD:
                    if (!(svalue instanceof PField)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    call = false;
                    member = true;
                    readonly = Modifier.isFinal(((PField)svalue).jfield.getModifiers());

                    atype = ((PField)svalue).atype;

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case ARRAY:
                    if (!(svalue instanceof ParseTree)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    if (atype.getSort() != ARRAY) {
                        throw new IllegalArgumentException();
                    }

                    call = false;
                    member = true;
                    readonly = false;

                    atype = getType(atype.getDescriptor().substring(1));

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case ARGUMENT:
                    if (!(svalue instanceof ParseTree)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    if (!call) {
                        throw new IllegalArgumentException();
                    }

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case CAST:
                    if (!(svalue instanceof PCast)) {
                        throw new IllegalArgumentException();
                    }

                    if (psegments.isEmpty()) {
                        throw new IllegalArgumentException();
                    }

                    readonly = true;

                    atype = ((PCast)svalue).ato;

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case AMAKE:
                    if (!(svalue instanceof Integer)) {
                        throw new IllegalArgumentException();
                    }

                    if (atype.getSort() == Type.ARRAY) {
                        throw new IllegalArgumentException();
                    }

                    call = true;
                    member = false;
                    readonly = true;

                    String descriptor = atype.getDescriptor();
                    final int length = (int)svalue;

                    for (int brace = 0; brace < length; ++brace) {
                        descriptor = "[" + descriptor;
                    }

                    atype = getType(descriptor);

                    psegments.add(new PSegment(stype, svalue));

                    break;
                case ALENGTH:
                    if (!(svalue instanceof Type)) {
                        throw new IllegalArgumentException();
                    }

                    call = false;
                    member = true;
                    readonly = true;

                    atype = (Type)svalue;

                    psegments.add(new PSegment(stype, svalue));
            }
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

        Type getAType() {
            return atype;
        }
    }
}

package painless;

import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

import static painless.Definition.*;

public class Runtime {
    private static Map<Class, Struct> classes;
    private static Struct[] structs;

    Runtime(final Definition definition) {
        final int length = definition.runtimes.size();

        classes = new HashMap<>();
        structs = new Struct[length];

        for (final painless.Definition.Runtime runtime : definition.runtimes) {
            classes.put(runtime.struct.clazz, runtime.struct);

            if (runtime.order > structs.length) {
                throw new IllegalArgumentException(); // TODO: message
            }

            structs[length - runtime.order - 1] = runtime.struct;
        }
    }

    /*public Object invokeMethod(final Object object, final String name, final Object[] objects) {
        final Struct struct = getStruct(object.getClass());
        final Method method = struct.methods.get(name);

        if (method == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        //cast objects to method parameters

        return null;
    }

    public Object readField(final Object object, final String name) {
        final Struct struct = getStruct(object.getClass());

        return null;
    }

    private Struct getStruct(final Class clazz) {
        final Struct struct = classes.get(clazz);

        if (struct == null) {
            for (final Struct local : structs) {
                try {
                    clazz.asSubclass(local.clazz);
                    return local;
                } catch (ClassCastException exception) {
                    // Do nothing.
                }
            }
        } else {
            return struct;
        }

        throw new ClassCastException(); // TODO: message
    }*/
}

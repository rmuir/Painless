package painless;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static painless.Definition.*;

public class Runtime {
    public static CallSite bootstrap(Object... objects)
            throws IllegalAccessException, NoSuchMethodException
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class thisClass = lookup.lookupClass();
        MethodHandle handle;

        if (thisClass.equals(Integer.class)) {
            handle = lookup.findVirtual(Integer.class, "longValue", MethodType.methodType(long.class));
        } else {
            handle = lookup.findVirtual(Long.class, "longValue", MethodType.methodType(long.class));
        }

        if (!objects[2].equals(handle.type())) {
            handle = handle.asType((MethodType)objects[2]);
        }

        try {
            handle.invokeWithArguments(2);
        } catch (Throwable throwable) {

        }

        MutableCallSite cs = new MutableCallSite((MethodType)objects[2]);
        cs.setTarget(handle);

        return cs;
    }

    private final Map<Class, Struct> classes;
    private final Struct[] structs;

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

    public Object invokeMethod(final Object object, final String name, final Object[] objects) {
        final Method method = getMethod(object.getClass(), name);

        if (method == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        try {
            return method.method.invoke(object, objects);
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new IllegalArgumentException(); // TODO: message
        }
    }

    public Object readField(final Object object, final String name) {
        return null;
    }

    public Object readArray(final Object object, final int index) {
        return null;
    }

    private Method getMethod(final Class clazz, final String name) {
        final Struct struct = classes.get(clazz);
        Method method = struct == null ? null : struct.methods.get(name);

        if (method == null) {
            for (final Struct local : structs) {
                try {
                    clazz.asSubclass(local.clazz);
                    method = local.methods.get(name);

                    if (method != null) {
                        return method;
                    }
                } catch (ClassCastException exception) {
                    // Do nothing.
                }
            }
        } else {
            return method;
        }

        throw new ClassCastException(); // TODO: message
    }
}

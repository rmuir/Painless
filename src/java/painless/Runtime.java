package painless;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Map;

class Runtime {
    static CallSite lookup(final Map<String, MethodHandle> runtime, Class clazz, final String name, MethodType type) {
        MethodHandle handle = null;

        while (clazz != null) {
            String local = name + "_" + clazz.getName();
            handle = runtime.get(local);

            if (handle != null) {
                break;
            }

            for (final Class iface : clazz.getInterfaces()) {
                local = name + "_" + iface.getName();
                handle = runtime.get(local);

                if (handle != null) {
                    break;
                }
            }

            if (handle != null) {
                break;
            }

            clazz = clazz.getSuperclass();
        }

        if (handle == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        if (!type.equals(handle.type())) {
            handle = handle.asType(type);
        }

        if (handle == null) {
            throw new IllegalArgumentException(); // TODO: message
        }

        return new ConstantCallSite(handle);
    }
}

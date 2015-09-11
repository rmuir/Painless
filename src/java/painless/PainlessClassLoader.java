package painless;

final class PainlessClassLoader extends ClassLoader {
    PainlessClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<? extends PainlessExecutable> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length).asSubclass(PainlessExecutable.class);
    }
}

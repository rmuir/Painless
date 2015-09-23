package painless;

public final class Painless {
    public static void main(String args[]) throws Exception {
        PainlessExecutable executable = compile("test", "for (;;) {}");
        //Object value = executable.execute(null);
        //System.out.println(value);
    }

    public static PainlessExecutable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader());
    }

    public static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        return PainlessCompiler.compile(name, source, parent);
    }

    private Painless() {}
}

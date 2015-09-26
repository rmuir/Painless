package painless;

public final class Painless {
    public static void main(String args[]) throws Exception {
        final long start = System.currentTimeMillis();
        //PainlessExecutable executable = compile("test", "bool b = true; while (true) { while (true) {if (b) break; b = !b;} break;}");
        //PainlessExecutable executable = compile("test", "int x = 0; while (true) {x = x + 1; if (x >= 5) continue; if (x <= 6) {break;}}");
        //PainlessExecutable executable = compile("test", "for (int x = 0; x < 5; x = x + 1);");
        //PainlessExecutable executable = compile("test", "return (long)(object)7L;");
        //PainlessExecutable executable = compile("test", "bool b; b = false; if (b) return null; else return 5;");
        PainlessExecutable executable = compile("test", "bool x, y; y = x = true; return x || y ;");
        final long end = System.currentTimeMillis() - start;
        Object value = executable.execute(null);
        System.out.println(value);
        System.out.println(end);
    }

    public static PainlessExecutable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader());
    }

    public static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        return PainlessCompiler.compile(name, source, parent);
    }

    private Painless() {}
}

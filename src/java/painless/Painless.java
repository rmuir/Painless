package painless;

import org.objectweb.asm.Type;

public final class Painless {
    public static void main(String args[]) throws Exception {
        //double d = 1/3;
        //System.out.println(d);
        //boolean last = true;
        //last &= true;
        //System.out.println(last);
        //System.out.println(Type.getType("[[[java/lang/Object;").equals(Type.getType("[[[java/lang/Object;")));
        //String test =
        //        "x([3]) = 1;"
        //        ;
        //System.out.println(compile("test", test));

        //int f = Integer.MIN_VALUE;
        //int n = -f;

        //System.out.println(f + " " + n);

        compile("test", "int[] x = int.makearray(10);");
    }

    public static PainlessExecutable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader());
    }

    public static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        return PainlessCompiler.compile(name, source, parent);
    }

    private Painless() {}
}

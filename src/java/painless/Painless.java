package painless;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;

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

        //Object.class.asSubclass(Integer.class);

        //Object o = 5;
        //int a = (int)o;
        //System.out.println(a);

        //Method method0 = Integer.class.getMethod("intValue");
        //int value0 = (int)method0.invoke(5);
        //System.out.println(value0);

        //Method method1 = Integer.class.getMethod("parseInt", String.class);
        //int value1 = (int)method1.invoke(null, "5");
        //System.out.println(value1);

        //Byte l = new Byte((byte)5);

        //int i = l*5;

        compile("test", "return 5*5;");
    }

    public static PainlessExecutable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader());
    }

    public static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        return PainlessCompiler.compile(name, source, parent);
    }

    private Painless() {}
}

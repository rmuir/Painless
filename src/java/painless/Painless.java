package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class Painless {
    public static void main(String args[]) throws Exception {
        //Executable executable = compile("test", "string x = \"teststring\"; return x[2:];");
        //Executable executable = compile("test", "int x = 4, y = 1; x += y += 2; return x;");
        //Executable executable = compile("test", "string[][] x = string.makearray(1, 1); x[0][0] = \"\";" +
        //        " for (int y = 0; y < 10; ++y) x[0][0]..=x[0][0]..y; return x[0][0];");
        //Executable executable = compile("test", "int y = this.x += 5; return y += --this.x;");
        //Executable executable = compile("test", "byte y = 1, x, z; x = ++y; z = y++; return x + z + y;");
        //Executable executable = compile("test", "int x = 0; while (true) {x = x + 1; if (x >= 5) continue; if (x <= 6) {break;}}");
        //Executable executable = compile("test", "for (int x = 0; x < 5; x = x + 1);");
        //Executable executable = compile("test", "bool x = true; x = false; if (x) return !x;");
        //Executable executable = compile("test", "long[][] x = long.makearray(1, 1); long y; y = x[0][0] = 5; return y;");
        //Executable executable = compile("test", "bool b; b = false; if (b) return null; else return 5;");
        Executable executable = new Painless().compile("test", "longobj x; x = 5L; return x.byte();");
        Map<String, Object> input = new HashMap<>();
        Map<Object, Object> inner = new HashMap<>();
        List<Object> list = new ArrayList<>();
        inner.put(3L, list);
        list.add(1);
        list.add(2);
        list.add(-3L);
        list.add(4);
        list.add(-Short.MAX_VALUE);
        list.add(6);
        input.put("inner", inner);

        //for (int count = 0; count < 10; ++count) {
            /*Executable executable = compile("test",
                    "\nbyte b = 0; list nums = input[\"inner\"][3L];\n" +
                            "int size = nums.size();\n" +
                            "char total;\n" +
                            "\n" +
                            "for (int count = 0; count < size; ++count) {\n" +
                            "    total += (char)(int)nums[count];\n" +
                            "}\n" +
                            "\n" +
                            "return total;"
            );*/

        //Executable executable = compile("test",
        //            "list nums = input[\"inner\"][\"list\"]; int size = nums.size(); nums[size] = \"idiot\"; return nums[size];"
        //    );

            final long start = System.currentTimeMillis();
            Object value = executable.execute(input);
            final long end = System.currentTimeMillis() - start;
            System.out.println("execute: " + end);

            if (value != null) {
                System.out.println(value.getClass().getName() + ": " + value);
            } else {
                System.out.println("NULL");
            }
        //}
    }

    public static Executable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader(), null);
    }

    public static Executable compile(String name, String source, ClassLoader parent, Properties properties) {
        return Compiler.compile(name, source, parent, properties);
    }

    private Painless() {}
}

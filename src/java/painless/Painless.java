package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Painless {
    public static void main(String args[]) throws Exception {
        //PainlessExecutable executable = compile("test", "bool b = true; while (true) { while (true) {if (b) break; b = !b;} break;}");
        //PainlessExecutable executable = compile("test", "int x = 0; while (true) {x = x + 1; if (x >= 5) continue; if (x <= 6) {break;}}");
        //PainlessExecutable executable = compile("test", "for (int x = 0; x < 5; x = x + 1);");
        //PainlessExecutable executable = compile("test", "bool x = true; x = false; if (x) return !x;");
        //PainlessExecutable executable = compile("test", "long[][] x = long.makearray(1, 1); long y; y = x[0][0] = 5; return y;");
        //PainlessExecutable executable = compile("test", "bool b; b = false; if (b) return null; else return 5;");
        PainlessExecutable executable = compile("test", "char c = 9; return c;");
        Map<String, Object> input = new HashMap<>();
        Map<String, Object> inner = new HashMap<>();
        List<Object> list = new ArrayList<>();
        inner.put("list", list);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        input.put("inner", inner);

        //for (int count = 0; count < 10; ++count) {
            /*PainlessExecutable executable = compile("test",
                    "\nlist nums = input[\"inner\"][\"list\"];\n" +
                            "int size = nums.size();\n" +
                            "int total;\n" +
                            "\n" +
                            "for (int count = 0; count < size; count = count + 1) {\n" +
                            "    total = total + (int)nums.get(count);\n" +
                            "}\n" +
                            "\n" +
                            "return total;"
            );*/

        //PainlessExecutable executable = compile("test",
        //            "list nums = input[\"inner\"][\"list\"]; int size = nums.size(); nums[size] = \"idiot\"; return nums[size];"
        //    );

            final long start = System.currentTimeMillis();
            Object value = executable.execute(input);
            final long end = System.currentTimeMillis() - start;
            System.out.println("execute: " + end);
            System.out.println(value);
        //}
    }

    public static PainlessExecutable compile(String name, String source) {
        return compile(name, source, Painless.class.getClassLoader());
    }

    public static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        return PainlessCompiler.compile(name, source, parent);
    }

    private Painless() {}
}

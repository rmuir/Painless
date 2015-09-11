package painless;

import java.util.Map;

public class Test {
    public Test() {

    }

    public Test t = new Test();
    int i = 0;

    public void test2() {
        test(2);
    }

    public int test(Map input) {
        int b = t.i;

        int x = (((t)).test(1));

        return 0;
    }

    public int test(Object o) {
        return 1;
    }
}

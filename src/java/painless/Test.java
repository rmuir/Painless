package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public Test() {

    }

    public Test t = new Test();
    int i = 0;

    public void test2() {
        test(new ArrayList());
    }

    public int test(Map input) {
        int[] x = new int[5];
        int l = x.length;
        x.clone();

        return 0;
    }

    public int test(Object o) {
        return 1;
    }
}

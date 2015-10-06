package painless;

import java.util.Map;

public class Test {
    int x;

    static class Test2 {
        public Object[] t = new Object[] {new Test2()};
        public int x;
    }

    void itest(Integer x) {
    }

    Test2 t;

    void stest() {
        int y, x[] = new int[1];
        y = x[1]++;
    }
}

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
        String x = "X";
        String y = "y";

        String z = x + y;
    }
}

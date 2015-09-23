package painless;

import java.util.HashMap;
import java.util.Map;

public class Test {
    static int i;
    int j;

    public Test() {
    }

    public static Object stest(boolean b) {
        for (;; b = !b) {
        }
    }

    public void mtest(int... blah) {

    }
}

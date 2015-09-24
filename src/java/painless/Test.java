package painless;

import java.util.HashMap;
import java.util.Map;

public class Test {
    static int i;
    int j;

    public Test() {
    }

    public static void stest(boolean a) {
        boolean b = true;

        while (b) {
            return;
        }

        return;
    }

    public void mtest(int... blah) {

    }
}

package painless;

import java.util.Map;

public class Test {
    public Test(String a, String b) {

    }

    public int test(Map input) {
        int [][] y = new int[1][1];
        (y[0])[0] = 3;

        Map x = (Map)((Map)input.get(0)).get(1);

        int z = 1;

        return y[0][0] = z;
    }
}

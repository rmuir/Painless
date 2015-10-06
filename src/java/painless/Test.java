package painless;

import java.util.Map;

public class Test {
    void itest(Integer x) {
        //long i = (long)++(int)x.get(x))[0];
    }

    Object stest(Map input) {
        byte y = 1, x, z; x = ++y; z = y++; return x + z + y;
    }
}

package painless;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public Boolean stest() {
        int x;
        long y;

        x = (int)(y = 2L);

        return true;
    }
}

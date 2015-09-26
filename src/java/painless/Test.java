package painless;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public void stest(boolean b) {
        Long l = (!b ? new Long(8L) : 8) * (b ? new Long(5L) : 12);
    }
}

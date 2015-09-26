package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public void stest(boolean b, Map m, List l) {

        if ((b ? m : 3) == (!b ? new ArrayList() : new HashMap())) {

        }
    }
}

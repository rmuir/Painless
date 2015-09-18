package painless;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public Test(HashMap<Integer, Integer> a) {
        a.put(2, 2);
        Integer i = a.get(2);
        a.put(3, i);
    }
}

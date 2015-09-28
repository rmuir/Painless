package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public Object stest() {
        HashMap<String, Object> sm = new HashMap<>();
        sm.put("1", 1);
        return sm.get("1");
    }
}

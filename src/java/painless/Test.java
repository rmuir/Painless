package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public Object stest() {
        boolean x = true; x = false; if (x) return !x; return null;
    }
}

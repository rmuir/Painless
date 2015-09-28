package painless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public Object stest() {
        float a = 1;
        float b = 0;

        if (a < b) {
            a = 5;
        }

        return null;
    }
}

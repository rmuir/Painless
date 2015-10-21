package org.elasticsearch.plan.a;

import java.util.Map;

public class TEst {
    Object test(Map<String, Object> input) {
        int[][] x = new int[5][5];
        byte b = 0, c = 0;

        while (b < 5) {
            while (c < 5) {
                x[b][c] = b * c;
                ++c;
            }

            ++b;
        }

        return x[4][4];
    }
}

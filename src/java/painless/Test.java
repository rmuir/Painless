package painless;

public class Test {
    static class T {
        int x[] = new int[1];
    }

    public Object stest() {
        //int[][] x = new int[1][1];
        //x[0][0] = 5;
        //return x[0][0];

        //int[] x = new int[1];
        //x[0] = 5;
        //return x[0];

        byte y;
        long[][][][] x = new long[1][1][1][1];

        y = (byte)(x[0][0][0][0] = 5);

        return null;
    }
}

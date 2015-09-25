package painless;

public class Test {
    public void stest() {
        boolean bool = false;
        bool = !bool;

        byte b = 0;
        b = (byte)+b;

        short s = 0;
        s = (short)+s;

        char c = 0;
        c = (char)+c;

        int i = 0;
        i = +i;

        long l = 0;
        l = +l;

        float f = 0;
        f = +f;

        double e = 0;
        e = +e;
    }
}

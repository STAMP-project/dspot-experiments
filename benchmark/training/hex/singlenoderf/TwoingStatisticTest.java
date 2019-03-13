package hex.singlenoderf;


import org.junit.Assert;
import org.junit.Test;


public class TwoingStatisticTest {
    @Test
    public void twoingTest() {
        // basic test cases to check twoing computation
        int[] dd_l = new int[]{ 4, 0, 0, 1 };
        int[] dd_r = new int[]{ 0, 3, 2, 0 };
        double result = twoing(dd_l, 5, dd_r, 5);
        Assert.assertTrue(((Math.abs((result - 4.0))) < 1.0E-10));
        dd_l[0] = 4;
        dd_l[1] = 3;
        dd_l[2] = 2;
        dd_l[3] = 0;
        dd_r[0] = 0;
        dd_r[1] = 0;
        dd_r[2] = 0;
        dd_r[3] = 1;
        result = twoing(dd_l, 9, dd_r, 1);
        Assert.assertTrue(((Math.abs((result - 4.0))) < 1.0E-10));
        dd_l[0] = 4;
        dd_l[1] = 3;
        dd_l[2] = 1;
        dd_l[3] = 0;
        dd_r[0] = 0;
        dd_r[1] = 0;
        dd_r[2] = 1;
        dd_r[3] = 1;
        result = twoing(dd_l, 8, dd_r, 2);
        Assert.assertTrue(((Math.abs((result - 3.0625))) < 1.0E-10));
        dd_l[0] = 999;
        dd_l[1] = 1000005;
        dd_l[2] = 3009;
        dd_l[3] = 1;
        dd_r[0] = 999;
        dd_r[1] = 1000005;
        dd_r[2] = 3009;
        dd_r[3] = 1;
        result = twoing(dd_l, (((999 + 1000005) + 3009) + 1), dd_r, (((999 + 1000005) + 3009) + 1));
        Assert.assertTrue(((Math.abs((result - 0.0))) < 1.0E-10));
    }
}


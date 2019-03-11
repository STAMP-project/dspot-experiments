package water;


import org.junit.Test;


public class AutoBuffer2JSONTest extends TestUtil {
    static class A1 extends Iced {
        double d1 = Double.NaN;

        double d2 = Double.POSITIVE_INFINITY;

        double d3 = Double.NEGATIVE_INFINITY;

        double d4 = -3.141527;
    }

    @Test
    public void testDouble() {
        assertEqual(new AutoBuffer2JSONTest.A1(), "{\"d1\":\"NaN\",\"d2\":\"Infinity\",\"d3\":\"-Infinity\",\"d4\":-3.141527}");
    }

    static class A2 extends Iced {
        public float f1 = Float.NaN;

        private float f2 = Float.POSITIVE_INFINITY;

        final float f3 = Float.NEGATIVE_INFINITY;

        float f4 = -3.141527F;
    }

    @Test
    public void testFloat() {
        assertEqual(new AutoBuffer2JSONTest.A2(), "{\"f1\":\"NaN\",\"f2\":\"Infinity\",\"f3\":\"-Infinity\",\"f4\":-3.141527}");
    }

    static class A3 extends Iced {
        int i = 3;

        int[] is = new int[]{ 1, 2, Integer.MAX_VALUE, -1 };

        String s = "hello";

        String[] ss = new String[]{ "there", null, "\"", ":" };
    }

    @Test
    public void testMisc() {
        assertEqual(new AutoBuffer2JSONTest.A3(), "{\"i\":3,\"is\":[1,2,2147483647,-1],\"s\":\"hello\",\"ss\":[\"there\",null,\"\\\"\",\":\"]}");
    }

    static class A4 extends Iced {
        int a = 7;
    }

    static class A5 extends Iced {
        float b = 9.0F;
    }

    static class A6 extends AutoBuffer2JSONTest.A4 {
        final AutoBuffer2JSONTest.A5 a5 = new AutoBuffer2JSONTest.A5();

        char c = 'Q';
    }

    @Test
    public void testNest() {
        assertEqual(new AutoBuffer2JSONTest.A4(), "{\"a\":7}");
        assertEqual(new AutoBuffer2JSONTest.A5(), "{\"b\":9.0}");
        assertEqual(new AutoBuffer2JSONTest.A6(), "{\"a\":7,\"a5\":{\"b\":9.0},\"c\":81}");
    }

    static class A7 extends Iced {}

    static class A8 extends AutoBuffer2JSONTest.A7 {}

    @Test
    public void testEmpty() {
        assertEqual(new AutoBuffer2JSONTest.A8(), "{}");
        assertEqual(new AutoBuffer2JSONTest.A7(), "{}");
    }

    // TODO: support arrays of booleans
    static class A9 extends Iced {
        boolean yep = true;

        boolean nope = false;
    }

    // Boolean[] yepNope = new Boolean[] {true, false};
    @Test
    public void testBoolean() {
        assertEqual(new AutoBuffer2JSONTest.A9(), "{\"yep\":true,\"nope\":false}");// ,"yepNope":[true,false]

    }
}


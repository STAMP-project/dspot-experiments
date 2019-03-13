package cc.blynk.test.utils;


import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.01.17.
 */
public class NumberUtilTest {
    @Test
    public void testCorrectResultForInt() {
        for (int i = 0; i < 10000; i++) {
            int random = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            double parsed = parseDouble(String.valueOf(random));
            // System.out.println(random);
            Assert.assertEquals(random, parsed, 1.0E-10);
        }
    }

    @Test
    public void testCorrectResultForDouble() {
        for (int i = 0; i < 10000; i++) {
            double random = ThreadLocalRandom.current().nextDouble((-100000), 100000);
            double parsed = parseDouble(String.valueOf(random));
            // System.out.println(random);
            Assert.assertEquals(random, parsed, 1.0E-10);
        }
    }

    @Test
    public void testCorrectResultForDouble2() {
        for (int i = 0; i < 10000; i++) {
            double random = ThreadLocalRandom.current().nextDouble();
            double parsed = parseDouble(String.valueOf(random));
            // System.out.println(random);
            Assert.assertEquals(random, parsed, 1.0E-10);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testExpectError() {
        parseDouble(null);
    }

    @Test
    public void testExpectError2() {
        Assert.assertTrue(((parseDouble("")) == (NO_RESULT)));
    }

    @Test
    public void testExpectError3() {
        Assert.assertTrue(((parseDouble("123.123F")) == (NO_RESULT)));
    }

    @Test
    public void testExpectError4() {
        Assert.assertTrue(((parseDouble("p 123.123")) == (NO_RESULT)));
    }

    @Test
    public void testExpectError5() {
        Assert.assertTrue(((parseDouble("p 123.123")) == (NO_RESULT)));
    }

    @Test
    public void testCustomValue() {
        double d;
        d = parseDouble("0");
        Assert.assertEquals(d, 0, 1.0E-10);
        d = parseDouble("0.0");
        Assert.assertEquals(d, 0, 1.0E-10);
        d = parseDouble("1.0");
        Assert.assertEquals(d, 1.0, 1.0E-10);
        d = parseDouble("+1.0");
        Assert.assertEquals(d, 1.0, 1.0E-10);
        d = parseDouble("-1.0");
        Assert.assertEquals(d, (-1.0), 1.0E-10);
    }
}


package org.robovm.rt.lambdas.test002;


import org.junit.Assert;
import org.junit.Test;


public class NoneCaptureParametersTest {
    @Test
    public void testNonCaptureParameters() {
        B bo = ( a, b) -> a && b;
        Assert.assertEquals((true && true), bo.add(true, true));
        By by = ( a, b) -> ((byte) (a + b));
        Assert.assertEquals((((byte) (1)) + ((byte) (2))), by.add(((byte) (1)), ((byte) (2))));
        C c = ( a, b) -> ((char) (a + b));
        Assert.assertEquals((((char) (1)) + ((char) (2))), c.add(((char) (1)), ((char) (2))));
        S s = ( a, b) -> ((short) (a + b));
        Assert.assertEquals((((short) (1)) + ((short) (2))), s.add(((short) (1)), ((short) (2))));
        I i = ( a, b) -> a + b;
        Assert.assertEquals(5, i.add(2, 3));
        L l = ( a, b) -> a + b;
        Assert.assertEquals((2L + 3L), l.add(2L, 3L));
        F f = ( a, b) -> a + b;
        Assert.assertEquals((2.0F + 3.0F), f.add(2.0F, 3.0F), 0);
        D d = ( a, b) -> a + b;
        Assert.assertEquals((2.0 + 3.0), d.add(2.0, 3.0), 0);
        O o = ( a, b) -> a + b;
        Assert.assertEquals(("Hello" + "World"), o.add("Hello", "World"));
        M m = ( b1, by1, c1, s1, i1, l1, f1, d1, o1) -> {
            return (((((((("" + b1) + by1) + c1) + s1) + i1) + l1) + f1) + d1) + o1;
        };
        Assert.assertEquals(((((((((("" + true) + ((byte) (1))) + 'a') + ((short) (2))) + 3) + ((long) (4))) + 5.0F) + 6.0) + "Hello"), m.add(true, ((byte) (1)), 'a', ((short) (2)), 3, ((long) (4)), 5.0F, 6.0, "Hello"));
    }
}


package com.annimon.stream.function;


import IntUnaryOperator.Util;
import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link IntUnaryOperator}
 */
public class IntUnaryOperatorTest {
    @Test
    public void testIdentity() {
        IntUnaryOperator identity = Util.identity();
        Assert.assertEquals(15, IntStream.of(1, 2, 3, 4, 5).map(identity).sum());
    }

    @Test
    public void testPrivateUtilConstructor() {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}


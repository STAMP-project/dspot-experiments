package com.annimon.stream.function;


import UnaryOperator.Util;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code UnaryOperator}.
 *
 * @see com.annimon.stream.function.UnaryOperator
 */
public class UnaryOperatorTest {
    private static List<Integer> data;

    @Test
    public void testIdentity() {
        final UnaryOperator<Integer> op = Util.identity();
        for (Integer value : UnaryOperatorTest.data) {
            Assert.assertEquals(value, op.apply(value));
        }
    }

    @Test
    public void testSquare() {
        final UnaryOperator<Integer> op = new UnaryOperator<Integer>() {
            @Override
            public Integer apply(Integer value) {
                return value * value;
            }
        };
        for (Integer value : UnaryOperatorTest.data) {
            final Integer expected = value * value;
            Assert.assertEquals(expected, op.apply(value));
        }
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}


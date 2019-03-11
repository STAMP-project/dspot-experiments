package com.annimon.stream.function;


import BinaryOperator.Util;
import com.annimon.stream.Functions;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code BinaryOperator}.
 *
 * @see com.annimon.stream.function.BinaryOperator
 */
public class BinaryOperatorTest {
    private static final List<Integer> data = new ArrayList<Integer>();

    @Test
    public void testSquare() {
        final BinaryOperator<Integer> op = new BinaryOperator<Integer>() {
            @Override
            public Integer apply(Integer value1, Integer value2) {
                return value1 * value2;
            }
        };
        for (Integer value : BinaryOperatorTest.data) {
            final Integer expected = value * value;
            Assert.assertEquals(expected, op.apply(value, value));
        }
    }

    @Test
    public void testMinBy() {
        final BinaryOperator<Integer> op = Util.minBy(Functions.naturalOrder());
        final int size = BinaryOperatorTest.data.size();
        for (int i = 0; i < size; i++) {
            final Integer value1 = BinaryOperatorTest.data.get(i);
            final Integer value2 = BinaryOperatorTest.data.get(((size - 1) - i));
            final Integer expected = Math.min(value1, value2);
            Assert.assertEquals(expected, op.apply(value1, value2));
        }
    }

    @Test
    public void testMaxBy() {
        final BinaryOperator<Integer> op = Util.maxBy(Functions.naturalOrder());
        final int size = BinaryOperatorTest.data.size();
        for (int i = 0; i < size; i++) {
            final Integer value1 = BinaryOperatorTest.data.get(i);
            final Integer value2 = BinaryOperatorTest.data.get(((size - 1) - i));
            final Integer expected = Math.max(value1, value2);
            Assert.assertEquals(expected, op.apply(value1, value2));
        }
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }
}


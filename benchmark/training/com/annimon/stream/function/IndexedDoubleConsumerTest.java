package com.annimon.stream.function;


import IndexedDoubleConsumer.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedDoubleConsumer}.
 *
 * @see IndexedDoubleConsumer
 */
public class IndexedDoubleConsumerTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testAndThen() {
        final double[] buffer = new double[]{ 1.0, 2.5, 4.0, 2.0 };
        IndexedDoubleConsumer addConsumer = new IndexedDoubleConsumer() {
            @Override
            public void accept(int index, double value) {
                buffer[index] += value;
            }
        };
        IndexedDoubleConsumer multiplyConsumer = new IndexedDoubleConsumer() {
            @Override
            public void accept(int index, double value) {
                buffer[index] *= value;
            }
        };
        IndexedDoubleConsumer consumer = Util.andThen(addConsumer, multiplyConsumer);
        // 2.5 + 2.5 = 5.0; 5.0 * 2.5 = 12.5
        consumer.accept(1, 2.5);
        Assert.assertEquals(12.5, buffer[1], 0.0);
        // 4.0 + (-6.0) = -2.0; -2.0 * (-6.0) = 12.0
        consumer.accept(2, (-6.0));
        Assert.assertEquals(12.0, buffer[2], 0.0);
    }

    @Test
    public void testUtilAccept() {
        final IndexedDoubleConsumerTest.IntHolder countHolder = new IndexedDoubleConsumerTest.IntHolder();
        final IndexedDoubleConsumerTest.DoubleHolder valueHolder = new IndexedDoubleConsumerTest.DoubleHolder(10.0);
        final IntConsumer indexConsumer = new IntConsumer() {
            @Override
            public void accept(int index) {
                (countHolder.value)++;
            }
        };
        IndexedDoubleConsumer consumer = Util.accept(indexConsumer, valueHolder);
        for (int i = 1; i < 11; i++) {
            consumer.accept(i, ((double) (i)));
            Assert.assertEquals(i, countHolder.value);
        }
        Assert.assertEquals(65.0, valueHolder.value, 0.0);
    }

    private static class IntHolder {
        public int value;
    }

    private static class DoubleHolder implements DoubleConsumer {
        public double value;

        DoubleHolder(double value) {
            this.value = value;
        }

        @Override
        public void accept(double value) {
            this.value += value;
        }
    }
}


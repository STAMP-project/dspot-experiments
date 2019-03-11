package com.annimon.stream.function;


import IndexedIntConsumer.Util;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedIntConsumer}.
 *
 * @see IndexedIntConsumer
 */
public class IndexedIntConsumerTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testAndThen() {
        final List<Integer> buffer = new ArrayList<Integer>();
        IndexedIntConsumer consumer = Util.andThen(new IndexedIntConsumerTest.Increment(buffer), new IndexedIntConsumerTest.Multiplier(buffer));
        // (10+2) (10*2)
        consumer.accept(10, 2);
        Assert.assertThat(buffer, Matchers.contains(12, 20));
        buffer.clear();
        // (22+3) (22*3)
        consumer.accept(22, 3);
        Assert.assertThat(buffer, Matchers.contains(25, 66));
        buffer.clear();
        consumer.accept((-10), 2);
        consumer.accept(5, 3);
        consumer.accept(42, (-2));
        Assert.assertThat(buffer, Matchers.contains((-8), (-20), 8, 15, 40, (-84)));
    }

    @Test
    public void testUtilAccept() {
        final IndexedIntConsumerTest.IntHolder countHolder = new IndexedIntConsumerTest.IntHolder(0);
        final IndexedIntConsumerTest.IntHolder valueHolder = new IndexedIntConsumerTest.IntHolder(10);
        final IntConsumer indexConsumer = new IntConsumer() {
            @Override
            public void accept(int index) {
                countHolder.accept(1);
            }
        };
        IndexedIntConsumer consumer = Util.accept(indexConsumer, valueHolder);
        for (int i = 1; i < 11; i++) {
            consumer.accept(i, (i * 2));
            Assert.assertEquals(i, countHolder.value);
        }
        Assert.assertEquals(120, valueHolder.value);
    }

    private static class Increment implements IndexedIntConsumer {
        private final List<Integer> buffer;

        Increment(List<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void accept(int value, int add) {
            value += add;
            buffer.add(value);
        }
    }

    private static class Multiplier implements IndexedIntConsumer {
        private final List<Integer> buffer;

        Multiplier(List<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void accept(int value, int factor) {
            value *= factor;
            buffer.add(value);
        }
    }

    private static class IntHolder implements IntConsumer {
        public int value;

        IntHolder(int value) {
            this.value = value;
        }

        @Override
        public void accept(int value) {
            this.value += value;
        }
    }
}


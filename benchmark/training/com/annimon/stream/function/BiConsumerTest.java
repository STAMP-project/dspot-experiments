package com.annimon.stream.function;


import BiConsumer.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code BiConsumer}.
 *
 * @see com.annimon.stream.function.BiConsumer
 */
public class BiConsumerTest {
    @Test
    public void testAccept() {
        BiConsumerTest.IntHolder holder1 = new BiConsumerTest.IntHolder(10);
        BiConsumerTest.IntHolder holder2 = new BiConsumerTest.IntHolder(20);
        BiConsumerTest.increment.accept(holder1, holder2);
        Assert.assertEquals(11, holder1.value);
        Assert.assertEquals(21, holder2.value);
        BiConsumerTest.increment.accept(holder1, holder2);
        Assert.assertEquals(12, holder1.value);
        Assert.assertEquals(22, holder2.value);
        BiConsumerTest.mulBy2.accept(holder1, holder2);
        Assert.assertEquals(24, holder1.value);
        Assert.assertEquals(44, holder2.value);
    }

    @Test
    public void testAndThen() {
        BiConsumer<BiConsumerTest.IntHolder, BiConsumerTest.IntHolder> consumer = Util.andThen(BiConsumerTest.increment, BiConsumerTest.mulBy2);
        BiConsumerTest.IntHolder holder1 = new BiConsumerTest.IntHolder(10);
        BiConsumerTest.IntHolder holder2 = new BiConsumerTest.IntHolder(20);
        consumer.accept(holder1, holder2);
        Assert.assertEquals(22, holder1.value);
        Assert.assertEquals(42, holder2.value);
        consumer.accept(holder1, holder2);
        Assert.assertEquals(46, holder1.value);
        Assert.assertEquals(86, holder2.value);
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final BiConsumer<BiConsumerTest.IntHolder, BiConsumerTest.IntHolder> increment = new BiConsumer<BiConsumerTest.IntHolder, BiConsumerTest.IntHolder>() {
        @Override
        public void accept(BiConsumerTest.IntHolder holder1, BiConsumerTest.IntHolder holder2) {
            (holder1.value)++;
            (holder2.value)++;
        }
    };

    private static final BiConsumer<BiConsumerTest.IntHolder, BiConsumerTest.IntHolder> mulBy2 = new BiConsumer<BiConsumerTest.IntHolder, BiConsumerTest.IntHolder>() {
        @Override
        public void accept(BiConsumerTest.IntHolder holder1, BiConsumerTest.IntHolder holder2) {
            holder1.value *= 2;
            holder2.value *= 2;
        }
    };

    static class IntHolder {
        int value;

        IntHolder(int value) {
            this.value = value;
        }
    }
}


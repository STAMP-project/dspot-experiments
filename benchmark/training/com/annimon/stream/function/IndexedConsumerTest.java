package com.annimon.stream.function;


import IndexedConsumer.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedConsumer}.
 *
 * @see com.annimon.stream.function.IndexedConsumer
 */
public class IndexedConsumerTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testAccept() {
        IndexedConsumerTest.IntHolder holder = new IndexedConsumerTest.IntHolder(10);
        IndexedConsumerTest.adder.accept(1, holder);
        Assert.assertEquals(11, holder.value);
        IndexedConsumerTest.adder.accept(31, holder);
        Assert.assertEquals(42, holder.value);
    }

    @Test
    public void testUtilWrap() {
        IndexedConsumer<IndexedConsumerTest.IntHolder> increment = Util.wrap(new Consumer<IndexedConsumerTest.IntHolder>() {
            @Override
            public void accept(IndexedConsumerTest.IntHolder t) {
                (t.value)++;
            }
        });
        IndexedConsumerTest.IntHolder holder = new IndexedConsumerTest.IntHolder(10);
        increment.accept(0, holder);
        Assert.assertEquals(11, holder.value);
        increment.accept(20, holder);
        Assert.assertEquals(12, holder.value);
    }

    @Test
    public void testUtilAccept() {
        final IndexedConsumerTest.IntHolder holder = new IndexedConsumerTest.IntHolder(0);
        final IntConsumer intConsumer = new IntConsumer() {
            @Override
            public void accept(int value) {
                holder.value -= value;
            }
        };
        final Consumer<IndexedConsumerTest.IntHolder> objectConsumer = new Consumer<IndexedConsumerTest.IntHolder>() {
            @Override
            public void accept(IndexedConsumerTest.IntHolder t) {
                (t.value)++;
            }
        };
        holder.value = 10;
        IndexedConsumer<IndexedConsumerTest.IntHolder> consumer = Util.accept(intConsumer, objectConsumer);
        // 10 - 0 + 1
        consumer.accept(0, holder);
        Assert.assertEquals(11, holder.value);
        // 11 - 5 + 1
        consumer.accept(5, holder);
        Assert.assertEquals(7, holder.value);
        holder.value = 10;
        IndexedConsumer<IndexedConsumerTest.IntHolder> consumerObject = Util.accept(null, objectConsumer);
        // 10 + 1
        consumerObject.accept(0, holder);
        Assert.assertEquals(11, holder.value);
        // 11 + 1
        consumerObject.accept(5, holder);
        Assert.assertEquals(12, holder.value);
        holder.value = 10;
        IndexedConsumer<IndexedConsumerTest.IntHolder> consumerIndex = Util.accept(intConsumer, null);
        // 10 - 0
        consumerIndex.accept(0, holder);
        Assert.assertEquals(10, holder.value);
        // 10 - 5
        consumerIndex.accept(5, holder);
        Assert.assertEquals(5, holder.value);
    }

    private static final IndexedConsumer<IndexedConsumerTest.IntHolder> adder = new IndexedConsumer<IndexedConsumerTest.IntHolder>() {
        @Override
        public void accept(int index, IndexedConsumerTest.IntHolder holder) {
            holder.value += index;
        }
    };

    static class IntHolder {
        int value;

        IntHolder(int value) {
            this.value = value;
        }
    }
}


package com.annimon.stream.function;


import Consumer.Util;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code Consumer}.
 *
 * @see com.annimon.stream.function.Consumer
 */
public class ConsumerTest {
    @Test
    public void testAccept() {
        ConsumerTest.IntHolder holder = new ConsumerTest.IntHolder(10);
        ConsumerTest.increment.accept(holder);
        Assert.assertEquals(11, holder.value);
        ConsumerTest.increment.accept(holder);
        Assert.assertEquals(12, holder.value);
        ConsumerTest.mulBy2.accept(holder);
        Assert.assertEquals(24, holder.value);
    }

    @Test
    public void testAndThen() {
        Consumer<ConsumerTest.IntHolder> consumer = Util.andThen(ConsumerTest.increment, ConsumerTest.mulBy2);
        ConsumerTest.IntHolder holder = new ConsumerTest.IntHolder(10);
        // (10+1) * 2
        consumer.accept(holder);
        Assert.assertEquals(22, holder.value);
        // (22+1) * 2
        consumer.accept(holder);
        Assert.assertEquals(46, holder.value);
    }

    @Test
    public void testSafe() {
        Consumer<OutputStream> consumer = Util.safe(ConsumerTest.writer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(5);
        consumer.accept(baos);
        consumer.accept(null);
        consumer.accept(null);
        consumer.accept(baos);
        Assert.assertEquals(">>", baos.toString());
    }

    @Test
    public void testSafeWithOnFailedConsumer() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(5);
        Consumer<OutputStream> consumer = Util.safe(ConsumerTest.writer, new Consumer<OutputStream>() {
            @Override
            public void accept(OutputStream os) {
                baos.write('<');
            }
        });
        consumer.accept(baos);
        consumer.accept(baos);
        consumer.accept(null);
        consumer.accept(null);
        Assert.assertEquals(">><<", baos.toString());
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final Consumer<ConsumerTest.IntHolder> increment = new Consumer<ConsumerTest.IntHolder>() {
        @Override
        public void accept(ConsumerTest.IntHolder holder) {
            (holder.value)++;
        }
    };

    private static final Consumer<ConsumerTest.IntHolder> mulBy2 = new Consumer<ConsumerTest.IntHolder>() {
        @Override
        public void accept(ConsumerTest.IntHolder holder) {
            holder.value *= 2;
        }
    };

    private static final ThrowableConsumer<OutputStream, Throwable> writer = new ThrowableConsumer<OutputStream, Throwable>() {
        @Override
        public void accept(OutputStream os) throws Throwable {
            os.write('>');
        }
    };

    static class IntHolder {
        int value;

        IntHolder(int value) {
            this.value = value;
        }
    }
}


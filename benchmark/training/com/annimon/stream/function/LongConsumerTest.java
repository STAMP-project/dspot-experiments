package com.annimon.stream.function;


import LongConsumer.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code LongConsumer}.
 *
 * @see com.annimon.stream.function.LongConsumer
 */
public class LongConsumerTest {
    @Test
    public void testAndThen() {
        final List<Long> buffer = new ArrayList<Long>();
        LongConsumer consumer = Util.andThen(new LongConsumerTest.Multiplier(buffer, 10000000000L), new LongConsumerTest.Multiplier(buffer, 2));
        // (10+1) (10*2)
        consumer.accept(10);
        Assert.assertThat(buffer, Matchers.contains(100000000000L, 20L));
        buffer.clear();
        // (22+1) (22*2)
        consumer.accept(22);
        Assert.assertThat(buffer, Matchers.contains(220000000000L, 44L));
        buffer.clear();
        consumer.accept((-10));
        consumer.accept(5);
        consumer.accept(118);
        Assert.assertThat(buffer, Matchers.contains((-100000000000L), (-20L), 50000000000L, 10L, 1180000000000L, 236L));
    }

    @Test
    public void testSafe() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(30);
        final DataOutputStream dos = new DataOutputStream(baos);
        LongConsumer consumer = Util.safe(new LongConsumerTest.UnsafeConsumer(dos));
        consumer.accept(10L);
        consumer.accept(20L);
        consumer.accept((-5L));
        consumer.accept((-8L));
        consumer.accept(500L);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readLong(), Matchers.is(10L));
        Assert.assertThat(dis.readLong(), Matchers.is(20L));
        Assert.assertThat(dis.readLong(), Matchers.is(500L));
    }

    @Test
    public void testSafeWithOnFailedConsumer() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(30);
        final DataOutputStream dos = new DataOutputStream(baos);
        LongConsumer consumer = Util.safe(new LongConsumerTest.UnsafeConsumer(dos), new LongConsumer() {
            @Override
            public void accept(long value) {
                baos.write(0);
            }
        });
        consumer.accept(10L);
        consumer.accept(20L);
        consumer.accept((-5L));
        consumer.accept((-8L));
        consumer.accept(500L);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readLong(), Matchers.is(10L));
        Assert.assertThat(dis.readLong(), Matchers.is(20L));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readLong(), Matchers.is(500L));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static class Multiplier implements LongConsumer {
        private final long factor;

        private final List<Long> buffer;

        Multiplier(List<Long> buffer, long factor) {
            this.buffer = buffer;
            this.factor = factor;
        }

        @Override
        public void accept(long value) {
            value *= factor;
            buffer.add(value);
        }
    }

    private static class UnsafeConsumer implements ThrowableLongConsumer<Throwable> {
        private final DataOutputStream os;

        UnsafeConsumer(DataOutputStream os) {
            this.os = os;
        }

        @Override
        public void accept(long value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            os.writeLong(value);
        }
    }
}


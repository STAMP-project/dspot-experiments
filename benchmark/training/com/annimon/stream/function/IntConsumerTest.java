package com.annimon.stream.function;


import IntConsumer.Util;
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
 * Tests {@code IntConsumer}.
 *
 * @see com.annimon.stream.function.IntConsumer
 */
public class IntConsumerTest {
    @Test
    public void testAndThen() {
        final List<Integer> buffer = new ArrayList<Integer>();
        IntConsumer consumer = Util.andThen(new IntConsumerTest.Increment(buffer), new IntConsumerTest.Multiplier(buffer, 2));
        // (10+1) (10*2)
        consumer.accept(10);
        Assert.assertThat(buffer, Matchers.contains(11, 20));
        buffer.clear();
        // (22+1) (22*2)
        consumer.accept(22);
        Assert.assertThat(buffer, Matchers.contains(23, 44));
        buffer.clear();
        consumer.accept((-10));
        consumer.accept(5);
        consumer.accept(118);
        Assert.assertThat(buffer, Matchers.contains((-9), (-20), 6, 10, 119, 236));
    }

    @Test
    public void testSafe() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(15);
        final DataOutputStream dos = new DataOutputStream(baos);
        IntConsumer consumer = Util.safe(new IntConsumerTest.UnsafeConsumer(dos));
        consumer.accept(10);
        consumer.accept(20);
        consumer.accept((-5));
        consumer.accept((-8));
        consumer.accept(500);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readInt(), Matchers.is(10));
        Assert.assertThat(dis.readInt(), Matchers.is(20));
        Assert.assertThat(dis.readInt(), Matchers.is(500));
    }

    @Test
    public void testSafeWithOnFailedConsumer() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(15);
        final DataOutputStream dos = new DataOutputStream(baos);
        IntConsumer consumer = Util.safe(new IntConsumerTest.UnsafeConsumer(dos), new IntConsumer() {
            @Override
            public void accept(int value) {
                baos.write(0);
            }
        });
        consumer.accept(10);
        consumer.accept(20);
        consumer.accept((-5));
        consumer.accept((-8));
        consumer.accept(500);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readInt(), Matchers.is(10));
        Assert.assertThat(dis.readInt(), Matchers.is(20));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readInt(), Matchers.is(500));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static class Increment implements IntConsumer {
        private final List<Integer> buffer;

        Increment(List<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void accept(int value) {
            value++;
            buffer.add(value);
        }
    }

    private static class Multiplier implements IntConsumer {
        private final int factor;

        private final List<Integer> buffer;

        Multiplier(List<Integer> buffer, int factor) {
            this.buffer = buffer;
            this.factor = factor;
        }

        @Override
        public void accept(int value) {
            value *= factor;
            buffer.add(value);
        }
    }

    private static class UnsafeConsumer implements ThrowableIntConsumer<Throwable> {
        private final DataOutputStream os;

        UnsafeConsumer(DataOutputStream os) {
            this.os = os;
        }

        @Override
        public void accept(int value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            os.writeInt(value);
        }
    }
}


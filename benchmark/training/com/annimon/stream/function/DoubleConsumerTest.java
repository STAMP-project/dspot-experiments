package com.annimon.stream.function;


import DoubleConsumer.Util;
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
 * Tests {@code DoubleConsumer}.
 *
 * @see com.annimon.stream.function.DoubleConsumer
 */
public class DoubleConsumerTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testAndThen() {
        final List<Double> buffer = new ArrayList<Double>();
        DoubleConsumer consumer = Util.andThen(new DoubleConsumerTest.Multiplier(buffer, 0.1), new DoubleConsumerTest.Multiplier(buffer, 2));
        consumer.accept(10.0);
        Assert.assertThat(buffer, Matchers.contains(Matchers.closeTo(1, 1.0E-4), Matchers.closeTo(20, 1.0E-4)));
        buffer.clear();
        consumer.accept(22);
        Assert.assertThat(buffer, Matchers.contains(Matchers.closeTo(2.2, 1.0E-4), Matchers.closeTo(44.0, 1.0E-4)));
        buffer.clear();
        consumer.accept((-10));
        consumer.accept(5);
        consumer.accept(0.08);
        Assert.assertThat(buffer, Matchers.contains(Matchers.closeTo((-1), 1.0E-4), Matchers.closeTo((-20), 1.0E-4), Matchers.closeTo(0.5, 1.0E-4), Matchers.closeTo(10, 1.0E-4), Matchers.closeTo(0.008, 1.0E-4), Matchers.closeTo(0.16, 1.0E-4)));
    }

    @Test
    public void testSafe() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(15);
        final DataOutputStream dos = new DataOutputStream(baos);
        DoubleConsumer consumer = Util.safe(new DoubleConsumerTest.UnsafeConsumer(dos));
        consumer.accept(0.16);
        consumer.accept(3.2);
        consumer.accept((-5));
        consumer.accept((-8));
        consumer.accept(500);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(0.16, 1.0E-4));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(3.2, 1.0E-4));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(500, 1.0E-4));
    }

    @Test
    public void testSafeWithOnFailedConsumer() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(15);
        final DataOutputStream dos = new DataOutputStream(baos);
        DoubleConsumer consumer = Util.safe(new DoubleConsumerTest.UnsafeConsumer(dos), new DoubleConsumer() {
            @Override
            public void accept(double value) {
                baos.write(0);
            }
        });
        consumer.accept(0.16);
        consumer.accept(3.2);
        consumer.accept((-5));
        consumer.accept((-8));
        consumer.accept(500);
        final byte[] result = baos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(result));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(0.16, 1.0E-4));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(3.2, 1.0E-4));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readByte(), Matchers.is(((byte) (0))));
        Assert.assertThat(dis.readDouble(), Matchers.closeTo(500, 1.0E-4));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static class Multiplier implements DoubleConsumer {
        private final double factor;

        private final List<Double> buffer;

        Multiplier(List<Double> buffer, double factor) {
            this.buffer = buffer;
            this.factor = factor;
        }

        @Override
        public void accept(double value) {
            value *= factor;
            buffer.add(value);
        }
    }

    private static class UnsafeConsumer implements ThrowableDoubleConsumer<Throwable> {
        private final DataOutputStream os;

        UnsafeConsumer(DataOutputStream os) {
            this.os = os;
        }

        @Override
        public void accept(double value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            os.writeDouble(value);
        }
    }
}


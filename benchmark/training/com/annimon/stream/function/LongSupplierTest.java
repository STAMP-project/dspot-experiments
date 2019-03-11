package com.annimon.stream.function;


import LongSupplier.Util;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code LongSupplier}.
 *
 * @see LongSupplier
 */
public class LongSupplierTest {
    private static byte[] input;

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(LongSupplierTest.input));
        LongSupplier supplier = Util.safe(new LongSupplierTest.UnsafeSupplier(dis));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(10L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(15L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(20L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(0L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(0L));
    }

    @Test
    public void testSafeWithOnFailedSupplier() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(LongSupplierTest.input));
        LongSupplier supplier = Util.safe(new LongSupplierTest.UnsafeSupplier(dis), 500L);
        Assert.assertThat(supplier.getAsLong(), Matchers.is(10L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(15L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(20L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(500L));
        Assert.assertThat(supplier.getAsLong(), Matchers.is(500L));
    }

    private static class UnsafeSupplier implements ThrowableLongSupplier<Throwable> {
        private final DataInputStream is;

        UnsafeSupplier(DataInputStream is) {
            this.is = is;
        }

        @Override
        public long getAsLong() throws IOException {
            return is.readLong();
        }
    }
}


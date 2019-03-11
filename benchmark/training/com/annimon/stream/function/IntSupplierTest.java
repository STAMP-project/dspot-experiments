package com.annimon.stream.function;


import IntSupplier.Util;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IntSupplier}.
 *
 * @see IntSupplier
 */
public class IntSupplierTest {
    private static byte[] input;

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(IntSupplierTest.input));
        IntSupplier supplier = Util.safe(new IntSupplierTest.UnsafeSupplier(dis));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(10));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(15));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(20));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(0));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(0));
    }

    @Test
    public void testSafeWithOnFailedSupplier() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(IntSupplierTest.input));
        IntSupplier supplier = Util.safe(new IntSupplierTest.UnsafeSupplier(dis), 500);
        Assert.assertThat(supplier.getAsInt(), Matchers.is(10));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(15));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(20));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(500));
        Assert.assertThat(supplier.getAsInt(), Matchers.is(500));
    }

    private static class UnsafeSupplier implements ThrowableIntSupplier<Throwable> {
        private final DataInputStream is;

        UnsafeSupplier(DataInputStream is) {
            this.is = is;
        }

        @Override
        public int getAsInt() throws IOException {
            return is.readInt();
        }
    }
}


package com.annimon.stream.function;


import DoubleSupplier.Util;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code DoubleSupplier}.
 *
 * @see DoubleSupplier
 */
public class DoubleSupplierTest {
    private static byte[] input;

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(DoubleSupplierTest.input));
        DoubleSupplier supplier = Util.safe(new DoubleSupplierTest.UnsafeSupplier(dis));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(0.16, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(3.2, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(5000, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(0.0, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(0.0, 1.0E-4));
    }

    @Test
    public void testSafeWithOnFailedSupplier() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(DoubleSupplierTest.input));
        DoubleSupplier supplier = Util.safe(new DoubleSupplierTest.UnsafeSupplier(dis), 5000);
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(0.16, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(3.2, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(5000, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(5000, 1.0E-4));
        Assert.assertThat(supplier.getAsDouble(), Matchers.closeTo(5000, 1.0E-4));
    }

    private static class UnsafeSupplier implements ThrowableDoubleSupplier<Throwable> {
        private final DataInputStream is;

        UnsafeSupplier(DataInputStream is) {
            this.is = is;
        }

        @Override
        public double getAsDouble() throws IOException {
            return is.readDouble();
        }
    }
}


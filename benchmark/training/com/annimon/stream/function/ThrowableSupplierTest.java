package com.annimon.stream.function;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code ThrowableSupplier}.
 *
 * @see com.annimon.stream.function.ThrowableSupplier
 */
public class ThrowableSupplierTest {
    @Test
    public void testGetWithIOException() {
        Assert.assertEquals("fantastic", new ThrowableSupplier<String, IOException>() {
            @Override
            public String get() {
                return "fantastic";
            }
        }.get());
    }

    @Test
    public void testGetDoubleWithRuntimeException() {
        ThrowableSupplier<Double, RuntimeException> supplier = new ThrowableSupplier<Double, RuntimeException>() {
            @Override
            public Double get() {
                return Double.parseDouble("5");
            }
        };
        Assert.assertEquals(5, supplier.get(), 0.1);
    }

    @Test(expected = NumberFormatException.class)
    public void testGetDoubleWithRuntimeExceptionAndWrongValue() {
        ThrowableSupplier<Double, RuntimeException> supplier = new ThrowableSupplier<Double, RuntimeException>() {
            @Override
            public Double get() {
                return Double.parseDouble("abc");
            }
        };
        supplier.get();
    }
}


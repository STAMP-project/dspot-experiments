package com.annimon.stream.function;


import Supplier.Util;
import com.annimon.stream.Functions;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code Supplier}.
 *
 * @see com.annimon.stream.function.Supplier
 */
public class SupplierTest {
    private static byte[] input;

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testGetString() {
        Supplier<String> supplier = new Supplier<String>() {
            @Override
            public String get() {
                return "fantastic";
            }
        };
        Assert.assertEquals("fantastic", supplier.get());
    }

    @Test
    public void testGetStringBuilder() {
        Supplier<StringBuilder> supplier = Functions.stringBuilderSupplier();
        Assert.assertThat(supplier.get(), CoreMatchers.instanceOf(StringBuilder.class));
        Assert.assertTrue(supplier.get().toString().isEmpty());
    }

    @Test
    public void testIncrement() {
        Supplier<Integer> supplier = new Supplier<Integer>() {
            private int counter = 0;

            @Override
            public Integer get() {
                return (counter)++;
            }
        };
        Assert.assertEquals(0, supplier.get().intValue());
        Assert.assertEquals(1, supplier.get().intValue());
        Assert.assertEquals(2, supplier.get().intValue());
        Assert.assertEquals(3, supplier.get().intValue());
    }

    @Test
    public void testSafe() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(SupplierTest.input));
        Supplier<String> supplier = Util.safe(new SupplierTest.UnsafeSupplier(dis));
        Assert.assertThat(supplier.get(), Matchers.is("test"));
        Assert.assertThat(supplier.get(), Matchers.is("123"));
        Assert.assertThat(supplier.get(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(supplier.get(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSafeWithOnFailedSupplier() throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(SupplierTest.input));
        Supplier<String> supplier = Util.safe(new SupplierTest.UnsafeSupplier(dis), "oops");
        Assert.assertThat(supplier.get(), Matchers.is("test"));
        Assert.assertThat(supplier.get(), Matchers.is("123"));
        Assert.assertThat(supplier.get(), Matchers.is("oops"));
        Assert.assertThat(supplier.get(), Matchers.is("oops"));
    }

    private static class UnsafeSupplier implements ThrowableSupplier<String, Throwable> {
        private final DataInputStream is;

        UnsafeSupplier(DataInputStream is) {
            this.is = is;
        }

        @Override
        public String get() throws IOException {
            return is.readUTF();
        }
    }
}


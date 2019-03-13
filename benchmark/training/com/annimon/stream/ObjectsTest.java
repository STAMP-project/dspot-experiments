package com.annimon.stream;


import com.annimon.stream.function.Supplier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests {@code Objects}.
 *
 * @see com.annimon.stream.Objects
 */
public class ObjectsTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEqualsThisObjects() {
        Assert.assertTrue(Objects.equals(this, this));
    }

    @Test
    public void testEqualsNonEqualNumbers() {
        Assert.assertFalse(Objects.equals(80, 10));
    }

    @Test
    public void testDeepEqualsBasic() {
        Assert.assertTrue(Objects.deepEquals(this, this));
        Assert.assertFalse(Objects.deepEquals(80, 10));
        Assert.assertFalse(Objects.deepEquals(80, null));
    }

    @Test
    public void testDeepEqualsArrays() {
        final Supplier<Object> s = new Supplier<Object>() {
            @Override
            public Object get() {
                return new Object[]{ this, 1, 2, 3, "test", new Integer[]{ 1, 2, 3 } };
            }
        };
        Assert.assertTrue(Objects.deepEquals(s.get(), s.get()));
        Assert.assertFalse(Objects.deepEquals(s.get(), new Object[]{ this, 1, 2, 3 }));
    }

    @Test
    public void testHashCode() {
        Object value = new Random();
        Assert.assertEquals(value.hashCode(), Objects.hashCode(value));
    }

    @Test
    public void testHashRepeated() {
        Object value = new Random();
        int initial = Objects.hash(value, "test", 10, true, value, null, 50);
        Assert.assertEquals(initial, Objects.hash(value, "test", 10, true, value, null, 50));
        Assert.assertEquals(initial, Objects.hash(value, "test", 10, true, value, null, 50));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("10", Objects.toString(10, ""));
    }

    @Test
    public void testToStringWithNullDefaults() {
        Assert.assertEquals("none", Objects.toString(null, "none"));
    }

    @Test
    public void testCompareLess() {
        int result = Objects.compare(10, 20, Functions.naturalOrder());
        Assert.assertEquals((-1), result);
    }

    @Test
    public void testCompareEquals() {
        int result = Objects.compare(20, 20, Functions.naturalOrder());
        Assert.assertEquals(0, result);
    }

    @Test
    public void testCompareGreater() {
        int result = Objects.compare(20, 10, Functions.naturalOrder());
        Assert.assertEquals(1, result);
    }

    @Test
    public void testCompareInt() {
        Assert.assertEquals((-1), Objects.compareInt(10, 20));
        Assert.assertEquals(0, Objects.compareInt(20, 20));
        Assert.assertEquals(1, Objects.compareInt(20, 10));
    }

    @Test
    public void testCompareLong() {
        Assert.assertEquals((-1), Objects.compareLong(100L, 10000L));
        Assert.assertEquals(0, Objects.compareLong(2000L, 2000L));
        Assert.assertEquals(1, Objects.compareLong(200000L, 10L));
    }

    @Test
    public void testRequireNonNull() {
        int result = Objects.requireNonNull(10);
        Assert.assertEquals(10, result);
    }

    @Test(expected = NullPointerException.class)
    public void testRequireNonNullWithException() {
        Objects.requireNonNull(null);
    }

    @Test
    public void testRequireNonNullWithExceptionAndMessage() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("message");
        Objects.requireNonNull(null, "message");
    }

    @Test
    public void testRequireNonNullWithMessageSupplier() {
        Object result = Objects.requireNonNull("test", new Supplier<String>() {
            @Override
            public String get() {
                return "supplied message";
            }
        });
        Assert.assertEquals("test", result);
    }

    @Test
    public void testRequireNonNullWithMessageSupplierAndException() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("supplied message");
        Objects.requireNonNull(null, new Supplier<String>() {
            @Override
            public String get() {
                return "supplied message";
            }
        });
    }

    @Test
    public void testRequireNonNullElse() {
        Object result = Objects.requireNonNullElse("a", "b");
        Assert.assertEquals("a", result);
    }

    @Test
    public void testRequireNonNullElseWithNullFirstArgument() {
        Object result = Objects.requireNonNullElse(null, "b");
        Assert.assertEquals("b", result);
    }

    @Test
    public void testRequireNonNullElseWithNullArguments() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("defaultObj");
        Objects.requireNonNullElse(null, null);
    }

    @Test
    public void testRequireNonNullElseGet() {
        Object result = Objects.requireNonNullElseGet("a", new Supplier<String>() {
            @Override
            public String get() {
                return "b";
            }
        });
        Assert.assertEquals("a", result);
    }

    @Test
    public void testRequireNonNullElseGetWithNullFirstArgument() {
        Object result = Objects.requireNonNullElseGet(null, new Supplier<String>() {
            @Override
            public String get() {
                return "b";
            }
        });
        Assert.assertEquals("b", result);
    }

    @Test
    public void testRequireNonNullElseGetWithNullArguments() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("supplier");
        Objects.requireNonNullElseGet(null, null);
    }

    @Test
    public void testRequireNonNullElseGetWithNullSupplied() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("supplier.get()");
        Objects.requireNonNullElseGet(null, new Supplier<String>() {
            @Override
            public String get() {
                return null;
            }
        });
    }

    @Test
    public void testRequireNonNullElements() {
        Collection<Integer> col = Objects.requireNonNullElements(Arrays.asList(1, 2, 3, 4));
        Assert.assertThat(col, Matchers.contains(1, 2, 3, 4));
    }

    @Test
    public void testRequireNonNullElementsWithNullCollection() {
        expectedException.expect(NullPointerException.class);
        Objects.requireNonNullElements(null);
    }

    @Test
    public void testRequireNonNullElementsWithNullElement() {
        expectedException.expect(NullPointerException.class);
        Objects.requireNonNullElements(Arrays.asList(1, 2, null, 4));
    }

    @Test
    public void testIsNull() {
        Assert.assertTrue(Objects.isNull(null));
        Assert.assertFalse(Objects.isNull(1));
    }

    @Test
    public void testNonNull() {
        Assert.assertFalse(Objects.nonNull(null));
        Assert.assertTrue(Objects.nonNull(1));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Objects.class, hasOnlyPrivateConstructors());
    }
}


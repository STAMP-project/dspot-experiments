package org.pac4j.core.profile.converter;


import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link org.pac4j.core.profile.converter.LongConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class LongConverterTests {
    private final LongConverter converter = new LongConverter();

    private static final int INT_VALUE = 5;

    private static final long LONG_VALUE = 1234567890123L;

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotAnInteger() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testLong() {
        Assert.assertEquals(LongConverterTests.LONG_VALUE, ((long) (this.converter.convert(LongConverterTests.LONG_VALUE))));
    }

    @Test
    public void testLongString() {
        Assert.assertEquals(LongConverterTests.LONG_VALUE, ((long) (this.converter.convert(("" + (LongConverterTests.LONG_VALUE))))));
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(((long) (LongConverterTests.INT_VALUE)), ((long) (this.converter.convert(Integer.valueOf(LongConverterTests.INT_VALUE)))));
    }
}


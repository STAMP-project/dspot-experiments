package org.pac4j.core.profile.converter;


import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link org.pac4j.core.profile.converter.IntegerConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class IntegerConverterTests {
    private final IntegerConverter converter = new IntegerConverter();

    private static final int VALUE = 12;

    @Test
    public void testNull() {
        Assert.assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotAnInteger() {
        Assert.assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(IntegerConverterTests.VALUE, ((int) (this.converter.convert(IntegerConverterTests.VALUE))));
    }

    @Test
    public void testIntegerString() {
        Assert.assertEquals(IntegerConverterTests.VALUE, ((int) (this.converter.convert(("" + (IntegerConverterTests.VALUE))))));
    }
}


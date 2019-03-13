package org.pac4j.core.profile.converter;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the {@link BooleanConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class BooleanConverterTests {
    private final BooleanConverter converter = new BooleanConverter();

    @Test
    public void testNull() {
        Assert.assertFalse(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotABoolean() {
        Assert.assertFalse(this.converter.convert(new Date()));
    }

    @Test
    public void testBooleanFalse() {
        Assert.assertEquals(Boolean.FALSE, this.converter.convert(Boolean.FALSE));
    }

    @Test
    public void testBooleanTrue() {
        Assert.assertEquals(Boolean.TRUE, this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testFalse() {
        Assert.assertEquals(Boolean.FALSE, this.converter.convert("false"));
    }

    @Test
    public void testTrue() {
        Assert.assertEquals(Boolean.TRUE, this.converter.convert("true"));
    }

    @Test
    public void testOneString() {
        Assert.assertEquals(Boolean.TRUE, this.converter.convert("1"));
    }

    @Test
    public void testOneNumber() {
        Assert.assertEquals(Boolean.TRUE, this.converter.convert(1));
    }
}


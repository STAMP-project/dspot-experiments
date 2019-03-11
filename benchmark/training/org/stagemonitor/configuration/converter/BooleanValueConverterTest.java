package org.stagemonitor.configuration.converter;


import org.junit.Assert;
import org.junit.Test;


public class BooleanValueConverterTest {
    private final BooleanValueConverter converter = new BooleanValueConverter();

    @Test
    public void testConvert() throws Exception {
        Assert.assertTrue(converter.convert("true"));
        Assert.assertFalse(converter.convert("false"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNull() throws Exception {
        converter.convert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidTrue() throws Exception {
        converter.convert("ture");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidFalse() throws Exception {
        converter.convert("fasle");
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}


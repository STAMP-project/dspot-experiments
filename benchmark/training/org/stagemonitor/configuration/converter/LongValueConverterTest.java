package org.stagemonitor.configuration.converter;


import org.junit.Assert;
import org.junit.Test;


public class LongValueConverterTest {
    private final LongValueConverter converter = new LongValueConverter();

    @Test
    public void testConvert() throws Exception {
        Assert.assertEquals(Long.valueOf(Long.MAX_VALUE), converter.convert(Long.toString(Long.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertNull() throws Exception {
        converter.convert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertInvalidTrue() throws Exception {
        converter.convert("one");
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}


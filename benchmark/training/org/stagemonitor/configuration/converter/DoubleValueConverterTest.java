package org.stagemonitor.configuration.converter;


import org.junit.Assert;
import org.junit.Test;

import static DoubleValueConverter.INSTANCE;


public class DoubleValueConverterTest {
    private final ValueConverter<Double> converter = INSTANCE;

    @Test
    public void testConvert() throws Exception {
        Assert.assertEquals(3.1415, converter.convert(Double.toString(3.1415)), 0);
    }

    @Test
    public void testConvertDot() throws Exception {
        Assert.assertEquals(3.1415, converter.convert("3.1415"), 0);
    }

    @Test
    public void testConvertComma() throws Exception {
        Assert.assertEquals(3.1415, converter.convert("3,1415"), 0);
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


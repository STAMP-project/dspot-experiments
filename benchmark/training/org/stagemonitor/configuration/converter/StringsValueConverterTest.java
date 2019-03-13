package org.stagemonitor.configuration.converter;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

import static SetValueConverter.LOWER_STRINGS_VALUE_CONVERTER;
import static SetValueConverter.STRINGS_VALUE_CONVERTER;


public class StringsValueConverterTest {
    private final SetValueConverter<String> converter = STRINGS_VALUE_CONVERTER;

    private final SetValueConverter<String> lowerConverter = LOWER_STRINGS_VALUE_CONVERTER;

    @Test
    public void testConvertSingleValue() throws Exception {
        Assert.assertEquals(new HashSet<String>(Arrays.asList("a")), converter.convert("a"));
        Assert.assertEquals(new HashSet<String>(Arrays.asList("a")), lowerConverter.convert("A"));
    }

    @Test
    public void testConvertMultipleValues() throws Exception {
        Assert.assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d")), converter.convert("a, b,c  ,  d "));
        Assert.assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d")), lowerConverter.convert("A, b,C  ,  D "));
    }

    @Test
    public void testConvertNull() throws Exception {
        Assert.assertEquals(Collections.<String>emptySet(), converter.convert(null));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("a, b, c, d", converter.toString(converter.convert("a, b,c  ,  d ")));
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}


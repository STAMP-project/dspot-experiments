package org.stagemonitor.configuration.converter;


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

import static RegexValueConverter.INSTANCE;


public class RegexListValueConverterTest {
    private final SetValueConverter<Pattern> converter = new SetValueConverter<Pattern>(INSTANCE);

    @Test
    public void testConvert() throws Exception {
        Assert.assertEquals(".*", converter.convert(".*").iterator().next().pattern());
    }

    @Test
    public void testToStringConvert() throws Exception {
        Assert.assertEquals(".*", converter.toString(converter.convert(".*")));
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}


package org.stagemonitor.configuration.converter;


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

import static RegexValueConverter.INSTANCE;


public class RegexMapValueConverterTest {
    private final MapValueConverter<Pattern, String> converter = new MapValueConverter<Pattern, String>(INSTANCE, StringValueConverter.INSTANCE);

    @Test
    public void testRoundtrip() {
        final String patterns = "(.*).js$: *.js,\n" + ((("(.*).css$: *.css,\n" + "(.*).jpg$: *.jpg,\n") + "(.*).jpeg$: *.jpeg,\n") + "(.*).png$: *.png");
        Assert.assertEquals(patterns, converter.toString(converter.convert(patterns)));
    }

    @Test
    public void testToStringNull() throws Exception {
        Assert.assertNull(converter.toString(null));
    }
}


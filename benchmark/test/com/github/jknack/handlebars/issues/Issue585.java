package com.github.jknack.handlebars.issues;


import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class Issue585 {
    @Test
    public void shouldReadTemplateUsingProvidedCharset() throws Exception {
        Assert.assertArrayEquals(new byte[]{ 63, 63, 63, 63, 63, 63, 63, 63, 63, 44, 32, 63, 63, 63, 63, 63, 63 }, bytes(StandardCharsets.US_ASCII));
        Assert.assertArrayEquals(new byte[]{ -20, -124, -72, -22, -77, -124, -20, -107, -68, 44, 32, -20, -107, -120, -21, -123, -107 }, bytes(StandardCharsets.UTF_8));
    }
}


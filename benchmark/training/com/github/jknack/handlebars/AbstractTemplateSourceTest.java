package com.github.jknack.handlebars;


import com.github.jknack.handlebars.io.StringTemplateSource;
import org.junit.Assert;
import org.junit.Test;


public class AbstractTemplateSourceTest {
    @Test
    public void testHashCode() {
        Assert.assertEquals(new StringTemplateSource("file", "abc").hashCode(), new StringTemplateSource("file", "abc").hashCode());
    }

    @Test
    public void testEqualsSameRef() {
        StringTemplateSource source1 = new StringTemplateSource("file", "abc");
        StringTemplateSource source2 = new StringTemplateSource("file", "abc");
        Assert.assertEquals(source1, source1);
        Assert.assertEquals(source1, source2);
    }
}


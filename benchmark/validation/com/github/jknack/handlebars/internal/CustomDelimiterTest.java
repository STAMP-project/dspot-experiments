package com.github.jknack.handlebars.internal;


import com.github.jknack.handlebars.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


public class CustomDelimiterTest extends AbstractTest {
    @Test
    public void block() throws Exception {
        Assert.assertEquals("`*`#test`*`inside`*`/test`*`", compile("{{=`*` `*`=}}`*`#test`*`inside`*`/test`*`").text());
    }

    @Test
    public void partial() throws Exception {
        Assert.assertEquals("^^>test%%", compile("{{=^^ %%=}}^^>test%%", AbstractTest.$(), AbstractTest.$("test", "")).text());
    }

    @Test
    public void variable() throws Exception {
        Assert.assertEquals("+-+test+-+", compile("{{=+-+ +-+=}}+-+test+-+").text());
    }

    @Test
    public void variableUnescaped() throws Exception {
        Assert.assertEquals("+-+&test+-+", compile("{{=+-+ +-+=}}+-+&test+-+").text());
    }

    @Test
    public void tripleVariable() throws Exception {
        Assert.assertEquals("+-+{test}-+-", compile("{{=+-+ -+-=}}+-+{test}-+-").text());
    }
}


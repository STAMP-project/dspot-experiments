package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue200 extends AbstractTest {
    @Test
    public void actualBug() throws IOException {
        Handlebars h = newHandlebars();
        h.registerHelper("replaceHelperTest", new Helper<String>() {
            @Override
            public Object apply(final String text, final Options options) {
                return "foo";
            }
        });
        h.registerHelpers(new DynamicHelperExample());
        Template t = h.compileInline("hello world: {{replaceHelperTest \"foobar\"}}");
        Assert.assertEquals("hello world: bar", t.apply(null));
    }
}


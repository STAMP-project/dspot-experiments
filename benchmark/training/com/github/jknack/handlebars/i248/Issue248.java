package com.github.jknack.handlebars.i248;


import EscapingStrategy.CSV;
import EscapingStrategy.JS;
import EscapingStrategy.XML;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class Issue248 {
    @Test
    public void defaultEscape() throws IOException {
        Template template = new Handlebars().compileInline("{{this}}");
        Assert.assertEquals("&quot;Escaping&quot;", template.apply("\"Escaping\""));
        Assert.assertEquals("", template.apply(null));
        Assert.assertEquals("", template.apply(""));
    }

    @Test
    public void csvEscape() throws IOException {
        Template template = new Handlebars().with(CSV).compileInline("{{this}}");
        Assert.assertEquals("\"\"\"Escaping\"\"\"", template.apply("\"Escaping\""));
        Assert.assertEquals("", template.apply(null));
        Assert.assertEquals("", template.apply(""));
    }

    @Test
    public void xmlEscape() throws IOException {
        Template template = new Handlebars().with(XML).compileInline("{{this}}");
        Assert.assertEquals("&lt;xml&gt;", template.apply("<xml>"));
        Assert.assertEquals("", template.apply(null));
        Assert.assertEquals("", template.apply(""));
    }

    @Test
    public void jsEscape() throws IOException {
        Template template = new Handlebars().with(JS).compileInline("{{this}}");
        Assert.assertEquals("\\\'javascript\\\'", template.apply("'javascript'"));
        Assert.assertEquals("", template.apply(null));
        Assert.assertEquals("", template.apply(""));
    }
}


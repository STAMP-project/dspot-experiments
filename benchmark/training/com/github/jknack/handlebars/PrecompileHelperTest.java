package com.github.jknack.handlebars;


import com.github.jknack.handlebars.io.URLTemplateLoader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PrecompileHelperTest {
    URLTemplateLoader loader = new MapTemplateLoader().define("input", "Hi {{this}}!").define("root", "{{> partial/child}}").define("partial/child", "CHILD!!!");

    Handlebars handlebars = new Handlebars(loader);

    private String wrapper;

    public PrecompileHelperTest(final String wrapper) {
        this.wrapper = wrapper;
    }

    @Test
    public void precompile() throws IOException {
        String js = handlebars.compileInline((("{{precompile \"input\" wrapper=\"" + (wrapper)) + "\"}}")).apply("Handlebar.js");
        InputStream in = getClass().getResourceAsStream((("/" + (wrapper)) + ".precompiled.js"));
        Assert.assertEquals(IOUtils.toString(in).replace("\r\n", "\n"), js.replace("\r\n", "\n"));
        in.close();
    }

    @Test
    public void precompileWithPartial() throws IOException {
        String js = handlebars.compileInline("{{precompile \"root\"}}").apply("Handlebar.js");
        InputStream in = getClass().getResourceAsStream("/partial.precompiled.js");
        Assert.assertEquals(IOUtils.toString(in).replace("\r\n", "\n"), js.replace("\r\n", "\n"));
        in.close();
    }
}


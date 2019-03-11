package com.github.jknack.handlebars;


import com.github.jknack.handlebars.custom.Blog;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ReflectiveHelperTest extends AbstractTest {
    @Test
    public void testHelperA() throws IOException {
        shouldCompileTo("{{helperA}}", AbstractTest.$, "helperA");
    }

    @Test
    public void testHelperAWithContext() throws IOException {
        shouldCompileTo("{{helperAWithContext}}", AbstractTest.$(), "helperAWithContext");
    }

    @Test
    public void testHelperAWithContextAndOptions() throws IOException {
        shouldCompileTo("{{helperAWithContextAndOptions}}", AbstractTest.$(), "helperAWithContextAndOptions");
    }

    @Test
    public void testHelperAWithOptions() throws IOException {
        shouldCompileTo("{{helperAWithOptions}}", AbstractTest.$, "helperAWithOptions");
    }

    @Test
    public void testHelperWithParams() throws IOException {
        shouldCompileTo("{{helperWithParams \"string\" true 4}}", AbstractTest.$, "helperWithParams:string:true:4");
    }

    @Test
    public void testHelperWithParamsAndOptions() throws IOException {
        shouldCompileTo("{{helperWithParamsAndOptions \"string\" true 4}}", AbstractTest.$, "helperWithParamsAndOptions:string:true:4");
    }

    @Test
    public void testBlog() throws IOException {
        shouldCompileTo("{{blog this}}", new Blog("title", "body"), "blog:title");
    }

    @Test
    public void testNullBlog() throws IOException {
        shouldCompileTo("{{nullBlog this}}", null, "blog:null");
    }

    @Test
    public void testBlogTitle() throws IOException {
        shouldCompileTo("{{blogTitle this title}}", new Blog("awesome!", "body"), "blog:awesome!");
    }

    @Test
    public void testParams() throws IOException {
        shouldCompileTo("{{params this l d f c b s}}", AbstractTest.$("l", 1L, "d", 2.0, "f", 3.0F, "c", '4', "b", ((byte) (5)), "s", ((short) (6))), "1, 2.0, 3.0, 4, 5, 6");
    }

    @Test
    public void testRuntimeException() throws IOException {
        try {
            shouldCompileTo("{{runtimeException}}", AbstractTest.$, "");
            Assert.fail("A runtime exception is expeced");
        } catch (HandlebarsException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testCheckedException() throws IOException {
        try {
            shouldCompileTo("{{checkedException}}", AbstractTest.$, "");
            Assert.fail("A checked exception is expeced");
        } catch (HandlebarsException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void testIOException() throws IOException {
        try {
            shouldCompileTo("{{ioException}}", AbstractTest.$, "");
            Assert.fail("A io exception is expeced");
        } catch (HandlebarsException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IOException));
        }
    }
}


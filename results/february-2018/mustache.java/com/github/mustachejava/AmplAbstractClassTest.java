package com.github.mustachejava;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplAbstractClassTest {
    abstract static class AbstractFoo {
        public abstract String getValue();
    }

    static class Foo extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Foo";
        }
    }

    static class Bar extends AmplAbstractClassTest.AbstractFoo {
        @Override
        public String getValue() {
            return "I am Bar";
        }
    }

    static class Container {
        public final AmplAbstractClassTest.AbstractFoo foo;

        public Container(final AmplAbstractClassTest.AbstractFoo foo) {
            this.foo = foo;
        }
    }

    @Test(timeout = 10000)
    public void testAbstractClassNoDots() throws IOException {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClassNoDots__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClassNoDots__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClassNoDots__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClassNoDots__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        // AssertGenerator add assertion
        Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        // AssertGenerator add assertion
        Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator create local variable with return value of invocation
        Object o_testAbstractClassNoDots__18 = scopes.put("containers", containers);
        // AssertGenerator add assertion
        Assert.assertNull(o_testAbstractClassNoDots__18);
        // AssertGenerator create local variable with return value of invocation
        Writer o_testAbstractClassNoDots__19 = mustache.execute(writer, scopes);
        // AssertGenerator add assertion
        Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClassNoDots__19).getEncoding());
        writer.flush();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClassNoDots__3);
        // AssertGenerator add assertion
        Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        // AssertGenerator add assertion
        Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClassNoDots__19).getEncoding());
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClassNoDots__6);
        // AssertGenerator add assertion
        Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        Assert.assertNull(o_testAbstractClassNoDots__18);
    }

    @Test(timeout = 10000)
    public void testAbstractClass() throws IOException {
        final List<AmplAbstractClassTest.Container> containers = new ArrayList<>();
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClass__3 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Foo()));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClass__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClass__6 = containers.add(new AmplAbstractClassTest.Container(new AmplAbstractClassTest.Bar()));
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClass__6);
        HashMap<String, Object> scopes = new HashMap<>();
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();
        // AssertGenerator add assertion
        Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        Mustache mustache = mf.compile(new StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        // AssertGenerator add assertion
        Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator create local variable with return value of invocation
        Object o_testAbstractClass__18 = scopes.put("containers", containers);
        // AssertGenerator add assertion
        Assert.assertNull(o_testAbstractClass__18);
        // AssertGenerator create local variable with return value of invocation
        Writer o_testAbstractClass__19 = mustache.execute(writer, scopes);
        // AssertGenerator add assertion
        Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass__19).getEncoding());
        writer.flush();
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClass__6);
        // AssertGenerator add assertion
        Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass__19).getEncoding());
        // AssertGenerator add assertion
        Assert.assertTrue(o_testAbstractClass__3);
        // AssertGenerator add assertion
        Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        // AssertGenerator add assertion
        Assert.assertNull(o_testAbstractClass__18);
        // AssertGenerator add assertion
        Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
    }
}


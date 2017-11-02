package com.github.mustachejava;


public class AmplAbstractClassTest {
    abstract static class AbstractFoo {
        public abstract java.lang.String getValue();
    }

    static class Foo extends com.github.mustachejava.AmplAbstractClassTest.AbstractFoo {
        @java.lang.Override
        public java.lang.String getValue() {
            return "I am Foo";
        }
    }

    static class Bar extends com.github.mustachejava.AmplAbstractClassTest.AbstractFoo {
        @java.lang.Override
        public java.lang.String getValue() {
            return "I am Bar";
        }
    }

    static class Container {
        public final com.github.mustachejava.AmplAbstractClassTest.AbstractFoo foo;

        public Container(final com.github.mustachejava.AmplAbstractClassTest.AbstractFoo foo) {
            this.foo = foo;
        }
    }

    @org.junit.Test
    public void testAbstractClass() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClassNoDots__3 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_testAbstractClassNoDots__6 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots__6);
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator create local variable with return value of invocation
        java.lang.Object o_testAbstractClassNoDots__18 = scopes.put("containers", containers);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testAbstractClassNoDots__18);
        // AssertGenerator create local variable with return value of invocation
        java.io.Writer o_testAbstractClassNoDots__19 = mustache.execute(writer, scopes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClassNoDots__19).getEncoding());
        writer.flush();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots__3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClassNoDots__19).getEncoding());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots__6);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testAbstractClassNoDots__18);
    }
}


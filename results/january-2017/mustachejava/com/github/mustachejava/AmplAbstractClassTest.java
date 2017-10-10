

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

    @org.junit.Test
    public void testAbstractClassNoDots() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test
    public void testAbstractClass_literalMutation6() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test
    public void testAbstractClass_literalMutation8_literalMutation77_literalMutation681() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 1000)
    public void testAbstractClass_add3_literalMutation38_literalMutation326_failAssert19() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{xoo.value}} {{{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
            // AssertGenerator replace invocation
            java.lang.Object o_testAbstractClass_add3__18 = // MethodCallAdder
scopes.put("containers", containers);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testAbstractClass_add3__18);
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_add3_literalMutation38_literalMutation326 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    @org.junit.Test
    public void testAbstractClassNoDots_literalMutation764() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    @org.junit.Test(timeout = 1000)
    public void testAbstractClassNoDots_add761_literalMutation794() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        // AssertGenerator replace invocation
        java.lang.Object o_testAbstractClassNoDots_add761__18 = // MethodCallAdder
scopes.put("containers", containers);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testAbstractClassNoDots_add761__18);
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    @org.junit.Test(timeout = 1000)
    public void testAbstractClassNoDots_add760_literalMutation784_add983() throws java.io.IOException {
        final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
        // AssertGenerator replace invocation
        boolean o_testAbstractClassNoDots_add760_literalMutation784_add983__3 = // MethodCallAdder
containers.add(new com.github.mustachejava.AbstractClassTest.Container(new com.github.mustachejava.AbstractClassTest.Foo()));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots_add760_literalMutation784_add983__3);
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
        // AssertGenerator replace invocation
        boolean o_testAbstractClassNoDots_add760__6 = // MethodCallAdder
containers.add(new com.github.mustachejava.AbstractClassTest.Container(new com.github.mustachejava.AbstractClassTest.Bar()));
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAbstractClassNoDots_add760__6);
        containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
        java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
        java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)mustache).getName(), "example");
        scopes.put("containers", containers);
        mustache.execute(writer, scopes);
        writer.flush();
    }
}


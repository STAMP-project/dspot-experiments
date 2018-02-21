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

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_literalMutationString6_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/cont}ainers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString6 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd45_failAssert8() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg2_10 = -845241424;
            int __DSPOT_arg1_9 = 831880648;
            java.lang.String __DSPOT_arg0_8 = "vg[?i!rb0/|]6^FT)-ef";
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            // StatementAdd: generate variable from return value
            java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.write(__DSPOT_arg0_8, __DSPOT_arg1_9, __DSPOT_arg2_10);
            org.junit.Assert.fail("testAbstractClass_sd45 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_literalMutationString5_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {*/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString5 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd34 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd34_failAssert5_literalMutationString371_failAssert2() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#contaners}} {{foo.value}} {{/containers}}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.notify();
                org.junit.Assert.fail("testAbstractClass_sd34 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClass_sd34_failAssert5_literalMutationString371 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd36 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd36_failAssert7_literalMutationString431() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd36_failAssert7_literalMutationString431__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd36_failAssert7_literalMutationString431__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd36_failAssert7_literalMutationString431__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd36_failAssert7_literalMutationString431__8);
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{%foo.value}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd36_failAssert7_literalMutationString431__23 = mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd36_failAssert7_literalMutationString431__23).getEncoding());
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.toString();
            org.junit.Assert.fail("testAbstractClass_sd36 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_literalMutationString6_failAssert1_literalMutationString169() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__8);
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value} {{/cont}ainers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString6 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd33 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd33_failAssert4_literalMutationString338_failAssert5() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.hashCode();
                org.junit.Assert.fail("testAbstractClass_sd33 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClass_sd33_failAssert4_literalMutationString338 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd33 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd33_failAssert4_literalMutationString336() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd33_failAssert4_literalMutationString336__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd33_failAssert4_literalMutationString336__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd33_failAssert4_literalMutationString336__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd33_failAssert4_literalMutationString336__8);
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.val(ue}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd33_failAssert4_literalMutationString336__23 = mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd33_failAssert4_literalMutationString336__23).getEncoding());
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClass_sd33 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd36 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd36_failAssert7_literalMutationString429() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd36_failAssert7_literalMutationString429__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd36_failAssert7_literalMutationString429__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd36_failAssert7_literalMutationString429__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd36_failAssert7_literalMutationString429__8);
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd36_failAssert7_literalMutationString429__23 = mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd36_failAssert7_literalMutationString429__23).getEncoding());
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.toString();
            org.junit.Assert.fail("testAbstractClass_sd36 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd32 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd32_failAssert3_literalMutationString264() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd32_failAssert3_literalMutationString264__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd32_failAssert3_literalMutationString264__5);
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd32_failAssert3_literalMutationString264__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testAbstractClass_sd32_failAssert3_literalMutationString264__8);
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containeCrs", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd32_failAssert3_literalMutationString264__23 = mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd32_failAssert3_literalMutationString264__23).getEncoding());
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.getClass();
            org.junit.Assert.fail("testAbstractClass_sd32 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd32 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd32_failAssert3_literalMutationString266_literalMutationString2420_failAssert9() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd32_failAssert3_literalMutationString266__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd32_failAssert3_literalMutationString266__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value} {{/containers}}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("conta/ners", containers);
                // AssertGenerator create local variable with return value of invocation
                java.io.Writer o_testAbstractClass_sd32_failAssert3_literalMutationString266__23 = mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.getClass();
                org.junit.Assert.fail("testAbstractClass_sd32 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClass_sd32_failAssert3_literalMutationString266_literalMutationString2420 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd35 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd35_failAssert6_literalMutationString400_failAssert7_literalMutationString10035() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd35_failAssert6_literalMutationString400_failAssert7_literalMutationString10035__7 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testAbstractClass_sd35_failAssert6_literalMutationString400_failAssert7_literalMutationString10035__7);
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd35_failAssert6_literalMutationString400_failAssert7_literalMutationString10035__10 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testAbstractClass_sd35_failAssert6_literalMutationString400_failAssert7_literalMutationString10035__10);
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/cEontainers}}"), "");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.notifyAll();
                org.junit.Assert.fail("testAbstractClass_sd35 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClass_sd35_failAssert6_literalMutationString400 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd32 */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd32_failAssert3_sd318 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd32_failAssert3_sd318_literalMutationString6497() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd32_failAssert3_sd318__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd32_failAssert3_sd318__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader(""), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd32_failAssert3_sd318__23 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: generate variable from return value
            java.lang.Class<?> __DSPOT_invoc_25 = // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.getClass();
            org.junit.Assert.fail("testAbstractClass_sd32 should have thrown NullPointerException");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_25.getSigners();
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd35 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd35_failAssert6_literalMutationString404_literalMutationString7236() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd35_failAssert6_literalMutationString404__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd35_failAssert6_literalMutationString404__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "z@l%MFZ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("z@l%MFZ", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("containes", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd35_failAssert6_literalMutationString404__23 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.notifyAll();
            org.junit.Assert.fail("testAbstractClass_sd35 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd45 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd45_failAssert8_literalMutationString524_literalMutationString5935() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg2_10 = -845241424;
            int __DSPOT_arg1_9 = 831880648;
            java.lang.String __DSPOT_arg0_8 = "page1.txt";
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationString524__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationString524__11 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testAbstractClass_sd45_failAssert8_literalMutationString524__23 = scopes.put("L!4$#O{/vN", containers);
            // StatementAdd: generate variable from return value
            java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.write(__DSPOT_arg0_8, __DSPOT_arg1_9, __DSPOT_arg2_10);
            org.junit.Assert.fail("testAbstractClass_sd45 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd45 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd45_failAssert8_literalMutationString543_literalMutationString8235() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg2_10 = -845241424;
            int __DSPOT_arg1_9 = 831880648;
            java.lang.String __DSPOT_arg0_8 = "vg[?i!rb0/|]6^FT)-ef";
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationString543__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationString543__11 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("w=(r?tLP,4K.t`WuNzgim?VySGWr_;|NEoFXwu&`Nm=2:"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testAbstractClass_sd45_failAssert8_literalMutationString543__23 = scopes.put("cntainers", containers);
            // StatementAdd: generate variable from return value
            java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.write(__DSPOT_arg0_8, __DSPOT_arg1_9, __DSPOT_arg2_10);
            org.junit.Assert.fail("testAbstractClass_sd45 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_literalMutationString5_failAssert0_literalMutationString135_literalMutationString3856() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString5_failAssert0_literalMutationString135__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString5_failAssert0_literalMutationString135__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {*/containers}}"), "exabmple");
            scopes.put("", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString5 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_literalMutationString6_failAssert1_literalMutationString169_sd2527() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_literalMutationString6_failAssert1_literalMutationString169__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value} {{/cont}ainers}}"), "example");
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_32 = scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClass_literalMutationString6 should have thrown MustacheException");
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_32.notify();
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd31 */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd31_failAssert2_sd245 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd31_failAssert2_sd245_literalMutationString2028_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                char[] __DSPOT_arg0_38 = new char[]{ 't', ' ', 'l' };
                java.lang.Object __DSPOT_arg0_0 = new java.lang.Object();
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd31_failAssert2_sd245__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                // AssertGenerator create local variable with return value of invocation
                boolean o_testAbstractClass_sd31_failAssert2_sd245__11 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#c=ontainers}} {{foo.value}} {{/containers}}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                // StatementAdd: generate variable from return value
                java.io.Writer __DSPOT_invoc_25 = mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.equals(__DSPOT_arg0_0);
                org.junit.Assert.fail("testAbstractClass_sd31 should have thrown NullPointerException");
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_25.write(__DSPOT_arg0_38);
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClass_sd31_failAssert2_sd245_literalMutationString2028 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd33 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd33_failAssert4_literalMutationString353_add9694() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd33_failAssert4_literalMutationString353__5 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd33_failAssert4_literalMutationString353__8 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // StatementAdd: generate variable from return value
            java.lang.Object __DSPOT_invoc_18 = scopes.put("2&pb?56TtK", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd33_failAssert4_literalMutationString353_add9694__27 = // MethodCallAdder
            mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd33_failAssert4_literalMutationString353_add9694__27).getEncoding());
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd33_failAssert4_literalMutationString353__23 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.hashCode();
            org.junit.Assert.fail("testAbstractClass_sd33 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClass_sd45 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClass_sd45_failAssert8_literalMutationNumber513_add7492() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg2_10 = // TestDataMutator on numbers
            -845241425;
            int __DSPOT_arg1_9 = 831880648;
            java.lang.String __DSPOT_arg0_8 = "vg[?i!rb0/|]6^FT)-ef";
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationNumber513__9 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            // AssertGenerator create local variable with return value of invocation
            boolean o_testAbstractClass_sd45_failAssert8_literalMutationNumber513__12 = containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{foo.value}} {{/containers}}"), "example");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("example", ((com.github.mustachejava.codes.DefaultMustache)mustache).getName());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)mustache).isRecursive());
            // AssertGenerator create local variable with return value of invocation
            java.lang.Object o_testAbstractClass_sd45_failAssert8_literalMutationNumber513__24 = scopes.put("containers", containers);
            // AssertGenerator create local variable with return value of invocation
            java.io.Writer o_testAbstractClass_sd45_failAssert8_literalMutationNumber513_add7492__31 = // MethodCallAdder
            mustache.execute(writer, scopes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("ASCII", ((java.io.OutputStreamWriter)o_testAbstractClass_sd45_failAssert8_literalMutationNumber513_add7492__31).getEncoding());
            // StatementAdd: generate variable from return value
            java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.write(__DSPOT_arg0_8, __DSPOT_arg1_9, __DSPOT_arg2_10);
            org.junit.Assert.fail("testAbstractClass_sd45 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_literalMutationString10766_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {W#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            mustache.execute(writer, scopes);
            writer.flush();
            org.junit.Assert.fail("testAbstractClassNoDots_literalMutationString10766 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_sd10806_failAssert8() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg2_3502 = -222683716;
            int __DSPOT_arg1_3501 = -53217313;
            java.lang.String __DSPOT_arg0_3500 = "UT*#^Vfgc[w.ncJtxQ&C";
            final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
            containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
            java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
            java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
            scopes.put("containers", containers);
            // StatementAdd: generate variable from return value
            java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
            writer.flush();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.write(__DSPOT_arg0_3500, __DSPOT_arg1_3501, __DSPOT_arg2_3502);
            org.junit.Assert.fail("testAbstractClassNoDots_sd10806 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots_sd10806 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_sd10806_failAssert8_literalMutationString11291_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int __DSPOT_arg2_3502 = -222683716;
                int __DSPOT_arg1_3501 = -53217313;
                java.lang.String __DSPOT_arg0_3500 = "UT*#^Vfgc[w.ncJtxQ&C";
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{!#containers}} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                scopes.put("containers", containers);
                // StatementAdd: generate variable from return value
                java.io.Writer __DSPOT_invoc_19 = mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_19.write(__DSPOT_arg0_3500, __DSPOT_arg1_3501, __DSPOT_arg2_3502);
                org.junit.Assert.fail("testAbstractClassNoDots_sd10806 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_sd10806_failAssert8_literalMutationString11291 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots_sd10795 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_sd10795_failAssert5_literalMutationString11133_failAssert4() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containersX} {{#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.notify();
                org.junit.Assert.fail("testAbstractClassNoDots_sd10795 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_sd10795_failAssert5_literalMutationString11133 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots_sd10794 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_sd10794_failAssert4_literalMutationString11100_failAssert6() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {#foo}}{{value}}{{/foo}} {{/containers}}"), "example");
                // StatementAdd: generate variable from return value
                java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                mustache.execute(writer, scopes);
                writer.flush();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_18.hashCode();
                org.junit.Assert.fail("testAbstractClassNoDots_sd10794 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_sd10794_failAssert4_literalMutationString11100 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots */
    /* amplification of com.github.mustachejava.AbstractClassTest#testAbstractClassNoDots_sd10795 */
    @org.junit.Test(timeout = 10000)
    public void testAbstractClassNoDots_sd10795_failAssert5_add11149_failAssert0_literalMutationString11722_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    final java.util.List<com.github.mustachejava.AmplAbstractClassTest.Container> containers = new java.util.ArrayList<>();
                    containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Foo()));
                    containers.add(new com.github.mustachejava.AmplAbstractClassTest.Container(new com.github.mustachejava.AmplAbstractClassTest.Bar()));
                    java.util.HashMap<java.lang.String, java.lang.Object> scopes = new java.util.HashMap<>();
                    java.io.Writer writer = new java.io.OutputStreamWriter(java.lang.System.out);
                    com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                    com.github.mustachejava.Mustache mustache = mf.compile(new java.io.StringReader("{{#containers}} {{#foo}}{{value}}{{/foo}} {{/tontainers}}"), "example");
                    // MethodCallAdder
                    scopes.put("containers", containers);
                    // StatementAdd: generate variable from return value
                    java.lang.Object __DSPOT_invoc_18 = scopes.put("containers", containers);
                    mustache.execute(writer, scopes);
                    writer.flush();
                    // StatementAdd: add invocation of a method
                    __DSPOT_invoc_18.notify();
                    org.junit.Assert.fail("testAbstractClassNoDots_sd10795 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testAbstractClassNoDots_sd10795_failAssert5_add11149 should have thrown IllegalMonitorStateException");
            } catch (java.lang.IllegalMonitorStateException eee) {
            }
            org.junit.Assert.fail("testAbstractClassNoDots_sd10795_failAssert5_add11149_failAssert0_literalMutationString11722 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}


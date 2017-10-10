

package com.github.mustachejava.util;


public class AmplDecoratedCollectionTest {
    @org.junit.Test
    public void testIndexLastFirst() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
        }).flush();
        org.junit.Assert.assertEquals("0: first: First\n1: Second\n2: Third\n3: last: Last\n", sw.toString());
    }

    @org.junit.Test
    public void testObjectHandler() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            @java.lang.Override
            public java.lang.Object coerce(java.lang.Object object) {
                if (object instanceof java.util.Collection) {
                    return new com.github.mustachejava.util.DecoratedCollection(((java.util.Collection) (object)));
                }
                return super.coerce(object);
            }
        });
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
        }).flush();
        org.junit.Assert.assertEquals("0: first: First\n1: Second\n2: Third\n3: last: Last\n", sw.toString());
    }

    @org.junit.Test
    public void testArrayOutput() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "test");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
        }).flush();
        org.junit.Assert.assertEquals("[\"one\", \"two\", \"three\"]", sw.toString());
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 1000)
    public void testArrayOutput_cf14_failAssert12_literalMutation92() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("\"Hello\" &amp world!&#10;"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_2 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2);
            // StatementAdderMethod cloned existing statement
            vc_2.iterator();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_cf14 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 1000)
    public void testArrayOutput_cf12_failAssert11_literalMutation86_literalMutation155() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader(""), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_0 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_0);
            // StatementAdderMethod cloned existing statement
            vc_0.size();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_cf12 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test(timeout = 1000)
    public void testIndexLastFirst_cf245_failAssert12_literalMutation323() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("\"Hello\" &amp world!&#10;"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_6 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6);
            // StatementAdderMethod cloned existing statement
            vc_6.iterator();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_cf245 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 1000)
    public void testObjectHandler_cf531_failAssert11_literalMutation616() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @java.lang.Override
                public java.lang.Object coerce(java.lang.Object object) {
                    if (object instanceof java.util.Collection) {
                        return new com.github.mustachejava.util.DecoratedCollection(((java.util.Collection) (object)));
                    }
                    return super.coerce(object);
                }
            });
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("\"Hello\" &amp world!&#10;"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_8 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8);
            // StatementAdderMethod cloned existing statement
            vc_8.size();
            // MethodAssertGenerator build local variable
            Object o_31_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_cf531 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 1000)
    public void testObjectHandler_cf533_failAssert12_literalMutation624_literalMutation794() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @java.lang.Override
                public java.lang.Object coerce(java.lang.Object object) {
                    if (object instanceof java.util.Collection) {
                        return new com.github.mustachejava.util.DecoratedCollection(((java.util.Collection) (object)));
                    }
                    return super.coerce(object);
                }
            });
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader(""), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_10 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_10);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_10);
            // StatementAdderMethod cloned existing statement
            vc_10.iterator();
            // MethodAssertGenerator build local variable
            Object o_31_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_cf533 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


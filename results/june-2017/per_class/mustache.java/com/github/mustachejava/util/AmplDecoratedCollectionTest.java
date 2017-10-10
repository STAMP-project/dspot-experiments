

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
    @org.junit.Test
    public void testArrayOutput_literalMutation4_failAssert3() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("*{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_16_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_literalMutation4 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test
    public void testArrayOutput_literalMutation8() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "tst");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "tst");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
        }).flush();
        org.junit.Assert.assertEquals("[\"one\", \"two\", \"three\"]", sw.toString());
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test
    public void testArrayOutput_literalMutation7_literalMutation35_failAssert24() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{2#last}}]{{/last}}{{/test}}"), "");
            // MethodAssertGenerator build local variable
            Object o_6_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_literalMutation7_literalMutation35 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 10000)
    public void testArrayOutput_cf29_failAssert22_literalMutation236_failAssert78() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{/test}}"), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_2 = (com.github.mustachejava.util.DecoratedCollection)null;
                // StatementAdderMethod cloned existing statement
                vc_2.iterator();
                // MethodAssertGenerator build local variable
                Object o_20_0 = sw.toString();
                org.junit.Assert.fail("testArrayOutput_cf29 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testArrayOutput_cf29_failAssert22_literalMutation236 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 10000)
    public void testArrayOutput_cf29_failAssert22_add231() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            // MethodCallAdder
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
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
            org.junit.Assert.fail("testArrayOutput_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 10000)
    public void testArrayOutput_cf29_failAssert22_literalMutation233() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader(""), "test");
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
            org.junit.Assert.fail("testArrayOutput_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test
    public void testArrayOutput_literalMutation11_literalMutation153_failAssert105() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first&}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "telst");
            // MethodAssertGenerator build local variable
            Object o_6_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_literalMutation11_literalMutation153 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 10000)
    public void testArrayOutput_cf29_failAssert22_literalMutation234_failAssert38() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#first}}[{{/first}e}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_2 = (com.github.mustachejava.util.DecoratedCollection)null;
                // StatementAdderMethod cloned existing statement
                vc_2.iterator();
                // MethodAssertGenerator build local variable
                Object o_20_0 = sw.toString();
                org.junit.Assert.fail("testArrayOutput_cf29 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testArrayOutput_cf29_failAssert22_literalMutation234 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test
    public void testArrayOutput_literalMutation10_literalMutation123_failAssert82() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("p]QM-k,I]-r8//GGUV@1wly$),bA%.UJum&)<4oK[>Va&1`i[aMe!@y;s?/OCDfviVTx>DODA!L#vK5WR{oE1L&q_{{"), "@est");
            // MethodAssertGenerator build local variable
            Object o_6_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testArrayOutput_literalMutation10_literalMutation123 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testArrayOutput */
    @org.junit.Test(timeout = 10000)
    public void testArrayOutput_cf27_failAssert21_literalMutation213_literalMutation1058_failAssert11() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{#firs7t}}[{{/first}}{{^first}}, {{/first}}\"{{value}}\"{{#last}}]{{/last}}{{/test}}"), "tst");
                // MethodAssertGenerator build local variable
                Object o_8_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
                // MethodAssertGenerator build local variable
                Object o_10_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("one", "two", "three"));
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_0 = (com.github.mustachejava.util.DecoratedCollection)null;
                // MethodAssertGenerator build local variable
                Object o_24_0 = vc_0;
                // StatementAdderMethod cloned existing statement
                vc_0.size();
                // MethodAssertGenerator build local variable
                Object o_20_0 = sw.toString();
                org.junit.Assert.fail("testArrayOutput_cf27 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testArrayOutput_cf27_failAssert21_literalMutation213_literalMutation1058 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test
    public void testIndexLastFirst_literalMutation2261_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#virst}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_16_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_literalMutation2261 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test
    public void testIndexLastFirst_literalMutation2267() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
        }).flush();
        org.junit.Assert.assertEquals("0: first: First\n1: Second\n2: Third\n3: last: Last\n", sw.toString());
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test(timeout = 10000)
    public void testIndexLastFirst_cf2293_failAssert27_literalMutation2563() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader(""), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_190 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_190);
            // StatementAdderMethod cloned existing statement
            vc_190.iterator();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_cf2293 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test
    public void testIndexLastFirst_literalMutation2267_literalMutation2335_failAssert29() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}8}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "");
            // MethodAssertGenerator build local variable
            Object o_6_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_literalMutation2267_literalMutation2335 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test(timeout = 10000)
    public void testIndexLastFirst_cf2293_failAssert27_literalMutation2567() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("^C#(v*2A$;:atF^Ng?.@K](!{/rp09KB8>eN$@QU6J+jq#3QX@M6)$Xa ^f&@[+%^=,E!RQ;E@SX@54/GI=u{ai$-r"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_190 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_190);
            // StatementAdderMethod cloned existing statement
            vc_190.iterator();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_cf2293 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testIndexLastFirst */
    @org.junit.Test(timeout = 10000)
    public void testIndexLastFirst_cf2291_failAssert26_add2529() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            // MethodCallAdder
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = new com.github.mustachejava.util.DecoratedCollection(java.util.Arrays.asList("First", "Second", "Third", "Last"));
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_188 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_188);
            // StatementAdderMethod cloned existing statement
            vc_188.size();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("testIndexLastFirst_cf2291 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test
    public void testObjectHandler_literalMutation4875_failAssert5() throws java.io.IOException {
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
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#la=st}}last: {{/last}}{{value}}\n{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_27_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_literalMutation4875 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test
    public void testObjectHandler_literalMutation4873_failAssert3() throws java.io.IOException {
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
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#lastG}last: {{/last}}{{value}}\n{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_27_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_literalMutation4873 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test
    public void testObjectHandler_literalMutation4878() throws java.io.IOException {
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
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "-Sdt");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "-Sdt");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
        }).flush();
        org.junit.Assert.assertEquals("0: first: First\n1: Second\n2: Third\n3: last: Last\n", sw.toString());
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4901_failAssert25_literalMutation5151_failAssert27() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{M#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_400 = (com.github.mustachejava.util.DecoratedCollection)null;
                // StatementAdderMethod cloned existing statement
                vc_400.size();
                // MethodAssertGenerator build local variable
                Object o_31_0 = sw.toString();
                org.junit.Assert.fail("testObjectHandler_cf4901 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testObjectHandler_cf4901_failAssert25_literalMutation5151 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4901_failAssert25_literalMutation5153_failAssert96() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}\n{{/test}}"), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_400 = (com.github.mustachejava.util.DecoratedCollection)null;
                // StatementAdderMethod cloned existing statement
                vc_400.size();
                // MethodAssertGenerator build local variable
                Object o_31_0 = sw.toString();
                org.junit.Assert.fail("testObjectHandler_cf4901 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testObjectHandler_cf4901_failAssert25_literalMutation5153 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4903_failAssert26_add5180() throws java.io.IOException {
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
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            // MethodCallAdder
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_402 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_402);
            // StatementAdderMethod cloned existing statement
            vc_402.iterator();
            // MethodAssertGenerator build local variable
            Object o_31_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_cf4903 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test
    public void testObjectHandler_literalMutation4876_literalMutation4910_failAssert100() throws java.io.IOException {
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
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/fiQrst}}{{#last}}last: {{/last}}{{value}}\n{{/test}}"), "");
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_31_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_literalMutation4876_literalMutation4910 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4903_failAssert26_literalMutation5183() throws java.io.IOException {
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
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
            }).flush();
            // StatementAdderOnAssert create null value
            com.github.mustachejava.util.DecoratedCollection vc_402 = (com.github.mustachejava.util.DecoratedCollection)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_402);
            // StatementAdderMethod cloned existing statement
            vc_402.iterator();
            // MethodAssertGenerator build local variable
            Object o_31_0 = sw.toString();
            org.junit.Assert.fail("testObjectHandler_cf4903 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4903_failAssert26_literalMutation5190_literalMutation6281_failAssert10() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/clast}}{{value}}\n{{/test}}"), "t%st");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
                // MethodAssertGenerator build local variable
                Object o_22_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_402 = (com.github.mustachejava.util.DecoratedCollection)null;
                // MethodAssertGenerator build local variable
                Object o_35_0 = vc_402;
                // StatementAdderMethod cloned existing statement
                vc_402.iterator();
                // MethodAssertGenerator build local variable
                Object o_31_0 = sw.toString();
                org.junit.Assert.fail("testObjectHandler_cf4903 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testObjectHandler_cf4903_failAssert26_literalMutation5190_literalMutation6281 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.util.DecoratedCollectionTest#testObjectHandler */
    @org.junit.Test(timeout = 10000)
    public void testObjectHandler_cf4903_failAssert26_literalMutation5187_literalMutation5831_failAssert0() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{#test}}{{index}}: {{#first}}first: {{/first}}{{#last}}last: {{/last}}{{value}}\n{Q/test}}"), "");
                // MethodAssertGenerator build local variable
                Object o_20_0 = ((com.github.mustachejava.codes.DefaultMustache)test).isRecursive();
                // MethodAssertGenerator build local variable
                Object o_22_0 = ((com.github.mustachejava.codes.DefaultMustache)test).getName();
                java.io.StringWriter sw = new java.io.StringWriter();
                test.execute(sw, new java.lang.Object() {
                    java.util.Collection test = java.util.Arrays.asList("First", "Second", "Third", "Last");
                }).flush();
                // StatementAdderOnAssert create null value
                com.github.mustachejava.util.DecoratedCollection vc_402 = (com.github.mustachejava.util.DecoratedCollection)null;
                // MethodAssertGenerator build local variable
                Object o_35_0 = vc_402;
                // StatementAdderMethod cloned existing statement
                vc_402.iterator();
                // MethodAssertGenerator build local variable
                Object o_31_0 = sw.toString();
                org.junit.Assert.fail("testObjectHandler_cf4903 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testObjectHandler_cf4903_failAssert26_literalMutation5187_literalMutation5831 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}


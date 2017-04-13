

package com.github.mustachejava.inverter;


public class AmplInverterTest extends com.github.mustachejava.inverter.InvertUtils {
    @org.junit.Test
    public void testParser() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile("fdbcli.mustache");
        java.nio.file.Path file = getPath("src/test/resources/fdbcli.txt");
        java.lang.String txt = new java.lang.String(java.nio.file.Files.readAllBytes(file), "UTF-8");
        com.github.mustachejava.util.Node invert = compile.invert(txt);
        java.lang.System.out.println(invert);
    }

    @org.junit.Test
    public void testSimple() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("test {{value}} test"), "test");
        com.github.mustachejava.util.Node invert = test.invert("test value test");
        com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
        node.put("value", com.github.mustachejava.util.NodeValue.value("value"));
        org.junit.Assert.assertEquals(node, invert);
    }

    @org.junit.Test
    public void testIterable() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{#values}}\ntest: {{value}}\n{{/values}}"), "test");
        com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
        com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
        com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
        sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
        com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
        fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
        node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
        org.junit.Assert.assertEquals(node, invert);
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, invert).close();
        java.lang.System.out.println(sw);
    }

    @org.junit.Test
    public void testCollectPoints() throws java.lang.Exception {
        com.github.mustachejava.MustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile(new java.io.StringReader("{{#page}}This is a {{test}}{{/page}}"), java.util.UUID.randomUUID().toString());
        com.github.mustachejava.util.Node node = compile.invert("This is a good day");
        org.junit.Assert.assertNotNull(node);
    }

    @org.junit.Test
    public void testNoNode() throws java.lang.Exception {
        com.github.mustachejava.MustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache compile = dmf.compile(new java.io.StringReader("Using cluster file [^\\n]+\nHello World"), java.util.UUID.randomUUID().toString());
        com.github.mustachejava.util.Node node = compile.invert("Using cluster file `/etc/foundationdb/fdb.cluster\'.\nHello World");
        org.junit.Assert.assertNotNull(node);
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable */
    @org.junit.Test(timeout = 10000)
    public void testIterable_add37() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{#values}}\ntest: {{value}}\n{{/values}}"), "test");
        com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
        com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
        com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testIterable_add37__12 = sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIterable_add37__12);
        com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testIterable_add37__16 = // MethodCallAdder
fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIterable_add37__16);
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testIterable_add37__19 = fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.util.NodeValue)o_testIterable_add37__19).isList());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.util.NodeValue)o_testIterable_add37__19).list());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.util.NodeValue)o_testIterable_add37__19).value(), "fred");
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testIterable_add37__21 = node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIterable_add37__21);
        org.junit.Assert.assertEquals(node, invert);
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, invert).close();
        java.lang.System.out.println(sw);
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable */
    @org.junit.Test
    public void testIterable_literalMutation45_failAssert4() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{#values}}\ntest: {{value}}\n{{/va*lues}}"), "test");
            com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
            com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
            com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
            sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
            fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
            node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, invert).close();
            java.lang.System.out.println(sw);
            org.junit.Assert.fail("testIterable_literalMutation45 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable */
    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable_add39 */
    @org.junit.Test(timeout = 10000)
    public void testIterable_add39_literalMutation73_failAssert20() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{#values}}\ntest: {{value}\n{{/values}}"), "test");
            com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
            com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
            com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add39__12 = sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            // MethodAssertGenerator build local variable
            Object o_14_0 = o_testIterable_add39__12;
            com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add39__16 = fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
            // MethodAssertGenerator build local variable
            Object o_20_0 = o_testIterable_add39__16;
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add39__18 = node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_testIterable_add39__18;
            java.io.StringWriter sw = new java.io.StringWriter();
            // MethodCallAdder
            test.execute(sw, invert).close();
            test.execute(sw, invert).close();
            java.lang.System.out.println(sw);
            org.junit.Assert.fail("testIterable_add39_literalMutation73 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable */
    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable_add40 */
    @org.junit.Test(timeout = 10000)
    public void testIterable_add40_literalMutation82_failAssert26() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{#valuesR}\ntest: {{value}}\n{{/values}}"), "test");
            com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
            com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
            com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add40__12 = sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            // MethodAssertGenerator build local variable
            Object o_14_0 = o_testIterable_add40__12;
            com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add40__16 = fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
            // MethodAssertGenerator build local variable
            Object o_20_0 = o_testIterable_add40__16;
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testIterable_add40__18 = node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_testIterable_add40__18;
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, invert).close();
            // MethodCallAdder
            java.lang.System.out.println(sw);
            java.lang.System.out.println(sw);
            org.junit.Assert.fail("testIterable_add40_literalMutation82 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testSimple */
    @org.junit.Test(timeout = 10000)
    public void testSimple_add479() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("test {{value}} test"), "test");
        com.github.mustachejava.util.Node invert = test.invert("test value test");
        com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testSimple_add479__10 = // MethodCallAdder
node.put("value", com.github.mustachejava.util.NodeValue.value("value"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testSimple_add479__10);
        // AssertGenerator replace invocation
        com.github.mustachejava.util.NodeValue o_testSimple_add479__13 = node.put("value", com.github.mustachejava.util.NodeValue.value("value"));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).isList());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).value(), "value");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).list());
        org.junit.Assert.assertEquals(node, invert);
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testSimple */
    /* amplification of com.github.mustachejava.inverter.InverterTest#testSimple_add479 */
    /* amplification of com.github.mustachejava.inverter.InverterTest#testSimple_add479_literalMutation486 */
    @org.junit.Test(timeout = 10000)
    public void testSimple_add479_literalMutation486_literalMutation514_failAssert13() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("test {{ovalueP} test"), "test");
            com.github.mustachejava.util.Node invert = test.invert("test value test");
            com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testSimple_add479__10 = // MethodCallAdder
node.put("value", com.github.mustachejava.util.NodeValue.value("value"));
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testSimple_add479__10;
            // AssertGenerator replace invocation
            com.github.mustachejava.util.NodeValue o_testSimple_add479__13 = node.put("value", com.github.mustachejava.util.NodeValue.value("value"));
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).isList();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).value();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((com.github.mustachejava.util.NodeValue)o_testSimple_add479__13).list();
            org.junit.Assert.fail("testSimple_add479_literalMutation486_literalMutation514 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}


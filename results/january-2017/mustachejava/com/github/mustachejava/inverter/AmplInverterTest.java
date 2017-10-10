

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
    @org.junit.Test
    public void testIterable_literalMutation154_failAssert3_literalMutation225() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("yMO!`j!MNC@I#`g*s,=^$;H)9+6W)!j:/P:z`|."), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
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
            org.junit.Assert.fail("testIterable_literalMutation154 should have thrown PatternSyntaxException");
        } catch (java.util.regex.PatternSyntaxException eee) {
        }
    }

    /* amplification of com.github.mustachejava.inverter.InverterTest#testIterable */
    @org.junit.Test(timeout = 1000)
    public void testIterable_literalMutation154_failAssert3_add216_add660() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("C*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICn"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            com.github.mustachejava.util.Node invert = test.invert("test: sam\ntest: fred\n");
            com.github.mustachejava.util.Node node = new com.github.mustachejava.util.Node();
            com.github.mustachejava.util.Node sam = new com.github.mustachejava.util.Node();
            // MethodCallAdder
            sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            // MethodCallAdder
            sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            sam.put("value", com.github.mustachejava.util.NodeValue.value("sam"));
            com.github.mustachejava.util.Node fred = new com.github.mustachejava.util.Node();
            fred.put("value", com.github.mustachejava.util.NodeValue.value("fred"));
            node.put("values", com.github.mustachejava.util.NodeValue.list(java.util.Arrays.asList(sam, fred)));
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, invert).close();
            java.lang.System.out.println(sw);
            org.junit.Assert.fail("testIterable_literalMutation154 should have thrown PatternSyntaxException");
        } catch (java.util.regex.PatternSyntaxException eee) {
        }
    }
}


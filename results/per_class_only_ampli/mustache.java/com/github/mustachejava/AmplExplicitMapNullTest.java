

package com.github.mustachejava;


public class AmplExplicitMapNullTest {
    private static final java.lang.String TEMPLATE = "{{nullData}}";

    private com.github.mustachejava.Mustache mustache;

    @org.junit.Before
    public void setUp() {
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory();
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplExplicitMapNullTest.TEMPLATE);
        mustache = factory.compile(reader, "template");
    }

    @org.junit.Test
    public void textExplicitNullMapValue() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        // AssertGenerator replace invocation
        java.lang.Object o_textExplicitNullMapValue__3 = model.put("nullData", null);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_textExplicitNullMapValue__3);
        java.io.StringWriter writer = new java.io.StringWriter();
        // AssertGenerator replace invocation
        java.io.Writer o_textExplicitNullMapValue__6 = mustache.execute(writer, model);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.io.StringWriter)o_textExplicitNullMapValue__6).getBuffer()).capacity(), 16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_textExplicitNullMapValue__6.equals(writer));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.io.StringWriter)o_textExplicitNullMapValue__6).getBuffer()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.lang.StringBuffer)((java.io.StringWriter)o_textExplicitNullMapValue__6).getBuffer()).reverse()).length(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.lang.StringBuffer)((java.io.StringWriter)o_textExplicitNullMapValue__6).getBuffer()).reverse()).capacity(), 16);
        org.junit.Assert.assertEquals("", writer.toString());
    }
}


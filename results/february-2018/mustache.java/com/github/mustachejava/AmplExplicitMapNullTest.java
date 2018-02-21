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
        model.put("nullData", null);
        java.io.StringWriter writer = new java.io.StringWriter();
        mustache.execute(writer, model);
        org.junit.Assert.assertEquals("", writer.toString());
    }
}


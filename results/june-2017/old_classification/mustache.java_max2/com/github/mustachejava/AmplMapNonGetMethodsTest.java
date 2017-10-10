

package com.github.mustachejava;


public class AmplMapNonGetMethodsTest {
    /**
     * Extended reflection handler that can access map methods.
     */
    private class MapMethodReflectionHandler extends com.github.mustachejava.reflect.ReflectionObjectHandler {
        @java.lang.Override
        protected boolean areMethodsAccessible(java.util.Map<?, ?> map) {
            return true;
        }
    }

    private class SimpleMapMethodHandler extends com.github.mustachejava.reflect.SimpleObjectHandler {
        @java.lang.Override
        protected boolean areMethodsAccessible(java.util.Map<?, ?> map) {
            return true;
        }
    }

    private static final java.lang.String TEMPLATE = "{{empty}}";

    private static final java.lang.String TEMPLATE2 = "{{#entrySet}}" + ("{{key}}={{value}}\n" + "{{/entrySet}}");

    private com.github.mustachejava.DefaultMustacheFactory factory;

    @org.junit.Before
    public void setUp() {
        factory = new com.github.mustachejava.DefaultMustacheFactory();
    }

    @org.junit.Test
    public void testKeyValues() {
        java.util.Map<java.lang.String, java.lang.String> model = new java.util.TreeMap<java.lang.String, java.lang.String>() {
            {
                put("test", "testvalue");
                put("key", "keyvalue");
            }
        };
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE2);
        factory.setObjectHandler(new com.github.mustachejava.AmplMapNonGetMethodsTest.MapMethodReflectionHandler());
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        verifyOutput("key=keyvalue\ntest=testvalue\n", model, mustache);
    }

    @org.junit.Test
    public void testMethodAccessDisallowed() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE);
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        verifyOutput("", model, mustache);
    }

    @org.junit.Test
    public void testMethodAccessAllowed() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        factory.setObjectHandler(new com.github.mustachejava.AmplMapNonGetMethodsTest.MapMethodReflectionHandler());
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE);
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        verifyOutput("true", model, mustache);
    }

    @org.junit.Test
    public void testWrapperCaching() {
        factory.setObjectHandler(new com.github.mustachejava.AmplMapNonGetMethodsTest.MapMethodReflectionHandler());
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE);
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        java.util.Map<java.lang.String, java.lang.String> model = new java.util.HashMap<>();
        verifyOutput("true", model, mustache);
        model.put("empty", "data");
        verifyOutput("data", model, mustache);
    }

    @org.junit.Test
    public void testSimpleHandlerMethodAccessDisallowed() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        factory.setObjectHandler(new com.github.mustachejava.reflect.SimpleObjectHandler());
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE);
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        verifyOutput("", model, mustache);
    }

    @org.junit.Test
    public void testSimpleHandlerMethodAccessAllowed() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        factory.setObjectHandler(new com.github.mustachejava.AmplMapNonGetMethodsTest.SimpleMapMethodHandler());
        java.io.Reader reader = new java.io.StringReader(com.github.mustachejava.AmplMapNonGetMethodsTest.TEMPLATE);
        com.github.mustachejava.Mustache mustache = factory.compile(reader, "template");
        verifyOutput("true", model, mustache);
    }

    private void verifyOutput(java.lang.String expected, java.lang.Object model, com.github.mustachejava.Mustache mustache) {
        java.io.StringWriter writer = new java.io.StringWriter();
        mustache.execute(writer, model);
        org.junit.Assert.assertEquals(expected, writer.toString());
    }
}


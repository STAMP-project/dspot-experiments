

package com.github.mustachejava;


public class AmplDotNotationTest {
    private static final java.lang.String EARLY_MISS_TEMPLATE = "{{container1.container2.target}}";

    private static final java.lang.String LAST_ELEMENT_MISS_TEMPLATE = "{{container1.nothing}}";

    private static final class ModelObject {
        @java.lang.SuppressWarnings(value = "unused")
        public java.lang.Object getContainer2() {
            return null;
        }
    }

    private com.github.mustachejava.MustacheFactory factory;

    private java.util.Map<java.lang.String, java.lang.Object> mapModel;

    private java.util.Map<java.lang.String, java.lang.Object> objectModel;

    @org.junit.Before
    public void setUp() {
        factory = new com.github.mustachejava.DefaultMustacheFactory();
        mapModel = new java.util.HashMap<>();
        java.util.Map<java.lang.String, java.lang.Object> container1 = new java.util.HashMap<>();
        mapModel.put("container1", container1);
        objectModel = new java.util.HashMap<>();
        objectModel.put("container1", new com.github.mustachejava.AmplDotNotationTest.ModelObject());
    }

    @org.junit.Test
    public void testIncompleteMapPath() {
        testMiss(mapModel, com.github.mustachejava.AmplDotNotationTest.EARLY_MISS_TEMPLATE);
    }

    @org.junit.Test
    public void testAlmostCompleteMapPath() {
        testMiss(mapModel, com.github.mustachejava.AmplDotNotationTest.LAST_ELEMENT_MISS_TEMPLATE);
    }

    @org.junit.Test
    public void testIncompleteObjectPath() {
        testMiss(objectModel, com.github.mustachejava.AmplDotNotationTest.EARLY_MISS_TEMPLATE);
    }

    @org.junit.Test
    public void testAlmostCompleteObjectPath() {
        testMiss(objectModel, com.github.mustachejava.AmplDotNotationTest.LAST_ELEMENT_MISS_TEMPLATE);
    }

    private void testMiss(java.lang.Object model, java.lang.String template) {
        com.github.mustachejava.Mustache mustache = compile(template);
        java.io.StringWriter writer = new java.io.StringWriter();
        mustache.execute(writer, model);
        org.junit.Assert.assertEquals("", writer.toString());
    }

    private com.github.mustachejava.Mustache compile(java.lang.String template) {
        java.io.Reader reader = new java.io.StringReader(template);
        return factory.compile(reader, "template");
    }
}


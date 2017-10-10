

package com.github.mustachejava;


/**
 * Test that wrappers are not cached too aggressively,
 * causing false misses or hits.
 */
public class TestWrapperCachingAmpl {
    private static final java.lang.String TEMPLATE = "{{object.data}}";

    private static final java.lang.String SCOPES_TEMPLATE = "{{#scope2}}{{#scope1}}{{data.data}}{{/scope1}}{{/scope2}}";

    private class TestObject {
        public TestObject() {
        }

        public TestObject(java.lang.Object data) {
            this.data = data;
        }

        private java.lang.Object data;

        public java.lang.Object getData() {
            return data;
        }

        public void setData(java.lang.Object data) {
            this.data = data;
        }

        public java.lang.String toString() {
            return ("{data=" + (data)) + "}";
        }
    }

    private com.github.mustachejava.Mustache template;

    private com.github.mustachejava.Mustache scopesTemplate;

    @org.junit.Before
    public void setUp() {
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory();
        template = factory.compile(new java.io.StringReader(com.github.mustachejava.TestWrapperCachingAmpl.TEMPLATE), "template");
        scopesTemplate = factory.compile(new java.io.StringReader(com.github.mustachejava.TestWrapperCachingAmpl.SCOPES_TEMPLATE), "template");
    }

    /**
     * Test that initial misses on dot-notation are not incorrectly cached.
     */
    @org.junit.Test
    public void testInitialMiss() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        org.junit.Assert.assertEquals("", render(template, model));
        com.github.mustachejava.TestWrapperCachingAmpl.TestObject object = new com.github.mustachejava.TestWrapperCachingAmpl.TestObject();
        object.setData("hit");
        model.put("object", object);
        org.junit.Assert.assertEquals("hit", render(template, model));
    }

    /**
     * Test that initial misses on map dot notation are not incorrectly cached.
     */
    @org.junit.Test
    public void testMapInitialMiss() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        org.junit.Assert.assertEquals("", render(template, model));
        java.util.Map<java.lang.String, java.lang.String> object = new java.util.HashMap<>();
        object.put("data", "hit");
        model.put("object", object);
        org.junit.Assert.assertEquals("hit", render(template, model));
    }

    @org.junit.Test
    public void testMultiScopeInitialHit() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        model.put("scope1", "foo");// scope 1 full miss
        
        model.put("scope2", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject(new com.github.mustachejava.TestWrapperCachingAmpl.TestObject("hit")));// scope 2 dot hit
        
        org.junit.Assert.assertEquals("hit", render(scopesTemplate, model));
        model.put("scope2", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject());// scope2 dot miss
        
        org.junit.Assert.assertEquals("", render(scopesTemplate, model));
    }

    @org.junit.Test
    public void testMultiScopeInitialHit2() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        model.put("scope1", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject(new com.github.mustachejava.TestWrapperCachingAmpl.TestObject("hit")));// scope 1 hit
        
        model.put("scope2", "foo");// scope 2 full miss (shouldn't matter)
        
        org.junit.Assert.assertEquals("hit", render(scopesTemplate, model));
        model.put("scope1", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject());// scope1 dot miss
        
        org.junit.Assert.assertEquals("", render(scopesTemplate, model));
    }

    @org.junit.Test
    public void testMultiScopeInitialMiss() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        model.put("scope1", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject());// scope 1 dot miss
        
        model.put("scope2", "foo");// scope 2 full miss (shouldn't matter)
        
        org.junit.Assert.assertEquals("", render(scopesTemplate, model));
        model.put("scope1", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject(new com.github.mustachejava.TestWrapperCachingAmpl.TestObject("hit")));// scope 1 dot hit
        
        org.junit.Assert.assertEquals("hit", render(scopesTemplate, model));
    }

    @org.junit.Test
    public void testMultiScopeInitialMiss2() {
        java.util.Map<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        model.put("scope1", "foo");// scope 1 full miss
        
        model.put("scope2", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject());// scope 2 dot miss
        
        org.junit.Assert.assertEquals("", render(scopesTemplate, model));
        model.put("scope2", new com.github.mustachejava.TestWrapperCachingAmpl.TestObject(new com.github.mustachejava.TestWrapperCachingAmpl.TestObject("hit")));// scope 2 hit
        
        org.junit.Assert.assertEquals("hit", render(scopesTemplate, model));
    }

    private java.lang.String render(com.github.mustachejava.Mustache template, java.lang.Object data) {
        java.io.Writer writer = new java.io.StringWriter();
        template.execute(writer, data);
        return writer.toString();
    }
}


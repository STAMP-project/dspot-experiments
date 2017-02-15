

package com.github.mustachejava;


public class AmplPolymorphicClassTest {
    static class Value {
        public java.lang.String getText() {
            return "ok";
        }
    }

    static class A {
        public com.github.mustachejava.AmplPolymorphicClassTest.Value getValue() {
            return new com.github.mustachejava.AmplPolymorphicClassTest.Value();
        }
    }

    static class B extends com.github.mustachejava.AmplPolymorphicClassTest.A {
        @java.lang.Override
        public com.github.mustachejava.AmplPolymorphicClassTest.Value getValue() {
            return new com.github.mustachejava.AmplPolymorphicClassTest.Value();
        }
    }

    java.lang.String compile(java.lang.String template, java.lang.Object model) {
        final java.io.StringWriter buffer = new java.io.StringWriter();
        factory.compile(template).execute(buffer, model);
        return buffer.toString();
    }

    com.github.mustachejava.DefaultMustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory() {
        public java.io.Reader getReader(java.lang.String resourceName) {
            return new java.io.StringReader(resourceName);
        }
    };

    /**
     * Test for issue 97, java.lang.IllegalArgumentException: object is not an instance of declaring class
     */
    @org.junit.Test
    public void testPolyClass() throws java.io.IOException {
        java.util.HashMap<java.lang.String, java.lang.Object> model = new java.util.HashMap<>();
        model.put("x", new com.github.mustachejava.AmplPolymorphicClassTest.B());
        org.junit.Assert.assertEquals("ok", compile("{{x.value.text}}", model));
        model.put("x", new com.github.mustachejava.AmplPolymorphicClassTest.A());
        org.junit.Assert.assertEquals("ok", compile("{{x.value.text}}", model));
        model.put("x", new com.github.mustachejava.AmplPolymorphicClassTest.B());
        org.junit.Assert.assertEquals("ok", compile("{{x.value.text}}", model));
    }
}


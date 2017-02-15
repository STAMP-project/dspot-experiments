

package com.github.mustachejava;


public class AmplFailOnMissingTest {
    @org.junit.Test
    public void testFail() {
        com.github.mustachejava.reflect.ReflectionObjectHandler roh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            @java.lang.Override
            public com.github.mustachejava.Binding createBinding(java.lang.String name, final com.github.mustachejava.TemplateContext tc, com.github.mustachejava.Code code) {
                return new com.github.mustachejava.reflect.GuardedBinding(this, name, tc, code) {
                    @java.lang.Override
                    protected synchronized com.github.mustachejava.util.Wrapper getWrapper(java.lang.String name, java.util.List<java.lang.Object> scopes) {
                        com.github.mustachejava.util.Wrapper wrapper = super.getWrapper(name, scopes);
                        if (wrapper instanceof com.github.mustachejava.reflect.MissingWrapper) {
                            throw new com.github.mustachejava.MustacheException(((name + " not found in ") + tc));
                        }
                        return wrapper;
                    }
                };
            }
        };
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        dmf.setObjectHandler(roh);
        try {
            com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader("{{test}}"), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                java.lang.String test = "ok";
            }).close();
            org.junit.Assert.assertEquals("ok", sw.toString());
            test.execute(new java.io.StringWriter(), new java.lang.Object());
            org.junit.Assert.fail("Should have failed");
        } catch (com.github.mustachejava.MustacheException me) {
            org.junit.Assert.assertEquals("test not found in [test:1] @[test:1]", me.getCause().getMessage());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}


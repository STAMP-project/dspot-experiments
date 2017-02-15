

package com.github.mustachejava;


public class AmplConvertMethodsToFunctionsTest {
    private static com.github.mustachejava.reflect.ReflectionObjectHandler roh;

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface TemplateMethod {    }

    @org.junit.BeforeClass
    public static void setup() {
        com.github.mustachejava.AmplConvertMethodsToFunctionsTest.roh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            /**
             * Find a wrapper given the current context. If not found, return null.
             *
             * @param scopeIndex the index into the scope array
             * @param wrappers   the current set of wrappers to get here
             * @param guards     the list of guards used to find this
             * @param scope      the current scope
             * @param name       the name in the scope
             * @return null if not found, otherwise a wrapper for this scope and name
             */
            @java.lang.Override
            protected com.github.mustachejava.util.Wrapper findWrapper(int scopeIndex, com.github.mustachejava.util.Wrapper[] wrappers, java.util.List<com.github.mustachejava.reflect.Guard> guards, java.lang.Object scope, java.lang.String name) {
                com.github.mustachejava.util.Wrapper wrapper = super.findWrapper(scopeIndex, wrappers, guards, scope, name);
                if (wrapper == null) {
                    // Now check to see if there is a method that takes a string
                    return getWrapper(scopeIndex, wrappers, guards, scope, name, scope.getClass());
                }
                return wrapper;
            }

            private com.github.mustachejava.util.Wrapper getWrapper(final int scopeIndex, final com.github.mustachejava.util.Wrapper[] wrappers, final java.util.List<com.github.mustachejava.reflect.Guard> guards, java.lang.Object scope, java.lang.String name, java.lang.Class<?> aClass) {
                try {
                    java.lang.reflect.Method method = aClass.getDeclaredMethod(name, java.lang.String.class);
                    method.setAccessible(true);
                    return new com.github.mustachejava.reflect.ReflectionWrapper(scopeIndex, wrappers, guards.toArray(new com.github.mustachejava.reflect.Guard[guards.size()]), method, null, this) {
                        @java.lang.Override
                        public java.lang.Object call(java.util.List<java.lang.Object> scopes) throws com.github.mustachejava.util.GuardException {
                            guardCall(scopes);
                            final java.lang.Object scope1 = unwrap(scopes);
                            if (scope1 == null)
                                return null;
                            
                            if ((method.getAnnotation(com.github.mustachejava.AmplConvertMethodsToFunctionsTest.TemplateMethod.class)) == null) {
                                return new java.util.function.Function<java.lang.String, java.lang.String>() {
                                    @java.lang.Override
                                    public java.lang.String apply(java.lang.String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            }else {
                                return new com.github.mustachejava.TemplateFunction() {
                                    @java.lang.Override
                                    public java.lang.String apply(java.lang.String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            }
                        }

                        private java.lang.String getString(java.lang.String input, java.lang.Object scope1) {
                            try {
                                java.lang.Object invoke = method.invoke(scope1, input);
                                return invoke == null ? null : java.lang.String.valueOf(invoke);
                            } catch (java.lang.reflect.InvocationTargetException e) {
                                throw new com.github.mustachejava.MustacheException(("Failed to execute method: " + (method)), e.getTargetException());
                            } catch (java.lang.IllegalAccessException e) {
                                throw new com.github.mustachejava.MustacheException(("Failed to execute method: " + (method)), e);
                            }
                        }
                    };
                } catch (java.lang.NoSuchMethodException e) {
                    java.lang.Class<?> superclass = aClass.getSuperclass();
                    return superclass == (java.lang.Object.class) ? null : getWrapper(scopeIndex, wrappers, guards, scope, name, superclass);
                }
            }
        };
    }

    @org.junit.Test
    public void testConvert() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        dmf.setObjectHandler(com.github.mustachejava.AmplConvertMethodsToFunctionsTest.roh);
        com.github.mustachejava.Mustache uppertest = dmf.compile(new java.io.StringReader("{{#upper}}{{test}}{{/upper}}"), "uppertest");
        java.io.StringWriter sw = new java.io.StringWriter();
        uppertest.execute(sw, new java.lang.Object() {
            java.lang.String test = "test";

            java.lang.String upper(java.lang.String s) {
                return s.toUpperCase();
            }
        }).close();
        junit.framework.Assert.assertEquals("TEST", sw.toString());
        sw = new java.io.StringWriter();
        uppertest.execute(sw, new java.lang.Object() {
            java.lang.String test2 = "test";

            @com.github.mustachejava.AmplConvertMethodsToFunctionsTest.TemplateMethod
            java.lang.String upper(java.lang.String s) {
                return "{{test2}}";
            }
        }).close();
        junit.framework.Assert.assertEquals("test", sw.toString());
    }
}


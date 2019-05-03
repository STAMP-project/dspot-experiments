package com.github.mustachejava;


import com.github.mustachejava.reflect.Guard;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.reflect.ReflectionWrapper;
import com.github.mustachejava.util.GuardException;
import com.github.mustachejava.util.Wrapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplConvertMethodsToFunctionsTest {
    private static ReflectionObjectHandler roh;

    @Retention(RetentionPolicy.RUNTIME)
    @interface TemplateMethod {}

    @BeforeClass
    public static void setup() {
        AmplConvertMethodsToFunctionsTest.roh = new ReflectionObjectHandler() {
            @Override
            protected Wrapper findWrapper(int scopeIndex, Wrapper[] wrappers, List<Guard> guards, Object scope, String name) {
                Wrapper wrapper = super.findWrapper(scopeIndex, wrappers, guards, scope, name);
                if (wrapper == null) {
                    return getWrapper(scopeIndex, wrappers, guards, scope, name, scope.getClass());
                }
                return wrapper;
            }

            private Wrapper getWrapper(final int scopeIndex, final Wrapper[] wrappers, final List<Guard> guards, Object scope, String name, Class<?> aClass) {
                try {
                    Method method = aClass.getDeclaredMethod(name, String.class);
                    method.setAccessible(true);
                    return new ReflectionWrapper(scopeIndex, wrappers, guards.toArray(new Guard[guards.size()]), method, null, this) {
                        @Override
                        public Object call(List<Object> scopes) throws GuardException {
                            guardCall(scopes);
                            final Object scope1 = unwrap(scopes);
                            if (scope1 == null) {
                                return null;
                            }
                            if ((method.getAnnotation(AmplConvertMethodsToFunctionsTest.TemplateMethod.class)) == null) {
                                return new Function<String, String>() {
                                    @Override
                                    public String apply(String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            } else {
                                return new TemplateFunction() {
                                    @Override
                                    public String apply(String input) {
                                        return getString(input, scope1);
                                    }
                                };
                            }
                        }

                        private String getString(String input, Object scope1) {
                            try {
                                Object invoke = method.invoke(scope1, input);
                                return invoke == null ? null : String.valueOf(invoke);
                            } catch (InvocationTargetException e) {
                                throw new MustacheException(("Failed to execute method: " + (method)), e.getTargetException());
                            } catch (IllegalAccessException e) {
                                throw new MustacheException(("Failed to execute method: " + (method)), e);
                            }
                        }
                    };
                } catch (NoSuchMethodException e) {
                    Class<?> superclass = aClass.getSuperclass();
                    return superclass == (Object.class) ? null : getWrapper(scopeIndex, wrappers, guards, scope, name, superclass);
                }
            }
        };
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString2_failAssert0_literalMutationString2241_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
                Mustache uppertest = dmf.compile(new StringReader("{{=toolyn}}"), "uppertest");
                StringWriter sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test = "thest";

                    String upper(String s) {
                        return s.toUpperCase();
                    }
                }).close();
                Assert.assertEquals("TEST", sw.toString());
                sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test2 = "test";

                    @AmplConvertMethodsToFunctionsTest.TemplateMethod
                    String upper(String s) {
                        return "{{test2}}";
                    }
                }).close();
                Assert.assertEquals("test", sw.toString());
                org.junit.Assert.fail("testConvert_literalMutationString2 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testConvert_literalMutationString2_failAssert0_literalMutationString2241 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[uppertest:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString35_failAssert0_literalMutationString2519_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
                Mustache uppertest = dmf.compile(new StringReader("{{=toolyn}}"), "uppertest");
                StringWriter sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test = "test";

                    String upper(String s) {
                        return s.toUpperCase();
                    }
                }).close();
                Assert.assertEquals("TEST", sw.toString());
                sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test2 = "test";

                    @AmplConvertMethodsToFunctionsTest.TemplateMethod
                    String upper(String s) {
                        return "{{Ttest2}}";
                    }
                }).close();
                Assert.assertEquals("test", sw.toString());
                org.junit.Assert.fail("testConvert_literalMutationString35 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testConvert_literalMutationString35_failAssert0_literalMutationString2519 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[uppertest:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString2_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
            Mustache uppertest = dmf.compile(new StringReader("{{=toolyn}}"), "uppertest");
            StringWriter sw = new StringWriter();
            uppertest.execute(sw, new Object() {
                String test = "test";

                String upper(String s) {
                    return s.toUpperCase();
                }
            }).close();
            Assert.assertEquals("TEST", sw.toString());
            sw = new StringWriter();
            uppertest.execute(sw, new Object() {
                String test2 = "test";

                @AmplConvertMethodsToFunctionsTest.TemplateMethod
                String upper(String s) {
                    return "{{test2}}";
                }
            }).close();
            Assert.assertEquals("test", sw.toString());
            org.junit.Assert.fail("testConvert_literalMutationString2 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[uppertest:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString2_failAssert0_add3608_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
                Mustache uppertest = dmf.compile(new StringReader("{{=toolyn}}"), "uppertest");
                StringWriter sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test = "test";

                    String upper(String s) {
                        return s.toUpperCase();
                    }
                }).close();
                Assert.assertEquals("TEST", sw.toString());
                sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test2 = "test";

                    @AmplConvertMethodsToFunctionsTest.TemplateMethod
                    String upper(String s) {
                        return "{{test2}}";
                    }
                }).close();
                sw.toString();
                Assert.assertEquals("test", sw.toString());
                org.junit.Assert.fail("testConvert_literalMutationString2 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testConvert_literalMutationString2_failAssert0_add3608 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[uppertest:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString2_failAssert0null4148_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
                Mustache uppertest = dmf.compile(new StringReader("{{=toolyn}}"), null);
                StringWriter sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test = "test";

                    String upper(String s) {
                        return s.toUpperCase();
                    }
                }).close();
                Assert.assertEquals("TEST", sw.toString());
                sw = new StringWriter();
                uppertest.execute(sw, new Object() {
                    String test2 = "test";

                    @AmplConvertMethodsToFunctionsTest.TemplateMethod
                    String upper(String s) {
                        return "{{test2}}";
                    }
                }).close();
                Assert.assertEquals("test", sw.toString());
                org.junit.Assert.fail("testConvert_literalMutationString2 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testConvert_literalMutationString2_failAssert0null4148 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[null:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testConvert_literalMutationString32_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            dmf.setObjectHandler(AmplConvertMethodsToFunctionsTest.roh);
            Mustache uppertest = dmf.compile(new StringReader("{{#upper}}{{test}}{{/upper}}"), "uppertest");
            StringWriter sw = new StringWriter();
            uppertest.execute(sw, new Object() {
                String test = "test";

                String upper(String s) {
                    return s.toUpperCase();
                }
            }).close();
            Assert.assertEquals("TEST", sw.toString());
            sw = new StringWriter();
            uppertest.execute(sw, new Object() {
                String test2 = "test";

                @AmplConvertMethodsToFunctionsTest.TemplateMethod
                String upper(String s) {
                    return "{{=toolyn}}";
                }
            }).close();
            Assert.assertEquals("test", sw.toString());
            org.junit.Assert.fail("testConvert_literalMutationString32 should have thrown MustacheException");
        } catch (MustacheException expected) {
            assertEquals("Invalid delimiter string: =toolyn @[uppertest:1]", expected.getMessage());
        }
    }
}


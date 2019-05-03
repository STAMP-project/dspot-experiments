package com.github.mustachejava;


import com.github.mustachejava.reflect.GuardedBinding;
import com.github.mustachejava.reflect.MissingWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplFailOnMissingTest {
    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0null1526_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString8_failAssert0null1526 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0null1527_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString8_failAssert0null1527 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_add24_failAssert0_literalMutationString428_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    Mustache test = dmf.compile(new StringReader("{{test}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_add24 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_add24_failAssert0_literalMutationString428 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0_literalMutationString668_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not foud in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString8_failAssert0_literalMutationString668 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0_add1193_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString8_failAssert0_add1193 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString18_failAssert0_literalMutationString713_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "Iest");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString18 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString18_failAssert0_literalMutationString713 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[Iest:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFailnull40_failAssert0_literalMutationString278_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, null);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFailnull40 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFailnull40_failAssert0_literalMutationString278 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0_add1189_failAssert0() throws Exception {
        try {
            {
                ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                    @Override
                    public Binding createBinding(String name, final TemplateContext tc, Code code) {
                        return new GuardedBinding(this, name, tc, code) {
                            @Override
                            protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                                Wrapper wrapper = super.getWrapper(name, scopes);
                                if (wrapper instanceof MissingWrapper) {
                                    throw new MustacheException(((name + " not found in ") + tc));
                                }
                                return wrapper;
                            }
                        };
                    }
                };
                DefaultMustacheFactory dmf = new DefaultMustacheFactory();
                dmf.setObjectHandler(roh);
                {
                    dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                    StringWriter sw = new StringWriter();
                    test.execute(sw, new Object() {
                        String test = "ok";
                    }).close();
                    sw.toString();
                    test.execute(new StringWriter(), new Object());
                }
                org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testFail_literalMutationString8_failAssert0_add1189 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testFail_literalMutationString8_failAssert0() throws Exception {
        try {
            ReflectionObjectHandler roh = new ReflectionObjectHandler() {
                @Override
                public Binding createBinding(String name, final TemplateContext tc, Code code) {
                    return new GuardedBinding(this, name, tc, code) {
                        @Override
                        protected synchronized Wrapper getWrapper(String name, List<Object> scopes) {
                            Wrapper wrapper = super.getWrapper(name, scopes);
                            if (wrapper instanceof MissingWrapper) {
                                throw new MustacheException(((name + " not found in ") + tc));
                            }
                            return wrapper;
                        }
                    };
                }
            };
            DefaultMustacheFactory dmf = new DefaultMustacheFactory();
            dmf.setObjectHandler(roh);
            {
                Mustache test = dmf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    String test = "ok";
                }).close();
                sw.toString();
                test.execute(new StringWriter(), new Object());
            }
            org.junit.Assert.fail("testFail_literalMutationString8 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }
}


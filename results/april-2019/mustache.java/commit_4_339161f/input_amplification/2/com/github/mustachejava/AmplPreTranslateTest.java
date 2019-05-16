package com.github.mustachejava;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplPreTranslateTest {
    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory() {
                MustacheParser mp = new MustacheParser(this) {
                    @Override
                    public Mustache compile(Reader reader, String file) {
                        return super.compile(reader, file, "{[", "]}");
                    }
                };

                @Override
                public Mustache compile(Reader reader, String file, String sm, String em) {
                    return super.compile(reader, file, "{[", "]}");
                }

                @Override
                protected Function<String, Mustache> getMustacheCacheFunction() {
                    return ( template) -> {
                        Mustache compile = mp.compile(template);
                        compile.init();
                        return compile;
                    };
                }
            };
            Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                Function i = ( input) -> "{{test}} Translate";
            }).close();
            Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
            mf = new DefaultMustacheFactory();
            m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {
                boolean show = true;

                String test = "Now";
            }).close();
            Assert.assertEquals("Now Translate\n", sw.toString());
            org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_add1348_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_add1348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0null1620_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals(null, sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0null1620 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString1_failAssert0_literalMutationString532_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("?$`@8iv`v!te+EBjQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString1 should have thrown StringIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString1_failAssert0_literalMutationString532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template ?$`@8iv`v!te+EBjQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_add1341_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            mp.compile(template);
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_add1341 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_add1345_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                });
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_add1345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_literalMutationString1003_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_literalMutationString1003 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_literalMutationString1020_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, "{[", "]}");
                        }
                    };

                    @Override
                    public Mustache compile(Reader reader, String file, String sm, String em) {
                        return super.compile(reader, file, "{[", "]}");
                    }

                    @Override
                    protected Function<String, Mustache> getMustacheCacheFunction() {
                        return ( template) -> {
                            Mustache compile = mp.compile(template);
                            compile.init();
                            return compile;
                        };
                    }
                };
                Mustache m = mf.compile("{2LA<=*f3g+7S33!R");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Trnslate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_literalMutationString1020 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template {2LA<=*f3g+7S33!R not found", expected.getMessage());
        }
    }
}


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
            Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
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
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0null1508_failAssert0() throws IOException {
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
                Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0null1508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_literalMutationString686_failAssert0() throws IOException {
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
                Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretr[anslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString17 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_literalMutationString686 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_add1227_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            super.compile(reader, file, "{[", "]}");
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
                Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_add1227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0_add1232_failAssert0() throws IOException {
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
                Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
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
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0_add1232 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString17_failAssert0null1496_failAssert0() throws IOException {
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
                Mustache m = mf.compile("3T%j2T7$Gxz(Q7E[D");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString17_failAssert0null1496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 3T%j2T7$Gxz(Q7E[D not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_literalMutationString866_failAssert0() throws IOException {
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
                Mustache m = mf.compile("pretr|nslatu.html");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_literalMutationString866 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template pretr|nslatu.html not found", expected.getMessage());
        }
    }
}


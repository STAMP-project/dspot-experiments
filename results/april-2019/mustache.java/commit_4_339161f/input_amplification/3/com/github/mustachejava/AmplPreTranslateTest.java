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
    public void testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0null9205_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "{[", null);
                            }
                        };

                        @Override
                        public Mustache compile(Reader reader, String file, String sm, String em) {
                            super.compile(reader, file, "{[", "]}");
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0null9205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0() throws IOException {
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
            Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0() throws IOException {
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
                        super.compile(reader, file, "{[", "]}");
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0null1621_failAssert0_literalMutationString4123_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("8j]WhyIu.hyap#o)a");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1621 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1621_failAssert0_literalMutationString4123 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap#o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0null8666_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "{[", null);
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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

                        String test = "page1.txt";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0null8666 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_literalMutationString1000_failAssert0() throws IOException {
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0null1621_failAssert0() throws IOException {
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), null);
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1621 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0_add7662_failAssert0() throws IOException {
        try {
            {
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
                            super.compile(reader, file, "{[", "]}");
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                    });
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0_add7662 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0() throws IOException {
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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

                    String test = "page1.txt";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0_literalMutationString3313_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), "page1.txt");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "page1.txt";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0_literalMutationString3313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0_literalMutationString5004_failAssert0() throws IOException {
        try {
            {
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
                            super.compile(reader, file, "{[", "]}");
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "^Y?<Y8*<}j:vs!)}mD";
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1340_failAssert0_literalMutationString5004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0null1621_failAssert0_add7364_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1621 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1621_failAssert0_add7364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_add1345_failAssert0() throws IOException {
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_add1345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0null1610_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, null, "{[", "]}");
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
                Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0null1610 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0_add7104_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("8j]WhyIu.hyap2o)a");
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

                        String test = "page1.txt";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString15_failAssert0_literalMutationString1039_failAssert0_add7104 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template 8j]WhyIu.hyap2o)a not found", expected.getMessage());
        }
    }
}


package com.github.mustachejava;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;
import junit.framework.Assert;
import junit.framework.ComparisonFailure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplPreTranslateTest {
    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0() throws IOException {
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
            Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0() throws IOException {
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
                        return super.compile(reader, null, "{[", "]}");
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0() throws IOException {
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
            Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0() throws IOException {
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
                        return super.compile(reader, file, null, "]}");
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0() throws IOException {
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
            Mustache m = mf.compile("");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_literalMutationString5642_failAssert0() throws IOException {
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
                            return super.compile(reader, null, "{[", "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), "pr7etranslate.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_literalMutationString5642 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0null20833_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0null20833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_add3341_failAssert0() throws IOException {
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
                Mustache m = mf.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                sw.toString();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_add3341 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_add17492_failAssert0() throws IOException {
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
                            return super.compile(reader, file, null, "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_add17492 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_literalMutationString5616_failAssert0() throws IOException {
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
                            return super.compile(reader, null, "{[", "");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_literalMutationString5616 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_add15028_failAssert0() throws IOException {
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
                            return super.compile(reader, null, "{[", "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_add15028 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0null4190_failAssert0() throws IOException {
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
                Mustache m = mf.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "{{test}} Translate";
                }).close();
                Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                mf = new DefaultMustacheFactory();
                m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
                sw = new StringWriter();
                m.execute(null, new Object() {
                    boolean show = true;

                    String test = "Now";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0null4190 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0_add3173_failAssert0() throws IOException {
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0_add3173 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_add17499_failAssert0() throws IOException {
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
                            return super.compile(reader, file, null, "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_add17499 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0null18302_failAssert0() throws IOException {
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
                            return super.compile(reader, null, null, "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0null18302 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_add15015_failAssert0() throws IOException {
        try {
            {
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
                            return super.compile(reader, null, "{[", "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0_add15015 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_literalMutationString2014_failAssert0() throws IOException {
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
                Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                Assert.assertEquals("No Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_literalMutationString2014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0null18954_failAssert0() throws IOException {
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
                            return super.compile(null, file, "{[", "]}");
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
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0null18954 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0null19933_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0null19933 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0() throws IOException {
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
                Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0() throws IOException {
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0() throws IOException {
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
                Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0_literalMutationString13482_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{N#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
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
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0_literalMutationString13482 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString43_failAssert0_add3160_failAssert0_literalMutationString9666_failAssert0() throws IOException {
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
                    Mustache m = mf.compile("");
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

                        String test = "row";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString43 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString43_failAssert0_add3160 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString43_failAssert0_add3160_failAssert0_literalMutationString9666 should have thrown ComparisonFailure");
        } catch (ComparisonFailure expected) {
            assertEquals("expected:<[{{#show}}\n{{test}} Translate\n{{/show}}]> but was:<[box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n]>", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0null4259_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory() {
                    MustacheParser mp = new MustacheParser(this) {
                        @Override
                        public Mustache compile(Reader reader, String file) {
                            return super.compile(reader, file, null, "]}");
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
                Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0null4259 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_literalMutationString12999_failAssert0() throws IOException {
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
                            return super.compile(reader, file, null, "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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

                        String test = "";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_literalMutationString12999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0_literalMutationString1253_failAssert0() throws IOException {
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
                        return super.compile(reader, file, "Template 0L0H2nsj;&E$fjp[A;7} not found", "]}");
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0_literalMutationString1253 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0_add3172_failAssert0() throws IOException {
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0_add3172 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0_add16730_failAssert0() throws IOException {
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0_add16730 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0_add15727_failAssert0() throws IOException {
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
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0_add15727 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_literalMutationString2016_failAssert0() throws IOException {
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
                Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                Assert.assertEquals("N?w Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_literalMutationString2016 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0_literalMutationString10686_failAssert0() throws IOException {
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "{{test}} Translate";
                    }).close();
                    Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
                    mf = new DefaultMustacheFactory();
                    m = mf.compile(new StringReader(sw.toString()), "Template 0L0H2nsj;&E$fjp[A;7} not found");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {
                        boolean show = true;

                        String test = "Now";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString13 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString13_failAssert0_literalMutationString1740_failAssert0_literalMutationString10686 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_literalMutationString12977_failAssert0() throws IOException {
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
                            return super.compile(reader, file, null, "]}");
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
                    Mustache m = mf.compile("qX|1*kf(T$:UP$/sLqXfyVX%?kq}/)]y-/JrsE]");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023_failAssert0_literalMutationString12977 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template qX|1*kf(T$:UP$/sLqXfyVX%?kq}/)]y-/JrsE] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString2_failAssert0_literalMutationString1117_failAssert0_literalMutationString10327_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(reader, file, "Template 0L0H2nsj;&E$fjp[A;7} not found", "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        Function i = ( input) -> "";
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString2 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString2_failAssert0_literalMutationString1117 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString2_failAssert0_literalMutationString1117_failAssert0_literalMutationString10327 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0_add17658_failAssert0() throws IOException {
        try {
            {
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
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3411_failAssert0_add17658 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4023_failAssert0null20688_failAssert0() throws IOException {
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
                            return super.compile(reader, file, null, null);
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4023_failAssert0null20688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0_literalMutationString7626_failAssert0() throws IOException {
        try {
            {
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
                                mp.compile(template);
                                Mustache compile = mp.compile(template);
                                compile.init();
                                return compile;
                            };
                        }
                    };
                    Mustache m = mf.compile("g=y;0+gS2#Zr>=D7&");
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
                org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString16_failAssert0_add3413_failAssert0_literalMutationString7626 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template g=y;0+gS2#Zr>=D7& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString39_failAssert0_add3433_failAssert0_literalMutationString13246_failAssert0() throws IOException {
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
                    Mustache m = mf.compile("zh|CRx,f3wQ[mk0r^");
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

                        String test = "Template 0L0H2nsj;&E$fjp[A;7} not found";
                    }).close();
                    Assert.assertEquals("Now Translate\n", sw.toString());
                    org.junit.Assert.fail("testPretranslate_literalMutationString39 should have thrown ComparisonFailure");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString39_failAssert0_add3433 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString39_failAssert0_add3433_failAssert0_literalMutationString13246 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template zh|CRx,f3wQ[mk0r^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0null4022_failAssert0null18297_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = new DefaultMustacheFactory() {
                        MustacheParser mp = new MustacheParser(this) {
                            @Override
                            public Mustache compile(Reader reader, String file) {
                                return super.compile(null, file, "{[", "]}");
                            }
                        };

                        @Override
                        public Mustache compile(Reader reader, String file, String sm, String em) {
                            return super.compile(reader, null, "{[", "]}");
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
                    Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
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
                    org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0null4022_failAssert0null18297 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0_literalMutationString1268_failAssert0() throws IOException {
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
                Mustache m = mf.compile("Template 0L0H2nsj;&E$fjp[A;7} not found");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    Function i = ( input) -> "u!uL2U`>Ts@rY)Hv8[";
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
                org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString14_failAssert0_literalMutationString1268 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template Template 0L0H2nsj;&E$fjp[A;7} not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString40_failAssert0_literalMutationString2376_failAssert0() throws IOException {
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
                Mustache m = mf.compile("f<.XQ(.kN%SCNZ$CR");
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

                    String test = "Nw";
                }).close();
                Assert.assertEquals("Now Translate\n", sw.toString());
                org.junit.Assert.fail("testPretranslate_literalMutationString40 should have thrown ComparisonFailure");
            }
            org.junit.Assert.fail("testPretranslate_literalMutationString40_failAssert0_literalMutationString2376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template f<.XQ(.kN%SCNZ$CR not found", expected.getMessage());
        }
    }
}


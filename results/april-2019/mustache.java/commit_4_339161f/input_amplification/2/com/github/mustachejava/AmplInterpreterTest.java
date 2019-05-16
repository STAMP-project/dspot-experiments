package com.github.mustachejava;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.PartialCode;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import com.github.mustachejava.util.CapturingMustacheVisitor;
import com.github.mustachejavabenchmarks.JsonCapturer;
import com.github.mustachejavabenchmarks.JsonInterpreterTest;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString75045_failAssert0null77169_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("w35He[LVCSr");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0null77169 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0_add76988_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("w35He[LVCSr");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0_add76988 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0_literalMutationString76118_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("w35He[LVCSr");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.tvxt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0_literalMutationString76118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("w35He[LVCSr");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_add75072_literalMutationString75351_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            DefaultMustacheFactory o_testSimple_add75072__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("+:Bo!>OPs^F");
            StringWriter sw = new StringWriter();
            Writer o_testSimple_add75072__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimple_add75072__15 = TestUtil.getContents(root, "simple.txt");
            String o_testSimple_add75072__16 = sw.toString();
            junit.framework.TestCase.fail("testSimple_add75072_literalMutationString75351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +:Bo!>OPs^F not found", expected.getMessage());
        }
    }

    private static class LocalizedMustacheResolver extends DefaultResolver {
        private final Locale locale;

        LocalizedMustacheResolver(File root, Locale locale) {
            super(root);
            this.locale = locale;
        }

        @Override
        public Reader getReader(String resourceName) {
            int index = resourceName.lastIndexOf('.');
            String newResourceName;
            if (index == (-1)) {
                newResourceName = resourceName;
            } else {
                newResourceName = (((resourceName.substring(0, index)) + "_") + (locale.toLanguageTag())) + (resourceName.substring(index));
            }
            Reader reader = super.getReader(newResourceName);
            if (reader == null) {
                reader = super.getReader(resourceName);
            }
            return reader;
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                Mustache m = c.compile("Cdn#M<X|q[v");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0null35571_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                    Mustache m = c.compile("simple.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple_ko.txt");
                    sw.toString();
                }
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                    Mustache m = c.compile("Cdn#M<X|q[v");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796_failAssert0null35571 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0_add34927_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                    Mustache m = c.compile("simple.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple_ko.txt");
                    sw.toString();
                }
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                    Mustache m = c.compile("Cdn#M<X|q[v");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796_failAssert0_add34927 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27767_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("9]}Pr=xc^uU");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27767 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 9]}Pr=xc^uU not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90576_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90608_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "ChIis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_add91650_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_add91650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90215_failAssert0_literalMutationString91430_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("iRl6eJfYutBW![&ANu`");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90215 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90215_failAssert0_literalMutationString91430 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iRl6eJfYutBW![&ANu` not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0null91813_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll(null, "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0null91813 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90220_failAssert0_literalMutationString91200_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("DjjyY^-xXOeGV^DI<LF");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simpefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90220 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90220_failAssert0_literalMutationString91200 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template DjjyY^-xXOeGV^DI<LF not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public String filterText(String appended, boolean startOfLine) {
                    if (startOfLine) {
                        appended = appended.replaceAll("^[\t ]+", "");
                    }
                    return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                }
            };
            Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simplefiltered.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_add91646_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended.replaceAll("^[\t ]+", "");
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_add91646 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString77438_failAssert0_literalMutationBoolean77597_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("D00|)`p4k7o+[&", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438_failAssert0_literalMutationBoolean77597 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77438_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("D00|)`p4k7o+[&", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationBoolean77443_failAssert0_literalMutationString77733_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("xitZ3[wY>/yr1h", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationBoolean77443 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationBoolean77443_failAssert0_literalMutationString77733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecurision_literalMutationString77442_failAssert0_literalMutationString77633_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("r:cursio.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77442 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77442_failAssert0_literalMutationString77633 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r:cursio.html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77448_failAssert0_literalMutationString77605_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("NWN(B.%w(%agh>", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77448 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77448_failAssert0_literalMutationString77605 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template NWN(B.%w(%agh> not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77438_failAssert0_add77786_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("D00|)`p4k7o+[&", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438_failAssert0_add77786 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0_add116913_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursi%n_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0_add116913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0null116987_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0null116987 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_add116919_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_add116919 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_add116918_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_add116918 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritancenull116572_failAssert0_literalMutationString116867_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion_with_inhe]ritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritancenull116572 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritancenull116572_failAssert0_literalMutationString116867 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_with_inhe]ritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116555_failAssert0_literalMutationString116712_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("WJH(R_3fIFA0n2I#Yb<&9vL>S0iC2h)k", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116555 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116555_failAssert0_literalMutationString116712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template WJH(R_3fIFA0n2I#Yb<&9vL>S0iC2h)k not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116769_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "redursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116769 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116768_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "* XN](<g/3S1E");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0_literalMutationString116758_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursi%n_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "&Z:cz#v3RIOUm");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0_literalMutationString116758 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0null116985_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursi%n_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0null116985 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0() throws IOException {
        try {
            StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursi%n_with_inheritance.html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_remove116570_literalMutationString116695_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("A)G%1!#:( ,}:Kkm]J/#,0+k%$ ,8*(", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            String o_testRecursionWithInheritance_remove116570__10 = TestUtil.getContents(root, "recursion.txt");
            String o_testRecursionWithInheritance_remove116570__11 = sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_remove116570_literalMutationString116695 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationBoolean116559_failAssert0_literalMutationString116802_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("wwrXw3S)%Z!=Hp,@AwdmW]%a@EcrN*;", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationBoolean116559 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationBoolean116559_failAssert0_literalMutationString116802 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template wwrXw3S)%Z!=Hp,@AwdmW]%a@EcrN*; not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74379_failAssert0_literalMutationString74600_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("[AGDJ@5 *a$S;ikzv[*4%-}<} ZWUv+0/p", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_4partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74379 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74379_failAssert0_literalMutationString74600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74732_failAssert0() throws IOException {
        try {
            {
                execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74732 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74733_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74587_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,ImVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74587 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,ImVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_remove74384_literalMutationString74496_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("<#?DiG?fGM[HC`f*AuiNj`QL@Lqc>waL(.", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_remove74384__10 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_remove74384__11 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove74384_literalMutationString74496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <#?DiG?fGM[HC`f*AuiNj`QL@Lqc>waL(. not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0null74803_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0null74803 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74595_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "F!F6FRvt^u<!2PoBIkLmZ0v{eRjRX+]TS");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74595 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString82492_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("AwmL4/d|q&+zPt3>f");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82492 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimplePragma_literalMutationString82490_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("sim[lepragma.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82490 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sim[lepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString82499_literalMutationString83228_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("s:implepragma.html");
            StringWriter sw = new StringWriter();
            Writer o_testSimplePragma_literalMutationString82499__7 = m.execute(sw, new Object() {
                String name = "Ch<ris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimplePragma_literalMutationString82499__14 = TestUtil.getContents(root, "simple.txt");
            String o_testSimplePragma_literalMutationString82499__15 = sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82499_literalMutationString83228 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s:implepragma.html not found", expected.getMessage());
        }
    }

    private class OkGenerator {
        public boolean isItOk() {
            return true;
        }
    }

    private String getOutput(final boolean setObjectHandler) {
        final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
        if (setObjectHandler) {
            mustacheFactory.setObjectHandler(new SimpleObjectHandler());
        }
        final Mustache defaultMustache = mustacheFactory.compile(new StringReader("{{#okGenerator.isItOk}}{{okGenerator.isItOk}}{{/okGenerator.isItOk}}"), "Test template");
        final Map<String, Object> params = new HashMap<>();
        params.put("okGenerator", new AmplInterpreterTest.OkGenerator());
        final Writer writer = new StringWriter();
        defaultMustache.execute(writer, params);
        return writer.toString();
    }

    public void testMultipleWrappers_literalMutationString96576_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("(@,U|y9Ak!n");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString96576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (@,U|y9Ak!n not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3232_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("@xl+Y)]GWMwTIPF2");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "How";
                };

                Callable<Object> nested = () -> {
                    Thread.sleep(200);
                    return "are";
                };

                Callable<Object> nestest = () -> {
                    Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3232 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @xl+Y)]GWMwTIPF2 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0null36711_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList(null);
                };
                StringWriter sw = execute("IX@{$(@!}+lu", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0null36711 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0_literalMutationString36298_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("p:|P[(1jN54{", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0_literalMutationString36298 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template p:|P[(1jN54{ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("IX@{$(@!}+lu", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_add35947_literalMutationString36113_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("is<empty.html", object);
            sw.toString();
            String o_testIsNotEmpty_add35947__10 = TestUtil.getContents(root, "isempty.txt");
            String o_testIsNotEmpty_add35947__11 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_add35947_literalMutationString36113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template is<empty.html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0_add36577_failAssert0() throws IOException {
        try {
            {
                Collections.singletonList("Test");
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("IX@{$(@!}+lu", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0_add36577 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    private StringWriter execute(String name, Object object) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, object);
        return sw;
    }

    private StringWriter execute(String name, List<Object> objects) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, objects);
        return sw;
    }

    public void testImmutableList_literalMutationString51915_literalMutationString52173_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("page1.txt");
            };
            StringWriter sw = execute("87-44HsftRG{", Collections.singletonList(object));
            String o_testImmutableList_literalMutationString51915__10 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_literalMutationString51915__11 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51915_literalMutationString52173 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 87-44HsftRG{ not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("(#9:<eP/GIH4", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0_literalMutationString52368_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("(#9:<eP/GIH4", Collections.singletonList(object));
                TestUtil.getContents(root, "isemptytxt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922_failAssert0_literalMutationString52368 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0_add52624_failAssert0() throws IOException {
        try {
            {
                Collections.singletonList("Test");
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("(#9:<eP/GIH4", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922_failAssert0_add52624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51914_literalMutationString52156_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Qest");
            };
            StringWriter sw = execute("]sempty.html", Collections.singletonList(object));
            String o_testImmutableList_literalMutationString51914__10 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_literalMutationString51914__11 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51914_literalMutationString52156 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]sempty.html not found", expected.getMessage());
        }
    }

    public void testSecurity_remove87161_literalMutationString88216_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile(":j!(i%D99+Oy=");
            StringWriter sw = new StringWriter();
            String o_testSecurity_remove87161__7 = TestUtil.getContents(root, "security.txt");
            String o_testSecurity_remove87161__8 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_remove87161_literalMutationString88216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :j!(i%D99+Oy= not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString87123_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("1*3]/o>pXj|oc");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString87123 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testProperties_literalMutationString93201_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("$zi@B!;C]$S");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String getName() {
                    return "Chris";
                }

                int getValue() {
                    return 10000;
                }

                int taxed_value() {
                    return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                }

                boolean isIn_ca() {
                    return true;
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0null95557_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("$zi@B!;C]$S");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 10000;
                    }

                    int taxed_value() {
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0null95557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0_literalMutationString94589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("$zi@B!C]$S");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 10000;
                    }

                    int taxed_value() {
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0_literalMutationString94589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $zi@B!C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0_add95390_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("$zi@B!;C]$S");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 10000;
                    }

                    int taxed_value() {
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0_add95390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_remove21732_literalMutationString23464_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("J.I Lc(ru:.", new HashMap<String, Object>() {
                {
                    Object o_testSimpleWithMap_remove21732__8 = put("name", "Chris");
                    Object o_testSimpleWithMap_remove21732__9 = put("taxed_value", 6000);
                    Object o_testSimpleWithMap_remove21732__10 = put("in_ca", true);
                }
            });
            String o_testSimpleWithMap_remove21732__11 = TestUtil.getContents(root, "simple.txt");
            String o_testSimpleWithMap_remove21732__12 = sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_remove21732_literalMutationString23464 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template J.I Lc(ru:. not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString21717_failAssert0_literalMutationString25471_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                StringWriter sw = execute("t/6eME!b( {", new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21717 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21717_failAssert0_literalMutationString25471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleWithMap_literalMutationString21671_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("simple.ht]ml", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21671 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull78129_failAssert0_literalMutationString78244_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(">O u^D-ZzW?#qL]b72!>!!hu5#^G+s");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull78129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull78129_failAssert0_literalMutationString78244 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >O u^D-ZzW?#qL]b72!>!!hu5#^G+s not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0null78447_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0null78447 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78219_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("parti}lintemplatKefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78219 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template parti}lintemplatKefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78398_failAssert0() throws MustacheException, IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78217_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("[2qgDIC}H]I(e]zUqM-i %Iy1&<2R8C");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78217 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [2qgDIC}H]I(e]zUqM-i %Iy1&<2R8C not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add78123_literalMutationString78182_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add78123__3 = c.compile("partialintemplatefunction.html");
            Mustache m = c.compile("W[r?#*(THgt^K|nTv) w=O!|_>{IrE");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add78123__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            String o_testPartialWithTF_add78123__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add78123_literalMutationString78182 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W[r?#*(THgt^K|nTv) w=O!|_>{IrE not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull78129_failAssert0_literalMutationString78245_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partiali<templatefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull78129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull78129_failAssert0_literalMutationString78245 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partiali<templatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78399_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78399 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78400_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78400 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78231_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86Pm#o.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78231 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86Pm#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78233_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Cz]<o^1%FBgZyrMowO(77Rix6U{(k8");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78233 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Cz]<o^1%FBgZyrMowO(77Rix6U{(k8 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78211_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`J!IhrKf6");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78211 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `J!IhrKf6 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78212_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78212 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78120_failAssert0_literalMutationString78237_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("v]3L>M+^p4&.q:_ORW-TR8!(ip%`N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78120 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78120_failAssert0_literalMutationString78237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v]3L>M+^p4&.q:_ORW-TR8!(ip%`N not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove78126_literalMutationString78203_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("-dO9F,Mm*+?`{LBWe(]:.7Qtp1|0u>");
            StringWriter sw = new StringWriter();
            String o_testPartialWithTF_remove78126__7 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove78126_literalMutationString78203 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -dO9F,Mm*+?`{LBWe(]:.7Qtp1|0u> not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121506_failAssert0_literalMutationString121999_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("comp{ex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121506 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121506_failAssert0_literalMutationString121999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comp{ex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121499_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("AlUw#pDJ#Z,n");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString121499 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template AlUw#pDJ#Z,n not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123837_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("7SzWAlh|C(ye");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString123837 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 7SzWAlh|C(ye not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123842_failAssert0_literalMutationString124481_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("Yc7,D[D,:zJ]H");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString123842 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123842_failAssert0_literalMutationString124481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Yc7,D[D,:zJ]H not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123853_failAssert0_add125143_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                jf.createJsonParser(json.toString()).readValueAsTree();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.h[tml");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString123853 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123853_failAssert0_add125143 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h[tml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121513_failAssert0_literalMutationString121831_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("co%mplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("co!plex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121513 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121513_failAssert0_literalMutationString121831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co%mplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121517_failAssert0_literalMutationString122061_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("& 0g<(IN0KSt");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.tt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121517 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121517_failAssert0_literalMutationString122061 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template & 0g<(IN0KSt not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_add122975_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_add122975 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_add122974_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_add122974 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_literalMutationString122193_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "((,fpq}G*m;");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_literalMutationString122193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("b`FV)LG`?tgm");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123844_failAssert0_literalMutationString124411_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("compl[ex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString123844 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123844_failAssert0_literalMutationString124411 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template compl[ex.html not found", expected.getMessage());
        }
    }

    public void testComplexnull123888_failAssert0_literalMutationString124888_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("C&TZkzg:%koi");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, null);
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull123888 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull123888_failAssert0_literalMutationString124888 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C&TZkzg:%koi not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123849_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("Y7_G1$%<xGl`");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString123849 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y7_G1$%<xGl` not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123853_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.h[tml");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString123853 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h[tml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_literalMutationString122189_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                TestUtil.getContents(root, "comple|x.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_literalMutationString122189 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39221_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("30+r <{dzN,|");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39221 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 30+r <{dzN,| not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0_add39569_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("co[mplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject());
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0_add39569 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39552_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39550_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39550 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("jlOVLSA?Y<RV");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("co[mplex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0null39682_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("co[mplex.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0null39682 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0_literalMutationString39265_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("co[mplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "compl4ex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0_literalMutationString39265 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39035_failAssert0_literalMutationString39272_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("complex:.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39035 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39035_failAssert0_literalMutationString39272 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex:.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0null39674_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0null39674 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0null39673_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0null39673 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39223_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39546_failAssert0() throws MustacheException, IOException {
        try {
            {
                initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39029_failAssert0_literalMutationString39235_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("RV^:E|_N6");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39029 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39029_failAssert0_literalMutationString39235 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RV^:E|_N6 not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39228_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("jlOVLSA?Y<RV");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "bfGxMkh]&Cz");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39228 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute(">if>G?19BchB", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add43357_literalMutationString43440_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter o_testSerialCallable_add43357__1 = execute("complex.html", new ParallelComplexObject());
            StringWriter sw = execute(",2l-=KW>A0n8", new ParallelComplexObject());
            String o_testSerialCallable_add43357__6 = TestUtil.getContents(root, "complex.txt");
            String o_testSerialCallable_add43357__7 = sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add43357_literalMutationString43440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,2l-=KW>A0n8 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_add43636_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(">if>G?19BchB", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_add43636 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_add43635_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(">if>G?19BchB", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_add43635 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0null43685_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(">if>G?19BchB", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0null43685 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43511_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(">if>G?19BchB", new ParallelComplexObject());
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43346_failAssert0_literalMutationString43472_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(" does not exist", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43346 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43346_failAssert0_literalMutationString43472 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add43359_literalMutationString43401_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("CMsS!CUDc )(", new ParallelComplexObject());
            sw.toString();
            String o_testSerialCallable_add43359__5 = TestUtil.getContents(root, "complex.txt");
            String o_testSerialCallable_add43359__6 = sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add43359_literalMutationString43401 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template CMsS!CUDc )( not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43508_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(">if>G?19Bc8hB", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >if>G?19Bc8hB not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0null51335_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(null, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0null51335 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48872_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("va5ue", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48872 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0_literalMutationString48762_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "page1.txt");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0_literalMutationString48762 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0null51226_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", null);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0null51226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47942_failAssert0_literalMutationNumber48617_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "seimple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47942 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47942_failAssert0_literalMutationNumber48617 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + seimple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_literalMutationString49141_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_literalMutationString49141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0_add50659_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0_add50659 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_add50743_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    tc.startOfLine();
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_add50743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0null51234_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0null51234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("}")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48844_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("FaiSed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48844 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0null51240_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), null, "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0null51240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0null51321_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0null51321 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_add50686_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_add50686 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_add50750_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_add50750 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_add50665_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                variable.startsWith("}");
                                if (variable.startsWith("}")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_add50665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("V|+eKL?p+)");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0null14934_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("V|+eKL?p+)");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString14235_failAssert0null14934 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadme_add14247_literalMutationString14311_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("iG&M[I9hAL");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add14247__9 = m.execute(sw, new AmplInterpreterTest.Context());
            Writer o_testReadme_add14247__11 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add14247__15 = TestUtil.getContents(root, "items.txt");
            String o_testReadme_add14247__16 = sw.toString();
            junit.framework.TestCase.fail("testReadme_add14247_literalMutationString14311 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iG&M[I9hAL not found", expected.getMessage());
        }
    }

    public void testReadmenull14254_failAssert0_literalMutationString14556_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("item[.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadmenull14254 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmenull14254_failAssert0_literalMutationString14556 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template item[.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0_add14819_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("V|+eKL?p+)");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString14235_failAssert0_add14819 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0_literalMutationString2546_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items%.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_37 = "Should be a little bit more than 48seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0_literalMutationString2546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2000_failAssert0_literalMutationString2589_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_41 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_42 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2000 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2000_failAssert0_literalMutationString2589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0null2985_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("v)LA|PjLTh9");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0null2985 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0_literalMutationNumber2344_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("v)LA|PjLTh9");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 0);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0_literalMutationNumber2344 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0_add2915_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items%.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_37 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0_add2915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("v)LA|PjLTh9");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_23 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_24 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2006_failAssert0_literalMutationString2412_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("$T_H--aJni>");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "itemsvtxt");
                sw.toString();
                String String_29 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2006 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2006_failAssert0_literalMutationString2412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $T_H--aJni> not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("items%.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_37 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_38 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0null3005_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items%.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_37 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0null3005 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2016_literalMutationString2202_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("$_8K%!{;g(#");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            Writer o_testReadmeSerial_add2016__9 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadmeSerial_add2016__13 = TestUtil.getContents(root, "items.txt");
            String o_testReadmeSerial_add2016__14 = TestUtil.getContents(root, "items.txt");
            String o_testReadmeSerial_add2016__15 = sw.toString();
            String String_13 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_14 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_add2016_literalMutationString2202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $_8K%!{;g(# not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0_add2863_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("v)LA|PjLTh9");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0_add2863 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_literalMutationNumber103957_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 1999);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_literalMutationNumber103957 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_add104517_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_add104517 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("ite|ms2.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("W)g%tpQx^Wu");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_139 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_140 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_literalMutationString103938_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_literalMutationString103938 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0null104684_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0null104684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0null104685_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0null104685 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104168_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 1000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103587_remove104624() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add103587__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__17);
        String o_testReadmeParallel_add103587__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__18);
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104161_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 998) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104161 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104572_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104572 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallelnull103594_failAssert0_literalMutationString104235_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("XF8`i=yZNlz");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_163 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_164 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallelnull103594 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallelnull103594_failAssert0_literalMutationString104235 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XF8`i=yZNlz not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0null104663_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0null104663 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0null104664_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0null104664 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104573_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104573 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104574_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103589_remove104626() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add103589__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__15);
        String o_testReadmeParallel_add103589__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__16);
        String String_125 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
        boolean boolean_126 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_add104514_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("W)g%tpQx^Wu");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_add104514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationString104155_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("ite|ms2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "page1.txt" + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationString104155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103585_literalMutationNumber103836() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        Mustache o_testReadmeParallel_add103585__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add103585__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add103585__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add103585__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__15);
        String o_testReadmeParallel_add103585__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__16);
        String String_135 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
        boolean boolean_136 = (diff > 1000) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add103585__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add103585__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
    }

    static class Context {
        List<AmplInterpreterTest.Context.Item> items() {
            return Arrays.asList(new AmplInterpreterTest.Context.Item("Item 1", "$19.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("New!"), new AmplInterpreterTest.Context.Feature("Awesome!"))), new AmplInterpreterTest.Context.Item("Item 2", "$29.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("Old."), new AmplInterpreterTest.Context.Feature("Ugly."))));
        }

        static class Item {
            Item(String name, String price, List<AmplInterpreterTest.Context.Feature> features) {
                this.name = name;
                this.price = price;
                this.features = features;
            }

            String name;

            String price;

            List<AmplInterpreterTest.Context.Feature> features;
        }

        static class Feature {
            Feature(String description) {
                this.description = description;
            }

            String description;

            Callable<String> desc() throws InterruptedException {
                return () -> {
                    Thread.sleep(1000);
                    return description;
                };
            }
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile(";hm51Ny&9DX|d");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0null43070_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(";hm51Ny&9DX|d");
                StringWriter sw = new StringWriter();
                m.execute(null, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0null43070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0_literalMutationString42284_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(";hm51NI&9DX|d");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0_literalMutationString42284 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;hm51NI&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0_add42845_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(";hm51Ny&9DX|d");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0_add42845 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41866_failAssert0_literalMutationString42388_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("&JPsJH}$z][wT");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "defergred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41866 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41866_failAssert0_literalMutationString42388 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &JPsJH}$z][wT not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add92209_literalMutationString92345_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("so}skeDd$t7{[t&vz9}!b1gYRzr");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_add92209__19 = TestUtil.getContents(root, "relative/paths.txt");
            String o_testRelativePathsTemplateFunction_add92209__20 = TestUtil.getContents(root, "relative/paths.txt");
            String o_testRelativePathsTemplateFunction_add92209__21 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add92209_literalMutationString92345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template so}skeDd$t7{[t&vz9}!b1gYRzr not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("1m0a$@xkeYti%+%?cWN%w$> AQR");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1m0a$@xkeYti%+%?cWN%w$> AQR not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_add92755_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("1m0a$@xkeYti%+%?cWN%w$> AQR");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_add92755 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1m0a$@xkeYti%+%?cWN%w$> AQR not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_remove92211_literalMutationString92393_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("M`#![4.lumpuIr9y%TFQ7*&3R;[");
            StringWriter sw = new StringWriter();
            String o_testRelativePathsTemplateFunction_remove92211__7 = TestUtil.getContents(root, "relative/paths.txt");
            String o_testRelativePathsTemplateFunction_remove92211__8 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_remove92211_literalMutationString92393 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M`#![4.lumpuIr9y%TFQ7*&3R;[ not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunctionnull92215_failAssert0_literalMutationString92548_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("n8^.OxPvQC)m]jtaN+n0O^vIxxm");
                StringWriter sw = new StringWriter();
                compile.execute(null, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull92215 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull92215_failAssert0_literalMutationString92548 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template n8^.OxPvQC)m]jtaN+n0O^vIxxm not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_literalMutationString92513_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("1m0a$@xkeYti%+%?WN%w$> AQR");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_literalMutationString92513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1m0a$@xkeYti%+%?WN%w$> AQR not found", expected.getMessage());
        }
    }

    private static class AccessTrackingMap extends HashMap<String, String> {
        Set<String> accessed = new HashSet<>();

        @Override
        public String get(Object key) {
            accessed.add(((String) (key)));
            return super.get(key);
        }

        void check() {
            Set<String> keyset = new HashSet<>(keySet());
            keyset.removeAll(accessed);
            if (!(keyset.isEmpty())) {
                throw new MustacheException("All keys in the map were not accessed");
            }
        }
    }

    private AmplInterpreterTest.AccessTrackingMap createBaseMap() {
        AmplInterpreterTest.AccessTrackingMap accessTrackingMap = new AmplInterpreterTest.AccessTrackingMap();
        accessTrackingMap.put("first", "Sam");
        accessTrackingMap.put("last", "Pullara");
        return accessTrackingMap;
    }

    private static class SuperClass {
        String values = "value";
    }

    private DefaultMustacheFactory initParallel() {
        DefaultMustacheFactory cf = createMustacheFactory();
        cf.setExecutorService(Executors.newCachedThreadPool());
        return cf;
    }

    protected void setUp() throws Exception {
        super.setUp();
        File file = new File("src/test/resources");
        root = (new File(file, "simple.html").exists()) ? file : new File("../src/test/resources");
    }
}


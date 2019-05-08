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

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("M]>QquCaw2F");
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
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_remove187610_literalMutationString187758_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("s{mple.html");
            StringWriter sw = new StringWriter();
            String o_testSimple_remove187610__7 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_remove187610_literalMutationString187758 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s{mple.html not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0null193629_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("M]>QquCaw2F");
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
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559_failAssert0null193629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_add193289_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("M]>QquCaw2F");
                    Mustache m = c.compile("M]>QquCaw2F");
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
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_add193289 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_literalMutationString192195_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("M]>QquCaw2F");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "C+ris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_literalMutationString192195 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_literalMutationString192193_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("M]>QquCawF");
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
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_literalMutationString192193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCawF not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0null189751_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("M]>QquCaw2F");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0null189751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_add193290_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("M]>QquCaw2F");
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
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString187579_failAssert0_add189559_failAssert0_add193290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString187579_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("M]>QquCaw2F");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString187579 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template M]>QquCaw2F not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationNumber187586_literalMutationString187948_failAssert0_literalMutationString191793_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("J^?a!DU+`1r");
                StringWriter sw = new StringWriter();
                Writer o_testSimple_literalMutationNumber187586__7 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 0;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimple_literalMutationNumber187586__15 = TestUtil.getContents(root, "si=ple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationNumber187586_literalMutationString187948 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationNumber187586_literalMutationString187948_failAssert0_literalMutationString191793 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template J^?a!DU+`1r not found", expected.getMessage());
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

    public void testSimpleI18N_literalMutationString71142_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile(".^0+T.Ia|*S");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString71142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .^0+T.Ia|*S not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString71199_failAssert0_literalMutationString75721_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                    Mustache m = c.compile("$+_oHX35##=");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "7M6@a]|LoH");
                    sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationString71199 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString71199_failAssert0_literalMutationString75721 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $+_oHX35##= not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_remove71209_literalMutationString71527_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_remove71209__9 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_remove71209__15 = TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                Mustache m = c.compile("I/c*u}@`JM&");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_remove71209__25 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_remove71209__32 = TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_remove71209_literalMutationString71527 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template I/c*u}@`JM& not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232755_failAssert0_literalMutationString233618_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("7f$c4yKjlb7+iFH:9rK");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "26(t/l}M]sA|cDS;B?");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232755 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232755_failAssert0_literalMutationString233618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 7f$c4yKjlb7+iFH:9rK not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0_literalMutationNumber234204_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 0;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0_literalMutationNumber234204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0_add234455_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
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
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0_add234455 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0_add234453_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0_add234453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232731_failAssert0_literalMutationString234253_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("hlG6LX%+ 90RfvQOGtS^");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232731 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232731_failAssert0_literalMutationString234253 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template hlG6LX%+ 90RfvQOGtS^ not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0null234666_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0null234666 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0_literalMutationString234192_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("oTdQ,} [F5;QIjyxRE:");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0_literalMutationString234192 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oTdQ,} [F5;QIjyxRE: not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0null234672_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", null);
                    }
                };
                Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728_failAssert0null234672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString232728_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            Mustache m = c.compile("iV1ha%;RO]7<(+y<yV%");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString232728 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iV1ha%;RO]7<(+y<yV% not found", expected.getMessage());
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString193897_failAssert0_add194264_failAssert0_literalMutationString195436_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("&) |HuZaWQx#n/", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString193897 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString193897_failAssert0_add194264 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193897_failAssert0_add194264_failAssert0_literalMutationString195436 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &) |HuZaWQx#n/ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193893_failAssert0_add194252_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("kSGLN9&?>1s6(+", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193893_failAssert0_add194252 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kSGLN9&?>1s6(+ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193893_failAssert0_literalMutationBoolean194076_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("kSGLN9&?>1s6(+", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193893_failAssert0_literalMutationBoolean194076 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kSGLN9&?>1s6(+ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193901_failAssert0_literalMutationString194087_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("8B18&wb[-2#R@E", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "raecursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193901 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193901_failAssert0_literalMutationString194087 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 8B18&wb[-2#R@E not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0_literalMutationString195522_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(",9#Ts?XkLgUE1<<", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "6JNW*TsGOOP=>");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString193891 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0_literalMutationString195522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,9#Ts?XkLgUE1<< not found", expected.getMessage());
        }
    }

    public void testRecurision_add193903_add194220_literalMutationString194703_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("2%O a]_NQq^2>H", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            String o_testRecurision_add193903_add194220__11 = TestUtil.getContents(root, "recursion.txt");
            String o_testRecurision_add193903__11 = TestUtil.getContents(root, "recursion.txt");
            String o_testRecurision_add193903__12 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_add193903_add194220_literalMutationString194703 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 2%O a]_NQq^2>H not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193893_failAssert0_add194249_failAssert0() throws IOException {
        try {
            {
                execute("kSGLN9&?>1s6(+", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                StringWriter sw = execute("kSGLN9&?>1s6(+", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193893_failAssert0_add194249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kSGLN9&?>1s6(+ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193893_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("kSGLN9&?>1s6(+", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString193893 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kSGLN9&?>1s6(+ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(",9#Ts?XkLgUE1<<", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193891 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,9#Ts?XkLgUE1<< not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationBoolean193895_failAssert0null194329_failAssert0_literalMutationString194971_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("55vyfxB}3QG([W", new Object() {
                        Object value = new Object() {
                            boolean value = true;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationBoolean193895 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationBoolean193895_failAssert0null194329 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationBoolean193895_failAssert0null194329_failAssert0_literalMutationString194971 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 55vyfxB}3QG([W not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0null196382_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(",9#Ts?XkLgUE1<<", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString193891 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0null196382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,9#Ts?XkLgUE1<< not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0_add196097_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(",9#Ts?XkLgUE1<<", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursion.txt");
                    TestUtil.getContents(root, "recursion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString193891 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193891_failAssert0_literalMutationString194121_failAssert0_add196097 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,9#Ts?XkLgUE1<< not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193893_failAssert0null194331_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("kSGLN9&?>1s6(+", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193893_failAssert0null194331 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kSGLN9&?>1s6(+ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString193892_failAssert0_literalMutationString194098_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("pa>ge1.txt", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString193892 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString193892_failAssert0_literalMutationString194098 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template pa>ge1.txt not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add306000_add306318_literalMutationString306986_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("9^^V_E GoSJNNW[x# s.NogNsss@F+z", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            String o_testRecursionWithInheritance_add306000__12 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add306000_add306318_literalMutationString306986 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 9^^V_E GoSJNNW[x# s.NogNsss@F+z not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0null308498_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0null308498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0null306427_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0null306427 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305987_failAssert0_add306349_failAssert0_literalMutationString307531_failAssert0() throws IOException {
        try {
            {
                {
                    execute(" does not exist", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    StringWriter sw = execute("page1.txt", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305987 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305987_failAssert0_add306349 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305987_failAssert0_add306349_failAssert0_literalMutationString307531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0_add308153_failAssert0() throws IOException {
        try {
            {
                {
                    execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0_add308153 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0_literalMutationString306175_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "reursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_literalMutationString306175 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0_literalMutationString307514_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursiJon.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305989_failAssert0_add306348_failAssert0_literalMutationString307514 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Y}t16Zr%3*M0K[3w<$b?b!kprNOg.&X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString305992_failAssert0_literalMutationString306273_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@@ }tJ*Hq/A<&n1`p&)`F# P,HpggC^", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305992 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString305992_failAssert0_literalMutationString306273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @@ }tJ*Hq/A<&n1`p&)`F# P,HpggC^ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184715_failAssert0_add185070_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("/<86:EF$vxP(snI{:D-NV#N1a:[<na9&ws", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184715 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184715_failAssert0_add185070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /<86:EF$vxP(snI{:D-NV#N1a:[<na9&ws not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184716_failAssert0_literalMutationString184899_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(",Of`SCD|-kK8!A#]CLqQ6qPcF!$){@K?r", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184716_failAssert0_literalMutationString184899 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,Of`SCD|-kK8!A#]CLqQ6qPcF!$){@K?r not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184715_failAssert0_literalMutationString184886_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("/<8:EF$vxP(snI{:D-NV#N1a:[<na9&ws", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184715 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184715_failAssert0_literalMutationString184886 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /<8:EF$vxP(snI{:D-NV#N1a:[<na9&ws not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString185000_failAssert0_literalMutationString186453_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("GhwM$y<@?mHZ *6q}lg!g0c4& _mhOw#q", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString185000 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString185000_failAssert0_literalMutationString186453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template GhwM$y<@?mHZ *6q}lg!g0c4& _mhOw#q not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir<", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir< not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_add186842_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir<", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_add186842 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir< not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_literalMutationBoolean185926_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir<", new Object() {
                        Object test = new Object() {
                            boolean test = true;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_literalMutationBoolean185926 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir< not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0null187242_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir<", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0null187242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir< not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritancenull184732_failAssert0_literalMutationString184876_failAssert0_literalMutationString186070_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(" does not exist", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull184732 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull184732_failAssert0_literalMutationString184876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull184732_failAssert0_literalMutationString184876_failAssert0_literalMutationString186070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184715_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("/<86:EF$vxP(snI{:D-NV#N1a:[<na9&ws", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /<86:EF$vxP(snI{:D-NV#N1a:[<na9&ws not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_add186843_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir<", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_add186843 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -ir< not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_literalMutationString185924_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("KN}&?ve<hRN3sfj_h[cV+_pht]a.x -r<", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString184720_failAssert0_literalMutationString184998_failAssert0_literalMutationString185924 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template KN}&?ve<hRN3sfj_h[cV+_pht]a.x -r< not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210139_failAssert0_literalMutationString211309_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Jj?Gr[R#Z=i6kU.|5");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chrs";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139_failAssert0_literalMutationString211309 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jj?Gr[R#Z=i6kU.|5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210164_failAssert0_literalMutationNumber211500_failAssert0_literalMutationString214744_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("HziR_{kgHD!o}4eQg");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * -0.6)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple.xt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString210164 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString210164_failAssert0_literalMutationNumber211500 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210164_failAssert0_literalMutationNumber211500_failAssert0_literalMutationString214744 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template HziR_{kgHD!o}4eQg not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210138_failAssert0_literalMutationString211424_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1i|C_DO_)n31s%XurX");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString210138 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210138_failAssert0_literalMutationString211424 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1i|C_DO_)n31s%XurX not found", expected.getMessage());
        }
    }

    public void testSimplePragma_remove210172_remove212153_literalMutationString212567_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simplepragma.[html");
            StringWriter sw = new StringWriter();
            Writer o_testSimplePragma_remove210172__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimplePragma_remove210172__13 = TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.fail("testSimplePragma_remove210172_remove212153_literalMutationString212567 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.[html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210139_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Jj?Gr[R#Z=i6kU.|5");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jj?Gr[R#Z=i6kU.|5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210139_failAssert0_add212105_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("Jj?Gr[R#Z=i6kU.|5");
                Mustache m = c.compile("Jj?Gr[R#Z=i6kU.|5");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139_failAssert0_add212105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jj?Gr[R#Z=i6kU.|5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString210139_failAssert0null212306_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Jj?Gr[R#Z=i6kU.|5");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString210139_failAssert0null212306 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jj?Gr[R#Z=i6kU.|5 not found", expected.getMessage());
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

    public void testMultipleWrappers_literalMutationNumber263080_literalMutationString263918_literalMutationString268146_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("T7Q%R7GE:^!");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationNumber263080__7 = m.execute(sw, new Object() {
                String name = "Chiris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 1.4)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationNumber263080__24 = TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber263080_literalMutationString263918_literalMutationString268146 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template T7Q%R7GE:^! not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString263067_failAssert0_add266384_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Em2$6/w&`Vv");
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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString263067 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString263067_failAssert0_add266384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Em2$6/w&`Vv not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString263067_failAssert0_literalMutationString265278_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Em2$6/w&`Vv");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "ChEis";

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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString263067 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString263067_failAssert0_literalMutationString265278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Em2$6/w&`Vv not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString263067_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Em2$6/w&`Vv");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString263067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Em2$6/w&`Vv not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber263081_literalMutationString264694_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("?NI`:-1=&#@");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationNumber263081__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * -0.6)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationNumber263081__24 = TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber263081_literalMutationString264694 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ?NI`:-1=&#@ not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber263085_literalMutationString264738_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("sim%ple.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationNumber263085__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.8)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationNumber263085__24 = TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber263085_literalMutationString264738 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sim%ple.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11543_failAssert0_add15476_failAssert0_literalMutationString19867_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    DefaultMustacheFactory c = createMustacheFactory();
                    c.setExecutorService(Executors.newCachedThreadPool());
                    Mustache m = c.compile(" does not exist");
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
                    junit.framework.TestCase.fail("testNestedLatches_literalMutationString11543 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11543_failAssert0_add15476 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11543_failAssert0_add15476_failAssert0_literalMutationString19867 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11540_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("e>nIHT^=bR:Q,4{T");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11540 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template e>nIHT^=bR:Q,4{T not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11552_literalMutationString12259_failAssert0_add21730_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("<U[=xL{!6o}>&!?$");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return ")ow";
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11552_literalMutationString12259 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11552_literalMutationString12259_failAssert0_add21730 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <U[=xL{!6o}>&!?$ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11552_literalMutationString12259_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("<U[=xL{!6o}>&!?$");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return ")ow";
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11552_literalMutationString12259 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <U[=xL{!6o}>&!?$ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11555_literalMutationString12466_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("ue;jKUu>36},DQq6");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "H/ow";
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11555_literalMutationString12466 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ue;jKUu>36},DQq6 not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11552_literalMutationString12259_failAssert0_literalMutationString19528_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("<U[=xL{!6o}>&!?$");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return ")ow";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "page1.txt";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11552_literalMutationString12259 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11552_literalMutationString12259_failAssert0_literalMutationString19528 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <U[=xL{!6o}>&!?$ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("Mm*+?`{LBWe(", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Mm*+?`{LBWe( not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0_add88638_failAssert0() throws IOException {
        try {
            {
                Collections.singletonList("Test");
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("{cJ5>OXp2]*-", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_add88638 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {cJ5>OXp2]*- not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401_failAssert0_literalMutationString90275_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Tst");
                    };
                    StringWriter sw = execute("Mm*+?`{LBWe(", object);
                    TestUtil.getContents(root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401_failAssert0_literalMutationString90275 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Mm*+?`{LBWe( not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_remove87988_remove88683_literalMutationString89155_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("ri;6_:UTU!R[", object);
            String o_testIsNotEmpty_remove87988__8 = TestUtil.getContents(this.root, "isempty.txt");
            junit.framework.TestCase.fail("testIsNotEmpty_remove87988_remove88683_literalMutationString89155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ri;6_:UTU!R[ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("{cJ5>OXp2]*-", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {cJ5>OXp2]*- not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87966_literalMutationString88167_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("{!ln");
            };
            StringWriter sw = execute("*!BS[xJ9@84(", object);
            String o_testIsNotEmpty_literalMutationString87966__9 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87966_literalMutationString88167 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *!BS[xJ9@84( not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0null88785_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("{cJ5>OXp2]*-", null);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0null88785 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {cJ5>OXp2]*- not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401_failAssert0_add91242_failAssert0() throws IOException {
        try {
            {
                {
                    Collections.singletonList("Test");
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute("Mm*+?`{LBWe(", object);
                    TestUtil.getContents(root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87976_failAssert0_literalMutationString88401_failAssert0_add91242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Mm*+?`{LBWe( not found", expected.getMessage());
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

    public void testImmutableList_literalMutationString143313_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("isemp[y.html", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143309_failAssert0_literalMutationString143848_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("<<<e48Iz&*S[", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143309 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143309_failAssert0_literalMutationString143848 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <<<e48Iz&*S[ not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143313_failAssert0null144193_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("isemp[y.html", Collections.singletonList(null));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0null144193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("isemp[y.html", Collections.singletonList(object));
                TestUtil.getContents(root, "2Eht[AfzuQ#");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143313_failAssert0_add144032_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                Collections.singletonList(object);
                StringWriter sw = execute("isemp[y.html", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_add144032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143314_failAssert0null144195_failAssert0_literalMutationString145534_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList(null);
                    };
                    StringWriter sw = execute("2h=p!i^LudA", Collections.singletonList(object));
                    TestUtil.getContents(root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString143314 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143314_failAssert0null144195 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143314_failAssert0null144195_failAssert0_literalMutationString145534 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 2h=p!i^LudA not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780_failAssert0_add146727_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute("isemp[y.html", Collections.singletonList(object));
                    TestUtil.getContents(root, "2Eht[AfzuQ#");
                    TestUtil.getContents(root, "2Eht[AfzuQ#");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780_failAssert0_add146727 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143305null144138_literalMutationString144853_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList(null);
            };
            StringWriter sw = execute("`*L{j,Z -9<;", Collections.singletonList(object));
            String o_testImmutableList_literalMutationString143305__10 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143305null144138_literalMutationString144853 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `*L{j,Z -9<; not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780_failAssert0_literalMutationString145858_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute("isemp[y.html", Collections.singletonList(object));
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString143313 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString143313_failAssert0_literalMutationString143780_failAssert0_literalMutationString145858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemp[y.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString224931_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("c8MNyZ9cAw[48");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString224931 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c8MNyZ9cAw[48 not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString224954_literalMutationString225665_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("tgH>Vh1h ]P{P");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationString224954__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "page1.txt";
            });
            String o_testSecurity_literalMutationString224954__15 = TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString224954_literalMutationString225665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template tgH>Vh1h ]P{P not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString224957_literalMutationString226180_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("L.s?%iU(,N[X]");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationString224957__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "y?P=";
            });
            String o_testSecurity_literalMutationString224957__15 = TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString224957_literalMutationString226180 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template L.s?%iU(,N[X] not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString224956_literalMutationString226068_failAssert0_literalMutationString230504_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("secu]rity.h?ml");
                StringWriter sw = new StringWriter();
                Writer o_testSecurity_literalMutationString224956__7 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Tst";
                });
                String o_testSecurity_literalMutationString224956__15 = TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString224956_literalMutationString226068 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString224956_literalMutationString226068_failAssert0_literalMutationString230504 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template secu]rity.h?ml not found", expected.getMessage());
        }
    }

    public void testPropertiesnull246597_failAssert0_literalMutationString247668_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("BH`P$m?S{_-");
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
                junit.framework.TestCase.fail("testPropertiesnull246597 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPropertiesnull246597_failAssert0_literalMutationString247668 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template BH`P$m?S{_- not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString246559_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("O6`@yM6.I_4");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString246559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template O6`@yM6.I_4 not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationNumber246578_add248389_literalMutationString250562_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("v`$-;Djc7[L");
            StringWriter sw = new StringWriter();
            Writer o_testProperties_literalMutationNumber246578__7 = m.execute(sw, new Object() {
                String getName() {
                    return "Chris";
                }

                int getValue() {
                    return 10000;
                }

                int taxed_value() {
                    this.getValue();
                    return ((int) ((this.getValue()) - ((this.getValue()) * 0.2)));
                }

                boolean isIn_ca() {
                    return true;
                }
            });
            String o_testProperties_literalMutationNumber246578__23 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_literalMutationNumber246578_add248389_literalMutationString250562 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v`$-;Djc7[L not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString57494_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("!iE3c<2PDN`", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString57494 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template !iE3c<2PDN` not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add196642_literalMutationString196715_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add196642__3 = c.compile("partialintemplatefunction.html");
            Mustache m = c.compile("0l+b`<]-[w[n]ov:0Zd7YM/O>!a!Ey");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add196642__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add196642_literalMutationString196715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0l+b`<]-[w[n]ov:0Zd7YM/O>!a!Ey not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196635_failAssert0_literalMutationString196757_failAssert0_add198302_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("#x.0SRWC9Z;_AKO7}2h{qw5?)W&gj&");
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
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196635 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196635_failAssert0_literalMutationString196757 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196635_failAssert0_literalMutationString196757_failAssert0_add198302 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #x.0SRWC9Z;_AKO7}2h{qw5?)W&gj& not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196639_failAssert0_add196934_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c");
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639_failAssert0_add196934 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196639_failAssert0_add196933_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("/D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c");
                Mustache m = c.compile("/D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639_failAssert0_add196933 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196639_failAssert0_literalMutationString196763_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/D.MP;g0o_,p#Gf}fm8Ory]a|I0]4c");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639_failAssert0_literalMutationString196763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /D.MP;g0o_,p#Gf}fm8Ory]a|I0]4c not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196639_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("/D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add196643_literalMutationString196703_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("partialintemplatefunctio`n.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add196643__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add196643__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add196643_literalMutationString196703 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunctio`n.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull196648_failAssert0_literalMutationString196734_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("P{v&))/|q-3U.=2!@de3V49 0(2w#=");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull196648 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull196648_failAssert0_literalMutationString196734 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template P{v&))/|q-3U.=2!@de3V49 0(2w#= not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196638_failAssert0_add196923_failAssert0_literalMutationString197649_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("akDczFAB=sCW$7sW({c!J4bPt] ?dEa");
                    Mustache m = c.compile("partialintemplatVefunction.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196638 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196638_failAssert0_add196923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196638_failAssert0_add196923_failAssert0_literalMutationString197649 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template akDczFAB=sCW$7sW({c!J4bPt] ?dEa not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196637_failAssert0_literalMutationString196744_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196637 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196637_failAssert0_literalMutationString196744 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add196643_remove196942_literalMutationString197289_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("X[c) S.SZEwB#M?c#z}OWI&&&(LJa1");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add196643__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add196643__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add196643_remove196942_literalMutationString197289 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template X[c) S.SZEwB#M?c#z}OWI&&&(LJa1 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add196643_add196836_literalMutationString197249_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("partialintemplatefunct<on.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add196643__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add196643__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            o_testPartialWithTF_add196643__14.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add196643_add196836_literalMutationString197249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunct<on.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove196646_literalMutationString196695_failAssert0_literalMutationString197614_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialintemplatefunctOion.>tml");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_remove196646__7 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196695 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196695_failAssert0_literalMutationString197614 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunctOion.>tml not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove196646_literalMutationString196694_failAssert0_literalMutationString197476_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("JJ@aJT}=&(%p9yN@k RPK)V.&`%m1%");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_remove196646__7 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196694 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196694_failAssert0_literalMutationString197476 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template JJ@aJT}=&(%p9yN@k RPK)V.&`%m1% not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add196643_literalMutationString196702_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("partialintempl|tefunction.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add196643__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add196643__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add196643_literalMutationString196702 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintempl|tefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196639_failAssert0null196977_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196639_failAssert0null196977 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /D.MP;_0o_,p#Gf}fm8Ory]a|I0]4c not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196635_failAssert0_literalMutationString196757_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("#x.0SRWC9Z;_AKO7}2h{qw5?)W&gj&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196635 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196635_failAssert0_literalMutationString196757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #x.0SRWC9Z;_AKO7}2h{qw5?)W&gj& not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196638_failAssert0_literalMutationString196751_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("#?S1Q3@*u?5+f#HDDr5s>wsMgP#WZq]");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196638 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196638_failAssert0_literalMutationString196751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #?S1Q3@*u?5+f#HDDr5s>wsMgP#WZq] not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove196646_literalMutationString196695_failAssert0_literalMutationString197612_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Q8I?1B=r&4i@/&h&.OHKIM?{v+h2&0k");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_remove196646__7 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196695 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_remove196646_literalMutationString196695_failAssert0_literalMutationString197612 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q8I?1B=r&4i@/&h&.OHKIM?{v+h2&0k not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString196637_failAssert0_literalMutationString196743_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("a^&|WL[N^");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196637 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString196637_failAssert0_literalMutationString196743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template a^&|WL[N^ not found", expected.getMessage());
        }
    }

    public void testComplexnull334524_failAssert0_add335752_failAssert0_literalMutationString338600_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("3CoXu_y;)T ^");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    Object o = JsonInterpreterTest.toObject(jsonNode);
                    sw = new StringWriter();
                    m = createMustacheFactory().compile(null);
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexnull334524 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testComplexnull334524_failAssert0_add335752 should have thrown EmptyStackException");
            }
            junit.framework.TestCase.fail("testComplexnull334524_failAssert0_add335752_failAssert0_literalMutationString338600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3CoXu_y;)T ^ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("[^O]<v4W#_Yj");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_literalMutationString327218_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(")T-7>9E=ORWR");
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
                TestUtil.getContents(root, "0(U0sV>{6`v");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327218 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_add328039_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                jf.createJsonGenerator(json);
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(")T-7>9E=ORWR");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334484_failAssert0_literalMutationString335226_failAssert0_literalMutationString337452_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("{]N**,]$+&i_");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    Object o = JsonInterpreterTest.toObject(jsonNode);
                    sw = new StringWriter();
                    m = createMustacheFactory().compile("page1.txt");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString334484 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString334484_failAssert0_literalMutationString335226 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334484_failAssert0_literalMutationString335226_failAssert0_literalMutationString337452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {]N**,]$+&i_ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_literalMutationString327218_failAssert0_add331912_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile(")T-7>9E=ORWR");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
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
                    TestUtil.getContents(root, "0(U0sV>{6`v");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327218_failAssert0_add331912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_add328046_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(")T-7>9E=ORWR");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
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
                junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328046 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0null328600_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(")T-7>9E=ORWR");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(null);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0null328600 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326684_failAssert0null328730_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("%sC6}I$?U%[(");
                m.execute(sw, o);
                TestUtil.getContents(root, null);
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString326684 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326684_failAssert0null328730 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template %sC6}I$?U%[( not found", expected.getMessage());
        }
    }

    public void testComplex_remove326712_failAssert0_add327822_failAssert0_literalMutationString330378_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("{UW8iEqJ.n|E");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    Object o = JsonInterpreterTest.toObject(jsonNode);
                    sw = new StringWriter();
                    createMustacheFactory().compile("complex.html");
                    m = createMustacheFactory().compile("complex.html");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_remove326712 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testComplex_remove326712_failAssert0_add327822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplex_remove326712_failAssert0_add327822_failAssert0_literalMutationString330378 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {UW8iEqJ.n|E not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0null336569_failAssert0_literalMutationString337070_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("[^O]<v4W#_Yj");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    TestUtil.getContents(root, "comple.x.txt");
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
                    junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569_failAssert0_literalMutationString337070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_add328039_failAssert0_literalMutationString330231_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter json = new StringWriter();
                    MappingJsonFactory jf = new MappingJsonFactory();
                    jf.createJsonGenerator(json);
                    final JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final JsonCapturer captured = new JsonCapturer(jg);
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new CapturingMustacheVisitor(this, captured);
                        }
                    };
                    Mustache m = c.compile(")T-7>9E=ORWR");
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
                    TestUtil.getContents(root, "com3plex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328039 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328039_failAssert0_literalMutationString330231 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0_literalMutationString335437_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("[^O]<v4W#_Yj");
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
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0_literalMutationString335437 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_add328039_failAssert0_add332398_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter json = new StringWriter();
                    MappingJsonFactory jf = new MappingJsonFactory();
                    jf.createJsonGenerator(json);
                    final JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final JsonCapturer captured = new JsonCapturer(jg);
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new CapturingMustacheVisitor(this, captured);
                        }
                    };
                    Mustache m = c.compile(")T-7>9E=ORWR");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
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
                    junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328039 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_add328039_failAssert0_add332398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0null336569_failAssert0_add339494_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("[^O]<v4W#_Yj");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
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
                    junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569_failAssert0_add339494 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0_add336158_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("[^O]<v4W#_Yj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                jf.createJsonParser(json.toString());
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0_add336158 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0null336569_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("[^O]<v4W#_Yj");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334480_failAssert0_literalMutationString335351_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("51*K*AG=09G%");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.t.xt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString334480 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334480_failAssert0_literalMutationString335351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 51*K*AG=09G% not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_literalMutationString327218_failAssert0null333538_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile(")T-7>9E=ORWR");
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
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327218_failAssert0null333538 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0_literalMutationString327204_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(")T-7>9E=ORWR");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("comple6x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326667_failAssert0_literalMutationString327204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334488_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("*2G%->-s&0?]");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString334488 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *2G%->-s&0?] not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334492_failAssert0_add336190_failAssert0_literalMutationString339109_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("(e+.Dpt}5UPi");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
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
                    TestUtil.getContents(root, "complex.Sxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString334492 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString334492_failAssert0_add336190 should have thrown EmptyStackException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334492_failAssert0_add336190_failAssert0_literalMutationString339109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (e+.Dpt}5UPi not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326667_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile(")T-7>9E=ORWR");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString326667 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )T-7>9E=ORWR not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326684_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("%sC6}I$?U%[(");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString326684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template %sC6}I$?U%[( not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString326684_failAssert0_add328281_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("%sC6}I$?U%[(");
                m.execute(sw, o);
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString326684 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString326684_failAssert0_add328281 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template %sC6}I$?U%[( not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString334475_failAssert0null336569_failAssert0null341258_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    Mustache m = c.compile("[^O]<v4W#_Yj");
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
                    TestUtil.getContents(root, null);
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString334475 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString334475_failAssert0null336569_failAssert0null341258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [^O]<v4W#_Yj not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98567_failAssert0_literalMutationString100095_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("comple}.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "compla.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98567 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98567_failAssert0_literalMutationString100095 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple}.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98320_failAssert0_literalMutationString98546_failAssert0_literalMutationString100197_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("comple<.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "comp>lex..txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98320 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98320_failAssert0_literalMutationString98546 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98320_failAssert0_literalMutationString98546_failAssert0_literalMutationString100197 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple<.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98322_failAssert0_add98879_failAssert0_literalMutationString99971_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    c.compile("complex.html");
                    Mustache m = c.compile("oa|jLt|nmr=;");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "c)mplex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98322 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98322_failAssert0_add98879 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98322_failAssert0_add98879_failAssert0_literalMutationString99971 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oa|jLt|nmr=; not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98312_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("x{6riivll{tH");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template x{6riivll{tH not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0null101327_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("fzU={u-Gbe@F");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0null101327 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fzU={u-Gbe@F not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add98323_literalMutationString98489_failAssert0_literalMutationString100107_failAssert0() throws MustacheException, IOException {
        try {
            {
                DefaultMustacheFactory o_testComplexParallel_add98323__1 = initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("/p|TI=S7n$rh");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                String o_testComplexParallel_add98323__11 = TestUtil.getContents(root, "coplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_add98323_literalMutationString98489 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_add98323_literalMutationString98489_failAssert0_literalMutationString100107 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /p|TI=S7n$rh not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98318_failAssert0_add98914_failAssert0_literalMutationString99951_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    c.compile("{E=D/n!anQyX");
                    Mustache m = c.compile("complex.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_add98914 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_add98914_failAssert0_literalMutationString99951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {E=D/n!anQyX not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98312_failAssert0_add98892_failAssert0() throws MustacheException, IOException {
        try {
            {
                initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("x{6riivll{tH");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312_failAssert0_add98892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template x{6riivll{tH not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98316_failAssert0null98997_failAssert0_literalMutationString100085_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("com}lex.tml");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98316 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98316_failAssert0null98997 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98316_failAssert0null98997_failAssert0_literalMutationString100085 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.tml not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add98327_literalMutationString98464_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("k[&`,8&#]2a,");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add98327__10 = TestUtil.getContents(root, "complex.txt");
            String o_testComplexParallel_add98327__11 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add98327_literalMutationString98464 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template k[&`,8&#]2a, not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0_literalMutationString99733_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("fzU={u-Gbe@F");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "page1.ttxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0_literalMutationString99733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fzU={u-Gbe@F not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98312_failAssert0_literalMutationString98601_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("x{6riivll{tH");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex^txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312_failAssert0_literalMutationString98601 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template x{6riivll{tH not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0_add100836_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    c.compile("fzU={u-Gbe@F");
                    Mustache m = c.compile("fzU={u-Gbe@F");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0_add100836 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fzU={u-Gbe@F not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98563_failAssert0_literalMutationString99634_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("Clt}LCSU&zV");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "comple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98563 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98319_failAssert0_literalMutationString98563_failAssert0_literalMutationString99634 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Clt}LCSU&zV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("fzU={u-Gbe@F");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98318_failAssert0_literalMutationString98635 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fzU={u-Gbe@F not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString98312_failAssert0null98994_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("x{6riivll{tH");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString98312_failAssert0null98994 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template x{6riivll{tH not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111860_literalMutationString111915_failAssert0_literalMutationString112791_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex`html", new ParallelComplexObject());
                String o_testSerialCallable_add111860__4 = TestUtil.getContents(root, "cofplex.txt");
                String o_testSerialCallable_add111860__5 = TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915_failAssert0_literalMutationString112791 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111851_failAssert0_add112172_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("comple<x.html", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851_failAssert0_add112172 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple<x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111855_failAssert0_literalMutationString111999_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.%html", new ParallelComplexObject());
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111855 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111855_failAssert0_literalMutationString111999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.%html not found", expected.getMessage());
        }
    }

    public void testSerialCallablenull111863_failAssert0_add112121_failAssert0_literalMutationString113425_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("{3nCDQ$Xa0c9", new ParallelComplexObject());
                    StringWriter sw = execute("complex.html", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallablenull111863 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testSerialCallablenull111863_failAssert0_add112121 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSerialCallablenull111863_failAssert0_add112121_failAssert0_literalMutationString113425 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {3nCDQ$Xa0c9 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111859_literalMutationString111957_failAssert0_literalMutationString113248_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter o_testSerialCallable_add111859__1 = execute("complex.html", new ParallelComplexObject());
                StringWriter sw = execute("r.2*.|=]mPXO", new ParallelComplexObject());
                String o_testSerialCallable_add111859__6 = TestUtil.getContents(root, "co_plex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111859_literalMutationString111957 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111859_literalMutationString111957_failAssert0_literalMutationString113248 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r.2*.|=]mPXO not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111851_failAssert0_literalMutationString112092_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("comple<xhtml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851_failAssert0_literalMutationString112092 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple<xhtml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111861_literalMutationString111932_failAssert0_add113994_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.ht|ml", new ParallelComplexObject());
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                String o_testSerialCallable_add111861__5 = TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932_failAssert0_add113994 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.ht|ml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111854_failAssert0_add112151_failAssert0_literalMutationString113014_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("(VnsfA#|m=wm", new ParallelComplexObject());
                    TestUtil.getContents(root, "Yy.mDPf7m)!");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0_add112151 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0_add112151_failAssert0_literalMutationString113014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (VnsfA#|m=wm not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111856_failAssert0_literalMutationString112056_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("m3#i{GJYs@U<", new ParallelComplexObject());
                TestUtil.getContents(root, "complTx.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111856 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111856_failAssert0_literalMutationString112056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m3#i{GJYs@U< not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111860_literalMutationString111915_failAssert0null114092_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex`html", new ParallelComplexObject());
                String o_testSerialCallable_add111860__4 = TestUtil.getContents(root, null);
                String o_testSerialCallable_add111860__5 = TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915_failAssert0null114092 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111861_literalMutationString111932_failAssert0null114219_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.ht|ml", new ParallelComplexObject());
                sw.toString();
                String o_testSerialCallable_add111861__5 = TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932_failAssert0null114219 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.ht|ml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111855_failAssert0null112195_failAssert0_literalMutationString112720_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("compl]x.html", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111855 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111855_failAssert0null112195 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111855_failAssert0null112195_failAssert0_literalMutationString112720 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template compl]x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111852_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("F6x@C!})tJWC", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111852 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F6x@C!})tJWC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111851_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("comple<x.html", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple<x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111860_literalMutationString111915_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("complex`html", new ParallelComplexObject());
            String o_testSerialCallable_add111860__4 = TestUtil.getContents(root, "complex.txt");
            String o_testSerialCallable_add111860__5 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111854_failAssert0_add112149_failAssert0_literalMutationString113389_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("|r416z44C:Of", new ParallelComplexObject());
                    StringWriter sw = execute("complex.html", new ParallelComplexObject());
                    TestUtil.getContents(root, "Yy.mDPf7m)!");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0_add112149 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0_add112149_failAssert0_literalMutationString113389 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |r416z44C:Of not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111848_failAssert0_add112134_failAssert0_literalMutationString113074_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("uEsj3>Z|9wa", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111848 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111848_failAssert0_add112134 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111848_failAssert0_add112134_failAssert0_literalMutationString113074 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template uEsj3>Z|9wa not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111861_literalMutationString111932_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("complex.ht|ml", new ParallelComplexObject());
            sw.toString();
            String o_testSerialCallable_add111861__5 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.ht|ml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111852_failAssert0_add112145_failAssert0() throws MustacheException, IOException {
        try {
            {
                execute("F6x@C!})tJWC", new ParallelComplexObject());
                StringWriter sw = execute("F6x@C!})tJWC", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111852 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111852_failAssert0_add112145 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F6x@C!})tJWC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111852_failAssert0_literalMutationString112025_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("F6x@C!})tJWC", new ParallelComplexObject());
                TestUtil.getContents(root, "1u,bsgw/O|C");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111852 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111852_failAssert0_literalMutationString112025 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F6x@C!})tJWC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111854_failAssert0null112201_failAssert0_literalMutationString113187_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("j4%.^5QF<(OC", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0null112201 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111854_failAssert0null112201_failAssert0_literalMutationString113187 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template j4%.^5QF<(OC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111861_literalMutationString111932_failAssert0_literalMutationString113519_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.ht|ml", new ParallelComplexObject());
                sw.toString();
                String o_testSerialCallable_add111861__5 = TestUtil.getContents(root, "comple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111861_literalMutationString111932_failAssert0_literalMutationString113519 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.ht|ml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add111860_literalMutationString111915_failAssert0_add113712_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex`html", new ParallelComplexObject());
                String o_testSerialCallable_add111860__4 = TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                String o_testSerialCallable_add111860__5 = TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_add111860_literalMutationString111915_failAssert0_add113712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111853_failAssert0_add112156_failAssert0_literalMutationString113024_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("co:mplex.html", new ParallelComplexObject());
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString111853 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111853_failAssert0_add112156 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111853_failAssert0_add112156_failAssert0_literalMutationString113024 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co:mplex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString111851_failAssert0null112211_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("comple<x.html", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString111851_failAssert0null112211 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple<x.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0_add140114_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                mustache.execute(writer, scopes);
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
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0_add140114 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                m.execute(null, new HashMap<String, Object>() {
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
                junit.framework.TestCase.fail("testDynamicPartialnull122967 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                                            mustache.execute(writer, scopes);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_add139713_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                partial.execute(null, scopes);
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_add139713 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_add122921_failAssert0_literalMutationString125210_failAssert0() throws MustacheException, IOException {
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
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
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
                junit.framework.TestCase.fail("testDynamicPartial_add122921 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_add122921_failAssert0_literalMutationString125210 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html[foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0null142477_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                            @Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new StringReader(name), "__dynpartial__", "[", null);
                                                if ((partial) == null) {
                                                    throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                            @Override
                                            public Writer execute(Writer writer, List<Object> scopes) {
                                                StringWriter sw = new StringWriter();
                                                partial.execute(null, scopes);
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0null142477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber122819_failAssert0_add126295_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819_failAssert0_add126295 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html+ simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_add126281_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126281 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122901_failAssert0_literalMutationNumber125503_failAssert0() throws MustacheException, IOException {
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
                        put("foo", "page1.txt");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122901 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122901_failAssert0_literalMutationNumber125503 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + page1.txt.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0null142492_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                partial.execute(null, scopes);
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
                            put(null, true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0null142492 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                                            partial.execute(null, scopes);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_literalMutationString135546_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                partial.execute(null, scopes);
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
                            put("page1.txt", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_literalMutationString135546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_literalMutationString135545_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                partial.execute(null, scopes);
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
                            put("", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_literalMutationString135545 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("page1.txt")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0_literalMutationString137166_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                                                mustache.execute(writer, scopes);
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
                            put("", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0_literalMutationString137166 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber122819_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0null141450_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                    m.execute(null, new HashMap<String, Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", null);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartialnull122967 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0null141450 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber122819_failAssert0null127006_failAssert0() throws MustacheException, IOException {
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
                        put("name", null);
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819_failAssert0null127006 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_literalMutationString124770_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_literalMutationString124770 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126976_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
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
                        put(null, "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber122819_failAssert0_literalMutationString124807_failAssert0() throws MustacheException, IOException {
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
                                                throw new MustacheException(("Failed to parse partial-name template: " + (name)));
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber122819_failAssert0_literalMutationString124807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0_add138776_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    variable.startsWith("page1.txt");
                                    if (variable.startsWith("page1.txt")) {
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
                    m.execute(null, new HashMap<String, Object>() {
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
                    junit.framework.TestCase.fail("testDynamicPartialnull122967 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0_add138776 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_add139690_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
                                        tc.file();
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
                                                partial.execute(null, scopes);
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0null126967_failAssert0_add139690 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0_literalMutationString131772_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
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
                    m.execute(null, new HashMap<String, Object>() {
                        {
                            put("name", "");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartialnull122967 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull122967_failAssert0_literalMutationString123945_failAssert0_literalMutationString131772 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0null142935_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("page1.txt")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                            @Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new StringReader(name), "__dynpartial__", "[", null);
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
                                                mustache.execute(writer, scopes);
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString122809_failAssert0_add126273_failAssert0null142935 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0_add39278_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/76<oD)^hw");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_add39278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0null39390_failAssert0null41863_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("/76<oD)^hw");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(null, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390_failAssert0null41863 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_add41486_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    System.currentTimeMillis();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_add41486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0_literalMutationString38942_failAssert0_literalMutationString40366_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("/76<loD)^hw");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "mUKuX/E}X");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_literalMutationString38942 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_literalMutationString38942_failAssert0_literalMutationString40366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<loD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_add41482_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    m.execute(sw, new AmplInterpreterTest.Context());
                    System.currentTimeMillis();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_add41482 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("/76<oD)^hw");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0null39390_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/76<oD)^hw");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_add38680_literalMutationString38790_failAssert0_literalMutationString40178_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("-Sx^ 2I]$n");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                Writer o_testReadme_add38680__10 = m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                String o_testReadme_add38680__14 = TestUtil.getContents(root, "items.xt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790_failAssert0_literalMutationString40178 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -Sx^ 2I]$n not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38667_failAssert0_literalMutationString38889_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("#[PtMj=l<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38667 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38667_failAssert0_literalMutationString38889 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #[PtMj=l< not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0null39390_failAssert0_literalMutationString40121_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("Z5FGa]VNJr");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390_failAssert0_literalMutationString40121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Z5FGa]VNJr not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0null41997_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0null41997 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0_add41668_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "page1.txt");
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0_add41668 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39256_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39256 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0_literalMutationString38942_failAssert0_add41466_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("/76<oD)^hw");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "mUKuX/E}X");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_literalMutationString38942 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_literalMutationString38942_failAssert0_add41466 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_add41314_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|ml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_add41314 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|ml not found", expected.getMessage());
        }
    }

    public void testReadme_add38680_literalMutationString38790_failAssert0_add41316_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("-Sx^ 2I]$n");
                Mustache m = c.compile("-Sx^ 2I]$n");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                Writer o_testReadme_add38680__10 = m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                String o_testReadme_add38680__14 = TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790_failAssert0_add41316 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -Sx^ 2I]$n not found", expected.getMessage());
        }
    }

    public void testReadme_add38682_literalMutationString38841_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("i}tems.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add38682__9 = m.execute(sw, new AmplInterpreterTest.Context());
            System.currentTimeMillis();
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add38682__14 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_add38682_literalMutationString38841 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i}tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_literalMutationString40161_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.<|ml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_literalMutationString40161 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.<|ml not found", expected.getMessage());
        }
    }

    public void testReadme_add38679_literalMutationString38812_failAssert0_literalMutationString40317_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache o_testReadme_add38679__3 = c.compile(";#r_Ee?F/`");
                Mustache m = c.compile("item.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                Writer o_testReadme_add38679__10 = m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                String o_testReadme_add38679__14 = TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_add38679_literalMutationString38812 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_add38679_literalMutationString38812_failAssert0_literalMutationString40317 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;#r_Ee?F/` not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0_literalMutationString40637_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38904_failAssert0_literalMutationString40637 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39253_failAssert0null41936_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    System.currentTimeMillis();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253_failAssert0null41936 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0null39381_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0null39381 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39253_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                System.currentTimeMillis();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("items.|tml");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|ml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|ml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0_literalMutationString38942_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/76<oD)^hw");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "mUKuX/E}X");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0_literalMutationString38942 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_add38680_literalMutationString38790_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("-Sx^ 2I]$n");
            StringWriter sw = new StringWriter();
            System.currentTimeMillis();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add38680__10 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add38680__14 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -Sx^ 2I]$n not found", expected.getMessage());
        }
    }

    public void testReadme_add38680_literalMutationString38790_failAssert0null41877_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("-Sx^ 2I]$n");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                Writer o_testReadme_add38680__10 = m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                String o_testReadme_add38680__14 = TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_add38680_literalMutationString38790_failAssert0null41877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -Sx^ 2I]$n not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38677_failAssert0null39404_failAssert0_literalMutationString40048_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("r)*55:ChY]");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(null, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "iEtems.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38677 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38677_failAssert0null39404 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38677_failAssert0null39404_failAssert0_literalMutationString40048 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r)*55:ChY] not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_add41313_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|ml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0_add41313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|ml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0null39380_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items.|tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0null39380 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0null41875_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|ml");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_literalMutationString38901_failAssert0null41875 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|ml not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38667_failAssert0_literalMutationString38889_failAssert0null41899_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("#[PtMj=l<");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(null, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38667_failAssert0_literalMutationString38889 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38667_failAssert0_literalMutationString38889_failAssert0null41899 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #[PtMj=l< not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38667_failAssert0_literalMutationString38889_failAssert0_add41379_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("#[PtMj=l<");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38667_failAssert0_literalMutationString38889 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38667_failAssert0_literalMutationString38889_failAssert0_add41379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #[PtMj=l< not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0null39390_failAssert0_add41279_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("/76<oD)^hw");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390_failAssert0_add41279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38668_failAssert0null39390_failAssert0_add41275_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("/76<oD)^hw");
                    StringWriter sw = new StringWriter();
                    System.currentTimeMillis();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38668_failAssert0null39390_failAssert0_add41275 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /76<oD)^hw not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_literalMutationString40392_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("items.|tl");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    System.currentTimeMillis();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38670 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38670_failAssert0_add39253_failAssert0_literalMutationString40392 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items.|tl not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add6101_remove7051_add10133() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add6101__9 = m.execute(sw, new AmplInterpreterTest.Context());
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add6101__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__13);
        String o_testReadmeSerial_add6101__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__14);
        String String_47 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
        boolean boolean_48 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
    }

    public void testReadmeSerial_literalMutationString6086_failAssert0_literalMutationString6641_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("s$Ug_Ng#jo{");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "");
                sw.toString();
                String String_25 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_26 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086_failAssert0_literalMutationString6641 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s$Ug_Ng#jo{ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add6098_add6868() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add6098__10 = m.execute(sw, new AmplInterpreterTest.Context());
        ((StringWriter) (o_testReadmeSerial_add6098__10)).getBuffer().toString();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add6098__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6098__14);
        sw.toString();
        String String_45 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
        boolean boolean_46 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6098__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
    }

    public void testReadmeSerial_literalMutationString6086_failAssert0_add7009_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("s$Ug_Ng#jo{");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_25 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_26 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086_failAssert0_add7009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s$Ug_Ng#jo{ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString6093_failAssert0_literalMutationString6732_failAssert0_literalMutationString8546_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("{tems2.html");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "ites.txt");
                    sw.toString();
                    String String_33 = "Should be a little bit more than H4 seconds: " + diff;
                    boolean boolean_34 = (diff > 3999) && (diff < 6000);
                    junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6093 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6093_failAssert0_literalMutationString6732 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6093_failAssert0_literalMutationString6732_failAssert0_literalMutationString8546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString6086_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("s$Ug_Ng#jo{");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_25 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_26 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s$Ug_Ng#jo{ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add6101() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add6101__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add6101__9)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add6101__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add6101__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__13);
        String o_testReadmeSerial_add6101__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__14);
        sw.toString();
        String String_47 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_47);
        boolean boolean_48 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add6101__9)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add6101__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6101__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_47);
    }

    public void testReadmeSerial_add6099_literalMutationNumber6366_literalMutationString7650() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add6099__9 = m.execute(sw, new AmplInterpreterTest.Context());
        Writer o_testReadmeSerial_add6099__11 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add6099__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6099__15);
        sw.toString();
        String String_49 = "Should be a little bit more tan 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more tan 4 seconds: 8001", String_49);
        boolean boolean_50 = (diff > 3998) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add6099__15);
        TestCase.assertEquals("Should be a little bit more tan 4 seconds: 8001", String_49);
    }

    public void testReadmeSerial_literalMutationString6086_failAssert0null7100_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("s$Ug_Ng#jo{");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_25 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_26 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString6086_failAssert0null7100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s$Ug_Ng#jo{ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280454_failAssert0_add281437_failAssert0null286067_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile(":tems2.html");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(null, new AmplInterpreterTest.Context());
                    m.execute(sw, new AmplInterpreterTest.Context()).close();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    String String_139 = "Should be a little bit more than 1 second: " + diff;
                    boolean boolean_140 = (diff > 999) && (diff < 2000);
                    junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_add281437 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_add281437_failAssert0null286067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280451_failAssert0null281549_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("]^Rz|O^*S$s");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_129 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_130 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451_failAssert0null281549 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]^Rz|O^*S$s not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280454_failAssert0_literalMutationNumber280999_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile(":tems2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 998) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_literalMutationNumber280999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280451_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("]^Rz|O^*S$s");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_129 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_130 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]^Rz|O^*S$s not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280454_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile(":tems2.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_139 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_140 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280451_failAssert0_add281400_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("]^Rz|O^*S$s");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_129 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_130 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451_failAssert0_add281400 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]^Rz|O^*S$s not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280454_failAssert0_add281437_failAssert0_add285563_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile(":tems2.html");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    m.execute(sw, new AmplInterpreterTest.Context()).close();
                    System.currentTimeMillis();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    String String_139 = "Should be a little bit more than 1 second: " + diff;
                    boolean boolean_140 = (diff > 999) && (diff < 2000);
                    junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_add281437 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_add281437_failAssert0_add285563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallelnull280472_failAssert0_add281373_failAssert0_literalMutationString283812_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("^QKKV.}z[!v");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context()).close();
                    m.execute(sw, new AmplInterpreterTest.Context()).close();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    String String_123 = "Should be a little bit more than 1 second: " + diff;
                    boolean boolean_124 = (diff > 999) && (diff < 2000);
                    junit.framework.TestCase.fail("testReadmeParallelnull280472 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testReadmeParallelnull280472_failAssert0_add281373 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallelnull280472_failAssert0_add281373_failAssert0_literalMutationString283812 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^QKKV.}z[!v not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString280451_failAssert0_literalMutationString280871_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("]^Rz|O^*S$s");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_129 = "Shoulde be a little bit more than 1 second: " + diff;
                boolean boolean_130 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280451_failAssert0_literalMutationString280871 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]^Rz|O^*S$s not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add280468_remove281506() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeParallel_add280468__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add280468__15);
        String String_159 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_159);
        boolean boolean_160 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add280468__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_159);
    }

    public void testReadmeParallel_add280463_remove281507_remove285877() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add280463__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add280463__15);
        String String_163 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
        boolean boolean_164 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add280463__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
    }

    public void testReadmeParallel_literalMutationString280454_failAssert0_add281437_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile(":tems2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString280454_failAssert0_add281437 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :tems2.html not found", expected.getMessage());
        }
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

    public void testDeferred_literalMutationString106941_add107733_literalMutationString109015_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("cq,QxfsapF/H<");
            StringWriter sw = new StringWriter();
            m.execute(sw, context);
            m.execute(sw, context).close();
            String o_testDeferred_literalMutationString106941__17 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString106941_add107733_literalMutationString109015 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template cq,QxfsapF/H< not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString106945_literalMutationString107099_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deerred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("K^sn([=PU<#nw");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            String o_testDeferred_literalMutationString106945__17 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString106945_literalMutationString107099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template K^sn([=PU<#nw not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString106951_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("v`2kV1DF3My:v");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString106951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v`2kV1DF3My:v not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString106951_failAssert0_add108021_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("v`2kV1DF3My:v");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString106951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString106951_failAssert0_add108021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template v`2kV1DF3My:v not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0_literalMutationString244564_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("[Od/q(:*cAlZ(Y.|FsO]S6o@zO&");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0_literalMutationString244564 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Od/q(:*cAlZ(Y.|FsO]S6o@zO& not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add242675_literalMutationString242829_failAssert0_literalMutationString244249_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relat ive/functionpaths.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                String o_testRelativePathsTemplateFunction_add242675__31 = TestUtil.getContents(root, "relatiLe/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add242675_literalMutationString242829 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add242675_literalMutationString242829_failAssert0_literalMutationString244249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relat ive/functionpaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_remove242680_remove243286_literalMutationString243698_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("relative/function{aths.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_remove242680__18 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_remove242680_remove243286_literalMutationString243698 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relative/function{aths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_add245480_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    });
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_add245480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0_add245452_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("[Od/q(:*cAlZ(Y.|FsO]S6o@zO&");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0_add245452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Od/q(:*cAlZ(Y.|FsO]S6o@zO& not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0null243391_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0null243391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0null245822_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0null245822 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007_failAssert0_add245458_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007_failAssert0_add245458 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0null245809_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("[Od/q(:*cAlZ(Y.|FsO]S6o@zO&");
                    StringWriter sw = new StringWriter();
                    compile.execute(null, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0null245809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Od/q(:*cAlZ(Y.|FsO]S6o@zO& not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243273_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
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
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242667_failAssert0null243369_failAssert0_literalMutationString243900_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("RG*w8Hm{)Q 6IV4+^-e;`5MJ}}0");
                    StringWriter sw = new StringWriter();
                    compile.execute(null, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0null243369 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0null243369_failAssert0_literalMutationString243900 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RG*w8Hm{)Q 6IV4+^-e;`5MJ}}0 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add242674_literalMutationString242809_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache o_testRelativePathsTemplateFunction_add242674__3 = mf.compile("relative/functionpaths.html");
            Mustache compile = mf.compile("$6XazQ@Es[9d`i+Tq^SqY{hycv&");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_add242674__20 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add242674_literalMutationString242809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $6XazQ@Es[9d`i+Tq^SqY{hycv& not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_literalMutationString244605_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "rel|tive/paths.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_literalMutationString244605 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("[Od/q(:*cAlZ(Y.|FsO]S6o@zO&");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242667_failAssert0_literalMutationString242925 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Od/q(:*cAlZ(Y.|FsO]S6o@zO& not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007_failAssert0null245812_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                    StringWriter sw = new StringWriter();
                    compile.execute(null, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007_failAssert0null245812 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242666_failAssert0null243388_failAssert0_literalMutationString243889_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("%]#zij<]n-H0LV?^o1Te=ADh)6u");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242666 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242666_failAssert0null243388 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242666_failAssert0null243388_failAssert0_literalMutationString243889 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template %]#zij<]n-H0LV?^o1Te=ADh)6u not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242669_failAssert0_add243226_failAssert0_literalMutationString244407_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("r elative/functionpaths.html");
                    StringWriter sw = new StringWriter();
                    compile.execute(sw, new Object() {
                        Function i = new TemplateFunction() {
                            @Override
                            public String apply(String s) {
                                return s;
                            }
                        };
                    }).close();
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242669 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242669_failAssert0_add243226 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242669_failAssert0_add243226_failAssert0_literalMutationString244407 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r elative/functionpaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0() throws IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_literalMutationString243007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("aTl> dkFS/`EeEIio|(IV*oME(9");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFS/`EeEIio|(IV*oME(9 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_literalMutationString244602_failAssert0() throws IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("aTl> dkFg/`EeEIio|(IV*oME(9");
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString242665_failAssert0_add243268_failAssert0_literalMutationString244602 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template aTl> dkFg/`EeEIio|(IV*oME(9 not found", expected.getMessage());
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


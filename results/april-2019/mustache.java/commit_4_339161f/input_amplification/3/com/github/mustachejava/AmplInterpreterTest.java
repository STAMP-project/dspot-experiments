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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString183345_failAssert0_add185321_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("+ke$Lr*<U)z");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_add185321 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_add185320_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("+ke$Lr*<U)z");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_add185320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_add183376_literalMutationString183643_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("s>imple.html");
            StringWriter sw = new StringWriter();
            Writer o_testSimple_add183376__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimple_add183376__14 = TestUtil.getContents(root, "simple.txt");
            String o_testSimple_add183376__15 = TestUtil.getContents(root, "simple.txt");
            String o_testSimple_add183376__16 = sw.toString();
            junit.framework.TestCase.fail("testSimple_add183376_literalMutationString183643 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s>imple.html not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189252_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+ke$Lr*<U)z");
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
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189252 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("+k$Lr*<U)z");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +k$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0_add189428_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+k$Lr*<U)z");
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
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0_add189428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +k$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184603_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("+ke$Lr*<U)z");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.xt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184603 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("+ke$Lr*<U)z");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187654_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+ke$Lr*<U)z");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 5000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187654 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0null189679_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+ke$Lr*<U)z");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0null189679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187656_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+ke$Lr*<U)z");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 1.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187656 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189251_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("+ke$Lr*<U)z");
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
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189251 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("+ke$Lr*<U)z");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
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

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_add86352_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                    Mustache m = c.compile("simple.html");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71176__16 = TestUtil.getContents(root, "simple_ko.txt");
                    String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
                }
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                    Mustache m = c.compile("#imple.html");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.8)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71176__34 = TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_add86352 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_literalMutationNumber84794_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                    Mustache m = c.compile("simple.html");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71176__16 = TestUtil.getContents(root, "simple_ko.txt");
                    String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
                }
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                    Mustache m = c.compile("#imple.html");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * -0.19999999999999996)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71176__34 = TestUtil.getContents(this.root, "simple.txt");
                    String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_literalMutationNumber84794 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71136null78823_failAssert0_literalMutationString83608_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                    Mustache m = c.compile("Ow}M[x#%Hk!");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71136__9 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10001;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71136__17 = TestUtil.getContents(root, null);
                    String o_testSimpleI18N_literalMutationNumber71136__18 = sw.toString();
                }
                {
                    MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                    Mustache m = c.compile("simple.html");
                    StringWriter sw = new StringWriter();
                    Writer o_testSimpleI18N_literalMutationNumber71136__27 = m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    String o_testSimpleI18N_literalMutationNumber71136__34 = TestUtil.getContents(this.root, "simple.txt");
                    String o_testSimpleI18N_literalMutationNumber71136__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71136null78823 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71136null78823_failAssert0_literalMutationString83608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ow}M[x#%Hk! not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber71176__16 = TestUtil.getContents(root, "simple_ko.txt");
                String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                Mustache m = c.compile("#imple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.8)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber71176__34 = TestUtil.getContents(this.root, "simple.txt");
                String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("simplefi:ltered.html");
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
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationString231134_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefilteed.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationString231134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235650_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefiltered.txt");
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236115_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "siVplefiltered.txt");
                    TestUtil.getContents(root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236115 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236113_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    c.compile("lI(%t_o$J,/pLWotg2c");
                    Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_add235912_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_add235912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0null229539_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("simplefi:ltered.html");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0null229539 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_literalMutationString232979_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_literalMutationString232979 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815_failAssert0_literalMutationString231881_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public String filterText(String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("page1.txt", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    Mustache m = c.compile("$-4fM{ Sa");
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
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815_failAssert0_literalMutationString231881 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $-4fM{ Sa not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236509_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public String filterText(String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll(null, " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236509 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "siVplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0_add229168_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0_add229168 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_literalMutationNumber234391_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 1.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_literalMutationNumber234391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationNumber231113_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationNumber231113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0_literalMutationString227946_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("", "\n");
                    }
                };
                Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0_literalMutationString227946 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            Mustache m = c.compile("simplefi:ltered.html");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0null237061_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0null237061 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_literalMutationString229140_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("simplefi:ltered.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "C1ris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_literalMutationString229140 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235648_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
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
                    c.compile("simplefi:ltered.html");
                    Mustache m = c.compile("simplefi:ltered.html");
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
                    TestUtil.getContents(root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235648 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0_add192078_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("6SVVy^$cw#g7#!", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursion.txt");
                    sw.toString();
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0_add192078 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("6SVVy^$cw#g7#!", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursion.|tml", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("6SVVy^$cw#g7#!", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189989_failAssert0_literalMutationString190135_failAssert0_literalMutationString191334_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("= &:8I86m", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recudsion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189989 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189989_failAssert0_literalMutationString190135 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189989_failAssert0_literalMutationString190135_failAssert0_literalMutationString191334 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template = &:8I86m not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_literalMutationBoolean190179_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion.|tml", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_literalMutationBoolean190179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189981_failAssert0_add190363_failAssert0_literalMutationString191203_failAssert0() throws IOException {
        try {
            {
                {
                    execute("Dd^z)qTUD", new Object() {
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
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189981 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189981_failAssert0_add190363 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189981_failAssert0_add190363_failAssert0_literalMutationString191203 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Dd^z)qTUD not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion.|tml", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurisionnull189999_failAssert0_literalMutationString190299_failAssert0_literalMutationString191472_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("]m33pC#4EnM[{@", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurisionnull189999 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testRecurisionnull189999_failAssert0_literalMutationString190299 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecurisionnull189999_failAssert0_literalMutationString190299_failAssert0_literalMutationString191472 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]m33pC#4EnM[{@ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0_literalMutationString191244_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("recursion.|tml", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursion.tt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0_literalMutationString191244 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0_literalMutationString190224_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("6SVVy^$cw#g7#!", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "m}8q6rv_=x,d[");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_literalMutationString190224 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0null190412_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion.|tml", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0null190412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014_failAssert0_literalMutationString302423_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("pa}ge1.txt", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritancenull300717 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014_failAssert0_literalMutationString302423 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template pa}ge1.txt not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0_literalMutationString300863_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "MA+:7S=fX&Vpb");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0_literalMutationString300863 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0null301123_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0null301123 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065_failAssert0_literalMutationString302370_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("{SV,,T0c.e[bk&C[Us(lgu^&jZ}.WHz", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065_failAssert0_literalMutationString302370 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {SV,,T0c.e[bk&C[Us(lgu^&jZ}.WHz not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_remove300715_literalMutationString300831_failAssert0_literalMutationString302147_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion_w=t[h_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                String o_testRecursionWithInheritance_remove300715__10 = TestUtil.getContents(root, "recursion.txt");
                String o_testRecursionWithInheritance_remove300715__11 = sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_remove300715_literalMutationString300831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_remove300715_literalMutationString300831_failAssert0_literalMutationString302147 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_w=t[h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0_add301047_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0_add301047 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300706_failAssert0_literalMutationString300967_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recu{sion_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300706 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300706_failAssert0_literalMutationString300967 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recu{sion_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add300713_literalMutationString300803_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursion_wit^h_inheritance.html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            String o_testRecursionWithInheritance_add300713__12 = TestUtil.getContents(root, "recursion.txt");
            String o_testRecursionWithInheritance_add300713__13 = sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add300713_literalMutationString300803 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_wit^h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929_failAssert0_literalMutationString302463_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(" does not exist", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, "recursiopn.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929_failAssert0_literalMutationString302463 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300700_failAssert0null301129_failAssert0_literalMutationString302260_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute(" does not exist", new Object() {
                        Object value = new Object() {
                            boolean value = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700_failAssert0null301129 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700_failAssert0null301129_failAssert0_literalMutationString302260 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300708_failAssert0_literalMutationString300907_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion_wi`h_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursio.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300708 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300708_failAssert0_literalMutationString300907 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_wi`h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0null183039_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0null183039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0() throws IOException {
        try {
            StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("recursive_partial_inher}tance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_add182807_failAssert0() throws IOException {
        try {
            {
                StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                execute("recursive_partial_inher}tance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                StringWriter sw = execute("recursive_partial_inher}tance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_add182807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_literalMutationString181913_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "recursive_parWtial_inheritancehtxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_literalMutationString181913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180496_failAssert0_literalMutationString180712_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("page:.txt", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180496 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180496_failAssert0_literalMutationString180712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template page:.txt not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0null180936_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0null180936 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_remove180509_literalMutationString180636_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("(flv/-`j73P5fJF]wC$FxD(jnF*`@A_@/6", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_remove180509__10 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_remove180509__11 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove180509_literalMutationString180636 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926_failAssert0_literalMutationString182105_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("$9oC0V(mvTQBD=!+F4co]h[QN`1UsUh@vZ", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926_failAssert0_literalMutationString182105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $9oC0V(mvTQBD=!+F4co]h[QN`1UsUh@vZ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_literalMutationString181918_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<tBkMLUCo`}#>J", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_literalMutationString181918 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationBoolean180499_failAssert0_literalMutationString180777_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("r^ecursive_partial_inheritance.html", new Object() {
                    Object test = new Object() {
                        boolean test = true;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationBoolean180499 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationBoolean180499_failAssert0_literalMutationString180777 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r^ecursive_partial_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_literalMutationString182305_failAssert0() throws IOException {
        try {
            {
                StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                StringWriter sw = execute("recursive_partial_inher}tance.[tml", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_literalMutationString182305 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursive_partial_inher}tance.[tml not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0null183098_failAssert0() throws IOException {
        try {
            {
                StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                StringWriter sw = execute("recursive_partial_inher}tance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, null);
                String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0null183098 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180595_failAssert0() throws IOException {
        try {
            StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("{lg>!5LRpZ|//-tlHoW9Lb[]sCdXc-JE3!", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("recursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180595 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180896_literalMutationString181321_failAssert0() throws IOException {
        try {
            StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("k*v[!VO-avzd#X;MlZ)ej-R #CxM/y=5zx", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("recursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180896_literalMutationString181321 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_add180873_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_add180873 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180897_literalMutationString181582_failAssert0() throws IOException {
        try {
            StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("XHLCnXXzML[S[!JorW;jh8rMd1!Q2OEmjC", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180897_literalMutationString181582 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XHLCnXXzML[S[!JorW;jh8rMd1!Q2OEmjC not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_parWtial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0null183037_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0null183037 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_add182656_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_add182656 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180895_literalMutationString181504_failAssert0() throws IOException {
        try {
            StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("PT<2F,@XS4;#%uR&s;ob(c]HU6W2m28hX9", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("recursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_add180506__20 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180895_literalMutationString181504 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template PT<2F,@XS4;#%uR&s;ob(c]HU6W2m28hX9 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924_failAssert0_literalMutationString182404_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("/)K6Egx9JMY_TQ!0b(/?%J6>hmJFs$cz)e", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924_failAssert0_literalMutationString182404 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritancenull180512_failAssert0_add180888_failAssert0_literalMutationString181748_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("lRyO<!k.2qR)c>9uC:T(%9cqAW&3%Xeo:M", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512_failAssert0_add180888 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512_failAssert0_add180888_failAssert0_literalMutationString181748 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template lRyO<!k.2qR)c>9uC:T(%9cqAW&3%Xeo:M not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_add182651_failAssert0() throws IOException {
        try {
            {
                {
                    StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new Object() {
                        Object test = new Object() {
                            boolean test = false;
                        };
                    });
                    TestUtil.getContents(root, "recursive_parWtial_inheritance.txt");
                    TestUtil.getContents(root, "recursive_parWtial_inheritance.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_add182651 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chzis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0null208050_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0null208050 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_add207879_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_add207879 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_remove205946_literalMutationString206774_failAssert0_literalMutationString210076_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("l,C}1Mju}z}vSa{/g");
                StringWriter sw = new StringWriter();
                Writer o_testSimplePragma_remove205946__7 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimplePragma_remove205946__13 = TestUtil.getContents(root, "simplo.txt");
                String o_testSimplePragma_remove205946__14 = sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_remove205946_literalMutationString206774 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_remove205946_literalMutationString206774_failAssert0_literalMutationString210076 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0null212048_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chzis";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0null212048 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_literalMutationString209876_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "jb>ty";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_literalMutationString209876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_add211626_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        String name = "Chzis";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_add211626 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
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

    public void testMultipleWrappers_literalMutationString257765_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simple.h[ml");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("sbohEm2mxp}");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0null261378_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("sbohEm2mxp}");
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
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0null261378 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0_literalMutationString259919_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("sbohEm2mxp}");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "tst";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0_literalMutationString259919 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257771_remove261143_literalMutationString262463_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("si<ple.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationString257771__7 = m.execute(sw, new Object() {
                String name = "Chis";

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
            String o_testMultipleWrappers_literalMutationString257771__23 = TestUtil.getContents(root, "simplerewrap.txt");
            String o_testMultipleWrappers_literalMutationString257771__24 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257771_remove261143_literalMutationString262463 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template si<ple.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0_add261066_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("sbohEm2mxp}");
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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0_add261066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber257792_literalMutationString258472_literalMutationString262087_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("TV 4&3F$djm");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationNumber257792__7 = m.execute(sw, new Object() {
                String name = "Ch[is";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.0)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationNumber257792__24 = TestUtil.getContents(root, "simplerewrap.txt");
            String o_testMultipleWrappers_literalMutationNumber257792__25 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber257792_literalMutationString258472_literalMutationString262087 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template TV 4&3F$djm not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257765_failAssert0_literalMutationString259820_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simple.h[ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chxis";

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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765_failAssert0_literalMutationString259820 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257765_failAssert0null261372_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simple.h[ml");
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
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765_failAssert0null261372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber257794_literalMutationString258802_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("s^imple.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationNumber257794__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.1)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationNumber257794__24 = TestUtil.getContents(root, "simplerewrap.txt");
            String o_testMultipleWrappers_literalMutationNumber257794__25 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber257794_literalMutationString258802 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^imple.html not found", expected.getMessage());
        }
    }

    public void testNestedLatchesIterable_add170825_literalMutationString171145_failAssert0_literalMutationString178382_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                final StringBuffer sb = new StringBuffer();
                final CountDownLatch cdl1 = new CountDownLatch(1);
                final CountDownLatch cdl2 = new CountDownLatch(1);
                m.execute(sw, new Object() {
                    Iterable list = Arrays.asList(() -> {
                        cdl1.await();
                        sb.append("How");
                        return "How";
                    }, ((Callable<Object>) (() -> {
                        cdl2.await();
                        sb.append("are");
                        cdl1.countDown();
                        return "are";
                    })), () -> {
                        sb.append("you?");
                        cdl2.countDown();
                        return "you?";
                    });
                }).close();
                TestUtil.getContents(root, "latchedtest.txt");
                TestUtil.getContents(root, "latchedtest.txt");
                sw.toString();
                sb.toString();
                junit.framework.TestCase.fail("testNestedLatchesIterable_add170825_literalMutationString171145 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatchesIterable_add170825_literalMutationString171145_failAssert0_literalMutationString178382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0_add14946_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("U>5MBP=$slrccq3[");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0_add14946 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0_literalMutationString13522_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("U>5MBP=$slrccq3[");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "Ahs";
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0_literalMutationString13522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11043_literalMutationString12532_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("u>36},DQq6o<D.L_");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "{A9";
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
            String o_testNestedLatches_literalMutationString11043__25 = sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11043_literalMutationString12532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template u>36},DQq6o<D.L_ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("latchedt1est].html");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_add21082_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory c = createMustacheFactory();
                    c.setExecutorService(Executors.newCachedThreadPool());
                    Mustache m = c.compile("latchedt1est].html");
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
                    sw.toString();
                    junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_add21082 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0null15454_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("U>5MBP=$slrccq3[");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(null, new Object() {
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0null15454 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_literalMutationNumber18485_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory c = createMustacheFactory();
                    c.setExecutorService(Executors.newCachedThreadPool());
                    Mustache m = c.compile("latchedt1est].html");
                    StringWriter sw = new StringWriter();
                    Writer execute = m.execute(sw, new Object() {
                        Callable<Object> nest = () -> {
                            Thread.sleep(300);
                            return "How";
                        };

                        Callable<Object> nested = () -> {
                            Thread.sleep(100);
                            return "are";
                        };

                        Callable<Object> nestest = () -> {
                            Thread.sleep(100);
                            return "you?";
                        };
                    });
                    execute.close();
                    sw.toString();
                    junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_literalMutationNumber18485 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("U>5MBP=$slrccq3[");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87210_failAssert0_add87893_failAssert0_literalMutationString89114_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    execute("`0qr:Z!&u", object);
                    StringWriter sw = execute("page1.txt", object);
                    TestUtil.getContents(root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210_failAssert0_add87893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210_failAssert0_add87893_failAssert0_literalMutationString89114 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `0qr:Z!&u not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_add90408_failAssert0() throws IOException {
        try {
            {
                Collections.singletonList("Tevst");
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Tevst");
                };
                StringWriter sw = execute("[Qa%0&zprtsx", object);
                String o_testIsNotEmpty_literalMutationString87206__9 = TestUtil.getContents(root, "isempty.txt");
                String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_add90408 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0null90916_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Tevst");
                };
                StringWriter sw = execute("[Qa%0&zprtsx", null);
                String o_testIsNotEmpty_literalMutationString87206__9 = TestUtil.getContents(root, "isempty.txt");
                String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0null90916 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87207_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("z85`(vdwgDUK", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z85`(vdwgDUK not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87214_failAssert0_literalMutationString87611_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("I!jUw4q4>GXp", object);
                TestUtil.getContents(root, "t;6y.BLRi_}");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87214 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87214_failAssert0_literalMutationString87611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template I!jUw4q4>GXp not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_add87221_literalMutationString87384_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("IIM3qHQjzVq{", object);
            String o_testIsNotEmpty_add87221__9 = TestUtil.getContents(root, "isempty.txt");
            String o_testIsNotEmpty_add87221__10 = TestUtil.getContents(root, "isempty.txt");
            String o_testIsNotEmpty_add87221__11 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_add87221_literalMutationString87384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template IIM3qHQjzVq{ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Tevst");
            };
            StringWriter sw = execute("[Qa%0&zprtsx", object);
            String o_testIsNotEmpty_literalMutationString87206__9 = TestUtil.getContents(root, "isempty.txt");
            String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmptynull87224_add87846_literalMutationString88582_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList(null);
            };
            StringWriter o_testIsNotEmptynull87224_add87846__7 = execute(")O vhw}Ux$2&", object);
            StringWriter sw = execute("isempty.html", object);
            String o_testIsNotEmptynull87224__9 = TestUtil.getContents(root, "isempty.txt");
            String o_testIsNotEmptynull87224__10 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmptynull87224_add87846_literalMutationString88582 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )O vhw}Ux$2& not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87209_failAssert0_add87878_failAssert0_literalMutationString90082_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute("isemty.ht}ml", object);
                    TestUtil.getContents(root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209_failAssert0_add87878 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209_failAssert0_add87878_failAssert0_literalMutationString90082 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isemty.ht}ml not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87205null87967_failAssert0_literalMutationString89816_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("vest");
                };
                StringWriter sw = execute("X!tvd{?yEQ.8", object);
                String o_testIsNotEmpty_literalMutationString87205__9 = TestUtil.getContents(root, null);
                String o_testIsNotEmpty_literalMutationString87205__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87205null87967 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87205null87967_failAssert0_literalMutationString89816 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template X!tvd{?yEQ.8 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_literalMutationString89460_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Tevst");
                };
                StringWriter sw = execute("[Qa%0&zprtsx", object);
                String o_testIsNotEmpty_literalMutationString87206__9 = TestUtil.getContents(root, "");
                String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_literalMutationString89460 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87207_failAssert0_add87879_failAssert0() throws IOException {
        try {
            {
                Collections.singletonList("Test");
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("z85`(vdwgDUK", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207_failAssert0_add87879 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z85`(vdwgDUK not found", expected.getMessage());
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

    public void testImmutableList_add140633_literalMutationString140722_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            List<Object> o_testImmutableList_add140633__7 = Collections.singletonList(object);
            StringWriter sw = execute("isempt|y.html", Collections.singletonList(object));
            String o_testImmutableList_add140633__11 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_add140633__12 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140633_literalMutationString140722 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isempt|y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_literalMutationString141105_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("]qmNxZO#WC4-", Collections.singletonList(object));
                TestUtil.getContents(root, "3:F3oU])5``");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_literalMutationString141105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_literalMutationString140795_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter o_testImmutableList_add140632__7 = execute("#z&I+4x7{:k0", Collections.singletonList(object));
            StringWriter sw = execute("isempty.html", Collections.singletonList(object));
            String o_testImmutableList_add140632__12 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_literalMutationString140795 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #z&I+4x7{:k0 not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("]qmNxZO#WC4-", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140628_failAssert0_add141326_failAssert0_literalMutationString143453_failAssert0() throws IOException {
        try {
            {
                {
                    Collections.singletonList("Test");
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute("isempty.[html", Collections.singletonList(object));
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString140628 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_add141326 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_add141326_failAssert0_literalMutationString143453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isempty.[html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_literalMutationString141095_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Teast");
                };
                StringWriter sw = execute("]qmNxZO#WC4-", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_literalMutationString141095 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140624_failAssert0_literalMutationString141117_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("vr0Ns_n}Om[", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140624 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140624_failAssert0_literalMutationString141117 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template vr0Ns_n}Om[ not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_add141244_literalMutationString141885_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter o_testImmutableList_add140632__7 = execute("2x`#o5G;H5x,", Collections.singletonList(object));
            ((StringWriter) (o_testImmutableList_add140632__7)).getBuffer().toString();
            StringWriter sw = execute("isempty.html", Collections.singletonList(object));
            String o_testImmutableList_add140632__12 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_add141244_literalMutationString141885 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 2x`#o5G;H5x, not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_add141339_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                Collections.singletonList(object);
                StringWriter sw = execute("]qmNxZO#WC4-", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_add141339 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_add141244_literalMutationString141892_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter o_testImmutableList_add140632__7 = execute("isempty.html", Collections.singletonList(object));
            ((StringWriter) (o_testImmutableList_add140632__7)).getBuffer().toString();
            StringWriter sw = execute("kvk$a)em%Jht", Collections.singletonList(object));
            String o_testImmutableList_add140632__12 = TestUtil.getContents(root, "isempty.txt");
            String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_add141244_literalMutationString141892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kvk$a)em%Jht not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065_failAssert0_literalMutationString142903_failAssert0() throws IOException {
        try {
            {
                {
                    Object object = new Object() {
                        List<String> people = Collections.singletonList("Test");
                    };
                    StringWriter sw = execute(":LMi0j%9^q41", Collections.singletonList(object));
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString140628 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065_failAssert0_literalMutationString142903 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :LMi0j%9^q41 not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString220094_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile(")Hb/ aesK*+tp");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString220094 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testSecurity_literalMutationString220116_remove222742_literalMutationString224543_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("security.ht<ml");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationString220116__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "page1.txt";
            });
            String o_testSecurity_literalMutationString220116__15 = TestUtil.getContents(root, "security.txt");
            String o_testSecurity_literalMutationString220116__16 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString220116_remove222742_literalMutationString224543 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.ht<ml not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString220117_literalMutationString220743_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("3B||$2PI^3?ye");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationString220117__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "T&est";
            });
            String o_testSecurity_literalMutationString220117__15 = TestUtil.getContents(root, "security.txt");
            String o_testSecurity_literalMutationString220117__16 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString220117_literalMutationString220743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3B||$2PI^3?ye not found", expected.getMessage());
        }
    }

    public void testProperties_add241207_literalMutationString241337_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testProperties_add241207__3 = c.compile("EFg]ACL# ??");
            Mustache m = c.compile("simple.html");
            StringWriter sw = new StringWriter();
            Writer o_testProperties_add241207__8 = m.execute(sw, new Object() {
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
            String o_testProperties_add241207__23 = TestUtil.getContents(root, "simple.txt");
            String o_testProperties_add241207__24 = sw.toString();
            junit.framework.TestCase.fail("testProperties_add241207_literalMutationString241337 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template EFg]ACL# ?? not found", expected.getMessage());
        }
    }

    public void testProperties_add241206_remove243407_literalMutationString244676_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            DefaultMustacheFactory o_testProperties_add241206__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Zx8(EE{meXr");
            StringWriter sw = new StringWriter();
            Writer o_testProperties_add241206__8 = m.execute(sw, new Object() {
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
            String o_testProperties_add241206__23 = TestUtil.getContents(root, "simple.txt");
            String o_testProperties_add241206__24 = sw.toString();
            junit.framework.TestCase.fail("testProperties_add241206_remove243407_literalMutationString244676 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Zx8(EE{meXr not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString56692_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("T ln,&?]&>n", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString56692 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template T ln,&?]&>n not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833null193136_failAssert0_literalMutationString193835_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache o_testPartialWithTF_add192833__3 = c.compile("YaUvnwp&s.,-nTpMB@^u72PP`|;i6J");
                Mustache m = c.compile("partialintemplatefunction.html");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_add192833__8 = m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833null193136 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833null193136_failAssert0_literalMutationString193835 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YaUvnwp&s.,-nTpMB@^u72PP`|;i6J not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192828_failAssert0_literalMutationString192945_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192828 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192828_failAssert0_literalMutationString192945 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_add194111_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_add194111 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192834_literalMutationString192888_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("7]!0Zg[:gT QJx]X(x?%#bHJa>i kX");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add192834__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add192834__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            String o_testPartialWithTF_add192834__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192834_literalMutationString192888 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 7]!0Zg[:gT QJx]X(x?%#bHJa>i kX not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193664_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`#LZr_?AN7T8UF(%_E34>,+l;,Ov$pF");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                String o_testPartialWithTF_add192835__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193664 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `#LZr_?AN7T8UF(%_E34>,+l;,Ov$pF not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_literalMutationString193552_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("?`]e8TsQrZ6*TVjtGxLXLb||ETdBJ^?");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_literalMutationString193552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ?`]e8TsQrZ6*TVjtGxLXLb||ETdBJ^? not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
            Mustache m = c.compile("partialintemplatefunction.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            String o_testPartialWithTF_add192833__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0_add194136_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
                Mustache m = c.compile("partialintemplatefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0_add194136 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192832_literalMutationString192898_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testPartialWithTF_add192832__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("%GgF MoRARw/@_w4w|9Q7YUTb`Ez.l");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add192832__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            String o_testPartialWithTF_add192832__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192832_literalMutationString192898 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192901_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("/]G=([#&3j9JZM[W%e&[D7Hx]sLBlR");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            String o_testPartialWithTF_add192835__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192901 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0_literalMutationString193539_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vkOm)to");
                Mustache m = c.compile("partialintemplatefunction.html");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0_literalMutationString193539 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialinte3mplatefunctio|.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0null194549_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0null194549 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0null194546_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
                Mustache m = c.compile(null);
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0null194546 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194405_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194405 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194406_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194406 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194141_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0null194536_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    Mustache m = c.compile(null);
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0null194536 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193663_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partial%ntemplatefunctionn.html");
                StringWriter sw = new StringWriter();
                Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                String o_testPartialWithTF_add192835__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193663 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partial%ntemplatefunctionn.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_literalMutationString193771_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("K]&b@$aH|Wn4wsY%!o!v`SZdGH@6PM[");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_literalMutationString193771 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192885_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add192833__3 = c.compile("partialintemplatefunction.html");
            Mustache m = c.compile("iaT.$!$I(wL ^ Id)/OX%?c97]o,cH");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            String o_testPartialWithTF_add192833__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192885 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_literalMutationString193517_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!vSZdGH@6PM[");
                    Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_literalMutationString193517 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_literalMutationString192942_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("K]&b@$aH|Wn4wsY!v!v`SZdGH@6PM[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_literalMutationString192942 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193110_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTFnull192839_failAssert0_literalMutationString192953_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1!oMMJ9h^Yc-Q`! 5(xnWn{bQ$(Jhy");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull192839 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull192839_failAssert0_literalMutationString192953 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1!oMMJ9h^Yc-Q`! 5(xnWn{bQ$(Jhy not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194139_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    c.compile("partialinte3mplatefunctio|.html");
                    Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {
                        public TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194139 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329012_failAssert0_literalMutationString329682_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("mz/|GePiHa@H");
                m.execute(sw, o);
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329012 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329012_failAssert0_literalMutationString329682 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_literalMutationString332073_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("z6w;<Z!R<q)C");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_literalMutationString332073 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6w;<Z!R<q)C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321214_failAssert0_add322438_failAssert0_literalMutationString323997_failAssert0() throws MustacheException, IOException {
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
                    Mustache m = c.compile("M:]0RfL?s/w ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    Object o = JsonInterpreterTest.toObject(jsonNode);
                    sw = new StringWriter();
                    m = createMustacheFactory().compile("complx.html");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString321214 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString321214_failAssert0_add322438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321214_failAssert0_add322438_failAssert0_literalMutationString323997 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_add330217_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_add330217 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_add330536_failAssert0() throws MustacheException, IOException {
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
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_add330536 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_add330211_failAssert0() throws MustacheException, IOException {
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
                createMustacheFactory().compile("comple:x.html");
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_add330211 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0null330833_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0null330833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329318_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329318 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString328994_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("ycl.MQ[]jfQo");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString328994 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ycl.MQ[]jfQo not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329797_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329797 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("comple:x.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_add322497_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_add322497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0_add335212_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("comple:x.html");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0_add335212 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplexnull321254_failAssert0_literalMutationString322192_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("complex.h%ml");
                StringWriter sw = new StringWriter();
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
                junit.framework.TestCase.fail("testComplexnull321254 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull321254_failAssert0_literalMutationString322192 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h%ml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329009_failAssert0_literalMutationString329712_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile("(=Xh_kl06],1");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString329009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329009_failAssert0_literalMutationString329712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (=Xh_kl06],1 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                TestUtil.getContents(root, "");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334232_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334232 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334230_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334230 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0_add327612_failAssert0() throws MustacheException, IOException {
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
                    Mustache m = c.compile("complex.html");
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
                    m = createMustacheFactory().compile(",nH{z_-2i@G[");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0_add327612 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0null336006_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(null, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0null336006 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321203_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("{AEcOm|mS17Q");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString321203 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {AEcOm|mS17Q not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_add322500_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_add322500 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0_literalMutationString332721_failAssert0() throws MustacheException, IOException {
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
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.tt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0_literalMutationString332721 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplexnull321253_failAssert0null323297_failAssert0_literalMutationString325942_failAssert0() throws MustacheException, IOException {
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
                    Mustache m = c.compile("ByR}i@Vd *W.");
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
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexnull321253 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testComplexnull321253_failAssert0null323297 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull321253_failAssert0null323297_failAssert0_literalMutationString325942 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ByR}i@Vd *W. not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("z6;<Z!R<q)9C");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0_add334692_failAssert0() throws MustacheException, IOException {
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
                    Mustache m = c.compile("complex.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    jg.flush();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    Object o = JsonInterpreterTest.toObject(jsonNode);
                    sw = new StringWriter();
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(sw, o);
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0_add334692 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile(",nH{z_-2i@G[");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_add330547_failAssert0() throws MustacheException, IOException {
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
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_add330547 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540_failAssert0_literalMutationString98577_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile(" does not exist");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540_failAssert0_literalMutationString98577 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add97316_literalMutationString97386_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache o_testComplexParallel_add97316__3 = c.compile("complex.html");
            Mustache m = c.compile("G_6Y3I6!14K ");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add97316__11 = TestUtil.getContents(root, "complex.txt");
            String o_testComplexParallel_add97316__12 = sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add97316_literalMutationString97386 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template G_6Y3I6!14K  not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97306_failAssert0null97951_failAssert0_literalMutationString98921_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile(" does not exist");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new ParallelComplexObject()).close();
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306_failAssert0null97951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306_failAssert0null97951_failAssert0_literalMutationString98921 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("w`%JTg?*(FnB");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0_add97862_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("w`%JTg?*(FnB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0_add97862 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0null97966_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("w`%JTg?*(FnB");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0null97966 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97309_failAssert0_literalMutationString97498_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("/`f&KMb;eJ3K");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97309 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97309_failAssert0_literalMutationString97498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0_literalMutationString97574_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("w`%JTg?*(FnB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "A,^;pJvEJ,o");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0_literalMutationString97574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97311_failAssert0_literalMutationString97483_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("6MjfTsl>}Cew");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "q7^}{:y-;c`");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97311 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97311_failAssert0_literalMutationString97483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6MjfTsl>}Cew not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97307_failAssert0_literalMutationString97530_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("2>V_Ne%z>NH|&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97307 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97307_failAssert0_literalMutationString97530 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 2>V_Ne%z>NH|& not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_add112303_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("coplex.[html", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_add112303 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(";e0 SE`|5JY|", new ParallelComplexObject());
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654_failAssert0_literalMutationString111591_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute(",Pz[].dZQM$c", new ParallelComplexObject());
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654_failAssert0_literalMutationString111591 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,Pz[].dZQM$c not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110674_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("-BbRi|<(OmT%*", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110674 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<(OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_add112322_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute(";e0 SE`|5JY|", new ParallelComplexObject());
                    StringWriter sw = execute(";e0 SE`|5JY|", new ParallelComplexObject());
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_add112322 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0null112753_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0null112753 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112021_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "cAmplex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112508_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112508 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112510_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112510 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112014_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_add112483_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("c7mpl x.html", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_add112483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_add112445_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_add112445 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0null112665_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("coplex.[html", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0null112665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_literalMutationString111940_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("c7mpl x.html", new ParallelComplexObject());
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_literalMutationString111940 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_literalMutationString111824_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    StringWriter sw = execute("", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_literalMutationString111824 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("c7mpl x.html", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0null110858_failAssert0_literalMutationString112080_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("c7mpl>x.html", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0null110858 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0null110858_failAssert0_literalMutationString112080 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c7mpl>x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("coplex.[html", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0() throws MustacheException, IOException {
        try {
            {
                execute("-BbRi|<OmT%*", new ParallelComplexObject());
                StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110516_failAssert0_literalMutationString110746_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("Jf=nekdAn81|", new ParallelComplexObject());
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110516 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110516_failAssert0_literalMutationString110746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jf=nekdAn81| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_literalMutationString111520_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("coplex.[html", new ParallelComplexObject());
                    TestUtil.getContents(root, "UyD(*0KgsrX");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_literalMutationString111520 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_literalMutationString111568_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute(";e0 SE]`|5JY|", new ParallelComplexObject());
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_literalMutationString111568 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;e0 SE]`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0null112719_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0null112719 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112006_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("h`nXJSO6Nu<)", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.Itxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112006 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template h`nXJSO6Nu<) not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112509_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.txt");
                    TestUtil.getContents(root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112509 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallablenull110526_failAssert0_literalMutationString110755_failAssert0_literalMutationString111928_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("[=ZZ$as^g26E", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallablenull110526 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testSerialCallablenull110526_failAssert0_literalMutationString110755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallablenull110526_failAssert0_literalMutationString110755_failAssert0_literalMutationString111928 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [=ZZ$as^g26E not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112004_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute(";+tpO[4q%21D", new ParallelComplexObject());
                    TestUtil.getContents(root, "complex.Itxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;+tpO[4q%21D not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0null110850_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0null110850 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110800_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("-BbRi|<OmT%*", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110800 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0null112675_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute(";e0 SE`|5JY|", new ParallelComplexObject());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0null112675 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112019_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    StringWriter sw = execute("-BbRi|JOmT%*", new ParallelComplexObject());
                    TestUtil.getContents(root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112019 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451_failAssert0_literalMutationString133909_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                                                partial = df.compile(new StringReader(name), "__dynpartial__", "#", "]");
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
                            put("", "simple");
                        }
                    });
                    TestUtil.getContents(root, "s|mple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451_failAssert0_literalMutationString133909 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0() throws MustacheException, IOException {
        try {
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
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "9", "]");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0() throws MustacheException, IOException {
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
                        put("foo", null);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationString121646_failAssert0() throws MustacheException, IOException {
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
                TestUtil.getContents(root, "simple.Mxt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationString121646 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135477_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("+")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
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
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133880_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "sim]ple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133880 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + sim]ple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull120837_failAssert0_literalMutationString122255_failAssert0_literalMutationString129492_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                                                partial = df.compile(new StringReader(name), "__dynpartial__", "G", "]");
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
                            put("foo", "");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartialnull120837 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDynamicPartialnull120837_failAssert0_literalMutationString122255 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull120837_failAssert0_literalMutationString122255_failAssert0_literalMutationString129492 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_literalMutationBoolean123555_failAssert0() throws MustacheException, IOException {
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
                        put("in_ca", false);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_literalMutationBoolean123555 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124288_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124288 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0() throws MustacheException, IOException {
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
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135469_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("+")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135469 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140077_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", null);
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140077 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137291_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140071_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140071 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0null138053_failAssert0() throws MustacheException, IOException {
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
                    m.execute(sw, new HashMap<String, Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put(null, 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0null138053 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0null124243_failAssert0() throws MustacheException, IOException {
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
                                            return appendText(null);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0null124243 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0() throws MustacheException, IOException {
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
                                    variable.substring(0).trim();
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0() throws MustacheException, IOException {
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
                        put("value", 0);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133834_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("na1me", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0null139147_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("foo", null);
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0null139147 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_add136460_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    variable.startsWith("+");
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
                            put("foo", null);
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_add136460 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137273_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("+")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0);
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
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0null124757_failAssert0() throws MustacheException, IOException {
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
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "9", "]");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699_failAssert0null124757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull120848_failAssert0_literalMutationNumber122473_failAssert0() throws MustacheException, IOException {
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
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartialnull120848 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull120848_failAssert0_literalMutationNumber122473 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_add123678_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_add123678 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120776_failAssert0_literalMutationString121684_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(new StringReader("{{>+K [foo].html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120776 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120776_failAssert0_literalMutationString121684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template K simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_add123677_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_add123677 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_literalMutationString121366_failAssert0() throws MustacheException, IOException {
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
                Mustache m = c.compile(new StringReader("{{>+ [foo]html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_literalMutationString121366 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo]html.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126580_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("+")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
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
                    TestUtil.getContents(root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126580 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_literalMutationNumber130555_failAssert0() throws MustacheException, IOException {
        try {
            {
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
                            put("foo", null);
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_literalMutationNumber130555 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126527_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(root) {
                        @Override
                        public MustacheVisitor createMustacheVisitor() {
                            return new DefaultMustacheVisitor(this) {
                                @Override
                                public void partial(TemplateContext tc, String variable) {
                                    if (variable.startsWith("+")) {
                                        TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
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
                            put("nme", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    TestUtil.getContents(root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126527 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0_add124092_failAssert0() throws MustacheException, IOException {
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
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "9", "]");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699_failAssert0_add124092 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38343_failAssert0_literalMutationString38611_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("?>C/5Dj}8Zw");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38343 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38343_failAssert0_literalMutationString38611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testReadme_literalMutationString38344_failAssert0_add38920_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("|tems.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38344_failAssert0_add38920 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0() throws MustacheException, IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("YwxBrK0_V>");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38341_failAssert0_literalMutationString38562_failAssert0() throws MustacheException, IOException {
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
                junit.framework.TestCase.fail("testReadme_literalMutationString38341 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38341_failAssert0_literalMutationString38562 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38350_failAssert0null39057_failAssert0_literalMutationString40013_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("{y9ZXB^u{.");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(null, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "item.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38350 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38350_failAssert0null39057 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38350_failAssert0null39057_failAssert0_literalMutationString40013 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {y9ZXB^u{. not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0null39046_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("YwxBrK0_V>");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0null39046 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38345_failAssert0_literalMutationString38640_failAssert0_literalMutationString40135_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("H}fJwO#W+");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "iteDms.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38345_failAssert0_literalMutationString38640 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38345_failAssert0_literalMutationString38640_failAssert0_literalMutationString40135 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H}fJwO#W+ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_literalMutationString38599_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("YwxBAK0_V>");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_literalMutationString38599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBAK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38344_failAssert0null39039_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("|tems.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38344_failAssert0null39039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_add40672_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("YwxBrK0_V>");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_add40672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("YwxBrK0_V>");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38344_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("|tems.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_add38354_literalMutationString38481_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("xsD:fLW=d>");
            StringWriter sw = new StringWriter();
            System.currentTimeMillis();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add38354__10 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add38354__14 = TestUtil.getContents(root, "items.txt");
            String o_testReadme_add38354__15 = sw.toString();
            junit.framework.TestCase.fail("testReadme_add38354_literalMutationString38481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xsD:fLW=d> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_literalMutationString39626_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory c = createMustacheFactory();
                    Mustache m = c.compile("YwxBrK0_V>");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context());
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, "=9j!m#7nX");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_literalMutationString39626 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_literalMutationString6290_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`-7OnlC$yi[");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "d-fD5Vc?FXt(-b`l}A)v%,52OUR8Dv`PX2M;3l;#P]Im" + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_literalMutationString6290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add5789_literalMutationNumber6005() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache o_testReadmeSerial_add5789__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add5789__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add5789__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        String o_testReadmeSerial_add5789__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        String String_17 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
        boolean boolean_18 = (diff > 3999) && (diff < 0);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0null6784_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`-7OnlC$yi[");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0null6784 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("`-7OnlC$yi[");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_35 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_36 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add5789() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeSerial_add5789__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add5789__10 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add5789__10)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add5789__10)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add5789__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        String o_testReadmeSerial_add5789__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        String String_17 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_17);
        boolean boolean_18 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add5789__10)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add5789__10)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_17);
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_add6685_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`-7OnlC$yi[");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_add6685 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerialnull5798_failAssert0_literalMutationString6453_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("jX-#0kdpXI#");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_47 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_48 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerialnull5798 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeSerialnull5798_failAssert0_literalMutationString6453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jX-#0kdpXI# not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_literalMutationNumber6293_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`-7OnlC$yi[");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 0) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_literalMutationNumber6293 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_add6688_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("`-7OnlC$yi[");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_add6688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add275035_add276003() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        m.execute(sw, new AmplInterpreterTest.Context());
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add275035__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035__16);
        String o_testReadmeParallel_add275035_add276003__21 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035_add276003__21);
        String String_135 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035_add276003__21);
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_add276124_failAssert0() throws MustacheException, IOException {
        try {
            {
                initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("items2.h}tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_add276124 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_add276126_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("items2.h}tml");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_add276126 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add275033_literalMutationString275307_remove280068() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add275033__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__15);
        String o_testReadmeParallel_add275033__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__16);
        String String_133 = "page1.txt" + diff;
        TestCase.assertEquals("page1.txt1002", String_133);
        boolean boolean_134 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__16);
        TestCase.assertEquals("page1.txt1002", String_133);
    }

    public void testReadmeParallel_add275036null276197_failAssert0_literalMutationString278195_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("i{ems2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                System.currentTimeMillis();
                long diff = (System.currentTimeMillis()) - start;
                String o_testReadmeParallel_add275036__15 = TestUtil.getContents(root, null);
                String o_testReadmeParallel_add275036__16 = sw.toString();
                String String_123 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_124 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_add275036null276197 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_add275036null276197_failAssert0_literalMutationString278195 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i{ems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("items2.h}tml");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_literalMutationString275651_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("items2.hk}tml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_literalMutationString275651 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.hk}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallelnull275042_failAssert0_literalMutationString275773_failAssert0_literalMutationString277861_failAssert0() throws MustacheException, IOException {
        try {
            {
                {
                    MustacheFactory c = initParallel();
                    Mustache m = c.compile("zT&FVklr8{U");
                    StringWriter sw = new StringWriter();
                    long start = System.currentTimeMillis();
                    m.execute(sw, new AmplInterpreterTest.Context()).close();
                    long diff = (System.currentTimeMillis()) - start;
                    TestUtil.getContents(root, null);
                    sw.toString();
                    String String_163 = "Should be a lit+tle bit more than 1 second: " + diff;
                    boolean boolean_164 = (diff > 999) && (diff < 2000);
                    junit.framework.TestCase.fail("testReadmeParallelnull275042 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testReadmeParallelnull275042_failAssert0_literalMutationString275773 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallelnull275042_failAssert0_literalMutationString275773_failAssert0_literalMutationString277861 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template zT&FVklr8{U not found", expected.getMessage());
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

    public void testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("xW-g$Cbswk8M ");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred[.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0_add106619_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("xW-g$Cbswk8M ");
                StringWriter sw = new StringWriter();
                m.execute(sw, context);
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_add106619 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("xW-g$Cbswk8M ");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0_add109537_failAssert0() throws IOException {
        try {
            {
                {
                    DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                    Executors.newCachedThreadPool();
                    mf.setExecutorService(Executors.newCachedThreadPool());
                    Object context = new Object() {
                        String title = "Deferred";

                        Object deferred = new DeferringMustacheFactory.DeferredCallable();

                        Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                    };
                    Mustache m = mf.compile("xW-g$Cbswk8M ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, context).close();
                    TestUtil.getContents(root, "deferred[.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0_add109537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_add105654_literalMutationString105777_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("nT[eK(S$9#`c!");
            StringWriter sw = new StringWriter();
            m.execute(sw, context);
            m.execute(sw, context).close();
            String o_testDeferred_add105654__18 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_add105654_literalMutationString105777 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template nT[eK(S$9#`c! not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127_failAssert0_literalMutationString239066_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("1Czd4jXF27LBPQQX$Sf] ZQ&x<");
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127_failAssert0_literalMutationString239066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1Czd4jXF27LBPQQX$Sf] ZQ&x< not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103_failAssert0_literalMutationString239301_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("relative/f[ncti:npaths.html");
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103_failAssert0_literalMutationString239301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_add237943_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_add237943 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add237451_literalMutationString237553_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory o_testRelativePathsTemplateFunction_add237451__1 = createMustacheFactory();
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("(.NF+qb]IpQn1J-,az,++rpEq0H");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_add237451__20 = TestUtil.getContents(root, "relative/paths.txt");
            String o_testRelativePathsTemplateFunction_add237451__21 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add237451_literalMutationString237553 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (.NF+qb]IpQn1J-,az,++rpEq0H not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_literalMutationString237657_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths2txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_literalMutationString237657 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_literalMutationString239195_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_literalMutationString239195 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_add240039_failAssert0() throws IOException {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
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
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_add240039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
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


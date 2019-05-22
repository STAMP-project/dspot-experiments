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

    public void testSimple_literalMutationString4066_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("#imple.html");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString4066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
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

    public void testSimpleI18N_literalMutationString1479_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("]^`2&CvM&sZ");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1479 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]^`2&CvM&sZ not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1513_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("kW )y#w9r24");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template kW )y#w9r24 not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString5119_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            Mustache m = c.compile("[}Iu_GAfO|-5v./!fV`");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString5119 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecursionWithInheritance_literalMutationString7035_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("Cgo$nQnWg>sQR&H#C,`psl(r&+?}!i6", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7035 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Cgo$nQnWg>sQR&H#C,`psl(r&+?}!i6 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString4002_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("rzSbBEHJCODSO!d|SR{;HSaQ^^}B^_$kc!", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString4002 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rzSbBEHJCODSO!d|SR{;HSaQ^^}B^_$kc! not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString3999_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursive_partial_inheritance.h^ml", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString3999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursive_partial_inheritance.h^ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4593_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("si:mplepragma.html");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString4593 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template si:mplepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4591_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("V?U3<T|pN:u/-!`!C");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString4591 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
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

    public void testMultipleWrappers_literalMutationString6035_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("[,u(^F5ZXXf");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString6035 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [,u(^F5ZXXf not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString223_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("Z:Cfs:y<jJK8#IJo");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Z:Cfs:y<jJK8#IJo not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1779_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("isempty.%html", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1779 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isempty.%html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1780_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("+yM-)5!w98Q%", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1780 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +yM-)5!w98Q% not found", expected.getMessage());
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

    public void testImmutableList_literalMutationString3119_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("x7cM{QPrU9IE", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3119 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template x7cM{QPrU9IE not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString4920_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("`MUSwn[b%{Sc*");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString4920 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `MUSwn[b%{Sc* not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5482_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simple.h]ml");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5482 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.h]ml not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5483_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("zqe&xL{@ZM!");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template zqe&xL{@ZM! not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1185_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("+S>X#5la]H>", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1185 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +S>X#5la]H> not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString4284_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("?a-<1O&z}kld.f<FkNyvXm)7DqRmjT");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString4284 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ?a-<1O&z}kld.f<FkNyvXm)7DqRmjT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7748_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("f)sd%B-0[rTE");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7748 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template f)sd%B-0[rTE not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7567_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("Jc (D#ZoC!,y");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7567 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Jc (D#ZoC!,y not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7743_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("comple%x.html");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comple%x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7760_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("`sj{v-#LCjcF");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `sj{v-#LCjcF not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7559_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("j}rKG]eCY7ro");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7559 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template j}rKG]eCY7ro not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2014_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("4WnL:97S|T|e");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 4WnL:97S|T|e not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2016_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("compl{ex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2016 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template compl{ex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString2345_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("fp?9Dj>KW11;", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString2345 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fp?9Dj>KW11; not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2665_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("-")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2683_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2683 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString834_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("GQz][q?;OH");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template GQz][q?;OH not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString143_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("QgN:j}k8+X_");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_25 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_26 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString143 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template QgN:j}k8+X_ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString6421_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("SZ9rdG]Ir|V");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_143 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_144 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString6421 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SZ9rdG]Ir|V not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add6429() throws MustacheException, IOException {
        DefaultMustacheFactory o_testReadmeParallel_add6429__1 = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getRecursionLimit())));
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add6429__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__15);
        String o_testReadmeParallel_add6429__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__16);
        String String_135 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__16);
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

    public void testDeferred_literalMutationString2237_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("Q]jb_8&5?SZX@");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q]jb_8&5?SZX@ not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString5385_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("Tyiyd4TyY-&*K44q[Uvw.Ja8 tX");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString5385 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Tyiyd4TyY-&*K44q[Uvw.Ja8 tX not found", expected.getMessage());
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


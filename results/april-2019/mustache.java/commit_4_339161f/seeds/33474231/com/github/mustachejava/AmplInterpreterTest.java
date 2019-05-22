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

    public void testSimple_literalMutationString4067_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("R[X=mN#c@2Z");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString4067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
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

    public void testSimpleI18N_literalMutationString1512_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("1{ s.liwWjB");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1512 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1{ s.liwWjB not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1511_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                Mustache m = c.compile("simp e.html");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1511 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp e.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1482_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("(7lm0^d_+ y");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1482 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (7lm0^d_+ y not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString5122_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            Mustache m = c.compile("Bf%uv7bJL&0T?K/s!,r");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString5122 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString4212_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursion%html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString4212 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion%html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString4216_failAssert0() throws IOException {
        try {
            StringWriter sw = execute(">R66J`2?z)?I)X", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString4216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >R66J`2?z)?I)X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString7038_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursion_with_inher|itance.html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7038 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_with_inher|itance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString7039_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("s*KDjPt}zI9Xdj7d7P26D]M#)& xHd>", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s*KDjPt}zI9Xdj7d7P26D]M#)& xHd> not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString3998_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("4U-$d`OOI!hfKhH8I&;KHoH4tw?{JHFt+W", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString3998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 4U-$d`OOI!hfKhH8I&;KHoH4tw?{JHFt+W not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4593_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Mnm(.90K) [,GdkS(");
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
            TestCase.assertEquals("Template Mnm(.90K) [,GdkS( not found", expected.getMessage());
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

    public void testMultipleWrappers_literalMutationString6034_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile(".}(AGYIPaTw");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString6034 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .}(AGYIPaTw not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString223_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile(";?KH^yP.QQS3%w_?");
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
            TestCase.assertEquals("Template ;?KH^yP.QQS3%w_? not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1775_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("pPc!m#?-}B67", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1775 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template pPc!m#?-}B67 not found", expected.getMessage());
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
            StringWriter sw = execute("isempty.h>ml", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3119 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isempty.h>ml not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString3118_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("#B.7U]a)3]_`", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #B.7U]a)3]_` not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString4920_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("@HdNZeP*wOs:0");
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
            TestCase.assertEquals("Template @HdNZeP*wOs:0 not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5480_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("rT2x:|AMS6M");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rT2x:|AMS6M not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1187_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute(";830*)h:_D{", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1187 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;830*)h:_D{ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString4279_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile(":K**W|!!;NKXt3GZq^>z2X(iD119`!");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString4279 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template :K**W|!!;NKXt3GZq^>z2X(iD119`! not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7758_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("a9$6DyJ>d|J0");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7758 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template a9$6DyJ>d|J0 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7572_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("9V w0K{!)d Y");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7572 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 9V w0K{!)d Y not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7746_failAssert0() throws MustacheException, IOException {
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
            Mustache m = c.compile("MAt:gc;y|y>7");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template MAt:gc;y|y>7 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7762_failAssert0() throws MustacheException, IOException {
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
            m = createMustacheFactory().compile("complex<.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7762 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex<.html not found", expected.getMessage());
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
            Mustache m = c.compile("t)%>w]gciWL]");
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
            TestCase.assertEquals("Template t)%>w]gciWL] not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2012_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("Bp!)A$5_,kp>");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2012 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Bp!)A$5_,kp> not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2009_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("co:mplex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template co:mplex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString2343_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute(" x.uhyHN,<5c", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString2343 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  x.uhyHN,<5c not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2681_failAssert0() throws MustacheException, IOException {
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
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "y", "]");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2681 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber2672_failAssert0() throws MustacheException, IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber2672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString836_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("6TH(.*qGm[");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString836 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6TH(.*qGm[ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString6422_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("i]tems2.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_147 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_148 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString6422 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i]tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add6438() throws MustacheException, IOException {
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
        sw.toString();
        String o_testReadmeParallel_add6438__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__15);
        String o_testReadmeParallel_add6438__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__16);
        String String_127 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
        boolean boolean_128 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
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

    public void testDeferred_literalMutationString2233_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("deferr<ed.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2233 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template deferr<ed.html not found", expected.getMessage());
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
            Mustache m = mf.compile("Pd>G1n`ZnyD$:");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Pd>G1n`ZnyD$: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString5382_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("<zk8Jty>@2n(_@&M/xI!cW])lSc");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString5382 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
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


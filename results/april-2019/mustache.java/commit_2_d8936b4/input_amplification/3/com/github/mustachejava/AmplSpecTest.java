package com.github.mustachejava;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.mustachejava.codes.DefaultMustache;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.Assert;
import junit.framework.TestCase;


public class AmplSpecTest {
    public void inheritance() throws IOException {
        run(getSpec("inheritance.yml"));
    }

    private void run(JsonNode spec) {
        int fail = 0;
        int success = 0;
        int whitespace = 0;
        Map<String, Object> functionMap = new HashMap<String, Object>() {
            {
                put("Interpolation", new Object() {
                    Function lambda() {
                        return ( input) -> "world";
                    }
                });
                put("Interpolation - Expansion", new Object() {
                    Function lambda() {
                        return ( input) -> "{{planet}}";
                    }
                });
                put("Interpolation - Alternate Delimiters", new Object() {
                    Function lambda() {
                        return ( input) -> "|planet| => {{planet}}";
                    }
                });
                put("Interpolation - Multiple Calls", new Object() {
                    int calls = 0;

                    Function lambda() {
                        return ( input) -> String.valueOf((++(calls)));
                    }
                });
                put("Escaping", new Object() {
                    Function lambda() {
                        return ( input) -> ">";
                    }
                });
                put("Section", new Object() {
                    Function lambda() {
                        return new TemplateFunction() {
                            @Override
                            public String apply(String input) {
                                return input.equals("{{x}}") ? "yes" : "no";
                            }
                        };
                    }
                });
                put("Section - Expansion", new Object() {
                    Function lambda() {
                        return new TemplateFunction() {
                            @Override
                            public String apply(String input) {
                                return (input + "{{planet}}") + input;
                            }
                        };
                    }
                });
                put("Section - Alternate Delimiters", new Object() {
                    Function lambda() {
                        return new TemplateFunction() {
                            @Override
                            public String apply(String input) {
                                return (input + "{{planet}} => |planet|") + input;
                            }
                        };
                    }
                });
                put("Section - Multiple Calls", new Object() {
                    Function lambda() {
                        return new Function<String, String>() {
                            @Override
                            public String apply(String input) {
                                return ("__" + input) + "__";
                            }
                        };
                    }
                });
                put("Inverted Section", new Object() {
                    Function lambda() {
                        return ( input) -> false;
                    }
                });
            }
        };
        for (final JsonNode test : spec.get("tests")) {
            boolean failed = false;
            final DefaultMustacheFactory CF = createMustacheFactory(test);
            String file = test.get("name").asText();
            System.out.print(((("Running " + file) + " - ") + (test.get("desc").asText())));
            StringReader template = new StringReader(test.get("template").asText());
            JsonNode data = test.get("data");
            try {
                Mustache compile = CF.compile(template, file);
                StringWriter writer = new StringWriter();
                compile.execute(writer, new Object[]{ new ObjectMapper().readValue(data.toString(), Map.class), functionMap.get(file) });
                String expected = test.get("expected").asText();
                if (writer.toString().replaceAll("\\s+", "").equals(expected.replaceAll("\\s+", ""))) {
                    System.out.print(": success");
                    if (writer.toString().equals(expected)) {
                        System.out.println("!");
                    } else {
                        whitespace++;
                        System.out.println(", whitespace differences.");
                    }
                } else {
                    System.out.println(": failed!");
                    System.out.println(((expected + " != ") + (writer.toString())));
                    System.out.println(test);
                    failed = true;
                }
            } catch (Throwable e) {
                System.out.println(": exception");
                e.printStackTrace();
                System.out.println(test);
                failed = true;
            }
            if (failed) {
                fail++;
            } else {
                success++;
            }
        }
        System.out.println(((((("Success: " + success) + " Whitespace: ") + whitespace) + " Fail: ") + fail));
        Assert.assertFalse((fail > 0));
    }

    protected DefaultMustacheFactory createMustacheFactory(final JsonNode test) {
        return new DefaultMustacheFactory("/spec/specs") {
            @Override
            public Reader getReader(String resourceName) {
                JsonNode partial = test.get("partials").get(resourceName);
                return new StringReader((partial == null ? "" : partial.asText()));
            }
        };
    }

    private JsonNode getSpec(String spec) throws IOException {
        return new YAMLFactory(new YAMLMapper()).createParser(new InputStreamReader(AmplSpecTest.class.getResourceAsStream(("/spec/specs/" + spec)))).readValueAsTree();
    }

    public void testNestedLatches_literalMutationNumber17758_literalMutationNumber19748_add27506() throws IOException {
        DefaultMustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        c.setExecutorService(Executors.newCachedThreadPool());
        Mustache m = c.compile("latchedtest.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("latchedtest.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.execute(sw, new Object() {
            Callable<Object> nest = () -> {
                Thread.sleep(150);
                return "How";
            };

            Callable<Object> nested = () -> {
                Thread.sleep(400);
                return "are";
            };

            Callable<Object> nestest = () -> {
                Thread.sleep(100);
                return "you?";
            };
        });
        Writer execute = m.execute(sw, new Object() {
            Callable<Object> nest = () -> {
                Thread.sleep(150);
                return "How";
            };

            Callable<Object> nested = () -> {
                Thread.sleep(400);
                return "are";
            };

            Callable<Object> nestest = () -> {
                Thread.sleep(100);
                return "you?";
            };
        });
        execute.close();
        String o_testNestedLatches_literalMutationNumber17758__26 = sw.toString();
        TestCase.assertEquals("<outer>\n<outer>\n<inner>How</inner>\n<inner>How</inner>\n<inner>are</inner>\n<inner>are</inner>\n<inner>you?</inner>\n</outer>\n<inner>you?</inner>\n</outer>\n", o_testNestedLatches_literalMutationNumber17758__26);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("latchedtest.html", ((DefaultMustache) (m)).getName());
    }

    public void testReadmeSerial_add12588_add13290_remove17281() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12588__10 = m.execute(sw, new AmplInterpreterTest.Context());
        ((StringWriter) (o_testReadmeSerial_add12588__10)).getBuffer().toString();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add12588__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12588__14);
        String o_testReadmeSerial_add12588__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12588__15);
        String String_38 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12588__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12588__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_38);
    }

    public void testReadmeSerial_add12591_add13357_add16570() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12591__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add12591_add13357_add16570__16 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591_add13357_add16570__16);
        String o_testReadmeSerial_add12591__13 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__13);
        String o_testReadmeSerial_add12591__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__14);
        String o_testReadmeSerial_add12591__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__15);
        String String_50 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_50);
        boolean boolean_51 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591_add13357_add16570__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_50);
    }

    public void testReadmeSerial_add12587() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeSerial_add12587__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add12587__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add12587__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12587__10 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add12587__10)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add12587__10)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add12587__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12587__14);
        String o_testReadmeSerial_add12587__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12587__15);
        String String_48 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_48);
        boolean boolean_49 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add12587__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add12587__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_add12587__10)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((StringWriter) (o_testReadmeSerial_add12587__10)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12587__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12587__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_48);
    }

    public void testReadmeSerial_add12591_add13357_remove17279() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12591__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add12591__13 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__13);
        String o_testReadmeSerial_add12591__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__14);
        String o_testReadmeSerial_add12591__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__15);
        String String_50 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_50);
        boolean boolean_51 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12591__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_50);
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0null179080_failAssert0_add181219_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=# ##=}}{{##={{ }}=####";
                    new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(null, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179080 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179080_failAssert0_add181219 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0null179080_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(null, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179080 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178277_failAssert0null179085_failAssert0() throws Exception {
        try {
            {
                String template = "{{=##m##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277_failAssert0null179085 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =##m##= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0() throws Exception {
        try {
            String template = "{{=# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178280_failAssert0_add179002_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## #c#=}}{{##={{ }}=####";
                new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0_add179002 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###c#= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178280_failAssert0null179082_failAssert0_literalMutationNumber180207_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## #c#=}}{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0null179082 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0null179082_failAssert0_literalMutationNumber180207 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###c#= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0_literalMutationNumber178694_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0_literalMutationNumber178694 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0_literalMutationNumber178693_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0_literalMutationNumber178693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0null179080_failAssert0null181476_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=# ##=}}{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                    StringWriter sw = new StringWriter();
                    mustache.execute(null, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179080 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179080_failAssert0null181476 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber178292_literalMutationString178400_failAssert0() throws Exception {
        try {
            String template = "{{=# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationNumber178292__8 = mustache.execute(sw, new Object[0]);
            String o_testOutputDelimiters_literalMutationNumber178292__10 = sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178292_literalMutationString178400 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178280_failAssert0null179082_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## #c#=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0null179082 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###c#= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber178292_literalMutationString178400_failAssert0_add181015_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                Writer o_testOutputDelimiters_literalMutationNumber178292__8 = mustache.execute(sw, new Object[0]);
                String o_testOutputDelimiters_literalMutationNumber178292__10 = sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178292_literalMutationString178400 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178292_literalMutationString178400_failAssert0_add181015 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178280_failAssert0() throws Exception {
        try {
            String template = "{{=## #c#=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###c#= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0null179079_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0null179079 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178277_failAssert0_add179008_failAssert0() throws Exception {
        try {
            {
                String template = "{{=##m##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277_failAssert0_add179008 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =##m##= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178280_failAssert0null179082_failAssert0_add181091_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## #c#=}}{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0null179082 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178280_failAssert0null179082_failAssert0_add181091 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###c#= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber178289_failAssert0_literalMutationString178670_failAssert0() throws Exception {
        try {
            {
                String template = "{{=####=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178289 should have thrown NegativeArraySizeException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178289_failAssert0_literalMutationString178670 should have thrown NegativeArraySizeException");
        } catch (NegativeArraySizeException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178277_failAssert0_literalMutationString178721_failAssert0() throws Exception {
        try {
            {
                String template = "{{=##m##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277_failAssert0_literalMutationString178721 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =##m##= @[:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178277_failAssert0_literalMutationString178724_failAssert0() throws Exception {
        try {
            {
                String template = "{{=##m##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "te(st");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277_failAssert0_literalMutationString178724 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =##m##= @[te(st:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber178292_literalMutationString178400_failAssert0_literalMutationNumber179955_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                Writer o_testOutputDelimiters_literalMutationNumber178292__8 = mustache.execute(sw, new Object[0]);
                String o_testOutputDelimiters_literalMutationNumber178292__10 = sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178292_literalMutationString178400 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber178292_literalMutationString178400_failAssert0_literalMutationNumber179955 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178277_failAssert0() throws Exception {
        try {
            String template = "{{=##m##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178277 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =##m##= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0_add179001_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0_add179001 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString178281_failAssert0_add179000_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString178281_failAssert0_add179000 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }
}


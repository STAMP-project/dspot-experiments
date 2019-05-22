package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;
import org.junit.BeforeClass;


public final class AmplMultipleRecursivePartialsTest {
    private static final String TEMPLATE_FILE = "multiple_recursive_partials.html";

    private static File root;

    @SuppressWarnings("unused")
    private static class Model {
        AmplMultipleRecursivePartialsTest.Type type;

        List<AmplMultipleRecursivePartialsTest.Model> items;

        Model(AmplMultipleRecursivePartialsTest.Type type, List<AmplMultipleRecursivePartialsTest.Model> items) {
            this.type = type;
            this.items = items;
        }

        Model(AmplMultipleRecursivePartialsTest.Type type) {
            this.type = type;
        }

        AmplMultipleRecursivePartialsTest.Type getType() {
            return type;
        }

        List<AmplMultipleRecursivePartialsTest.Model> getItems() {
            return items;
        }
    }

    @SuppressWarnings("unused")
    private enum Type {

        FOO,
        BAR;
        boolean isFoo() {
            return (this) == (AmplMultipleRecursivePartialsTest.Type.FOO);
        }

        boolean isBar() {
            return (this) == (AmplMultipleRecursivePartialsTest.Type.BAR);
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplMultipleRecursivePartialsTest.root = (new File(file, AmplMultipleRecursivePartialsTest.TEMPLATE_FILE).exists()) ? file : new File("src/test/resources");
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


package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

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

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
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

    public void testReadmeSerial_add4467_remove5307() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add4467__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add4467__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4467__14);
        String String_42 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
        boolean boolean_43 = (diff > 3999) && (diff < 6000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4467__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
    }

    public void testReadmeParallel_add119158_add119925() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        ((DefaultMustacheFactory) (c)).getExecutorService().isTerminated();
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add119158__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        sw.toString();
        String String_281 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
    }

    public void testReadmeParallel_add119159_remove120199() throws MustacheException, IOException {
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
        String o_testReadmeParallel_add119159__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__14);
        String o_testReadmeParallel_add119159__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__15);
        String String_291 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_291);
        boolean boolean_292 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_291);
    }

    public void testReadmeParallel_add119158_remove120189() throws MustacheException, IOException {
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
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add119158__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        String String_281 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
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

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90993_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to|olong}}"), "GCM#{boqzVzsmX?@M@,(>");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90993 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|olong @[GCM#{boqzVzsmX?@M@,(>:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_literalMutationString91007_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaQidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_literalMutationString91007 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaQidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_add91088_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_add91088 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_literalMutationString91004_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_literalMutationString91004 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91068_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91068 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91064_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91064 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91085_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91085 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_add91087_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_add91087 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_literalMutationString90910_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "te1stInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_literalMutationString90910 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[te1stInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0() throws Exception {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91091_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91091 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91093_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91093 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_add91066_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_add91066 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91092_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91092 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_add91067_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_add91067 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91069_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91069 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91094_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91094 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_literalMutationString90923_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "test[InvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_literalMutationString90923 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[test[InvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91095_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91095 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91097_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91097 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0null91103_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0null91103 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90945_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90945 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91082_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91082 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91070_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91070 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91081_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91081 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91080_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91080 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_literalMutationString91024_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{u{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_literalMutationString91024 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90955_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "EUB.B#K}6-<x(X$&VTm>T");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90955 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[EUB.B#K}6-<x(X$&VTm>T:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91074_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91074 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0null91104_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0null91104 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91099_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91099 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91098_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91098 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_literalMutationString91016_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_literalMutationString91016 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90958_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvali2dDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90958 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvali2dDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0null91113_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0null91113 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0null91112_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0null91112 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90969_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelPmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90969 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelPmiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91075_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91075 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90946_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90946 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0null91114_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0null91114 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91076_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91076 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0null91106_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0null91106 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90947_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90947 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0null91111_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to|olong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0null91111 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|olong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90986_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to|ol@ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90986 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|ol@ng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91073_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91073 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0null91108_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0null91108 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91072_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91072 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91071_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91071 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0null91110_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0null91110 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90967_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90967 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0null91105_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0null91105 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91089_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91089 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0null91109_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too1ong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0null91109 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=too1ong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91077_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91077 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91084_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91084 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91078_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91078 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90981_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90981 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli#iters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_literalMutationString90935_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_literalMutationString90935 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_literalMutationString90902_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pae1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_literalMutationString90902 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[pae1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91063_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91063 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=to|olong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91079_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91079 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91062_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91062 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91083_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91083 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0null91107_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0null91107 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90983_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#2} Y(sb[{s;c$?qqpe1^");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90983 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[#2} Y(sb[{s;c$?qqpe1^:1]", expected.getMessage());
        }
    }

    private static class SuperClass {
        String values = "value";
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0_literalMutationString65964_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "tet");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0_literalMutationString65964 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={}}= @[tet:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0() throws Exception {
        try {
            String template = "{{=#]# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =#]###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65574_literalMutationString65744_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{W}}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString65574__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65574_literalMutationString65744 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{W}}= @[:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0_add66300_failAssert0() throws Exception {
        try {
            {
                String template = "{{=#]# ##=}}{{##={{ }}=####";
                new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0_add66300 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =#]###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0null66390_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0null66390 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={}}= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={}}= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0_add66294_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0_add66294 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={}}= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0_literalMutationString65998_failAssert0() throws Exception {
        try {
            {
                String template = "{{=#]# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "]?ya");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0_literalMutationString65998 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =#]###= @[]?ya:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0null66396_failAssert0() throws Exception {
        try {
            {
                String template = "{{=#]# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0null66396 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =#]###= @[null:1]", expected.getMessage());
        }
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


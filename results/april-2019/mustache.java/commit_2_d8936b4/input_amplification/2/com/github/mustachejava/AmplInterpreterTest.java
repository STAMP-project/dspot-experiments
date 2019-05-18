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
import junit.framework.AssertionFailedError;
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

    public void testReadmeSerial_add4390_add5321() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add4390__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add4390__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__13);
        String o_testReadmeSerial_add4390__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__14);
        String o_testReadmeSerial_add4390__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__15);
        String String_42 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
        boolean boolean_43 = (diff > 3999) && (diff < 6000);
        ((StringWriter) (o_testReadmeSerial_add4390__9)).getBuffer().toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
    }

    public void testReadmeSerial_add4391_literalMutationString4557() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add4391__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add4391__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__14);
        String o_testReadmeSerial_add4391__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__15);
        String String_34 = "page1.txt" + diff;
        TestCase.assertEquals("page1.txt4001", String_34);
        boolean boolean_35 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__15);
        TestCase.assertEquals("page1.txt4001", String_34);
    }

    public void testReadmeParallel_add118792_literalMutationNumber118970() throws MustacheException, IOException {
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
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add118792__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        String o_testReadmeParallel_add118792__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        String String_281 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_281);
        boolean boolean_282 = (diff > 0) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_281);
    }

    public void testReadmeParallel_add118792_remove119885() throws MustacheException, IOException {
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
        String o_testReadmeParallel_add118792__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        String o_testReadmeParallel_add118792__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        String String_281 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_281);
    }

    public void testReadmeParallel_add118793_literalMutationNumber119041() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add118793__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__16);
        String o_testReadmeParallel_add118793__17 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__17);
        String String_285 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_286 = (diff > 1998) && (diff < 2000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__17);
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

    public void testInvalidDelimiters_add90438_failAssert0_literalMutationString90642_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalid]Delimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_literalMutationString90642 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalid]Delimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90681_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90681 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90682_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90682 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0null90728_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0null90728 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "test}nvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90607_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jwz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90607 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jwz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90608_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "9$}v]+Mj ^QN!eJ%oMm;!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90608 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[9$}v]+Mj ^QN!eJ%oMm;!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_add90689_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_add90689 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_literalMutationString90589_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo1g}}"), "g2@tQ;O}0D&.IbvTrWmQe");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_literalMutationString90589 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo1g @[g2@tQ;O}0D&.IbvTrWmQe:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90628_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("={=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90628 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0null90719_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0null90719 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_literalMutationString90647_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidD`limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_literalMutationString90647 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD`limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90595_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooloag}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90595 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooloag @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90567_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toZolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90567 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toZolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90592_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90592 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =oolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90599_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90599 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_literalMutationString90550_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong5}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_literalMutationString90550 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong5 @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0null90720_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0null90720 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90573_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90573 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90708_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90708 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90686_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90686 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90685_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90685 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90683_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90683 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0null90729_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0null90729 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90508_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tlong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90508 should have thrown AssertionFailedError");
        } catch (AssertionFailedError expected) {
            TestCase.assertEquals("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90706_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90706 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90707_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90707 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90684_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90684 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90710_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90710 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90712_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90712 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_add90697_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_add90697 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90632_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90632 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90690_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90690 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90524_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90524 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0null90727_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0null90727 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90516_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90516 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelmiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90693_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90691_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90691 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90679_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90679 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0null90726_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0null90726 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90526_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90526 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0null90730_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0null90730 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90704_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90704 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90692_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90692 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90678_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90678 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0null90723_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0null90723 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90619_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90619 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90703_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90703 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0null90724_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0null90724 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0null90721_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0null90721 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90701_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90701 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90702_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90702 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90700_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90700 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0_add90713_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440_failAssert0_add90713 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90538_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90538 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90561_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90561 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_add90696_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_add90696 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0_add90715_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440_failAssert0_add90715 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90521_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tBolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90521 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tBolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0null90722_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong5}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0null90722 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong5 @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90711_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90711 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0null90725_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo1g}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0null90725 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo1g @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidD6elimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_add90688_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_add90688 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90536_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too=long}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90536 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too=long @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90699_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90699 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90695_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90695 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90680_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90680 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0() throws Exception {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90694_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90694 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90556_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=oolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90556 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =oolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong5}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    private static class SuperClass {
        String values = "value";
    }

    public void testEmptyMustachenull106457_failAssert0_literalMutationString106769_failAssert0() throws Exception {
        try {
            {
                {
                    new DefaultMustacheFactory().compile(new StringReader("{{=}}"), null);
                }
                junit.framework.TestCase.fail("testEmptyMustachenull106457 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testEmptyMustachenull106457_failAssert0_literalMutationString106769 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: = @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0null66239_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={{ }}=#f##";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0null66239 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65427_literalMutationString65693_failAssert0() throws Exception {
        try {
            String template = "{{=#5# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "e;1H");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString65427__8 = mustache.execute(sw, new Object[0]);
            String o_testOutputDelimiters_literalMutationString65427__9 = sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65427_literalMutationString65693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =#5###= @[e;1H:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0_add66166_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##M=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0_add66166 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####M= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0null66242_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##M=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0null66242 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####M= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0_add66162_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={{ }}=#f##";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0_add66162 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{ }}=#f##";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0() throws Exception {
        try {
            String template = "{{=## ##M=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####M= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0_literalMutationNumber65858_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}}{{##={{ }}=#f##";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0_literalMutationNumber65858 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0_literalMutationString65866_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##M=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), ">A L");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0_literalMutationString65866 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####M= @[>A L:1]", expected.getMessage());
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


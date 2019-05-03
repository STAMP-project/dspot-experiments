package com.github.mustachejava;


import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0() throws Exception {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
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


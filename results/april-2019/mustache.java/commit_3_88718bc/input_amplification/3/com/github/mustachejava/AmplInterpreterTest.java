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

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1453_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1453 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1452_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1452 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null300_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null300 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1456_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "iy >WAE8z-eSkmNWSz_6");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_literalMutationString1456 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[iy >WAE8z-eSkmNWSz_6:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong%}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null295_failAssert0_literalMutationString813_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=Toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null295 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null295_failAssert0_literalMutationString813 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add261_failAssert0null2167_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add261 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add261_failAssert0null2167 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0_literalMutationString1300_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0_literalMutationString1300 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_add2112_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_add2112 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_add2111_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_add2111 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null304_failAssert0_add2008_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304_failAssert0_add2008 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0_add2136_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong%}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0_add2136 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159_failAssert0_add1988_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidrelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159_failAssert0_add1988 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidrelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0null2227_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0null2227 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null302_failAssert0_add1919_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tool*ng}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null302 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null302_failAssert0_add1919 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0_add2031_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0_add2031 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null302_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null302 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0_add1949_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                        mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0_add1949 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString148_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$Q5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString148 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$Q5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0_literalMutationString1729_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon%}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0_literalMutationString1729 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add261_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add261 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString185_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "Eq_{{l>^r@)C1RND7C-6y");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString185 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[Eq_{{l>^r@)C1RND7C-6y:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add275_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add275 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString99_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolongc}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString99 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add274_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add274 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString203_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "hD9P/&h4]]s%=])JWOM_4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString203 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[hD9P/&h4]]s%=])JWOM_4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159_failAssert0null2184_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159_failAssert0null2184 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null305_failAssert0null2164_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305_failAssert0null2164 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString184_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "tesLtInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString184 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tesLtInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0_literalMutationString1213_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvanlidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0_literalMutationString1213 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvanlidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null300_failAssert0_literalMutationString823_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tocolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null300 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null300_failAssert0_literalMutationString823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add282_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add282 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_add1939_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_add1939 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add281_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add281 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0null2218_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0null2218 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString150_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5u:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString150 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5u:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add280_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add280 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0null2219_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0null2219 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0null296_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0null296 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add270_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add270 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add290_failAssert0_add1951_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290_failAssert0_add1951 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add262_failAssert0_add1927_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add262 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add262_failAssert0_add1927 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0null2168_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0null2168 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add286_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add286 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_add2080_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_add2080 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add260_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add260 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null295_failAssert0_add1902_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null295 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null295_failAssert0_add1902 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_add1941_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_add1941 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0_literalMutationString1285_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitYrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0_literalMutationString1285 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0_literalMutationString1539_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), " !-3SN*sw)`v}zN/0NJ?w");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0_literalMutationString1539 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0null2212_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0null2212 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0null301_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0null301 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0_add2096_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0_add2096 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0null2223_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0null2223 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0_add2026_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0_add2026 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add262_failAssert0null2165_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add262 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add262_failAssert0null2165 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString161_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString161 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add266_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add266 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add265_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add265 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString86_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=todolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString86 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString138_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "O");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString138 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[O:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2063_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstHnvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2063 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstHnvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0_add2076_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolxn}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0_add2076 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString88_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString88 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0null297_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0null297 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add290_failAssert0null2174_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290_failAssert0null2174 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add290_failAssert0null2173_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290_failAssert0null2173 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0null2192_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0null2192 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add259_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add259 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2064_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstHnvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstHnvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2064 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString137_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString137 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null305_failAssert0_add1913_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305_failAssert0_add1913 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2065_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstHnvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0_add2065 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstHnvalidDelimiters:1]", expected.getMessage());
        }
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

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_add2037_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_add2037 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_add2039_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_add2039 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add273_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add273 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0null2203_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0null2203 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString112_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "M]:bMoV#N");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString112 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[M]:bMoV#N:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0null2200_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0null2200 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null305_failAssert0_literalMutationString841_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testIn_alidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305_failAssert0_literalMutationString841 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testIn_alidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0null2169_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0null2169 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0_add2024_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0_add2024 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add267_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add267 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_literalMutationString921_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstIBvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add256_failAssert0_literalMutationString921 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstIBvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add272_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add272 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0null2222_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0null2222 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0null2217_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0null2217 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add271_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add271 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString92 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1935_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1935 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0_literalMutationString1276_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolTong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0_literalMutationString1276 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolxn}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1936_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1936 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null305_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null305 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add262_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add262 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1938_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0_add1938 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null295_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null295 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidrelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString159 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidrelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0null2172_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0null2172 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0null2235_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong%}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString143_failAssert0null2235 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null304_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add14_failAssert0_add290_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0null2171_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0null2171 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0null2197_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0null2197 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0null298_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0null298 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0_add2012_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add264_failAssert0_add2012 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add285_failAssert0null2198_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add285_failAssert0null2198 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_literalMutationString1634_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add258_failAssert0_literalMutationString1634 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0_literalMutationString1504_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolxn}}"), "testInvalidDelimiUers");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0_literalMutationString1504 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiUers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_literalMutationString1509_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolng}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_literalMutationString1509 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null304_failAssert0_literalMutationString1188_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=t.olong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304_failAssert0_literalMutationString1188 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null300_failAssert0_add1907_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null300 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null300_failAssert0_add1907 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_literalMutationString1342_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add268_failAssert0_literalMutationString1342 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add278_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add278 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add279_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add279 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0_literalMutationString1577_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                        Mustache m = mf.compile(new StringReader(""), "testInvalidDe,limiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add263_failAssert0_literalMutationString1577 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString105_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "lKC*+{5@T5!^MYU(dM7K");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString105 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[lKC*+{5@T5!^MYU(dM7K:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add288_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add288 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add290_failAssert0_literalMutationString969_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvaldDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add290_failAssert0_literalMutationString969 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvaldDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString148_failAssert0null2158_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString148 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString148_failAssert0null2158 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add287_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add287 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_literalMutationString1512_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pa)e1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_literalMutationString1512 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[pa)e1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null304_failAssert0null2191_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null304_failAssert0null2191 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add277_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add277 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null302_failAssert0_literalMutationString857_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool8*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null302 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null302_failAssert0_literalMutationString857 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0null2199_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add257_failAssert0null2199 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString222_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("eV8<Or;(?xw0"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString222 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_add2079_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString147_failAssert0_add2079 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString225_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString225 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0null299_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0null299 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0null303_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0null303 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString170_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString170 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0_literalMutationString909_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testnvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0_literalMutationString909 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add283_failAssert0_literalMutationString908_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add283_failAssert0_literalMutationString908 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0_literalMutationString954_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                        Mustache m = mf.compile(new StringReader("{{=toolosng}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add269_failAssert0_literalMutationString954 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0null298_failAssert0_add1916_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0null298 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0null298_failAssert0_add1916 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0null294_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0null294 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString108_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Ztoolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString108 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0_add2085_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString213_failAssert0_add2085 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add253_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add253 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString185_failAssert0null2162_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString185 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString185_failAssert0null2162 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add254_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add254 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add255_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add255 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0null2216_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolxn}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString152_failAssert0null2216 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
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


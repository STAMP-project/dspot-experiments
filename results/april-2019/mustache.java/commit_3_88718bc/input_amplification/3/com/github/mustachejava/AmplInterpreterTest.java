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

    public void testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_add1825_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_add1825 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_add2045_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_add2045 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_add1873_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_add1873 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "o-9M/^zOCxu?!rIXp5pNO");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[o-9M/^zOCxu?!rIXp5pNO:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_add2046_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_add2046 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add254_failAssert0_add2051_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add254 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add254_failAssert0_add2051 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_add1874_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_add1874 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0null2118_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0null2118 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0_add1902_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0_add1902 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_add1823_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_add1823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_literalMutationString83_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolxng}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_literalMutationString83 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString190_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:6Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString190 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:6Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add289_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add289 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add254_failAssert0_literalMutationString1548_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("WO=QzF5*<#D<"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add254 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add254_failAssert0_literalMutationString1548 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_literalMutationString1643_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolng}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_literalMutationString1643 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add289_failAssert0null2119_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tool*ng}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add289 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add289_failAssert0null2119 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add287_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add287 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add261_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add261 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add263_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add263 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null295_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null295 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add288_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add288 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0_literalMutationString961_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "J(^Wif:x,");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0_literalMutationString961 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[J(^Wif:x,:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add270_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add270 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0null296_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0null296 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add262_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add262 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0null2187_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0null2187 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString173_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString173 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add273_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add273 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0null304_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0null304 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add274_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add274 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString130_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString130 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_add257_failAssert0null2138_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add257 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add257_failAssert0null2138 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_add275_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_add275 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add255_failAssert0_add1967_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add255 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add255_failAssert0_add1967 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0null299_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0null299 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0null2174_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0null2174 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0_literalMutationString1200_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*nX}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0_literalMutationString1200 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0null2175_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0null2175 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add253_failAssert0_add1879_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add253 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add253_failAssert0_add1879 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString178_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString178 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString133_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "4}^w[&oDAIOw? O!T}Lq8");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString133 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[4}^w[&oDAIOw? O!T}Lq8:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_literalMutationString1263_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Ma !VX)*-");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_literalMutationString1263 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[Ma !VX)*-:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString90_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tIoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString90 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_literalMutationString1262_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), " does not exist");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_literalMutationString1262 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[ does not exist:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_add1985_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_add1985 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString139_failAssert0null2152_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolo;ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString139 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString139_failAssert0null2152 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0null301_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0null301 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_add1986_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0_add1986 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0_add1907_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0_add1907 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0_add1905_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0_add1905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString154_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString154 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_literalMutationString704_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*eng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_literalMutationString704 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString168_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "A%.UJum&)<4oK[>Va&1`i[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString168 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[A%.UJum&)<4oK[>Va&1`i[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString167_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString167 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_literalMutationString1538_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("{{=tolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_literalMutationString1538 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add264_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add264 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add265_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add265 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString164_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo]ng}}"), "testInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString164 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString7_failAssert0null302_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0null302 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString9_failAssert0_add266_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_add266 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString158_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "b>_1JVt2Y");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString158 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[b>_1JVt2Y:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0_add2091_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0_add2091 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null298_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null298 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0_add1888_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0_add1888 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0null2151_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0null2151 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0null2183_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0null2183 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0null2126_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0null2126 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0null2127_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString199_failAssert0null2127 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_literalMutationString1540_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("|II(+VTO@{^)"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add277_failAssert0_literalMutationString1540 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_literalMutationString667_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tLoolon}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0null303_failAssert0_literalMutationString667 should have thrown MustacheException");
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

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString96_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString96 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0null2109_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0null2109 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0null2158_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0null2158 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add253_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add253 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add255_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add255 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add254_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add254 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString144 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString139_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo;ng}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString139 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0_add2007_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "o-9M/^zOCxu?!rIXp5pNO");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0_add2007 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[o-9M/^zOCxu?!rIXp5pNO:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add287_failAssert0null2147_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add287 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add287_failAssert0null2147 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add258_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add258 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add258_failAssert0null2165_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add258 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add258_failAssert0null2165 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0_add2008_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tool*ng}}"), "o-9M/^zOCxu?!rIXp5pNO");
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "o-9M/^zOCxu?!rIXp5pNO");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString226_failAssert0_add2008 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[o-9M/^zOCxu?!rIXp5pNO:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add257_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add257 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add281_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add281 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add256_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add256 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString146_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "!][,J^uy}s#6CE3#^t l");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString146 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[!][,J^uy}s#6CE3#^t l:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add280_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add280 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add268_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add268 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add269_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add269 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_add267_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_add267 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_add279_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_add279 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0_add1939_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0_add1939 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0null294_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0null294 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0_literalMutationString1096_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "lpPW`h79`");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0_literalMutationString1096 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[lpPW`h79`:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add261_failAssert0null2117_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add261 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add261_failAssert0null2117 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString215_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimi1ers");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString215 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimi1ers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString190_failAssert0null2128_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString190 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString190_failAssert0null2128 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_add2078_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_add2078 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0_literalMutationString768_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0_literalMutationString768 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null298_failAssert0_add1821_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null298 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null298_failAssert0_add1821 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0null298_failAssert0_add1822_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null298 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0null298_failAssert0_add1822 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0null2146_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0null2146 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_add2077_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString187_failAssert0_add2077 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_add1838_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_add1838 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString109_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInJvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString109 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInJvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0null297_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0null297 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString104_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString104 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add284_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add284 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0null300_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0null300 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString127_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testHnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString127 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString106_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString106 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0null2137_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add272_failAssert0null2137 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add278_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add278 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0_literalMutationString900_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInSalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0_literalMutationString900 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInSalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0_add1969_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_add290_failAssert0_add1969 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0null2121_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add285_failAssert0null2121 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add283_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add283 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_literalMutationString862_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_add276_failAssert0_literalMutationString862 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_literalMutationString83_failAssert0_add2006_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolxng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_literalMutationString83 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_literalMutationString83_failAssert0_add2006 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0_add1852_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0_add1852 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_add1836_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0null305_failAssert0_add1836 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0_literalMutationString1687_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString223_failAssert0_literalMutationString1687 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0null2110_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_add271_failAssert0null2110 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_add282_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_add282 should have thrown MustacheException");
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


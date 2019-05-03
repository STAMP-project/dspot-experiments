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

    public void testInvalidDelimiters_add13_failAssert0_add282_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add282 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_add281_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add281 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString146_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "(q2 5[gpbL[{$QV5*Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString146 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5*Wz2[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString145_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString145 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString217_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString217 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString218_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "N6{+DN-eV8<Or;(?xw0]W");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString218 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString144_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "(q2 5[gpbL[{$QV5:Wz2[");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString7_failAssert0_literalMutationString144 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[(q2 5[gpbL[{$QV5:Wz2[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString169_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString169 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString212_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolhng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString212 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_add280_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_add280 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString98_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon7}}"), "tstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString98 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString186_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvalidDelimEters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString186 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimEters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString183_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*ng}}"), "testInvlidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString183 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvlidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString6_failAssert0_add276_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_add276 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimitersnull15_failAssert0_add289_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add289 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add288_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add288 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString162_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "tes4tInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString162 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tes4tInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString196_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tesInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString196 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tesInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString197_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDel}imiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString197 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDel}imiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString190_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t2oolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString190 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString198_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "RQHCH3r6EO;&FZRy/=u e");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString198 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[RQHCH3r6EO;&FZRy/=u e:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString89_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testH5nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString89 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testH5nvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString127_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "westInvalidDe,limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString127 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[westInvalidDe,limiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString129_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,l}imiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString129 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,l}imiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString136_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString136 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add13_failAssert0_literalMutationString194_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add13 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add13_failAssert0_literalMutationString194 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString135_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString8_failAssert0_literalMutationString135 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString116_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), " does not exist");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString116 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[ does not exist:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString125_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe,limiers");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString125 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDe,limiers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString167_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}ool/ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString167 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString124_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString12_failAssert0_literalMutationString124 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString113_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString113 should have thrown MustacheException");
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

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString199_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader(""), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString199 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString114_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "paCge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString114 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[paCge1.txt:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString93_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testHnvaoidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString93 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testHnvaoidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString91_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "83OI`-k-a8(J8Bp[$XdYQ");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString9_failAssert0_literalMutationString91 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[83OI`-k-a8(J8Bp[$XdYQ:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString156_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=too?lon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString156 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString157_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString2_failAssert0_literalMutationString157 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString103_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString103 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString101_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString101 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString179_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool*Tg}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString179 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add286_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add286 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_add14_failAssert0_add287_failAssert0() throws Exception {
        try {
            {
                {
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

    public void testInvalidDelimiters_add14_failAssert0_add285_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add285 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_add290_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_add290 should have thrown MustacheException");
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

    public void testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString177_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=>tool*ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5_failAssert0_literalMutationString177 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_add284_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_add284 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString102_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tstIn6alidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString11_failAssert0_literalMutationString102 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tstIn6alidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString206_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString206 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_literalMutationString224_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool!ng}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_literalMutationString224 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString207_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "6fdF&0xT!&b-W-(y_V1a;");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString207 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[6fdF&0xT!&b-W-(y_V1a;:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString202_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{%=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString202 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add14_failAssert0_literalMutationString201_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{Ztoolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add14 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add14_failAssert0_literalMutationString201 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull15_failAssert0_literalMutationString226_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Itoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull15 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull15_failAssert0_literalMutationString226 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[null:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString172_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "m&)<4oK[>Va&1`i[aMe!@");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString172 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[m&)<4oK[>Va&1`i[aMe!@:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString173_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "tetInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString173 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[tetInvalidDelimiters:1]", expected.getMessage());
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

    public void testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString109_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString10_failAssert0_literalMutationString109 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString171_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=t}oolong}}"), "testInvalJidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString6_failAssert0_literalMutationString171 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string @[testInvalJidDelimiters:1]", expected.getMessage());
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


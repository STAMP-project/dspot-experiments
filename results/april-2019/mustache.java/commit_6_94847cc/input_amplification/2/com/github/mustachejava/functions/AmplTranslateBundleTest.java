package com.github.mustachejava.functions;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheNotFoundException;
import com.github.mustachejava.TestUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplTranslateBundleTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add805_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add805 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString244_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
            String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString244 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString10_literalMutationString308_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("m[sIOKbC2V%f]Ai]HVzQ");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_literalMutationString10__9 = scope.put("tras", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_literalMutationString10__11 = m.execute(sw, scope);
            String o_testTranslation_literalMutationString10__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString10_literalMutationString308 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m[sIOKbC2V%f]Ai]HVzQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_literalMutationString433_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.5xt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_literalMutationString433 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString8_literalMutationString326_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("translate{bundle.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_literalMutationString8__9 = scope.put("trvans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_literalMutationString8__11 = m.execute(sw, scope);
            String o_testTranslation_literalMutationString8__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString8_literalMutationString326 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template translate{bundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString2_literalMutationString257_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template XI%U^=`EY>I, not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_literalMutationString2__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_literalMutationString2__11 = m.execute(sw, scope);
            String o_testTranslation_literalMutationString2__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString2_literalMutationString257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0null974_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template XI%U^=`EY>I, not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0null974 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template XI%U^=`EY>I, not found not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplTranslateBundleTest.root = new File(compiler, "src/test/resources/functions");
    }
}


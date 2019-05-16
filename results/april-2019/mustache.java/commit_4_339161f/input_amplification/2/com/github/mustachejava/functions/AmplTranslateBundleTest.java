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
    public void testTranslation_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Tfzbwqf)P0S6E]4{S_Nz");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tfzbwqf)P0S6E]4{S_Nz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add22_literalMutationString110_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("#!)_jb&)@jsbA_=1i;:1");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add22__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add22__11 = m.execute(sw, scope);
            String o_testTranslation_add22__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            String o_testTranslation_add22__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            String o_testTranslation_add22__14 = sw.toString();
            org.junit.Assert.fail("testTranslation_add22_literalMutationString110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #!)_jb&)@jsbA_=1i;:1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString5_failAssert0_add797_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Tfzbwqf)P0S6E]4{S_Nz");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString5_failAssert0_add797 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tfzbwqf)P0S6E]4{S_Nz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString5_failAssert0null954_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Tfzbwqf)P0S6E]4{S_Nz");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString5_failAssert0null954 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tfzbwqf)P0S6E]4{S_Nz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString5_failAssert0_literalMutationString415_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Tfzbwqf)P0S6E]4{S_Nz");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("Xrans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString5_failAssert0_literalMutationString415 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Tfzbwqf)P0S6E]4{S_Nz not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplTranslateBundleTest.root = new File(compiler, "src/test/resources/functions");
    }
}


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


public class AmplBundleFunctionsTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1358_failAssert0_literalMutationString2715_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("#{*z!+.!r(8@");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "r6eplaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString1358 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString1358_failAssert0_literalMutationString2715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #{*z!+.!r(8@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1369_literalMutationString2106_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("dB$IFUa[)%uJ");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString1369__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString1369__11 = scope.put("replalceMe", "replaced");
            Writer o_testPostLabels_literalMutationString1369__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString1369__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1369_literalMutationString2106 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dB$IFUa[)%uJ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("#{*z!+.!r(8@");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #{*z!+.!r(8@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1358_failAssert0_add3373_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("#{*z!+.!r(8@");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString1358 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString1358_failAssert0_add3373 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #{*z!+.!r(8@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1358_failAssert0null3730_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("#{*z!+.!r(8@");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString1358 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString1358_failAssert0null3730 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #{*z!+.!r(8@ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString16_failAssert0_literalMutationString496_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("dRLq|uj)R_uL");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nul;llabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString16 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString16_failAssert0_literalMutationString496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template dRLq|uj)R_uL not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("h{2[;F_HC.{*");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template h{2[;F_HC.{* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_literalMutationString458_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("@m>jqgY&KaN`");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString458 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template @m>jqgY&KaN` not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_literalMutationString390_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("A Comedy of Errors");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString2__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString2__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString2__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2_literalMutationString390 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A Comedy of Errors not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}


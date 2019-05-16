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
    public void testPreLabels_literalMutationString3941_failAssert0_literalMutationString5144_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("f^@t,}Wj+!vu`");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString3941 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString3941_failAssert0_literalMutationString5144 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template f^@t,}Wj+!vu` not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString3940_failAssert0_literalMutationString5201_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("j4&}(cJX]?g/");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString3940 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString3940_failAssert0_literalMutationString5201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add1354_literalMutationString1498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("DY;Ff4.4qk%k");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add1354__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add1354__11 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add1354__12 = m.execute(sw, scope);
            Writer o_testPostLabels_add1354__13 = m.execute(sw, scope);
            String o_testPostLabels_add1354__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            String o_testPostLabels_add1354__15 = sw.toString();
            org.junit.Assert.fail("testPostLabels_add1354_literalMutationString1498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template DY;Ff4.4qk%k not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1334_literalMutationString2205_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("OS|kJc A/V?m");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString1334__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString1334__11 = scope.put("repiaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString1334__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString1334__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            String o_testPostLabels_literalMutationString1334__14 = sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1334_literalMutationString2205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString1323_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("98G7J,FuU2:u");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1323 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 98G7J,FuU2:u not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0_add8599_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("?undles.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554_failAssert0_add8599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6573_literalMutationString7700_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("gT)vp9LFj{6x");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPreNullLabels_literalMutationString6573__9 = scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPreNullLabels_literalMutationString6573__11 = scope.put("replaceMe", "repla!ced");
            Writer o_testPreNullLabels_literalMutationString6573__12 = m.execute(sw, scope);
            String o_testPreNullLabels_literalMutationString6573__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPreNullLabels_literalMutationString6573__14 = sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6573_literalMutationString7700 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template gT)vp9LFj{6x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("?undles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0_literalMutationString8021_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("?undles.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("tr|ns", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554_failAssert0_literalMutationString8021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString7_literalMutationString329_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("%$(k3}<1[p(7");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString7__9 = scope.put("", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString7__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString7__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_literalMutationString7__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString7_literalMutationString329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template %$(k3}<1[p(7 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString276_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("fW&S=}:h/BV;");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString9__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_literalMutationString9__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString276 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString8_literalMutationString376_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("FbDzW*;2{#KY");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString8__9 = scope.put("Xrans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString8__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString8__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            String o_testPostNullLabels_literalMutationString8__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString8_literalMutationString376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template FbDzW*;2{#KY not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(".Ou}= MGts8i");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .Ou}= MGts8i not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}


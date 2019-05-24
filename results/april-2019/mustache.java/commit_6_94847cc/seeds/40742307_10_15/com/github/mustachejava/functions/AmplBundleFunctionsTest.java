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
    public void testPreLabels_literalMutationString114_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles`html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString114 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles`html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreLabels_literalMutationString112_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("}fMbvf4r7<wa");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString112 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template }fMbvf4r7<wa not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString286_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("94@u$?D3d_{d");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString286 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 94@u$?D3d_{d not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString463_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(">&BHiHTF@z3C");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString463 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >&BHiHTF@z3C not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString461_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bund|es.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString461 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bund|es.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("|rvhN[<_jF*^");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString2 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |rvhN[<_jF*^ not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}


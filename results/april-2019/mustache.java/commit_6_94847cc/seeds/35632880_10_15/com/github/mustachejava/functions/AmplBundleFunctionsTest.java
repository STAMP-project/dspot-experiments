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
    public void testPreLabels_literalMutationString113_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("lr)v/T3E=>&s");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template lr)v/T3E=>&s not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString290_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("?<b>z.2cW+dR");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?<b>z.2cW+dR not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString463_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bun[dles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString463 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bun[dles.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString462_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("_{L@eZ)EP{rH");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString459_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles.h[ml");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPreTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString459 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles.h[ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("X$-wm:N:>Z v");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template X$-wm:N:>Z v not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}


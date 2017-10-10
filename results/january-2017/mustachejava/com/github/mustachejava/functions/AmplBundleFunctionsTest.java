

package com.github.mustachejava.functions;


/**
 * @author R.A. Porter
 * @version 1.0
 * @since 1/17/13
 */
public class AmplBundleFunctionsTest {
    private static java.io.File root;

    private static final java.lang.String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @org.junit.Test
    public void testPreLabels() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
        com.github.mustachejava.Mustache m = c.compile("bundles.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
        scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
        scope.put("replaceMe", "replaced");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt"), sw.toString());
    }

    @org.junit.Test
    public void testPostLabels() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
        com.github.mustachejava.Mustache m = c.compile("bundles.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
        scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
        scope.put("replaceMe", "replaced");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt"), sw.toString());
    }

    @org.junit.Test
    public void testPreNullLabels() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
        com.github.mustachejava.Mustache m = c.compile("bundles.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
        scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
        scope.put("replaceMe", "replaced");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt"), sw.toString());
    }

    @org.junit.Test
    public void testPostNullLabels() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
        com.github.mustachejava.Mustache m = c.compile("bundles.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
        scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
        // Intentionally leave off the replacement value
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt"), sw.toString());
    }

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.io.File compiler = (new java.io.File("compiler").exists()) ? new java.io.File("compiler") : new java.io.File(".");
        com.github.mustachejava.functions.AmplBundleFunctionsTest.root = new java.io.File(compiler, "src/test/resources/functions");
    }
}


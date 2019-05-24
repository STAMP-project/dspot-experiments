package com.github.mustachejavabenchmarks;


public class AmplBenchmarkTest extends junit.framework.TestCase {
    private static final int TIME = 2000;

    protected java.io.File root;

    protected void setUp() throws java.lang.Exception {
        super.setUp();
        java.io.File file = new java.io.File("src/test/resources");
        root = (new java.io.File(file, "simple.html").exists()) ? file : new java.io.File("../src/test/resources");
    }

    public static boolean skip() {
        return (java.lang.System.getenv().containsKey("CI")) || ((java.lang.System.getProperty("CI")) != null);
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString289_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("bu|ndles.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString289 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bu|ndles.html not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString287_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("b%6Jja[SWPpG");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString287 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template b%6Jja[SWPpG not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString113_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("bu[dles.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString113 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bu[dles.html not found", expected.getMessage());
        }
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory();
    }

    private java.io.Writer complextest(com.github.mustachejava.Mustache m, java.lang.Object complexObject) throws com.github.mustachejava.MustacheException, java.io.IOException {
        java.io.Writer sw = new com.github.mustachejavabenchmarks.NullWriter();
        m.execute(sw, complexObject).close();
        return sw;
    }

    public static void main(java.lang.String[] args) throws java.lang.Exception {
        com.github.mustachejavabenchmarks.AmplBenchmarkTest benchmarkTest = new com.github.mustachejavabenchmarks.AmplBenchmarkTest();
        benchmarkTest.setUp();
        benchmarkTest.testComplex();
        benchmarkTest.testParallelComplex();
        benchmarkTest.testParallelComplexNoExecutor();
        java.lang.System.exit(0);
    }
}


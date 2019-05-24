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
    public void testPreLabels_literalMutationString285_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("bundles.h{tml");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString285 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bundles.h{tml not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString111_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("@O2<b%#)-&#p");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString111 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template @O2<b%#)-&#p not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString463_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("9Q:/=<8(N<Wi");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString463 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 9Q:/=<8(N<Wi not found", expected.getMessage());
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


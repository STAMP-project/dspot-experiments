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
            com.github.mustachejava.Mustache m = c.compile("4t!?8i_8zj<p");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString289 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 4t!?8i_8zj<p not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString286_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("bundles.h{ml");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString286 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bundles.h{ml not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString463_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("s6z4_OO$V%Z)");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString463 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template s6z4_OO$V%Z) not found", expected.getMessage());
        }
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory();
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString5_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("U g]02{[jQI*");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template U g]02{[jQI* not found", expected.getMessage());
        }
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


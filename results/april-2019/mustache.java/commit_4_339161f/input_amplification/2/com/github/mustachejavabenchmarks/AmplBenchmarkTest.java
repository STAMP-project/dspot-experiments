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
    public void testPreLabels_literalMutationString3941_failAssert0_literalMutationString5144_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("f^@t,}Wj+!vu`");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString3941 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString3941_failAssert0_literalMutationString5144 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template f^@t,}Wj+!vu` not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString3940_failAssert0_literalMutationString5201_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("j4&}(cJX]?g/");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreLabels_literalMutationString3940 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreLabels_literalMutationString3940_failAssert0_literalMutationString5201 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_add1354_literalMutationString1498_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("DY;Ff4.4qk%k");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostLabels_add1354__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPostLabels_add1354__11 = scope.put("replaceMe", "replaced");
            java.io.Writer o_testPostLabels_add1354__12 = m.execute(sw, scope);
            java.io.Writer o_testPostLabels_add1354__13 = m.execute(sw, scope);
            java.lang.String o_testPostLabels_add1354__14 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            java.lang.String o_testPostLabels_add1354__15 = sw.toString();
            org.junit.Assert.fail("testPostLabels_add1354_literalMutationString1498 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template DY;Ff4.4qk%k not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString1334_literalMutationString2205_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("OS|kJc A/V?m");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostLabels_literalMutationString1334__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPostLabels_literalMutationString1334__11 = scope.put("repiaceMe", "replaced");
            java.io.Writer o_testPostLabels_literalMutationString1334__12 = m.execute(sw, scope);
            java.lang.String o_testPostLabels_literalMutationString1334__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            java.lang.String o_testPostLabels_literalMutationString1334__14 = sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1334_literalMutationString2205 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString1323_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("98G7J,FuU2:u");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString1323 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 98G7J,FuU2:u not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0_add8599_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("?undles.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554_failAssert0_add8599 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6573_literalMutationString7700_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("gT)vp9LFj{6x");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPreNullLabels_literalMutationString6573__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPreNullLabels_literalMutationString6573__11 = scope.put("replaceMe", "repla!ced");
            java.io.Writer o_testPreNullLabels_literalMutationString6573__12 = m.execute(sw, scope);
            java.lang.String o_testPreNullLabels_literalMutationString6573__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPreNullLabels_literalMutationString6573__14 = sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6573_literalMutationString7700 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template gT)vp9LFj{6x not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("?undles.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString6554_failAssert0_literalMutationString8021_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("?undles.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("tr|ns", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString6554 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString6554_failAssert0_literalMutationString8021 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template ?undles.html not found", expected.getMessage());
        }
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory();
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString7_literalMutationString329_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("%$(k3}<1[p(7");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_literalMutationString7__9 = scope.put("", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_literalMutationString7__11 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_literalMutationString7__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_literalMutationString7__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString7_literalMutationString329 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template %$(k3}<1[p(7 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString276_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("fW&S=}:h/BV;");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_literalMutationString9__9 = scope.put("tans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_literalMutationString9__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_literalMutationString9__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString276 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString8_literalMutationString376_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("FbDzW*;2{#KY");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_literalMutationString8__9 = scope.put("Xrans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_literalMutationString8__11 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_literalMutationString8__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_literalMutationString8__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString8_literalMutationString376 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template FbDzW*;2{#KY not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString6_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile(".Ou}= MGts8i");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString6 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template .Ou}= MGts8i not found", expected.getMessage());
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


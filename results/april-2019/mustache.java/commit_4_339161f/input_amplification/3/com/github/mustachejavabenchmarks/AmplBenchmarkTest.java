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
    public void testPreLabels_literalMutationString10914_literalMutationString11488_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("&@r6Ona:uwZv");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPreLabels_literalMutationString10914__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPreLabels_literalMutationString10914__11 = scope.put("replaceMe", "repliaced");
            java.io.Writer o_testPreLabels_literalMutationString10914__12 = m.execute(sw, scope);
            java.lang.String o_testPreLabels_literalMutationString10914__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            java.lang.String o_testPreLabels_literalMutationString10914__14 = sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10914_literalMutationString11488 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template &@r6Ona:uwZv not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString10898_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("0&%)sb@uMq[j");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10898 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 0&%)sb@uMq[j not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString10917_literalMutationString11605_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("/JY6*!d25]Hu");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPreLabels_literalMutationString10917__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPreLabels_literalMutationString10917__11 = scope.put("replaceMe", "replaGed");
            java.io.Writer o_testPreLabels_literalMutationString10917__12 = m.execute(sw, scope);
            java.lang.String o_testPreLabels_literalMutationString10917__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            java.lang.String o_testPreLabels_literalMutationString10917__14 = sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10917_literalMutationString11605 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreLabels_literalMutationString10899_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("bundles[.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_pre_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreLabels_literalMutationString10899 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bundles[.html not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_literalMutationString9075_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("relaceMe", "replaced");
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_literalMutationString9075 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0null10548_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put(null, com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0null10548 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_literalMutationString8873_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "");
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_literalMutationString8873 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_add9953_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "replaced");
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6090_failAssert0_add9953 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_add6086_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_add6086 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_literalMutationString5543_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("rplaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_literalMutationString5543 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_add9885_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":po1n8]SHsE6");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0null6411_failAssert0_add9885 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1n8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4041_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("b`undles.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString4041 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template b`undles.html not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostLabels_literalMutationString4039_failAssert0_literalMutationString5531_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":po1zn8]SHsE6");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslate(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString4039 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString4039_failAssert0_literalMutationString5531 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :po1zn8]SHsE6 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_literalMutationString22543_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "");
                    m.execute(null, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_literalMutationString22543 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_add23437_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0_add23437 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17674_failAssert0_literalMutationString18939_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("C3e.793<a[+j");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "}(LG-[^]hrY.Zp`l9x]!d(");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17674 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17674_failAssert0_literalMutationString18939 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template C3e.793<a[+j not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0_add19656_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0_add19656 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0_literalMutationString18985_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("FxP/<Hy0T", "replaced");
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0_literalMutationString18985 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0null24071_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("E)nT}(({|eAn");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPreNullLabels_literalMutationString17649 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPreNullLabels_literalMutationString17649_failAssert0null19986_failAssert0null24071 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template E)nT}(({|eAn not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPreNullLabels_add17675_literalMutationString17823_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache o_testPreNullLabels_add17675__3 = c.compile("bundles.h<ml");
            com.github.mustachejava.Mustache m = c.compile("bundles.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPreNullLabels_add17675__10 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPreTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.lang.Object o_testPreNullLabels_add17675__12 = scope.put("replaceMe", "replaced");
            java.io.Writer o_testPreNullLabels_add17675__13 = m.execute(sw, scope);
            java.lang.String o_testPreNullLabels_add17675__14 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPreNullLabels_add17675__15 = sw.toString();
            org.junit.Assert.fail("testPreNullLabels_add17675_literalMutationString17823 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template bundles.h<ml not found", expected.getMessage());
        }
    }

    protected com.github.mustachejava.DefaultMustacheFactory createMustacheFactory() {
        return new com.github.mustachejava.DefaultMustacheFactory();
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString9_literalMutationString282_literalMutationString1632_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("g|azou|gm9wo");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_literalMutationString9__9 = scope.put("bundles5post_labels.txt", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_literalMutationString9__11 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_literalMutationString9__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_literalMutationString9__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString9_literalMutationString282_literalMutationString1632 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0null3618_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put(null, com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0null3618 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_add3082_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_add3082 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("7r[}eQ>{]^tQ");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_add3122_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("fSGjyR[IMIsf");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.lang.Object o_testPostNullLabels_add22__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                java.io.Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_add22__15 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                java.lang.String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_add3122 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_literalMutationString2153_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "Tundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0_literalMutationString2153 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0null3679_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("7r[}eQ>{]^tQ");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0null3679 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2257_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("fSGjyR[IMIsf");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                java.lang.Object o_testPostNullLabels_add22__9 = scope.put("t2rans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                java.io.Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_add22__15 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                java.lang.String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2257 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("fSGjyR[IMIsf");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_add22__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
            java.io.Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_add22__15 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_add22__16 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("gY$e5wb>3`97");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            java.lang.Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_literalMutationString10__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add899_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add899 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0_add3325_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("gY$e5wb>3`97");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("tra^ns", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.lang.Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_literalMutationString10__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                java.lang.String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0_add3325 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2254_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("fSGjyR[IMIsf");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                java.lang.Object o_testPostNullLabels_add22__9 = scope.put("Nrans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                java.io.Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_add22__15 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                java.lang.String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_literalMutationString2254 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabelsnull31_failAssert0_literalMutationString606_failAssert0_literalMutationString2453_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile(":");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabelsnull31 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString606 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostNullLabelsnull31_failAssert0_literalMutationString606_failAssert0_literalMutationString2453 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template : not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add22_literalMutationString223_failAssert0_add3121_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                c.compile("fSGjyR[IMIsf");
                com.github.mustachejava.Mustache m = c.compile("fSGjyR[IMIsf");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                java.lang.Object o_testPostNullLabels_add22__9 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_add22__11 = m.execute(sw, scope);
                java.io.Writer o_testPostNullLabels_add22__13 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_add22__15 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                java.lang.String o_testPostNullLabels_add22__16 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_add22_literalMutationString223_failAssert0_add3121 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template fSGjyR[IMIsf not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534_failAssert0_literalMutationString2751_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("7s#4=5^sn6P>");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString15 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString15_failAssert0_literalMutationString534_failAssert0_literalMutationString2751 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 7s#4=5^sn6P> not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString557_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                m.execute(sw, scope);
                com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels:.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString557 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0null3785_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                com.github.mustachejava.Mustache m = c.compile("gY$e5wb>3`97");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                java.lang.Object o_testPostNullLabels_literalMutationString10__9 = scope.put("tra^ns", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                java.io.Writer o_testPostNullLabels_literalMutationString10__11 = m.execute(sw, scope);
                java.lang.String o_testPostNullLabels_literalMutationString10__13 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, null);
                java.lang.String o_testPostNullLabels_literalMutationString10__14 = sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString10_literalMutationString379_failAssert0null3785 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template gY$e5wb>3`97 not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile(":i Mgy2-k1 z");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            m.execute(sw, scope);
            com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template :i Mgy2-k1 z not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_literalMutationString2346_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("7r[}eQ>{]^tQ");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_literalMutationString2346 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_add3159_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
                    com.github.mustachejava.Mustache m = c.compile("7r[}eQ>{]^tQ");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
                    scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
                    m.execute(sw, scope);
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "");
                    com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString13_failAssert0_literalMutationString489_failAssert0_add3159 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template 7r[}eQ>{]^tQ not found", expected.getMessage());
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testPostNullLabels_add21_literalMutationString116_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplBundleFunctionsTest.root);
            com.github.mustachejava.Mustache m = c.compile("tg6u14;-&Q,^");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
            com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US);
            java.lang.Object o_testPostNullLabels_add21__10 = scope.put("trans", com.github.mustachejava.functions.BundleFunctions.newPostTranslateNullableLabel(com.github.mustachejava.functions.AmplBundleFunctionsTest.BUNDLE, java.util.Locale.US));
            java.io.Writer o_testPostNullLabels_add21__12 = m.execute(sw, scope);
            java.lang.String o_testPostNullLabels_add21__14 = com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            java.lang.String o_testPostNullLabels_add21__15 = sw.toString();
            org.junit.Assert.fail("testPostNullLabels_add21_literalMutationString116 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            org.junit.Assert.assertEquals("Template tg6u14;-&Q,^ not found", expected.getMessage());
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


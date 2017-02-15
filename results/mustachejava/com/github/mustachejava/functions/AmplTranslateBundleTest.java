

package com.github.mustachejava.functions;


public class AmplTranslateBundleTest {
    private static java.io.File root;

    private static final java.lang.String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @org.junit.Test
    public void testTranslation() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.functions.AmplTranslateBundleTest.root);
        com.github.mustachejava.Mustache m = c.compile("translatebundle.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map<java.lang.String, java.lang.Object> scope = new java.util.HashMap<>();
        scope.put("trans", new com.github.mustachejava.functions.TranslateBundleFunction(com.github.mustachejava.functions.AmplTranslateBundleTest.BUNDLE, java.util.Locale.US));
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.functions.AmplTranslateBundleTest.root, "translatebundle.txt"), sw.toString());
    }

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.io.File compiler = (new java.io.File("compiler").exists()) ? new java.io.File("compiler") : new java.io.File(".");
        com.github.mustachejava.functions.AmplTranslateBundleTest.root = new java.io.File(compiler, "src/test/resources/functions");
    }
}


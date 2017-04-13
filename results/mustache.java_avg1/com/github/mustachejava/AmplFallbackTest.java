

package com.github.mustachejava;


public class AmplFallbackTest {
    private static java.io.File rootDefault;

    private static java.io.File rootOverride;

    @org.junit.Test
    public void testDefaultPage1() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.FallbackMustacheFactory(com.github.mustachejava.AmplFallbackTest.rootDefault, com.github.mustachejava.AmplFallbackTest.rootDefault);// no override
        
        com.github.mustachejava.Mustache m = c.compile("page1.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map scope = new java.util.HashMap();
        scope.put("title", "My title");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.AmplFallbackTest.rootDefault, "page1.txt"), sw.toString());
    }

    @org.junit.Test
    public void testOverridePage1() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.FallbackMustacheFactory(com.github.mustachejava.AmplFallbackTest.rootOverride, com.github.mustachejava.AmplFallbackTest.rootDefault);
        com.github.mustachejava.Mustache m = c.compile("page1.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map scope = new java.util.HashMap();
        scope.put("title", "My title");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.AmplFallbackTest.rootOverride, "page1.txt"), sw.toString());
    }

    @org.junit.Test
    public void testOverridePage2() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.FallbackMustacheFactory(com.github.mustachejava.AmplFallbackTest.rootOverride, com.github.mustachejava.AmplFallbackTest.rootDefault);
        com.github.mustachejava.Mustache m = c.compile("page2.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map scope = new java.util.HashMap();
        scope.put("title", "My title");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(com.github.mustachejava.AmplFallbackTest.rootOverride, "page2.txt"), sw.toString());
    }

    @org.junit.Test
    public void testMustacheNotFoundException() {
        java.lang.String nonExistingMustache = "404";
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.FallbackMustacheFactory(com.github.mustachejava.AmplFallbackTest.rootOverride, com.github.mustachejava.AmplFallbackTest.rootDefault);
            c.compile(nonExistingMustache);
            junit.framework.Assert.fail("Didn't throw an exception");
        } catch (com.github.mustachejava.MustacheNotFoundException e) {
            org.junit.Assert.assertEquals(nonExistingMustache, e.getName());
        }
    }

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.io.File file = new java.io.File("compiler/src/test/resources/fallback/default");
        com.github.mustachejava.AmplFallbackTest.rootDefault = (new java.io.File(file, "base.html").exists()) ? file : new java.io.File("src/test/resources/fallback/default");
        file = new java.io.File("compiler/src/test/resources/fallback/override");
        com.github.mustachejava.AmplFallbackTest.rootOverride = (new java.io.File(file, "base.html").exists()) ? file : new java.io.File("src/test/resources/fallback/override");
    }
}


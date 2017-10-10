

package com.github.mustachejava.functions;


public class AmplCommentTest {
    @org.junit.Test
    public void testCommentBlock() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        java.io.File root = getRoot("comment.html");
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(root);
        com.github.mustachejava.Mustache m = c.compile("comment.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map scope = new java.util.HashMap();
        scope.put("ignored", "ignored");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(root, "comment.txt"), sw.toString());
    }

    @org.junit.Test
    public void testCommentInline() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        java.io.File root = getRoot("commentinline.html");
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(root);
        com.github.mustachejava.Mustache m = c.compile("commentinline.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map scope = new java.util.HashMap();
        scope.put("title", "A Comedy of Errors");
        m.execute(sw, scope);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(root, "commentinline.txt"), sw.toString());
    }

    private java.io.File getRoot(java.lang.String fileName) {
        java.io.File file = new java.io.File("compiler/src/test/resources/functions");
        return new java.io.File(file, fileName).exists() ? file : new java.io.File("src/test/resources/functions");
    }

    @org.junit.Test
    public void testInlineCommentWithinExtendCodeBlock() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        java.io.File root = getRoot("commentWithinExtendCodeBlock.html");
        com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(root);
        com.github.mustachejava.Mustache m = c.compile("commentWithinExtendCodeBlock.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        // AssertGenerator replace invocation
        java.io.Writer o_testInlineCommentWithinExtendCodeBlock__9 = m.execute(sw, java.util.Collections.emptyList());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.io.StringWriter)o_testInlineCommentWithinExtendCodeBlock__9).getBuffer()).length(), 32);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testInlineCommentWithinExtendCodeBlock__9.equals(sw));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.lang.StringBuffer)((java.io.StringWriter)o_testInlineCommentWithinExtendCodeBlock__9).getBuffer()).reverse()).capacity(), 34);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.lang.StringBuffer)((java.io.StringWriter)o_testInlineCommentWithinExtendCodeBlock__9).getBuffer()).reverse()).length(), 32);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.StringBuffer)((java.io.StringWriter)o_testInlineCommentWithinExtendCodeBlock__9).getBuffer()).capacity(), 34);
        org.junit.Assert.assertEquals(com.github.mustachejava.TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt"), sw.toString());
    }
}


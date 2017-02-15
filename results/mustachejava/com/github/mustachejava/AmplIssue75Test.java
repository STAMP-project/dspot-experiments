

package com.github.mustachejava;


/**
 * Reproduction test case
 */
public class AmplIssue75Test {
    @org.junit.Test
    public void testDotNotationWithNull() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("[{{category.name}}]"), "test");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map map = new java.util.HashMap();
        map.put("category", null);
        m.execute(sw, map).close();
    }

    /* amplification of com.github.mustachejava.Issue75Test#testDotNotationWithNull */
    @org.junit.Test
    public void testDotNotationWithNull_literalMutation3() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(""), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map map = new java.util.HashMap();
        map.put("category", null);
        m.execute(sw, map).close();
    }

    /* amplification of com.github.mustachejava.Issue75Test#testDotNotationWithNull */
    @org.junit.Test
    public void testDotNotationWithNull_literalMutation6_literalMutation36() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(""), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map map = new java.util.HashMap();
        map.put("category", null);
        m.execute(sw, map).close();
    }

    /* amplification of com.github.mustachejava.Issue75Test#testDotNotationWithNull */
    @org.junit.Test
    public void testDotNotationWithNull_literalMutation5_literalMutation31_literalMutation188() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(""), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "test");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.util.Map map = new java.util.HashMap();
        map.put("category", null);
        m.execute(sw, map).close();
    }
}




package com.github.mustachejava;


public final class AmplAbsolutePartialReferenceTest {
    private static final java.lang.String TEMPLATE_FILE = "absolute_partials_template.html";

    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_classloader() throws java.lang.Exception {
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory("templates");
        com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
        }).close();
        org.junit.Assert.assertEquals("w00pw00p mustache rocks ", sw.toString());
    }

    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_filepath() throws java.lang.Exception {
        java.io.File file = new java.io.File("compiler/src/test/resources/templates_filepath");
        java.io.File root = (new java.io.File(file, com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE).exists()) ? file : new java.io.File("src/test/resources/templates_filepath");
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(root);
        com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
        }).close();
        org.junit.Assert.assertEquals("w00pw00p mustache rocks ", sw.toString());
    }

    /* amplification of com.github.mustachejava.AbsolutePartialReferenceTest#should_load_teamplates_with_absolute_references_using_classloader */
    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutation4_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory("tem*lates");
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            // MethodAssertGenerator build local variable
            Object o_14_0 = sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutation4 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbsolutePartialReferenceTest#should_load_teamplates_with_absolute_references_using_classloader */
    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutation6_failAssert5_literalMutation46() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory("Gdhscb&S@");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.DefaultMustacheFactory)factory).getRecursionLimit(), 100);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)factory).getExecutorService());
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            // MethodAssertGenerator build local variable
            Object o_14_0 = sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutation6 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbsolutePartialReferenceTest#should_load_teamplates_with_absolute_references_using_classloader */
    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_classloader_literalMutation3_failAssert2_literalMutation29_literalMutation168() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory("");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.DefaultMustacheFactory)factory).getRecursionLimit(), 100);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)factory).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.DefaultMustacheFactory)factory).getRecursionLimit(), 100);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)factory).getExecutorService());
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            // MethodAssertGenerator build local variable
            Object o_14_0 = sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_classloader_literalMutation3 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException eee) {
        }
    }

    /* amplification of com.github.mustachejava.AbsolutePartialReferenceTest#should_load_teamplates_with_absolute_references_using_filepath */
    @org.junit.Test
    public void should_load_teamplates_with_absolute_references_using_filepath_literalMutation454_failAssert4() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.File file = new java.io.File("compiler/src/test/resources/templates_filepath");
            java.io.File root = (new java.io.File(file, com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE).exists()) ? file : new java.io.File("src/test/resopurces/templates_filepath");
            com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(root);
            com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplAbsolutePartialReferenceTest.TEMPLATE_FILE);
            java.io.StringWriter sw = new java.io.StringWriter();
            maven.execute(sw, new java.lang.Object() {
                java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
            }).close();
            // MethodAssertGenerator build local variable
            Object o_20_0 = sw.toString();
            org.junit.Assert.fail("should_load_teamplates_with_absolute_references_using_filepath_literalMutation454 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}


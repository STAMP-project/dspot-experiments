

package com.github.mustachejava;


public final class AmplNestedPartialTest {
    private static final java.lang.String TEMPLATE_FILE = "nested_partials_template.html";

    private static java.io.File root;

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.io.File file = new java.io.File("compiler/src/test/resources");
        com.github.mustachejava.AmplNestedPartialTest.root = (new java.io.File(file, com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE).exists()) ? file : new java.io.File("src/test/resources");
    }

    @org.junit.Test
    public void should_handle_more_than_one_level_of_partial_nesting() throws java.lang.Exception {
        com.github.mustachejava.MustacheFactory factory = new com.github.mustachejava.DefaultMustacheFactory(com.github.mustachejava.AmplNestedPartialTest.root);
        com.github.mustachejava.Mustache maven = factory.compile(com.github.mustachejava.AmplNestedPartialTest.TEMPLATE_FILE);
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.util.List<java.lang.String> messages = java.util.Arrays.asList("w00pw00p", "mustache rocks");
        }).close();
        org.junit.Assert.assertEquals("w00pw00p mustache rocks ", sw.toString());
    }
}


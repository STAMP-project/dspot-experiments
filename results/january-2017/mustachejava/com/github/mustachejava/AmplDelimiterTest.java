

package com.github.mustachejava;


public class AmplDelimiterTest {
    @org.junit.Test
    public void testMavenDelimiter() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.AmplDelimiterTest.NoEncodingMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("Hello, ${foo}."), "maven", "${", "}");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }

    @org.junit.Test
    public void testAntDelimiter() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.AmplDelimiterTest.NoEncodingMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("Hello, @foo@."), "maven", "@", "@");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }

    @org.junit.Test
    public void testWithTemplateFunction() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("Hello, ${#f}${foo}${/f}."), "maven", "${", "}");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            com.github.mustachejava.TemplateFunction f = ( s) -> s;

            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }

    @org.junit.Test
    public void testWithTemplateFunction2() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("Hello, ${foo}."), "maven", "${", "}");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            com.github.mustachejava.TemplateFunction foo = ( s) -> "${name}";

            java.lang.String name = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, ${name}.", sw.toString());
    }

    @org.junit.Test
    public void testStrSubstitutor() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("Hello, $<foo>."), "maven", "$<", ">");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }

    @org.junit.Test
    public void testStrSubstitutor2() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("{{=$< >=}}Hello, $<foo>."), "maven");
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }

    private static class NoEncodingMustacheFactory extends com.github.mustachejava.DefaultMustacheFactory {
        @java.lang.Override
        public void encode(java.lang.String value, java.io.Writer writer) {
            // TODO: encoding rules
            try {
                writer.write(value);
            } catch (java.io.IOException e) {
                throw new com.github.mustachejava.MustacheException(e);
            }
        }
    }

    /* amplification of com.github.mustachejava.DelimiterTest#testStrSubstitutor2 */
    @org.junit.Test
    public void testStrSubstitutor2_literalMutation6174() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        com.github.mustachejava.Mustache maven = mf.compile(new java.io.StringReader("{{=$<! >=}}Hello, $<foo>."), "maven");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)maven).getName(), "maven");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)maven).isRecursive());
        java.io.StringWriter sw = new java.io.StringWriter();
        maven.execute(sw, new java.lang.Object() {
            java.lang.String foo = "Jason";
        }).close();
        org.junit.Assert.assertEquals("Hello, Jason.", sw.toString());
    }
}


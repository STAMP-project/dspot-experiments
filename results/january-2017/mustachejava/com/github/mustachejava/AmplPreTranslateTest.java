

package com.github.mustachejava;


/**
 * If you want to precompile translations, you will need to do two passes against
 * templates. This is an example of how to implement this.
 */
public class AmplPreTranslateTest {
    @org.junit.Test
    public void testPretranslate() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory() {
            com.github.mustachejava.MustacheParser mp = new com.github.mustachejava.MustacheParser(this) {
                @java.lang.Override
                public com.github.mustachejava.Mustache compile(java.io.Reader reader, java.lang.String file) {
                    return super.compile(reader, file, "{[", "]}");
                }
            };

            @java.lang.Override
            public com.github.mustachejava.Mustache compile(java.io.Reader reader, java.lang.String file, java.lang.String sm, java.lang.String em) {
                return super.compile(reader, file, "{[", "]}");
            }

            @java.lang.Override
            protected java.util.function.Function<java.lang.String, com.github.mustachejava.Mustache> getMustacheCacheFunction() {
                return ( template) -> {
                    com.github.mustachejava.Mustache compile = mp.compile(template);
                    compile.init();
                    return compile;
                };
            }
        };
        com.github.mustachejava.Mustache m = mf.compile("pretranslate.html");
        java.io.StringWriter sw = new java.io.StringWriter();
        m.execute(sw, new java.lang.Object() {
            java.util.function.Function i = ( input) -> "{{test}} Translate";
        }).close();
        junit.framework.Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
        mf = new com.github.mustachejava.DefaultMustacheFactory();
        m = mf.compile(new java.io.StringReader(sw.toString()), "pretranslate.html");
        sw = new java.io.StringWriter();
        m.execute(sw, new java.lang.Object() {
            boolean show = true;

            java.lang.String test = "Now";
        }).close();
        junit.framework.Assert.assertEquals("Now Translate\n", sw.toString());
    }
}


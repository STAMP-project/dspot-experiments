package com.github.mustachejava;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Function;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AmplPreTranslateTest {
    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString14_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory() {
                MustacheParser mp = new MustacheParser(this) {
                    @Override
                    public Mustache compile(Reader reader, String file) {
                        return super.compile(reader, file, "{[", "]}");
                    }
                };

                @Override
                public Mustache compile(Reader reader, String file, String sm, String em) {
                    return super.compile(reader, file, "{[", "]}");
                }

                @Override
                protected Function<String, Mustache> getMustacheCacheFunction() {
                    return ( template) -> {
                        Mustache compile = mp.compile(template);
                        compile.init();
                        return compile;
                    };
                }
            };
            Mustache m = mf.compile("dV5 yIw7)4*&&33N9");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                Function i = ( input) -> "{{test}} Translate";
            }).close();
            Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
            mf = new DefaultMustacheFactory();
            m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {
                boolean show = true;

                String test = "Now";
            }).close();
            Assert.assertEquals("Now Translate\n", sw.toString());
            org.junit.Assert.fail("testPretranslate_literalMutationString14 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template dV5 yIw7)4*&&33N9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPretranslate_literalMutationString16_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory() {
                MustacheParser mp = new MustacheParser(this) {
                    @Override
                    public Mustache compile(Reader reader, String file) {
                        return super.compile(reader, file, "{[", "]}");
                    }
                };

                @Override
                public Mustache compile(Reader reader, String file, String sm, String em) {
                    return super.compile(reader, file, "{[", "]}");
                }

                @Override
                protected Function<String, Mustache> getMustacheCacheFunction() {
                    return ( template) -> {
                        Mustache compile = mp.compile(template);
                        compile.init();
                        return compile;
                    };
                }
            };
            Mustache m = mf.compile("p^retranslate.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                Function i = ( input) -> "{{test}} Translate";
            }).close();
            Assert.assertEquals("{{#show}}\n{{test}} Translate\n{{/show}}", sw.toString());
            mf = new DefaultMustacheFactory();
            m = mf.compile(new StringReader(sw.toString()), "pretranslate.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {
                boolean show = true;

                String test = "Now";
            }).close();
            Assert.assertEquals("Now Translate\n", sw.toString());
            org.junit.Assert.fail("testPretranslate_literalMutationString16 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            assertEquals("Template p^retranslate.html not found", expected.getMessage());
        }
    }
}


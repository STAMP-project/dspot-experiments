package handlebarsjs.spec;


import HelperRegistry.HELPER_MISSING;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BlockHelperMissingTest extends AbstractTest {
    @Test
    public void ifContextIsNotFoundHelperMissingIsUsed() throws IOException {
        String string = "{{hello}} {{link_to world}}";
        String context = "{ hello: Hello, world: world }";
        AbstractTest.Hash helpers = AbstractTest.$(HELPER_MISSING, new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String context, final Options options) throws IOException {
                return new Handlebars.SafeString((("<a>" + context) + "</a>"));
            }
        });
        shouldCompileTo(string, context, helpers, "Hello <a>world</a>");
    }

    @Test
    public void eachWithHash() throws IOException {
        String string = "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!";
        Object hash = AbstractTest.$("goodbyes", AbstractTest.$("<b>#1</b>", AbstractTest.$("text", "goodbye"), "2", AbstractTest.$("text", "GOODBYE")), "world", "world");
        shouldCompileTo(string, hash, "&lt;b&gt;#1&lt;/b&gt;. goodbye! 2. GOODBYE! cruel world!");
    }

    @Test
    @SuppressWarnings("unused")
    public void eachWithJavaBean() throws IOException {
        String string = "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!";
        Object hash = new Object() {
            public Object getGoodbyes() {
                return new Object() {
                    public Object getB1() {
                        return new Object() {
                            public String getText() {
                                return "goodbye";
                            }
                        };
                    }

                    public Object get2() {
                        return new Object() {
                            public String getText() {
                                return "GOODBYE";
                            }
                        };
                    }
                };
            }

            public String getWorld() {
                return "world";
            }
        };
        try {
            shouldCompileTo(string, hash, "b1. goodbye! 2. GOODBYE! cruel world!");
        } catch (Throwable ex) {
            // on jdk7 property order differ from jdk6
            shouldCompileTo(string, hash, "2. GOODBYE! b1. goodbye! cruel world!");
        }
    }

    @Test
    public void with() throws IOException {
        String string = "{{#with person}}{{first}} {{last}}{{/with}}";
        shouldCompileTo(string, "{person: {first: Alan, last: Johnson}}", "Alan Johnson");
    }

    @Test
    public void ifHelper() throws IOException {
        String string = "{{#if goodbye}}GOODBYE {{/if}}cruel {{world}}!";
        shouldCompileTo(string, "{goodbye: true, world: world}", "GOODBYE cruel world!", "if with boolean argument shows the contents when true");
        shouldCompileTo(string, "{goodbye: dummy, world: world}", "GOODBYE cruel world!", "if with string argument shows the contents");
        shouldCompileTo(string, "{goodbye: false, world: world}", "cruel world!", "if with boolean argument does not show the contents when false");
        shouldCompileTo(string, "{world: world}", "cruel world!", "if with undefined does not show the contents");
        shouldCompileTo(string, AbstractTest.$("goodbye", new Object[]{ "foo" }, "world", "world"), "GOODBYE cruel world!", "if with non-empty array shows the contents");
        shouldCompileTo(string, AbstractTest.$("goodbye", new Object[0], "world", "world"), "cruel world!", "if with empty array does not show the contents");
    }

    @Test
    public void dataCanBeLookupViaAnnotation() throws IOException {
        Template template = compile("{{@hello}}");
        String result = template.apply(Context.newContext(AbstractTest.$).data("hello", "hello"));
        Assert.assertEquals("hello", result);
    }

    @Test
    public void deepAnnotationTriggersAutomaticTopLevelData() throws IOException {
        String string = "{{#let world=\"world\"}}{{#if foo}}{{#if foo}}Hello {{@world}}{{/if}}{{/if}}{{/let}}";
        AbstractTest.Hash helpers = AbstractTest.$("let", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                for (Map.Entry<String, Object> entry : options.hash.entrySet()) {
                    options.data(entry.getKey(), entry.getValue());
                }
                return options.fn(context);
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(AbstractTest.$("foo", true));
        Assert.assertEquals("Hello world", result);
    }

    @Test
    public void parameterCanBeLookupViaAnnotation() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return "Hello " + (options.hash("noun"));
            }
        });
        Template template = compile("{{hello noun=@world}}", helpers);
        String result = template.apply(Context.newContext(AbstractTest.$).data("world", "world"));
        Assert.assertEquals("Hello world", result);
    }

    @Test
    public void dataIsInheritedDownstream() throws IOException {
        String string = "{{#let foo=bar.baz}}{{@foo}}{{/let}}";
        AbstractTest.Hash helpers = AbstractTest.$("let", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                for (Map.Entry<String, Object> entry : options.hash.entrySet()) {
                    options.data(entry.getKey(), entry.getValue());
                }
                return options.fn(context);
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(AbstractTest.$("bar", AbstractTest.$("baz", "hello world")));
        Assert.assertEquals("data variables are inherited downstream", "hello world", result);
    }

    @Test
    public void passingInDataWorksWithHelpersInPartials() throws IOException {
        String string = "{{>my_partial}}";
        AbstractTest.Hash partials = AbstractTest.$("my_partial", "{{hello}}");
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((options.data("adjective")) + " ") + (options.get("noun"));
            }
        });
        Template template = compile(string, helpers, partials);
        String result = template.apply(Context.newContext(AbstractTest.$("noun", "cat")).data("adjective", "happy"));
        Assert.assertEquals("Data output by helper inside partial", "happy cat", result);
    }

    @Test
    public void passingInDataWorksWithBlockHelpers() throws IOException {
        String string = "{{#hello}}{{world}}{{/hello}}";
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn();
            }
        }, "world", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object thing, final Options options) throws IOException {
                Boolean exclaim = options.get("exclaim");
                return ((options.data("adjective")) + " world") + (exclaim ? "!" : "");
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(Context.newContext(AbstractTest.$("exclaim", true)).data("adjective", "happy"));
        Assert.assertEquals("happy world!", result);
    }

    @Test
    public void passingInDataWorksWithBlockHelpersThatUsePaths() throws IOException {
        String string = "{{#hello}}{{world ../zomg}}{{/hello}}";
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(AbstractTest.$("exclaim", "?"));
            }
        }, "world", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object thing, final Options options) throws IOException {
                return (((options.data("adjective")) + " ") + thing) + (options.get("exclaim", ""));
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(Context.newContext(AbstractTest.$("exclaim", true, "zomg", "world")).data("adjective", "happy"));
        Assert.assertEquals("happy world?", result);
    }

    @Test
    public void passingInDataWorksWithBlockHelpersWhereChildrenUsePaths() throws IOException {
        String string = "{{#hello}}{{world ../zomg}}{{/hello}}";
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((options.data("accessData")) + " ") + (options.fn(AbstractTest.$("exclaim", "?")));
            }
        }, "world", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object thing, final Options options) throws IOException {
                return (((options.data("adjective")) + " ") + thing) + (options.get("exclaim", ""));
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(Context.newContext(AbstractTest.$("exclaim", true, "zomg", "world")).data("adjective", "happy").data("accessData", "#win"));
        Assert.assertEquals("#win happy world?", result);
    }

    @Test
    public void overrideInheritedDataWhenInvokingHelper() throws IOException {
        String string = "{{#hello}}{{world zomg}}{{/hello}}";
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(Context.newContext(AbstractTest.$("exclaim", "?", "zomg", "world")).data("adjective", "sad"));
            }
        }, "world", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object thing, final Options options) throws IOException {
                return (((options.data("adjective")) + " ") + thing) + (options.get("exclaim", ""));
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(Context.newContext(AbstractTest.$("exclaim", true, "zomg", "planet")).data("adjective", "happy").data("accessData", "#win"));
        Assert.assertEquals("Overriden data output by helper", "sad world?", result);
    }

    @Test
    public void overrideInheritedDataWhenInvokingHelperWithDepth() throws IOException {
        String string = "{{#hello}}{{world zomg}}{{/hello}}";
        AbstractTest.Hash helpers = AbstractTest.$("hello", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(Context.newContext(AbstractTest.$("exclaim", "?", "zomg", "world")).data("adjective", "sad"));
            }
        }, "world", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object thing, final Options options) throws IOException {
                return (((options.data("adjective")) + " ") + thing) + (options.get("exclaim", ""));
            }
        });
        Template template = compile(string, helpers);
        String result = template.apply(Context.newContext(AbstractTest.$("exclaim", true, "zomg", "planet")).data("adjective", "happy").data("accessData", "#win"));
        Assert.assertEquals("Overriden data output by helper", "sad world?", result);
    }

    @Test
    public void helpersTakePrecedenceOverSameNamedContextProperties() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Map<String, Object>>() {
            @Override
            public Object apply(final Map<String, Object> context, final Options options) throws IOException {
                return context.get("goodbye").toString().toUpperCase();
            }
        }, "cruel", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String world, final Options options) throws IOException {
                return "cruel " + (world.toUpperCase());
            }
        });
        shouldCompileTo("{{goodbye}} {{cruel world}}", "{goodbye: goodbye, world: world}", helpers, "GOODBYE cruel WORLD");
    }

    @Test
    public void blockHelpersTakePrecedenceOverSameNamedContextProperties() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Map<String, Object>>() {
            @Override
            public Object apply(final Map<String, Object> context, final Options options) throws IOException {
                return (context.get("goodbye").toString().toUpperCase()) + (options.fn(context));
            }
        }, "cruel", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String world, final Options options) throws IOException {
                return "cruel " + (world.toUpperCase());
            }
        });
        shouldCompileTo("{{#goodbye}} {{cruel world}}{{/goodbye}}", "{goodbye: goodbye, world: world}", helpers, "GOODBYE cruel WORLD");
    }

    @Test
    public void scopedNamesTakePrecedenceOverHelpers() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Map<String, Object>>() {
            @Override
            public Object apply(final Map<String, Object> context, final Options options) throws IOException {
                return context.get("goodbye").toString().toUpperCase();
            }
        }, "cruel", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String world, final Options options) throws IOException {
                return "cruel " + (world.toUpperCase());
            }
        });
        shouldCompileTo("{{this.goodbye}} {{cruel world}} {{cruel this.goodbye}}", "{goodbye: goodbye, world: world}", helpers, "goodbye cruel WORLD cruel GOODBYE");
    }

    @Test
    public void scopedNamesTakePrecedenceOverBlockHelpers() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Map<String, Object>>() {
            @Override
            public Object apply(final Map<String, Object> context, final Options options) throws IOException {
                return (context.get("goodbye").toString().toUpperCase()) + (options.fn(context));
            }
        }, "cruel", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String world, final Options options) throws IOException {
                return "cruel " + (world.toUpperCase());
            }
        });
        shouldCompileTo("{{#goodbye}} {{cruel world}}{{/goodbye}} {{this.goodbye}}", "{goodbye: goodbye, world: world}", helpers, "GOODBYE cruel WORLD goodbye");
    }

    @Test
    public void helperCanTakeOptionalHash() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((((("GOODBYE " + (options.hash("cruel"))) + " ") + (options.hash("world"))) + " ") + (options.hash("times"))) + " TIMES";
            }
        });
        shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" times=12}}", AbstractTest.$, helpers, "GOODBYE CRUEL WORLD 12 TIMES");
    }

    @Test
    public void helperCanTakeOptionalHashWithBooleans() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Boolean print = options.hash("print");
                if (print) {
                    return (("GOODBYE " + (options.hash("cruel"))) + " ") + (options.hash("world"));
                } else {
                    return "NOT PRINTING";
                }
            }
        });
        shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=true}}", AbstractTest.$, helpers, "GOODBYE CRUEL WORLD");
        shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=false}}", AbstractTest.$, helpers, "NOT PRINTING");
    }

    @Test
    public void blockHelperCanTakeOptionalHash() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((((("GOODBYE " + (options.hash("cruel"))) + " ") + (options.fn(context))) + " ") + (options.hash("times"))) + " TIMES";
            }
        });
        shouldCompileTo("{{#goodbye cruel=\"CRUEL\" times=12}}world{{/goodbye}}", AbstractTest.$, helpers, "GOODBYE CRUEL world 12 TIMES");
    }

    @Test
    public void blockHelperCanTakeOptionalHashWithSingleQuotedStrings() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((((("GOODBYE " + (options.hash("cruel"))) + " ") + (options.fn(context))) + " ") + (options.hash("times"))) + " TIMES";
            }
        });
        shouldCompileTo("{{#goodbye cruel='CRUEL' times=12}}world{{/goodbye}}", AbstractTest.$, helpers, "GOODBYE CRUEL world 12 TIMES");
    }

    @Test
    public void blockHelperCanTakeOptionalHashWithBooleans() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("goodbye", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Boolean print = options.hash("print");
                if (print) {
                    return (("GOODBYE " + (options.hash("cruel"))) + " ") + (options.fn(context));
                } else {
                    return "NOT PRINTING";
                }
            }
        });
        shouldCompileTo("{{#goodbye cruel=\"CRUEL\" print=true}}world{{/goodbye}}", AbstractTest.$, helpers, "GOODBYE CRUEL world");
        shouldCompileTo("{{#goodbye cruel=\"CRUEL\" print=false}}world{{/goodbye}}", AbstractTest.$, helpers, "NOT PRINTING");
    }

    @Test
    public void argumentsToHelpersCanBeRetrievedFromOptionsHashInStringForm() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("wycats", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (("HELP ME MY BOSS " + (options.param(0))) + ' ') + (options.param(1));
            }
        });
        Assert.assertEquals("HELP ME MY BOSS is.a slave.driver", compile("{{wycats this is.a slave.driver}}", helpers, true).apply(AbstractTest.$));
    }

    @Test
    public void whenUsingBlockFormArgumentsToHelpersCanBeRetrievedFromOptionsHashInStringForm() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("wycats", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((("HELP ME MY BOSS " + (options.param(0))) + ' ') + (options.param(1))) + ": ") + (options.fn());
            }
        });
        Assert.assertEquals("HELP ME MY BOSS is.a slave.driver: help :(", compile("{{#wycats this is.a slave.driver}}help :({{/wycats}}", helpers, true).apply(AbstractTest.$));
    }

    @Test
    public void whenInsideABlockInStringModePassesTheAppropriateContextInTheOptionsHash() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("tomdale", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (("STOP ME FROM READING HACKER NEWS I " + context) + " ") + (options.param(0));
            }
        }, "with", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(context);
            }
        });
        Assert.assertEquals("STOP ME FROM READING HACKER NEWS I need-a dad.joke", compile("{{#with dale}}{{tomdale ../need dad.joke}}{{/with}}", helpers, true).apply(AbstractTest.$("dale", AbstractTest.$, "need", "need-a")));
    }

    @Test
    public void whenInsideABlockInStringModePassesTheAppropriateContextInTheOptionsHashToABlockHelper() throws IOException {
        AbstractTest.Hash helpers = AbstractTest.$("tomdale", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (((("STOP ME FROM READING HACKER NEWS I " + context) + " ") + (options.param(0))) + " ") + (options.fn(context));
            }
        }, "with", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(context);
            }
        });
        Assert.assertEquals("STOP ME FROM READING HACKER NEWS I need-a dad.joke wot", compile("{{#with dale}}{{#tomdale ../need dad.joke}}wot{{/tomdale}}{{/with}}", helpers, true).apply(AbstractTest.$("dale", AbstractTest.$, "need", "need-a")));
    }
}


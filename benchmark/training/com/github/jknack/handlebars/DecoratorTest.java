package com.github.jknack.handlebars;


import TagType.START_SECTION;
import TagType.STAR_VAR;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class DecoratorTest extends v4Test {
    @Test
    public void shouldApplyMustacheDecorators() throws IOException {
        shouldCompileTo("{{#helper}}{{*decorator}}{{/helper}}", v4Test.$("helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.data("run");
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("run", "success");
            }
        })), "success");
    }

    @Test
    public void shouldApplyAllowUndefinedReturn() throws IOException {
        shouldCompileTo("{{#helper}}{{*decorator}}suc{{/helper}}", v4Test.$("helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return (options.fn().toString()) + (options.data("run"));
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("run", "cess");
            }
        })), "success");
    }

    @Test
    public void shouldApplyBlockDecorators() throws IOException {
        shouldCompileTo("{{#helper}}{{#*decorator}}success{{/decorator}}{{/helper}}", v4Test.$("helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.data("run");
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("run", options.fn());
            }
        })), "success");
    }

    @Test
    public void shouldSupportNestedDecorators() throws IOException {
        shouldCompileTo("{{#helper}}{{#*decorator}}{{#*nested}}suc{{/nested}}cess{{/decorator}}{{/helper}}", v4Test.$("helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.data("run");
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("run", ((options.data("nested").toString()) + (options.fn())));
            }
        }, "nested", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("nested", options.fn());
            }
        })), "success");
    }

    @Test
    public void shouldApplyMultipleDecorators() throws IOException {
        shouldCompileTo("{{#helper}}{{#*decorator}}suc{{/decorator}}{{#*decorator}}cess{{/decorator}}{{/helper}}", v4Test.$("helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.data("run");
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                Object run = options.data("run");
                options.data("run", (run == null ? options.fn() : (run.toString()) + (options.fn())));
            }
        })), "success");
    }

    @Test
    public void shouldAccessParentVariables() throws IOException {
        shouldCompileTo("{{#helper}}{{*decorator foo}}{{/helper}}", v4Test.$("hash", v4Test.$("foo", "success"), "helpers", v4Test.$("helper", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.data("run");
            }
        }), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                options.data("run", options.param(0));
            }
        })), "success");
    }

    @Test
    public void shouldWorkWithRootProgram() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{*decorator \"success\"}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                Assert.assertEquals("success", options.param(0));
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void shouldFailWhenAccessingVariablesFromRoot() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{*decorator foo}}", v4Test.$("hash", v4Test.$("foo", "fail"), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                Assert.assertEquals(null, options.param(0));
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void shouldBlockFailWhenAccessingVariablesFromRoot() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{#*decorator foo}}success{{/decorator}}", v4Test.$("hash", v4Test.$("foo", "fail"), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                Assert.assertEquals(null, options.param(0));
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void shouldBlockFailWhenAccessingVariablesFromRoot2() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{#*decorator foo}}success{{/decorator}}{{#*decorator foo}}success{{/decorator}}", v4Test.$("hash", v4Test.$("foo", "fail"), "decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                Assert.assertEquals(null, options.param(0));
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(2, count.get());
    }

    @Test
    public void controlNumberOfExecutions() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{*decorator}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void controlNumberOfExecutions2() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{*decorator}}{{*decorator}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(2, count.get());
    }

    @Test
    public void controlNumberOfExecutions3() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        shouldCompileTo("{{*decorator}}{{*decorator}}{{*decorator}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                count.incrementAndGet();
            }
        })), "");
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void gridData() throws Exception {
        shouldCompileTo(("{{#grid people}}\n" + (("  {{#*column \"First Name\"}}{{firstName}}{{/column}}\n" + "  {{#*column \"Last Name\"}}{{lastName}}{{/column}}\n") + "{{/grid}}")), v4Test.$("helpers", v4Test.$("grid", new Helper<java.util.List<v4Test.Hash>>() {
            @Override
            public Object apply(final java.util.List<v4Test.Hash> people, final Options options) throws IOException {
                java.util.List<v4Test.Hash> columns = options.data("columns");
                String headers = "";
                for (v4Test.Hash c : columns) {
                    headers += (c.get("key")) + ", ";
                }
                String output = headers + "\n";
                for (v4Test.Hash person : people) {
                    for (v4Test.Hash c : columns) {
                        output += (((Template) (c.get("body"))).apply(person)) + ", ";
                    }
                    output += "\n";
                }
                return output;
            }
        }), "decorators", v4Test.$("column", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
                java.util.List<v4Test.Hash> columns = options.data("columns");
                if (columns == null) {
                    columns = new ArrayList<>();
                    options.data("columns", columns);
                }
                columns.add(v4Test.$("key", options.param(0), "body", options.fn));
            }
        }), "hash", v4Test.$("people", Arrays.asList(v4Test.$("firstName", "Pedro", "lastName", "PicaPiedra"), v4Test.$("firstName", "Pablo", "lastName", "Marmol")))), ("First Name, Last Name, \n" + (("Pedro, PicaPiedra, \n" + "Pablo, Marmol, \n") + "")));
    }

    @Test
    public void text() throws Exception {
        text("{{*decorator}}{{*decorator}}{{*decorator}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
            }
        })), "{{*decorator}}{{*decorator}}{{*decorator}}");
        text("{{#*decorator}}{{*decorator}}{{/decorator}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
            }
        })), "{{#*decorator}}{{*decorator}}{{/decorator}}");
    }

    @Test
    public void collectVarDecorator() throws IOException {
        assertSetEquals(Arrays.asList("decorator"), compile("{{#hello}}{{*decorator}}{{/hello}}{{k}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
            }
        }))).collect(STAR_VAR));
    }

    @Test
    public void collectBlockDecorator() throws IOException {
        assertSetEquals(Arrays.asList("decorator"), compile("{{#*decorator}}deco{{/decorator}}{{k}}", v4Test.$("decorators", v4Test.$("decorator", new Decorator() {
            @Override
            public void apply(final Template fn, final Options options) throws IOException {
            }
        }))).collect(START_SECTION));
    }
}


package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class BlockParamsTest extends AbstractTest {
    @Test
    public void eachWithNamedIndex() throws IOException {
        shouldCompileTo(("{{#each users as |user userId|}}\n" + ("  Id: {{userId}} Name: {{user.name}}\n" + "{{/each}}")), AbstractTest.$("users", new Object[]{ AbstractTest.$("name", "Pedro"), AbstractTest.$("name", "Pablo") }), ("\n" + ((("  Id: 0 Name: Pedro\n" + "\n") + "  Id: 1 Name: Pablo\n") + "")));
    }

    @Test
    public void eachWithNamedKey() throws IOException {
        shouldCompileTo(("{{#each users as |user userId|}}\n" + ("  Id: {{userId}} Name: {{user.name}}\n" + "{{/each}}")), AbstractTest.$("users", Arrays.asList(AbstractTest.$("name", "Pedro"))), ("\n" + "  Id: 0 Name: Pedro\n"));
    }

    @Test
    public void shouldTakePrecedenceOverContextValues() throws IOException {
        shouldCompileTo("{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{value}}", AbstractTest.$("value", "foo"), AbstractTest.$("goodbyes", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(1, options.blockParams.size());
                return options.apply(options.fn, AbstractTest.$("value", "bar"), Arrays.<Object>asList(1, 2));
            }
        }), "1foo");
    }

    @Test
    public void shouldTakePrecedenceOverHelperValues() throws IOException {
        shouldCompileTo("{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{value}}", AbstractTest.$, AbstractTest.$("goodbyes", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(1, options.blockParams.size());
                return options.apply(options.fn, AbstractTest.$, Arrays.<Object>asList(1, 2));
            }
        }, "value", "foo"), "1foo");
    }

    @Test
    public void shouldNotTakePrecedenceOverPathedValues() throws IOException {
        shouldCompileTo("{{#goodbyes as |value|}}{{./value}}{{/goodbyes}}{{value}}", AbstractTest.$("value", "bar"), AbstractTest.$("goodbyes", new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                Assert.assertEquals(1, options.blockParams.size());
                return options.apply(options.fn, AbstractTest.$, Arrays.<Object>asList(1, 2));
            }
        }, "value", "foo"), "barfoo");
    }

    @Test
    public void shouldTakePrecedenceOverParentBlocParams() throws IOException {
        shouldCompileTo("{{#goodbyes as |value|}}{{#goodbyes}}{{value}}{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{/goodbyes}}{{/goodbyes}}{{value}}", AbstractTest.$("value", "foo"), AbstractTest.$("goodbyes", new Helper<Object>() {
            int value = 1;

            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                if ((options.blockParams.size()) > 0) {
                    return options.apply(options.fn, AbstractTest.$("value", "bar"), Arrays.<Object>asList(((value)++), ((value)++)));
                }
                return options.fn(AbstractTest.$("value", "bar"));
            }
        }), "13foo");
    }

    @Test
    public void shouldAllowBlockParamsOnChainedHelpers() throws IOException {
        shouldCompileTo("{{#if bar}}{{else goodbyes as |value|}}{{value}}{{/if}}{{value}}", AbstractTest.$("value", "foo"), AbstractTest.$("goodbyes", new Helper<Object>() {
            int value = 1;

            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                if ((options.blockParams.size()) > 0) {
                    return options.apply(options.fn, AbstractTest.$("value", "bar"), Arrays.<Object>asList(((value)++), ((value)++)));
                }
                return options.fn(AbstractTest.$("value", "bar"));
            }
        }), "1foo");
    }

    @Test
    public void with() throws IOException {
        shouldCompileTo("{{#with title as |t|}}{{t}}{{/with}}", AbstractTest.$("title", "Block Param"), "Block Param");
    }

    @Test
    public void blockParamText() throws IOException {
        Assert.assertEquals("{{#each users as |user userId|}}{{/each}}", compile("{{#each users as |user userId|}}{{/each}}").text());
    }
}


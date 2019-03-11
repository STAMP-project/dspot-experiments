package com.github.jknack.handlebars.i275;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class Issue275 {
    public static class Item {
        private String name;

        public Item(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void temporalPartials() throws IOException {
        Handlebars handlebars = prettyPrint(true).deletePartialAfterMerge(true);
        handlebars.registerHelper("item", new com.github.jknack.handlebars.Helper<Issue275.Item>() {
            @Override
            public CharSequence apply(final Issue275.Item item, final Options options) throws IOException {
                Template template = options.handlebars.compile(("item" + (item.getName())));
                return template.apply(options.context);
            }
        });
        Template template = handlebars.compile("temporal-partials");
        Assert.assertEquals(("Items:\n" + (((("\n" + "Item: Custom\n") + "...\n") + "Item: 2\n") + "...\n")), template.apply(Arrays.asList(new Issue275.Item("1"), new Issue275.Item("2"))));
    }

    @Test
    public void defaultPartials() throws IOException {
        Handlebars handlebars = prettyPrint(true).deletePartialAfterMerge(false);
        handlebars.registerHelper("item", new com.github.jknack.handlebars.Helper<Issue275.Item>() {
            @Override
            public CharSequence apply(final Issue275.Item item, final Options options) throws IOException {
                Template template = options.handlebars.compile(("item" + (item.getName())));
                return template.apply(options.context);
            }
        });
        Template template = handlebars.compile("temporal-partials");
        Assert.assertEquals(("Items:\n" + (((("\n" + "Item: Custom\n") + "...\n") + "Item: Custom\n") + "...\n")), template.apply(Arrays.asList(new Issue275.Item("1"), new Issue275.Item("2"))));
    }
}


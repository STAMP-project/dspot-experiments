package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TemporalPartialTest {
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
        Handlebars handlebars = new Handlebars();
        handlebars.setPrettyPrint(true);
        handlebars.registerHelper("item", new Helper<TemporalPartialTest.Item>() {
            @Override
            public CharSequence apply(final TemporalPartialTest.Item item, final Options options) throws IOException {
                Template template = options.handlebars.compile(("item" + (item.getName())));
                return template.apply(options.context);
            }
        });
        Template template = handlebars.compile("temporal-partials");
        Assert.assertEquals(("Items:\n" + (((("\n" + "Item: Custom\n") + "...\n") + "Item: 2\n") + "...\n")), template.apply(Arrays.asList(new TemporalPartialTest.Item("1"), new TemporalPartialTest.Item("2"))));
    }

    @Test
    public void defaultPartials() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.setPrettyPrint(true);
        Template template = handlebars.compile("derived");
        Assert.assertEquals(("\n" + (((((((((("<html>\n" + "<head>\n") + "  <title>\n") + "     Home \n") + "  </title>\n") + "</head>\n") + "<body>\n") + "  <h1> Home </h1>\n") + "  HOME\n") + "</body>\n") + "</html>")), template.apply(null));
    }
}


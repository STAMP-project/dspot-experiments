package handlebarsjs.spec;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import org.junit.Test;


public class BlockTest extends AbstractTest {
    @Test
    public void array() throws IOException {
        String string = "{{#goodbyes}}{{text}}! {{/goodbyes}}cruel {{world}}!";
        Object hash = AbstractTest.$("goodbyes", new Object[]{ AbstractTest.$("text", "goodbye"), AbstractTest.$("text", "Goodbye"), AbstractTest.$("text", "GOODBYE") }, "world", "world");
        shouldCompileTo(string, hash, "goodbye! Goodbye! GOODBYE! cruel world!", "Arrays iterate over the contents when not empty");
        shouldCompileTo(string, AbstractTest.$("goodbyes", new Object[0], "world", "world"), "cruel world!", "Arrays ignore the contents when empty");
    }

    @Test
    public void arrayWithIndex() throws IOException {
        String string = "{{#goodbyes}}{{@index}}. {{text}}! {{/goodbyes}}cruel {{world}}!";
        Object hash = AbstractTest.$("goodbyes", new Object[]{ AbstractTest.$("text", "goodbye"), AbstractTest.$("text", "Goodbye"), AbstractTest.$("text", "GOODBYE") }, "world", "world");
        shouldCompileTo(string, hash, "0. goodbye! 1. Goodbye! 2. GOODBYE! cruel world!", "The @index variable is used");
    }

    @Test
    public void emptyBlock() throws IOException {
        String string = "{{#goodbyes}}{{/goodbyes}}cruel {{world}}!";
        Object hash = AbstractTest.$("goodbyes", new Object[]{ AbstractTest.$("text", "goodbye"), AbstractTest.$("text", "Goodbye"), AbstractTest.$("text", "GOODBYE") }, "world", "world");
        shouldCompileTo(string, hash, "cruel world!", "Arrays iterate over the contents when not empty");
        hash = AbstractTest.$("goodbyes", new Object[0], "world", "world");
        shouldCompileTo(string, hash, "cruel world!", "Arrays ignore the contents when empty");
    }

    @Test
    public void blockWithComplexLookup() throws IOException {
        String string = "{{#goodbyes}}{{text}} cruel {{../name}}! {{/goodbyes}}";
        Object hash = AbstractTest.$("goodbyes", new Object[]{ AbstractTest.$("text", "goodbye"), AbstractTest.$("text", "Goodbye"), AbstractTest.$("text", "GOODBYE") }, "name", "Alan");
        shouldCompileTo(string, hash, "goodbye cruel Alan! Goodbye cruel Alan! GOODBYE cruel Alan! ", "Templates can access variables in contexts up the stack with relative path syntax");
    }

    @Test
    public void helperWithComplexLookup$() throws IOException {
        String string = "{{#goodbyes}}{{{link ../prefix}}}{{/goodbyes}}";
        Object hash = AbstractTest.$("prefix", "/root", "goodbyes", new Object[]{ AbstractTest.$("text", "Goodbye", "url", "goodbye") });
        AbstractTest.Hash helpers = AbstractTest.$("link", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object prefix, final Options options) throws IOException {
                Object url = options.context.get("url");
                Object text = options.context.get("text");
                return ((((("<a href='" + prefix) + "/") + url) + "'>") + text) + "</a>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<a href='/root/goodbye'>Goodbye</a>");
    }

    @Test
    public void helperWithComplexLookupExpression() throws IOException {
        String string = "{{#goodbyes}}{{../name}}{{/goodbyes}}";
        String hash = "{name: Alan}";
        AbstractTest.Hash helpers = AbstractTest.$("goodbyes", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                String out = "";
                String[] byes = new String[]{ "Goodbye", "goodbye", "GOODBYE" };
                for (String bye : byes) {
                    out += ((bye + " ") + (options.fn(this))) + "! ";
                }
                return out;
            }
        });
        shouldCompileTo(string, hash, helpers, "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
    }

    @Test
    public void helperWithComplexLookupAndNestedTemplate() throws IOException {
        String string = "{{#goodbyes}}{{#link ../prefix}}{{text}}{{/link}}{{/goodbyes}}";
        Object hash = AbstractTest.$("prefix", "/root", "goodbyes", new Object[]{ AbstractTest.$("text", "Goodbye", "url", "goodbye") });
        AbstractTest.Hash helpers = AbstractTest.$("link", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object prefix, final Options options) throws IOException {
                Object url = options.context.get("url");
                Object text = options.context.get("text");
                return ((((("<a href='" + prefix) + "/") + url) + "'>") + text) + "</a>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<a href='/root/goodbye'>Goodbye</a>");
    }

    @Test
    public void blockWithDeepNestedComplexLookup() throws IOException {
        String string = "{{#outer}}Goodbye {{#inner}}cruel {{../../omg}}{{/inner}}{{/outer}}";
        Object hash = AbstractTest.$("omg", "OMG!", "outer", new Object[]{ AbstractTest.$("inner", new Object[]{ AbstractTest.$("text", "goodbye") }) });
        shouldCompileTo(string, hash, "Goodbye cruel OMG!");
    }

    @Test
    public void blockHelper() throws IOException {
        String string = "{{#goodbyes}}{{text}}! {{/goodbyes}}cruel {{world}}!";
        String hash = "{world: world}";
        AbstractTest.Hash helpers = AbstractTest.$("goodbyes", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return options.fn(AbstractTest.$("text", "GOODBYE"));
            }
        });
        shouldCompileTo(string, hash, helpers, "GOODBYE! cruel world!", "Block helper executed");
    }

    @Test
    public void blockHelperStayingInTheSameContext() throws IOException {
        String string = "{{#form}}<p>{{name}}</p>{{/form}}";
        String hash = "{name: Yehuda}";
        AbstractTest.Hash helpers = AbstractTest.$("form", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("<form>" + (options.fn(this))) + "</form>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<form><p>Yehuda</p></form>", "Block helper executed with current context");
    }

    @Test
    public void blockHelperShouldHaveContextInThis() throws IOException {
        String string = "<ul>{{#people}}<li>{{#link}}{{name}}{{/link}}</li>{{/people}}</ul>";
        Object hash = AbstractTest.$("people", new Object[]{ AbstractTest.$("name", "Alan", "id", 1), AbstractTest.$("name", "Yehuda", "id", 2) });
        AbstractTest.Hash helpers = AbstractTest.$("link", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((("<a href=\"/people/" + (options.get("id"))) + "\">") + (options.fn(this))) + "</a>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<ul><li><a href=\"/people/1\">Alan</a></li><li><a href=\"/people/2\">Yehuda</a></li></ul>");
    }

    @Test
    public void blockHelperForUndefinedValue() throws IOException {
        shouldCompileTo("{{#_empty}}shouldn't render{{/_empty}}", AbstractTest.$, "");
    }

    @Test
    public void blockHelperPassingNewContext() throws IOException {
        String string = "{{#form yehuda}}<p>{{name}}</p>{{/form}}";
        String hash = "{yehuda: {name: Yehuda}}";
        AbstractTest.Hash helpers = AbstractTest.$("form", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("<form>" + (options.fn(context))) + "</form>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<form><p>Yehuda</p></form>", "Context variable resolved");
    }

    @Test
    public void blockHelperPassingComplexContextPath() throws IOException {
        String string = "{{#form yehuda/cat}}<p>{{name}}</p>{{/form}}";
        String hash = "{yehuda: {name: Yehuda, cat: {name: Harold}}}";
        AbstractTest.Hash helpers = AbstractTest.$("form", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("<form>" + (options.fn(context))) + "</form>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<form><p>Harold</p></form>", "Complex path variable resolved");
    }

    @Test
    public void nestedBlockHelpers() throws IOException {
        String string = "{{#form yehuda}}<p>{{name}}</p>{{#link}}Hello{{/link}}{{/form}}";
        String hash = "yehuda: {name: Yehuda}";
        AbstractTest.Hash helpers = AbstractTest.$("form", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ("<form>" + (options.fn(context))) + "</form>";
            }
        }, "link", new com.github.jknack.handlebars.Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options) throws IOException {
                return ((("<a href='" + (options.get("name"))) + "'>") + (options.fn(this))) + "</a>";
            }
        });
        shouldCompileTo(string, hash, helpers, "<form><p>Yehuda</p><a href='Yehuda'>Hello</a></form>", "Both blocks executed");
    }

    @Test
    public void blockInvertedSections() throws IOException {
        shouldCompileTo("{{#people}}{{name}}{{^}}{{none}}{{/people}}", "{none: No people}", "No people");
    }

    @Test
    public void blockInvertedSectionsWithEmptyArrays() throws IOException {
        shouldCompileTo("{{#people}}{{name}}{{^}}{{none}}{{/people}}", AbstractTest.$("none", "No people", "people", new Object[0]), "No people");
    }

    @Test
    public void blockHelperInvertedSections() throws Exception {
        String string = "{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}";
        AbstractTest.Hash helpers = AbstractTest.$("list", new com.github.jknack.handlebars.Helper<java.util.List<Object>>() {
            @Override
            public Object apply(final java.util.List<Object> context, final Options options) throws IOException {
                if ((context.size()) > 0) {
                    String out = "<ul>";
                    for (Object element : context) {
                        out += "<li>";
                        out += options.fn(element);
                        out += "</li>";
                    }
                    out += "</ul>";
                    return out;
                } else {
                    return ("<p>" + (options.inverse(this))) + "</p>";
                }
            }
        });
        Object hash = AbstractTest.$("people", new Object[]{ AbstractTest.$("name", "Alan"), AbstractTest.$("name", "Yehuda") });
        Object empty = AbstractTest.$("people", new Object[0]);
        Object rootMessage = AbstractTest.$("people", new Object[0], "message", "Nobody's here");
        String messageString = "{{#list people}}Hello{{^}}{{message}}{{/list}}";
        // the meaning here may be kind of hard to catch, but list.not is always called,
        // so we should see the output of both
        shouldCompileTo(string, hash, helpers, "<ul><li>Alan</li><li>Yehuda</li></ul>", "an inverse wrapper is passed in as a new context");
        shouldCompileTo(string, empty, helpers, "<p><em>Nobody's here</em></p>", "an inverse wrapper can be optionally called");
        shouldCompileTo(messageString, rootMessage, helpers, "<p>Nobody&#x27;s here</p>", "the context of an inverse is the parent of the block");
    }
}


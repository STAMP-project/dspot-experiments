package com.github.jknack.handlebars.js;


import FieldValueResolver.INSTANCE;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import org.junit.Test;


public class JavaScriptHelperTest extends AbstractTest {
    private static Handlebars handlebars = new Handlebars();

    public static class Bean {
        private String name;

        public Bean(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class ObjectWithPublicFields {
        public String name;

        public ObjectWithPublicFields(final String name) {
            this.name = name;
        }
    }

    @Test
    public void simple() throws Exception {
        eval("{{simple}}", AbstractTest.$, "Long live to Js!");
    }

    @Test
    public void mapContext() throws Exception {
        eval("{{context this}}", AbstractTest.$("name", "moe"), "moe");
    }

    @Test
    public void beanContext() throws Exception {
        eval("{{context this}}", new JavaScriptHelperTest.Bean("curly"), "curly");
    }

    @Test
    public void publicFieldsContext() throws Exception {
        Context ctx = Context.newBuilder(new JavaScriptHelperTest.ObjectWithPublicFields("curly")).resolver(INSTANCE).build();
        JavaScriptHelperTest.eval("{{context this}}", ctx, "curly");
    }

    @Test
    public void thisContextAsMap() throws Exception {
        eval("{{thisContext}}", AbstractTest.$("name", "larry"), "larry");
    }

    @Test
    public void thisContextAsBean() throws Exception {
        eval("{{thisContext}}", new JavaScriptHelperTest.Bean("curly"), "curly");
    }

    @Test
    public void beanWithParam() throws Exception {
        eval("{{param1 this 32}}", new JavaScriptHelperTest.Bean("edgar"), "edgar is 32 years old");
    }

    @Test
    public void params() throws Exception {
        eval("{{params this 1 2 3}}", AbstractTest.$, "1, 2, 3");
    }

    @Test
    public void hash() throws Exception {
        eval("{{hash this h1=1 h2='2' h3=true}}", AbstractTest.$, "1, 2, true");
    }

    @Test
    public void fn() throws Exception {
        eval("{{#fn this}}I'm {{name}}!{{/fn}}", new JavaScriptHelperTest.Bean("curly"), "I'm curly!");
    }

    @Test
    public void fnWithNewContext() throws Exception {
        eval("{{#fnWithNewContext this}}I'm {{name}}!{{/fnWithNewContext}}", new JavaScriptHelperTest.Bean("curly"), "I'm moe!");
    }

    @Test
    public void escapeString() throws Exception {
        eval("{{escapeString}}", AbstractTest.$, "&lt;a&gt;&lt;/a&gt;");
    }

    @Test
    public void safeString() throws Exception {
        eval("{{safeString}}", AbstractTest.$, "<a></a>");
    }

    @Test
    public void helper_with_complex_lookup$() throws Exception {
        eval("{{#goodbyes}}{{{link ../prefix}}}{{/goodbyes}}", AbstractTest.$("prefix", "/root", "goodbyes", new Object[]{ AbstractTest.$("text", "Goodbye", "url", "goodbye") }), "<a href='/root/goodbye'>Goodbye</a>");
    }

    @Test
    public void helper_block_with_complex_lookup_expression() throws Exception {
        eval("{{#goodbyes2}}{{name}}{{/goodbyes2}}", AbstractTest.$("name", "Alan"), "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
    }

    @Test
    public void helper_block_with_complex_lookup_expression4() throws Exception {
        eval("{{#goodbyes4}}{{../name}}{{/goodbyes4}}", AbstractTest.$("name", "Alan"), "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
    }

    @Test
    public void helper_with_complex_lookup_and_nested_template() throws Exception {
        eval("{{#goodbyes}}{{#link2 ../prefix}}{{text}}{{/link2}}{{/goodbyes}}", AbstractTest.$("prefix", "/root", "goodbyes", new Object[]{ AbstractTest.$("text", "Goodbye", "url", "goodbye") }), "<a href='/root/goodbye'>Goodbye</a>");
    }

    @Test
    public void block_helper() throws Exception {
        eval("{{#goodbyes3}}{{text}}! {{/goodbyes3}}cruel {{world}}!", AbstractTest.$("world", "world"), "GOODBYE! cruel world!");
    }

    @Test
    public void block_helper_staying_in_the_same_context() throws Exception {
        eval("{{#form}}<p>{{name}}</p>{{/form}}", AbstractTest.$("name", "Yehuda"), "<form><p>Yehuda</p></form>");
    }

    @Test
    public void block_helper_should_have_context_in_this() throws Exception {
        eval("<ul>{{#people}}<li>{{#link3}}{{name}}{{/link3}}</li>{{/people}}</ul>", AbstractTest.$("people", new Object[]{ AbstractTest.$("name", "Alan", "id", 1), AbstractTest.$("name", "Yehuda", "id", 2) }), "<ul><li><a href=\"/people/1\">Alan</a></li><li><a href=\"/people/2\">Yehuda</a></li></ul>");
    }

    @Test
    public void block_helper_for_undefined_value() throws Exception {
        eval("{{#empty2}}shouldn't render{{/empty2}}", AbstractTest.$, "");
    }

    @Test
    public void block_helper_passing_a_new_context() throws Exception {
        eval("{{#form2 yehuda}}<p>{{name}}</p>{{/form2}}", AbstractTest.$("yehuda", AbstractTest.$("name", "Yehuda")), "<form><p>Yehuda</p></form>");
    }

    @Test
    public void block_helper_inverted_sections() throws Exception {
        eval("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}", AbstractTest.$("people", new Object[]{ AbstractTest.$("name", "Alan"), AbstractTest.$("name", "Yehuda") }), "<ul><li>Alan</li><li>Yehuda</li></ul>");
        eval("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}", AbstractTest.$("people", new Object[0]), "<p><em>Nobody's here</em></p>");
        eval("{{#list people}}Hello{{^}}{{message}}{{/list}}", AbstractTest.$("people", new Object[0], "message", "Nobody's here"), "<p>Nobody&#x27;s here</p>");
    }
}


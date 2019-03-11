package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue506 extends v4Test {
    @Test
    public void nestedAs() throws IOException {
        shouldCompileTo(("{{#each things}}" + ("not using \"as\": value is {{this}}, key is {{@index}}, hello is {{lower \"HELLO\"}}" + "{{/each}}")), v4Test.$("hash", v4Test.$("things", new String[]{ "foo" }), "helpers", v4Test.$("lower", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
            }
        })), "not using \"as\": value is foo, key is 0, hello is hello");
        shouldCompileTo(("{{#each things as |value key|}}" + ("using \"as\": value is {{value}}, key is {{key}}, hello is {{lower \"HELLO\"}}" + "{{/each}}")), v4Test.$("hash", v4Test.$("things", new String[]{ "foo" }), "helpers", v4Test.$("lower", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
            }
        })), "using \"as\": value is foo, key is 0, hello is hello");
        shouldCompileTo(("{{#each things as |value key|}}" + ("using \"as\" and \"#\": value is {{value}}, key is {{key}}, hello is {{#lower \"HELLO\"}}{{this}}{{/lower}}" + "{{/each}}")), v4Test.$("hash", v4Test.$("things", new String[]{ "foo" }), "helpers", v4Test.$("lower", new com.github.jknack.handlebars.Helper<String>() {
            @Override
            public Object apply(final String str, final Options options) throws IOException {
                return str.toLowerCase();
            }
        })), "using \"as\" and \"#\": value is foo, key is 0, hello is hello");
    }
}


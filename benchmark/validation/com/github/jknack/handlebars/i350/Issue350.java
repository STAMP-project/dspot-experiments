package com.github.jknack.handlebars.i350;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue350 extends AbstractTest {
    @Test
    public void partialWithParameters() throws IOException {
        shouldCompileToWithPartials(("<ul>\n" + ((("{{#each dudes}}\n" + "  {{> dude title=../title class=\"list-item\"}}\n") + "{{/each}}\n") + "</ul>")), AbstractTest.$("title", "profile", "dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") }), AbstractTest.$("dude", ("<li class=\"{{class}}\">\n" + ("  {{title}}: <a href=\"{{url}}\">{{name}}</a>\n" + "</li>\n"))), ("<ul>\n" + (((((("  <li class=\"list-item\">\n" + "    profile: <a href=\"http://yehuda\">Yehuda</a>\n") + "  </li>\n") + "  <li class=\"list-item\">\n") + "    profile: <a href=\"http://alan\">Alan</a>\n") + "  </li>\n") + "</ul>")));
    }
}


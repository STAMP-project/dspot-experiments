package com.github.jknack.handlebars.i407;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue407 extends AbstractTest {
    @Test
    public void shouldNotThrowNPE() throws IOException {
        shouldCompileTo(("{{#compare true true}}\n" + ((("    {{uppercase \"aaa\"}}\n" + "{{else}}\n") + "    {{uppercase \"bbb\"}}\n") + "{{/compare}}")), AbstractTest.$, "\n    AAA\n");
        shouldCompileTo(("{{#compare true true}}\n" + ((("    {{#compare true true}}\n" + "        kkk\n") + "    {{/compare}}\n") + "{{/compare}}")), AbstractTest.$, ("\n" + (("    \n" + "        kkk\n") + "    \n")));
    }
}


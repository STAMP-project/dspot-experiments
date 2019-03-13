package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue123 extends AbstractTest {
    @Test
    public void spacesInBlock() throws IOException {
        shouldCompileTo("{{#if \"stuff\" }}Bingo{{/if}}", AbstractTest.$, "Bingo");
        shouldCompileTo("{{#if \"stuff\"  }}Bingo{{/if}}", AbstractTest.$, "Bingo");
        shouldCompileTo("{{#if \"stuff\"}}Bingo{{/if}}", AbstractTest.$, "Bingo");
        shouldCompileTo("{{# if \"stuff\"}}Bingo{{/if}}", AbstractTest.$, "Bingo");
        shouldCompileTo("{{#if \"stuff\"}}Bingo{{/ if}}", AbstractTest.$, "Bingo");
        shouldCompileTo("{{# if \"stuff\" }}Bingo{{/ if }}", AbstractTest.$, "Bingo");
    }

    @Test
    public void spacesInVar() throws IOException {
        shouldCompileTo("{{var}}", AbstractTest.$, "");
        shouldCompileTo("{{ var}}", AbstractTest.$, "");
        shouldCompileTo("{{var }}", AbstractTest.$, "");
        shouldCompileTo("{{ var }}", AbstractTest.$, "");
        shouldCompileTo("{{var x }}", AbstractTest.$, AbstractTest.$("var", ""), "");
    }
}


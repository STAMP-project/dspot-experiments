package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class ChainedElseBlockTest extends AbstractTest {
    @Test
    public void chainedInvertedSections() throws IOException {
        shouldCompileTo("{{#people}}{{name}}{{else if none}}{{none}}{{/people}}", AbstractTest.$("none", "No people"), "No people");
        shouldCompileTo("{{#people}}{{name}}{{else if nothere}}fail{{else unless nothere}}{{none}}{{/people}}", AbstractTest.$("none", "No people"), "No people");
        shouldCompileTo("{{#people}}{{name}}{{else if none}}{{none}}{{else}}fail{{/people}}", AbstractTest.$("none", "No people"), "No people");
    }

    @Test(expected = HandlebarsException.class)
    public void chainedInvertedSectionsWithMismatch() throws IOException {
        shouldCompileTo("{{#people}}{{name}}{{else if none}}{{none}}{{/if}}", AbstractTest.$("none", "No people"), "No people");
    }

    @Test
    public void blockStandaloneChainedElseSections() throws IOException {
        shouldCompileTo("{{#people}}{{name}}{{else if none}}{{none}}{{/people}}", AbstractTest.$("none", "No people"), "No people");
        shouldCompileTo("{{#people}}{{name}}{{else if none}}{{none}}{{^}}{{/people}}", AbstractTest.$("none", "No people"), "No people");
    }
}


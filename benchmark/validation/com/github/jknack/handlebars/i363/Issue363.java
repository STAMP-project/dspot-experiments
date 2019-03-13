package com.github.jknack.handlebars.i363;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue363 extends AbstractTest {
    @Test
    public void shouldNotDependsOnNewLine() throws IOException {
        shouldCompileTo("{{model1.listOfValues1.[0]}}{{#if model3}}{{model2.users.[0].name}}{{/if}}", AbstractTest.$, "");
    }
}


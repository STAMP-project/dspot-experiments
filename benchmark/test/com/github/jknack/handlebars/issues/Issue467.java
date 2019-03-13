package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class Issue467 extends v4Test {
    @Test
    public void shouldHaveAccessToKeyViaBlockParam() throws IOException {
        shouldCompileTo("{{#each this as |eitem key| }}{{this}} || {{key}} | {{eitem}}{{/each}}", v4Test.$("hash", v4Test.$("africa", Arrays.asList("egypt", "kenya"))), "[egypt, kenya] || africa | [egypt, kenya]");
    }
}


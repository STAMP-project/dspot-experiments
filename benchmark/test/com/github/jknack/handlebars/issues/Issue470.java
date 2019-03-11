package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class Issue470 extends v4Test {
    @Test
    public void indexFromParentContext() throws IOException {
        shouldCompileTo("{{#each families}} {{#each this}}{{../@index}} :{{name}} {{/each}} {{/each}}", v4Test.$("hash", v4Test.$("families", Arrays.asList(Arrays.asList(v4Test.$("age", 10, "name", "jimmy"), v4Test.$("age", 15, "name", "rose")), Arrays.asList(v4Test.$("age", 35, "name", "John"), v4Test.$("age", 32, "name", "Jessy"))))), " 0 :jimmy 0 :rose   1 :John 1 :Jessy  ");
    }

    @Test
    public void indexFromParentContextInJs() throws IOException {
        shouldCompileTo("{{#each families}} {{#each this}}{{@../index}} :{{name}} {{/each}} {{/each}}", v4Test.$("hash", v4Test.$("families", Arrays.asList(Arrays.asList(v4Test.$("age", 10, "name", "jimmy"), v4Test.$("age", 15, "name", "rose")), Arrays.asList(v4Test.$("age", 35, "name", "John"), v4Test.$("age", 32, "name", "Jessy"))))), " 0 :jimmy 0 :rose   1 :John 1 :Jessy  ");
    }
}


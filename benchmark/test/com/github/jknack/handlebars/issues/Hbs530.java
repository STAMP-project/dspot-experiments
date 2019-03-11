package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class Hbs530 extends v4Test {
    @Test
    public void indexOutOfBoundsExceptioWhenUsingBlockParametersOnAnEmptyList() throws IOException {
        shouldCompileTo(("{{#each users as |user userId|}}\n" + ("  Id: {{userId}} Name: {{user.name}} <BR>\n" + "{{/each}}")), v4Test.$("hash", v4Test.$("users", Arrays.asList())), "");
    }
}


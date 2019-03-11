package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.util.Arrays;
import org.junit.Test;


public class Issue574 extends v4Test {
    @Test
    public void eachShouldExecuteElseBranchOnFalsyValue() throws Exception {
        shouldCompileTo("{{#each list}}not empty{{else}}empty{{/each}}", v4Test.$("hash", v4Test.$("list", Arrays.asList())), "empty");
        shouldCompileTo("{{#each list}}not empty{{else}}empty{{/each}}", v4Test.$("hash", v4Test.$("list", null)), "empty");
    }
}


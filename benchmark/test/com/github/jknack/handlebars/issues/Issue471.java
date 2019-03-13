package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;


public class Issue471 extends v4Test {
    class Dummy {
        public String value;

        public Collection<Issue471.Dummy> subDummies;

        public String getVal() {
            return value;
        }

        public Collection<Issue471.Dummy> getSubDum() {
            return subDummies;
        }
    }

    @Test
    public void expansionsOfDifferentObjectsOfTheSameType() throws IOException {
        Issue471.Dummy dummy1 = new Issue471.Dummy();
        Issue471.Dummy dummy2 = new Issue471.Dummy();
        Issue471.Dummy dummy3 = new Issue471.Dummy();
        Issue471.Dummy dummy4 = new Issue471.Dummy();
        dummy1.value = "d1";
        dummy2.value = "d2";
        dummy3.value = "d3";
        dummy4.value = "d4";
        dummy1.subDummies = Arrays.asList(dummy3);
        dummy2.subDummies = Arrays.asList(dummy4);
        Collection<Issue471.Dummy> dummies = Arrays.asList(dummy1, dummy2);
        shouldCompileTo("{{#this}}{{val}}{{#subDum}}{{val}}{{/subDum}}{{/this}}", v4Test.$("hash", dummies), "d1d3d2d4");
        shouldCompileTo("{{#each this}}{{val}}{{#each subDum}}{{val}}{{/each}}{{/each}}", v4Test.$("hash", dummies), "d1d3d2d4");
    }
}


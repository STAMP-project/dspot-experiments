package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;


public class Issue476 extends v4Test {
    @Test
    public void jsLengthMisbehaviorWithLists() throws IOException {
        shouldCompileTo("{{length this}}", v4Test.$("hash", new ArrayList<>(Arrays.asList("1", "3", "4"))), "3");
    }

    @Test
    public void jsLengthMisbehaviorWithArray() throws IOException {
        shouldCompileTo("{{length this}}", v4Test.$("hash", new String[]{ "1", "3", "4" }), "3");
    }
}


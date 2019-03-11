package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue468 extends v4Test {
    @Test
    public void rawHelperShouldWork() throws IOException {
        shouldCompileTo("{{{{raw-helper}}}}{{bar}}{{{{/raw-helper}}}}", v4Test.$, "{{bar}}");
    }
}


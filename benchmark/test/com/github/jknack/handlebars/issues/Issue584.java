package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue584 extends v4Test {
    @Test
    public void shouldRemoveBlankAroundElse() throws IOException {
        shouldCompileTo(("A\n" + ((((("{{#if someVariableWhichIsFalse}}\n" + "B\n") + "{{else}}\n") + "C\n") + "{{/if}}\n") + "D")), v4Test.$, ("A\n" + ("C\n" + "D")));
        shouldCompileTo(("A\n" + ((((("{{#if someVariableWhichIsFalse}}\n" + "B\n") + "{{^}}\n") + "C\n") + "{{/if}}\n") + "D")), v4Test.$, ("A\n" + ("C\n" + "D")));
    }
}


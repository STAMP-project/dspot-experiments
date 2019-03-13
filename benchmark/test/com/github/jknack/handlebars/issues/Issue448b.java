package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue448b extends v4Test {
    @Test
    public void shouldApplySubOnBlockParams() throws IOException {
        shouldCompileTo(("{{#each this as |c|}} {{c}} - {{#is 0 (remainder c 2)}}even{{else}} odd{{/is}} \n" + "{{/each}}"), v4Test.$("hash", new Object[]{ 4, 5, 6, 7, 8 }), (" 4 - even \n" + (((" 5 -  odd \n" + " 6 - even \n") + " 7 -  odd \n") + " 8 - even \n")));
    }
}


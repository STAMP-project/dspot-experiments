package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue489 extends v4Test {
    @Test
    public void shouldPassObjectResult() throws IOException {
        shouldCompileTo(("{{#each myLibrary}}" + ((("{{#with (lookup @root.booksByIsbn .) as |book|}}" + "{{book.title}} by {{book.author}} ({{book.isbn}})\n") + "{{/with}}") + "{{/each}}")), v4Test.$("hash", v4Test.$("booksByIsbn", v4Test.$("0321356683", v4Test.$("title", "Effective Java (2nd Edition)", "author", "Joshua Bloch", "isbn", "0321356683")), "myLibrary", new String[]{ "0321356683" })), "Effective Java (2nd Edition) by Joshua Bloch (0321356683)\n");
    }
}


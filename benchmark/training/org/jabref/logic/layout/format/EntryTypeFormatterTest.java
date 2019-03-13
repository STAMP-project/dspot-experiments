package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EntryTypeFormatterTest {
    private EntryTypeFormatter formatter;

    @Test
    public void testCorrectFormatArticle() {
        Assertions.assertEquals("Article", formatter.format("article"));
    }

    @Test
    public void testCorrectFormatInBook() {
        Assertions.assertEquals("InBook", formatter.format("inbook"));
    }

    @Test
    public void testIncorrectTypeAarticle() {
        Assertions.assertEquals("Aarticle", formatter.format("aarticle"));
    }
}


package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HtmlToUnicodeFormatterTest {
    private HtmlToUnicodeFormatter formatter;

    @Test
    public void formatWithoutHtmlCharactersReturnsSameString() {
        Assertions.assertEquals("abc", formatter.format("abc"));
    }

    @Test
    public void formatMultipleHtmlCharacters() {
        Assertions.assertEquals("???", formatter.format("&aring;&auml;&ouml;"));
    }

    @Test
    public void formatCombinedAccent() {
        Assertions.assertEquals("i?", formatter.format("i&#x301;"));
    }

    @Test
    public void testBasic() {
        Assertions.assertEquals("aaa", formatter.format("aaa"));
    }

    @Test
    public void testUmlauts() {
        Assertions.assertEquals("?", formatter.format("&auml;"));
        Assertions.assertEquals("?", formatter.format("&#228;"));
        Assertions.assertEquals("?", formatter.format("&#xe4;"));
    }

    @Test
    public void testGreekLetter() {
        Assertions.assertEquals("?", formatter.format("&Epsilon;"));
    }

    @Test
    public void testHTMLRemoveTags() {
        Assertions.assertEquals("aaa", formatter.format("<p>aaa</p>"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("bread & butter", formatter.format(formatter.getExampleInput()));
    }
}


package org.jabref.logic.bibtex;


import OS.NEWLINE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldContentParserTest {
    private FieldContentParser parser;

    @Test
    public void unifiesLineBreaks() {
        String original = "I\r\nunify\nline\rbreaks.";
        String expected = "I\nunify\nline\nbreaks.".replace("\n", NEWLINE);
        String processed = parser.format(new StringBuilder(original), "abstract").toString();
        Assertions.assertEquals(expected, processed);
    }

    @Test
    public void retainsWhitespaceForMultiLineFields() {
        String original = "I\nkeep\nline\nbreaks\nand\n\ttabs.";
        String formatted = original.replace("\n", NEWLINE);
        String abstrakt = parser.format(new StringBuilder(original), "abstract").toString();
        String review = parser.format(new StringBuilder(original), "review").toString();
        Assertions.assertEquals(formatted, abstrakt);
        Assertions.assertEquals(formatted, review);
    }

    @Test
    public void removeWhitespaceFromNonMultiLineFields() {
        String original = "I\nshould\nnot\ninclude\nadditional\nwhitespaces  \nor\n\ttabs.";
        String expected = "I should not include additional whitespaces or tabs.";
        String abstrakt = parser.format(new StringBuilder(original), "title").toString();
        String any = parser.format(new StringBuilder(original), "anyotherfield").toString();
        Assertions.assertEquals(expected, abstrakt);
        Assertions.assertEquals(expected, any);
    }
}


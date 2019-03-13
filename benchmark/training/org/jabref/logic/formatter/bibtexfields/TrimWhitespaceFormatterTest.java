package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TrimWhitespaceFormatterTest {
    private TrimWhitespaceFormatter formatter;

    @Test
    public void removeHorizontalTabulations() {
        Assertions.assertEquals("whitespace", formatter.format("\twhitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace\t"));
        Assertions.assertEquals("whitespace", formatter.format("\twhitespace\t\t"));
    }

    @Test
    public void removeLineFeeds() {
        Assertions.assertEquals("whitespace", formatter.format("\nwhitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace\n"));
        Assertions.assertEquals("whitespace", formatter.format("\nwhitespace\n\n"));
    }

    @Test
    public void removeFormFeeds() {
        Assertions.assertEquals("whitespace", formatter.format("\fwhitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace\f"));
        Assertions.assertEquals("whitespace", formatter.format("\fwhitespace\f\f"));
    }

    @Test
    public void removeCarriageReturnFeeds() {
        Assertions.assertEquals("whitespace", formatter.format("\rwhitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace\r"));
        Assertions.assertEquals("whitespace", formatter.format("\rwhitespace\r\r"));
    }

    @Test
    public void removeSeparatorSpaces() {
        Assertions.assertEquals("whitespace", formatter.format(" whitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace "));
        Assertions.assertEquals("whitespace", formatter.format(" whitespace  "));
    }

    @Test
    public void removeMixedWhitespaceChars() {
        Assertions.assertEquals("whitespace", formatter.format(" \r\t\fwhitespace"));
        Assertions.assertEquals("whitespace", formatter.format("whitespace \n \r"));
        Assertions.assertEquals("whitespace", formatter.format("   \f\t whitespace  \r \n"));
    }
}


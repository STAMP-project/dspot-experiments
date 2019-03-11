package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemoveNewlinesFormatterTest {
    private RemoveNewlinesFormatter formatter;

    @Test
    public void removeCarriageReturnLineFeed() {
        Assertions.assertEquals("rn linebreak", formatter.format("rn\r\nlinebreak"));
    }

    @Test
    public void removeCarriageReturn() {
        Assertions.assertEquals("r linebreak", formatter.format("r\rlinebreak"));
    }

    @Test
    public void removeLineFeed() {
        Assertions.assertEquals("n linebreak", formatter.format("n\nlinebreak"));
    }

    @Test
    public void removePlatformSpecificNewLine() {
        String newLine = String.format("%n");
        Assertions.assertEquals("linebreak on current platform", formatter.format((("linebreak on" + newLine) + "current platform")));
    }
}


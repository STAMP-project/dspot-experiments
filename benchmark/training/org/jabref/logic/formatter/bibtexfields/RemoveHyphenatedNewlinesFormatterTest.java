package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemoveHyphenatedNewlinesFormatterTest {
    private RemoveHyphenatedNewlinesFormatter formatter;

    @Test
    public void removeHyphensBeforeNewlines() {
        Assertions.assertEquals("water", formatter.format("wa-\nter"));
        Assertions.assertEquals("water", formatter.format("wa-\r\nter"));
        Assertions.assertEquals("water", formatter.format("wa-\rter"));
    }

    @Test
    public void removeHyphensBeforePlatformSpecificNewlines() {
        String newLine = String.format("%n");
        Assertions.assertEquals("water", formatter.format((("wa-" + newLine) + "ter")));
    }
}


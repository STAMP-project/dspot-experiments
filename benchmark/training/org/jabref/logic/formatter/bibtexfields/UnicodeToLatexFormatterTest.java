package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class UnicodeToLatexFormatterTest {
    private UnicodeToLatexFormatter formatter;

    @Test
    public void formatWithoutUnicodeCharactersReturnsSameString() {
        Assertions.assertEquals("abc", formatter.format("abc"));
    }

    @Test
    public void formatMultipleUnicodeCharacters() {
        Assertions.assertEquals("{{\\aa}}{\\\"{a}}{\\\"{o}}", formatter.format("\u00e5\u00e4\u00f6"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("M{\\\"{o}}nch", formatter.format(formatter.getExampleInput()));
    }
}


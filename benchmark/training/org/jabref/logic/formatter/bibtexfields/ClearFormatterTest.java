package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class ClearFormatterTest {
    private ClearFormatter formatter;

    /**
     * Check whether the clear formatter really returns the empty string for the empty string
     */
    @Test
    public void formatReturnsEmptyForEmptyString() throws Exception {
        Assertions.assertEquals("", formatter.format(""));
    }

    /**
     * Check whether the clear formatter really returns the empty string for some string
     */
    @Test
    public void formatReturnsEmptyForSomeString() throws Exception {
        Assertions.assertEquals("", formatter.format("test"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("", formatter.format(formatter.getExampleInput()));
    }
}


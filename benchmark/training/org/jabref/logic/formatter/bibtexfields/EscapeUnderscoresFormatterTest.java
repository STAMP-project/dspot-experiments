package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EscapeUnderscoresFormatterTest {
    private EscapeUnderscoresFormatter formatter;

    /**
     * Check whether the clear formatter really returns the empty string for the empty string
     */
    @Test
    public void formatReturnsSameTextIfNoUnderscoresPresent() throws Exception {
        Assertions.assertEquals("Lorem ipsum", formatter.format("Lorem ipsum"));
    }

    /**
     * Check whether the clear formatter really returns the empty string for some string
     */
    @Test
    public void formatEscapesUnderscoresIfPresent() throws Exception {
        Assertions.assertEquals("Lorem\\_ipsum", formatter.format("Lorem_ipsum"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("Text\\_with\\_underscores", formatter.format(formatter.getExampleInput()));
    }
}


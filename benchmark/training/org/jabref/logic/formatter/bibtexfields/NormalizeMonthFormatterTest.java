package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class NormalizeMonthFormatterTest {
    private NormalizeMonthFormatter formatter;

    @Test
    public void formatExample() {
        Assertions.assertEquals("#dec#", formatter.format(formatter.getExampleInput()));
    }
}


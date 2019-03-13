package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class SentenceCaseFormatterTest {
    private SentenceCaseFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("Upper first", formatter.format("upper First"));
        Assertions.assertEquals("Upper first", formatter.format("uPPER FIRST"));
        Assertions.assertEquals("Upper {NOT} first", formatter.format("upper {NOT} FIRST"));
        Assertions.assertEquals("Upper {N}ot first", formatter.format("upper {N}OT FIRST"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("I have {Aa} dream", formatter.format(formatter.getExampleInput()));
    }
}


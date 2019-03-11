package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class CapitalizeFormatterTest {
    private CapitalizeFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("Upper Each First", formatter.format("upper each First"));
        Assertions.assertEquals("Upper Each First {NOT} {this}", formatter.format("upper each first {NOT} {this}"));
        Assertions.assertEquals("Upper Each First {N}ot {t}his", formatter.format("upper each first {N}OT {t}his"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("I Have {a} Dream", formatter.format(formatter.getExampleInput()));
    }
}


package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class UnitsToLatexFormatterTest {
    private UnitsToLatexFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("1~{A}", formatter.format("1 A"));
        Assertions.assertEquals("1\\mbox{-}{mA}", formatter.format("1-mA"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("1~{Hz}", formatter.format(formatter.getExampleInput()));
    }
}


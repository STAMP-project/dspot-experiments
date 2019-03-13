package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class LatexCleanupFormatterTest {
    private LatexCleanupFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("$\\alpha\\beta$", formatter.format("$\\alpha$$\\beta$"));
        Assertions.assertEquals("{VLSI DSP}", formatter.format("{VLSI} {DSP}"));
        Assertions.assertEquals("\\textbf{VLSI} {DSP}", formatter.format("\\textbf{VLSI} {DSP}"));
        Assertions.assertEquals("A ${\\Delta\\Sigma}$ modulator for {FPGA DSP}", formatter.format("A ${\\Delta}$${\\Sigma}$ modulator for {FPGA} {DSP}"));
    }

    @Test
    public void preservePercentSign() {
        Assertions.assertEquals("\\%", formatter.format("%"));
    }

    @Test
    public void escapePercentSignOnlyOnce() {
        Assertions.assertEquals("\\%", formatter.format("\\%"));
    }

    @Test
    public void escapePercentSignOnlnyOnceWithNumber() {
        Assertions.assertEquals("50\\%", formatter.format("50\\%"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("{VLSI DSP}", formatter.format(formatter.getExampleInput()));
    }
}


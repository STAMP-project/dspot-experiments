package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class NormalizePagesFormatterTest {
    private NormalizePagesFormatter formatter;

    @Test
    public void formatSinglePageResultsInNoChange() {
        Assertions.assertEquals("1", formatter.format("1"));
    }

    @Test
    public void formatPageNumbers() {
        Assertions.assertEquals("1--2", formatter.format("1-2"));
    }

    @Test
    public void formatPageNumbersCommaSeparated() {
        Assertions.assertEquals("1,2,3", formatter.format("1,2,3"));
    }

    @Test
    public void formatPageNumbersPlusRange() {
        Assertions.assertEquals("43+", formatter.format("43+"));
    }

    @Test
    public void ignoreWhitespaceInPageNumbers() {
        Assertions.assertEquals("1--2", formatter.format("   1  - 2 "));
    }

    @Test
    public void removeWhitespaceSinglePage() {
        Assertions.assertEquals("1", formatter.format("   1  "));
    }

    @Test
    public void removeWhitespacePageRange() {
        Assertions.assertEquals("1--2", formatter.format("   1 -- 2  "));
    }

    @Test
    public void ignoreWhitespaceInPageNumbersWithDoubleDash() {
        Assertions.assertEquals("43--103", formatter.format("43 -- 103"));
    }

    @Test
    public void keepCorrectlyFormattedPageNumbers() {
        Assertions.assertEquals("1--2", formatter.format("1--2"));
    }

    @Test
    public void formatPageNumbersRemoveUnexpectedLiterals() {
        Assertions.assertEquals("1--2", formatter.format("{1}-{2}"));
    }

    @Test
    public void formatPageNumbersRegexNotMatching() {
        Assertions.assertEquals("12", formatter.format("12"));
    }

    @Test
    public void doNotRemoveLetters() {
        Assertions.assertEquals("R1-R50", formatter.format("R1-R50"));
    }

    @Test
    public void replaceLongDashWithDoubleDash() {
        Assertions.assertEquals("1--50", formatter.format("1 \u2014 50"));
    }

    @Test
    public void removePagePrefix() {
        Assertions.assertEquals("50", formatter.format("p.50"));
    }

    @Test
    public void removePagesPrefix() {
        Assertions.assertEquals("50", formatter.format("pp.50"));
    }

    @Test
    public void formatACMPages() {
        // This appears in https://doi.org/10.1145/1658373.1658375
        Assertions.assertEquals("2:1--2:33", formatter.format("2:1-2:33"));
    }

    @Test
    public void keepFormattedACMPages() {
        // This appears in https://doi.org/10.1145/1658373.1658375
        Assertions.assertEquals("2:1--2:33", formatter.format("2:1--2:33"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("1--2", formatter.format(formatter.getExampleInput()));
    }
}


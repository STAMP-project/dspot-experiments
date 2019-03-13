package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class NormalizeEnDashesFormatterTest {
    private NormalizeEnDashesFormatter formatter;

    @Test
    public void formatExample() {
        Assertions.assertEquals("Winery -- A Modeling Tool for TOSCA-based Cloud Applications", formatter.format(formatter.getExampleInput()));
    }

    @Test
    public void formatExampleOfChangelog() {
        Assertions.assertEquals("Example -- illustrative", formatter.format("Example - illustrative"));
    }

    @Test
    public void dashesWithinWordsAreKept() {
        Assertions.assertEquals("Example-illustrative", formatter.format("Example-illustrative"));
    }

    @Test
    public void dashesPreceededByASpaceAreKept() {
        Assertions.assertEquals("Example -illustrative", formatter.format("Example -illustrative"));
    }

    @Test
    public void dashesFollowedByASpaceAreKept() {
        Assertions.assertEquals("Example- illustrative", formatter.format("Example- illustrative"));
    }

    @Test
    public void dashAtTheBeginningIsKept() {
        Assertions.assertEquals("- illustrative", formatter.format("- illustrative"));
    }

    @Test
    public void dashAtTheEndIsKept() {
        Assertions.assertEquals("Example-", formatter.format("Example-"));
    }
}


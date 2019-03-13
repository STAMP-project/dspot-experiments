package org.jabref.logic.formatter.minifier;


import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class MinifyNameListFormatterTest {
    private MinifyNameListFormatter formatter;

    @Test
    public void minifyAuthorNames() {
        expectCorrect("Simon Harrer", "Simon Harrer");
        expectCorrect("Simon Harrer and others", "Simon Harrer and others");
        expectCorrect("Simon Harrer and J?rg Lenhard", "Simon Harrer and J?rg Lenhard");
        expectCorrect("Simon Harrer and J?rg Lenhard and Guido Wirtz", "Simon Harrer and others");
        expectCorrect("Simon Harrer and J?rg Lenhard and Guido Wirtz and others", "Simon Harrer and others");
    }

    @Test
    public void formatExample() {
        expectCorrect(formatter.getExampleInput(), "Stefan Kolb and others");
    }
}


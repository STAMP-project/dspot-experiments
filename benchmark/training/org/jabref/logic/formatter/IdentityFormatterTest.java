package org.jabref.logic.formatter;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class IdentityFormatterTest {
    private IdentityFormatter formatter;

    @Test
    public void formatExample() {
        Assertions.assertEquals("JabRef", formatter.format(formatter.getExampleInput()));
    }
}


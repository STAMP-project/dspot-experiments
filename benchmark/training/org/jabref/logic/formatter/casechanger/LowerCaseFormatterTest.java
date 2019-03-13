package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class LowerCaseFormatterTest {
    private LowerCaseFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("lower", formatter.format("LOWER"));
        Assertions.assertEquals("lower {UPPER}", formatter.format("LOWER {UPPER}"));
        Assertions.assertEquals("lower {U}pper", formatter.format("LOWER {U}PPER"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("kde {Amarok}", formatter.format(formatter.getExampleInput()));
    }
}


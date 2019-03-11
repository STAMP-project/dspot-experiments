package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class UpperCaseFormatterTest {
    private UpperCaseFormatter formatter;

    @Test
    public void test() {
        Assertions.assertEquals("LOWER", formatter.format("LOWER"));
        Assertions.assertEquals("UPPER", formatter.format("upper"));
        Assertions.assertEquals("UPPER", formatter.format("UPPER"));
        Assertions.assertEquals("UPPER {lower}", formatter.format("upper {lower}"));
        Assertions.assertEquals("UPPER {l}OWER", formatter.format("upper {l}ower"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("KDE {Amarok}", formatter.format(formatter.getExampleInput()));
    }
}


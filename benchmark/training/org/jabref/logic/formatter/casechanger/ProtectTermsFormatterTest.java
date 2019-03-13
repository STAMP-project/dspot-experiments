package org.jabref.logic.formatter.casechanger;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class ProtectTermsFormatterTest {
    private ProtectTermsFormatter formatter;

    @Test
    public void testSingleWord() {
        Assertions.assertEquals("{VLSI}", formatter.format("VLSI"));
    }

    @Test
    public void testDoNotProtectAlreadyProtected() {
        Assertions.assertEquals("{VLSI}", formatter.format("{VLSI}"));
    }

    @Test
    public void testCaseSensitivity() {
        Assertions.assertEquals("VLsI", formatter.format("VLsI"));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("In {CDMA}", formatter.format(formatter.getExampleInput()));
    }

    @Test
    public void testCorrectOrderingOfTerms() {
        Assertions.assertEquals("{3GPP} {3G}", formatter.format("3GPP 3G"));
    }

    @Test
    public void test() {
        Assertions.assertEquals("{VLSI} {VLSI}", formatter.format("VLSI {VLSI}"));
        Assertions.assertEquals("{BPEL}", formatter.format("{BPEL}"));
        Assertions.assertEquals("{Testing BPEL Engine Performance: A Survey}", formatter.format("{Testing BPEL Engine Performance: A Survey}"));
    }
}


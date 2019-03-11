package org.jabref.logic.integrity;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BracesCorrectorTest {
    @Test
    public void inputIsNull() {
        Assertions.assertNull(BracesCorrector.apply(null));
    }

    @Test
    public void inputIsEmpty() {
        Assertions.assertEquals("", BracesCorrector.apply(""));
    }

    @Test
    public void inputWithoutBraces() {
        Assertions.assertEquals("banana", BracesCorrector.apply("banana"));
    }

    @Test
    public void inputMissingClosing() {
        Assertions.assertEquals("{banana}", BracesCorrector.apply("{banana"));
    }

    @Test
    public void inputMissingOpening() {
        Assertions.assertEquals("{banana}", BracesCorrector.apply("banana}"));
    }

    @Test
    public void inputWithMaskedBraces() {
        Assertions.assertEquals("\\\\\\{banana", BracesCorrector.apply("\\\\\\{banana"));
    }

    @Test
    public void inputWithMixedBraces() {
        Assertions.assertEquals("{b{anana\\\\\\}}}", BracesCorrector.apply("{b{anana\\\\\\}"));
    }
}


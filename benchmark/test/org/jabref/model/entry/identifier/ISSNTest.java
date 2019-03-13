package org.jabref.model.entry.identifier;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ISSNTest {
    @Test
    public void testIsCanBeCleaned() {
        Assertions.assertTrue(new ISSN("00279633").isCanBeCleaned());
    }

    @Test
    public void testIsCanBeCleanedIncorrectRubbish() {
        Assertions.assertFalse(new ISSN("A brown fox").isCanBeCleaned());
    }

    @Test
    public void testIsCanBeCleanedDashAlreadyThere() {
        Assertions.assertFalse(new ISSN("0027-9633").isCanBeCleaned());
    }

    @Test
    public void testGetCleanedISSN() {
        Assertions.assertEquals("0027-9633", new ISSN("00279633").getCleanedISSN());
    }

    @Test
    public void testGetCleanedISSNDashAlreadyThere() {
        Assertions.assertEquals("0027-9633", new ISSN("0027-9633").getCleanedISSN());
    }

    @Test
    public void testGetCleanedISSNDashRubbish() {
        Assertions.assertEquals("A brown fox", new ISSN("A brown fox").getCleanedISSN());
    }

    @Test
    public void testIsValidChecksumCorrect() {
        Assertions.assertTrue(new ISSN("0027-9633").isValidChecksum());
        Assertions.assertTrue(new ISSN("2434-561X").isValidChecksum());
        Assertions.assertTrue(new ISSN("2434-561x").isValidChecksum());
    }

    @Test
    public void testIsValidChecksumIncorrect() {
        Assertions.assertFalse(new ISSN("0027-9634").isValidChecksum());
    }

    @Test
    public void testIsValidFormatCorrect() {
        Assertions.assertTrue(new ISSN("0027-963X").isValidFormat());
    }

    @Test
    public void testIsValidFormatIncorrect() {
        Assertions.assertFalse(new ISSN("00279634").isValidFormat());
    }
}


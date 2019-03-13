package org.jabref.logic.journals;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AbbreviationTest {
    @Test
    public void testAbbreviationsWithTrailingSpaces() {
        Abbreviation abbreviation = new Abbreviation(" Long Name ", " L. N. ");
        Assertions.assertEquals("Long Name", abbreviation.getName());
        Assertions.assertEquals("L. N.", abbreviation.getIsoAbbreviation());
        Assertions.assertEquals("L N", abbreviation.getMedlineAbbreviation());
    }

    @Test
    public void testAbbreviationsWithUnusedElements() {
        Abbreviation abbreviation = new Abbreviation(" Long Name ", " L. N.;LN;M");
        Assertions.assertEquals("Long Name", abbreviation.getName());
        Assertions.assertEquals("L. N.", abbreviation.getIsoAbbreviation());
        Assertions.assertEquals("L N", abbreviation.getMedlineAbbreviation());
    }

    @Test
    public void testGetNextElement() {
        Abbreviation abbreviation = new Abbreviation(" Long Name ", " L. N.;LN;M");
        Assertions.assertEquals("L. N.", abbreviation.getNext("Long Name"));
        Assertions.assertEquals("L N", abbreviation.getNext("L. N."));
        Assertions.assertEquals("Long Name", abbreviation.getNext("L N"));
    }

    @Test
    public void testGetNextElementWithTrailingSpaces() {
        Abbreviation abbreviation = new Abbreviation(" Long Name ", " L. N.; LN ;M ");
        Assertions.assertEquals("L. N.", abbreviation.getNext(" Long Name "));
        Assertions.assertEquals("L N", abbreviation.getNext(" L. N. "));
        Assertions.assertEquals("Long Name", abbreviation.getNext(" L N "));
    }

    @Test
    public void testIsoAndMedlineAbbreviationsAreSame() {
        Abbreviation abbreviation = new Abbreviation(" Long Name ", " L N ");
        Assertions.assertEquals(abbreviation.getIsoAbbreviation(), abbreviation.getMedlineAbbreviation());
    }
}


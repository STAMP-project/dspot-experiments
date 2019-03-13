package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReplaceUnicodeLigaturesFormatterTest {
    private ReplaceUnicodeLigaturesFormatter formatter;

    @Test
    public void testPlainFormat() {
        Assertions.assertEquals("lorem ipsum", formatter.format("lorem ipsum"));
    }

    @Test
    public void testSingleLigatures() {
        Assertions.assertEquals("AA", formatter.format("\ua732"));
        Assertions.assertEquals("fi", formatter.format("?"));
        Assertions.assertEquals("et", formatter.format("\ud83d\ude70"));
    }

    @Test
    public void testLigatureSequence() {
        Assertions.assertEquals("aefffflstue", formatter.format("?????"));
    }

    @Test
    public void testSampleInput() {
        Assertions.assertEquals("AEneas", formatter.format("?neas"));
    }
}


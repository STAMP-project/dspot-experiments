package org.jabref.logic.layout.format;


import org.jabref.logic.util.OS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RisKeywordsTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals("", new RisKeywords().format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertEquals("", new RisKeywords().format(null));
    }

    @Test
    public void testSingleKeyword() {
        Assertions.assertEquals("KW  - abcd", new RisKeywords().format("abcd"));
    }

    @Test
    public void testTwoKeywords() {
        Assertions.assertEquals((("KW  - abcd" + (OS.NEWLINE)) + "KW  - efg"), new RisKeywords().format("abcd, efg"));
    }

    @Test
    public void testMultipleKeywords() {
        Assertions.assertEquals((((((("KW  - abcd" + (OS.NEWLINE)) + "KW  - efg") + (OS.NEWLINE)) + "KW  - hij") + (OS.NEWLINE)) + "KW  - klm"), new RisKeywords().format("abcd, efg, hij, klm"));
    }
}


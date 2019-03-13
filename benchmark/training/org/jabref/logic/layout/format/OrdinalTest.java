package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class OrdinalTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals("", new Ordinal().format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertNull(new Ordinal().format(null));
    }

    @Test
    public void testSingleDigit() {
        Assertions.assertEquals("1st", new Ordinal().format("1"));
        Assertions.assertEquals("2nd", new Ordinal().format("2"));
        Assertions.assertEquals("3rd", new Ordinal().format("3"));
        Assertions.assertEquals("4th", new Ordinal().format("4"));
    }

    @Test
    public void testMultiDigits() {
        Assertions.assertEquals("11th", new Ordinal().format("11"));
        Assertions.assertEquals("111th", new Ordinal().format("111"));
        Assertions.assertEquals("21st", new Ordinal().format("21"));
    }

    @Test
    public void testAlreadyOrdinals() {
        Assertions.assertEquals("1st", new Ordinal().format("1st"));
        Assertions.assertEquals("111th", new Ordinal().format("111th"));
        Assertions.assertEquals("22nd", new Ordinal().format("22nd"));
    }

    @Test
    public void testFullSentence() {
        Assertions.assertEquals("1st edn.", new Ordinal().format("1 edn."));
        Assertions.assertEquals("1st edition", new Ordinal().format("1st edition"));
        Assertions.assertEquals("The 2nd conference on 3rd.14th", new Ordinal().format("The 2 conference on 3.14"));
    }

    @Test
    public void testLetters() {
        Assertions.assertEquals("abCD eFg", new Ordinal().format("abCD eFg"));
    }
}


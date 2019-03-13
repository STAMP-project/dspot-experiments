package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ToUpperCaseTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals("", new ToUpperCase().format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertNull(new ToUpperCase().format(null));
    }

    @Test
    public void testLowerCase() {
        Assertions.assertEquals("ABCD EFG", new ToUpperCase().format("abcd efg"));
    }

    @Test
    public void testUpperCase() {
        Assertions.assertEquals("ABCD EFG", new ToUpperCase().format("ABCD EFG"));
    }

    @Test
    public void testMixedCase() {
        Assertions.assertEquals("ABCD EFG", new ToUpperCase().format("abCD eFg"));
    }
}


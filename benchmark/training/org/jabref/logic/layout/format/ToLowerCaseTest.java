package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ToLowerCaseTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals("", new ToLowerCase().format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertNull(new ToLowerCase().format(null));
    }

    @Test
    public void testLowerCase() {
        Assertions.assertEquals("abcd efg", new ToLowerCase().format("abcd efg"));
    }

    @Test
    public void testUpperCase() {
        Assertions.assertEquals("abcd efg", new ToLowerCase().format("ABCD EFG"));
    }

    @Test
    public void testMixedCase() {
        Assertions.assertEquals("abcd efg", new ToLowerCase().format("abCD eFg"));
    }
}


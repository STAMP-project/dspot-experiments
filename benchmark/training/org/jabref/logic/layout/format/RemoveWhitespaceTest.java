package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemoveWhitespaceTest {
    @Test
    public void testEmptyExpectEmpty() {
        Assertions.assertEquals("", new RemoveWhitespace().format(""));
    }

    @Test
    public void testNullExpectNull() {
        Assertions.assertNull(new RemoveWhitespace().format(null));
    }

    @Test
    public void testNormal() {
        Assertions.assertEquals("abcd EFG", new RemoveWhitespace().format("abcd EFG"));
    }

    @Test
    public void testTab() {
        Assertions.assertEquals("abcd EFG", new RemoveWhitespace().format("abcd\t EFG"));
    }

    @Test
    public void testNewLineCombo() {
        Assertions.assertEquals("abcd EFG", new RemoveWhitespace().format("abcd\r E\nFG\r\n"));
    }
}


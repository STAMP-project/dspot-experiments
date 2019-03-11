package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorAndsReplacerTest {
    /**
     * Test method for {@link org.jabref.logic.layout.format.AuthorAndsReplacer#format(java.lang.String)}.
     */
    @Test
    public void testFormat() {
        LayoutFormatter a = new AuthorAndsReplacer();
        // Empty case
        Assertions.assertEquals("", a.format(""));
        // Single Names don't change
        Assertions.assertEquals("Someone, Van Something", a.format("Someone, Van Something"));
        // Two names just an &
        Assertions.assertEquals("John Smith & Black Brown, Peter", a.format("John Smith and Black Brown, Peter"));
        // Three names put a comma:
        Assertions.assertEquals("von Neumann, John; Smith, John & Black Brown, Peter", a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
        Assertions.assertEquals("John von Neumann; John Smith & Peter Black Brown", a.format("John von Neumann and John Smith and Peter Black Brown"));
    }
}


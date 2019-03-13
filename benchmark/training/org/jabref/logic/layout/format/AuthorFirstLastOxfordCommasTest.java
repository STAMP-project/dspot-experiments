package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorFirstLastOxfordCommasTest {
    /**
     * Test method for {@link org.jabref.logic.layout.format.AuthorFirstLastOxfordCommas#format(java.lang.String)}.
     */
    @Test
    public void testFormat() {
        LayoutFormatter a = new AuthorFirstLastOxfordCommas();
        // Empty case
        Assertions.assertEquals("", a.format(""));
        // Single Names
        Assertions.assertEquals("Van Something Someone", a.format("Someone, Van Something"));
        // Two names
        Assertions.assertEquals("John von Neumann and Peter Black Brown", a.format("John von Neumann and Peter Black Brown"));
        // Three names
        Assertions.assertEquals("John von Neumann, John Smith, and Peter Black Brown", a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
        Assertions.assertEquals("John von Neumann, John Smith, and Peter Black Brown", a.format("John von Neumann and John Smith and Black Brown, Peter"));
    }
}


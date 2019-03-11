package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorLastFirstTest {
    @Test
    public void testFormat() {
        LayoutFormatter a = new AuthorLastFirst();
        // Empty case
        Assertions.assertEquals("", a.format(""));
        // Single Names
        Assertions.assertEquals("Someone, Van Something", a.format("Van Something Someone"));
        // Two names
        Assertions.assertEquals("von Neumann, John and Black Brown, Peter", a.format("John von Neumann and Black Brown, Peter"));
        // Three names
        Assertions.assertEquals("von Neumann, John and Smith, John and Black Brown, Peter", a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
        Assertions.assertEquals("von Neumann, John and Smith, John and Black Brown, Peter", a.format("John von Neumann and John Smith and Black Brown, Peter"));
    }
}


package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorLastFirstAbbrCommasTest {
    /**
     * Test method for {@link org.jabref.logic.layout.format.AuthorLastFirstAbbrCommas#format(java.lang.String)}.
     */
    @Test
    public void testFormat() {
        LayoutFormatter a = new AuthorLastFirstAbbrCommas();
        // Empty case
        Assertions.assertEquals("", a.format(""));
        // Single Names
        Assertions.assertEquals("Someone, V. S.", a.format("Van Something Someone"));
        // Two names
        Assertions.assertEquals("von Neumann, J. and Black Brown, P.", a.format("John von Neumann and Black Brown, Peter"));
        // Three names
        Assertions.assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", a.format("von Neumann, John and Smith, John and Black Brown, Peter"));
        Assertions.assertEquals("von Neumann, J., Smith, J. and Black Brown, P.", a.format("John von Neumann and John Smith and Black Brown, Peter"));
    }
}


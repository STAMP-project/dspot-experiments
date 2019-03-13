package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NoSpaceBetweenAbbreviationsTest {
    @Test
    public void testFormat() {
        LayoutFormatter f = new NoSpaceBetweenAbbreviations();
        Assertions.assertEquals("", f.format(""));
        Assertions.assertEquals("John Meier", f.format("John Meier"));
        Assertions.assertEquals("J.F. Kennedy", f.format("J. F. Kennedy"));
        Assertions.assertEquals("J.R.R. Tolkien", f.format("J. R. R. Tolkien"));
        Assertions.assertEquals("J.R.R. Tolkien and J.F. Kennedy", f.format("J. R. R. Tolkien and J. F. Kennedy"));
    }
}


package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemoveTildeTest {
    private LayoutFormatter formatter;

    @Test
    public void testFormatString() {
        Assertions.assertEquals("", formatter.format(""));
        Assertions.assertEquals("simple", formatter.format("simple"));
        Assertions.assertEquals(" ", formatter.format("~"));
        Assertions.assertEquals("   ", formatter.format("~~~"));
        Assertions.assertEquals(" \\~ ", formatter.format("~\\~~"));
        Assertions.assertEquals("\\\\ ", formatter.format("\\\\~"));
        Assertions.assertEquals("Doe Joe and Jane, M. and Kamp, J. A.", formatter.format("Doe Joe and Jane, M. and Kamp, J.~A."));
        Assertions.assertEquals("T\\~olkien, J. R. R.", formatter.format("T\\~olkien, J.~R.~R."));
    }
}


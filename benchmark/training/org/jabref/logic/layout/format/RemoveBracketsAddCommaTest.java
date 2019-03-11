package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RemoveBracketsAddCommaTest {
    private LayoutFormatter formatter;

    @Test
    public void testFormat() throws Exception {
        Assertions.assertEquals("some text,", formatter.format("{some text}"));
        Assertions.assertEquals("some text", formatter.format("{some text"));
        Assertions.assertEquals("some text,", formatter.format("some text}"));
    }
}


package org.jabref.logic.layout.format;


import org.jabref.logic.layout.ParamLayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DateFormatterTest {
    private ParamLayoutFormatter formatter;

    @Test
    public void testDefaultFormat() {
        Assertions.assertEquals("2016-07-15", formatter.format("2016-07-15"));
    }

    @Test
    public void testRequestedFormat() {
        formatter.setArgument("MM/yyyy");
        Assertions.assertEquals("07/2016", formatter.format("2016-07-15"));
    }
}


package org.jabref.logic.layout.format;


import org.jabref.logic.layout.ParamLayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IfPluralTest {
    @Test
    public void testStandardUsageOneEditor() {
        ParamLayoutFormatter a = new IfPlural();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals("Ed.", a.format("Bob Bruce"));
    }

    @Test
    public void testStandardUsageTwoEditors() {
        ParamLayoutFormatter a = new IfPlural();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals("Eds.", a.format("Bob Bruce and Jolly Jumper"));
    }

    @Test
    public void testFormatNull() {
        ParamLayoutFormatter a = new IfPlural();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals("", a.format(null));
    }

    @Test
    public void testFormatEmpty() {
        ParamLayoutFormatter a = new IfPlural();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals("", a.format(""));
    }

    @Test
    public void testNoArgumentSet() {
        ParamLayoutFormatter a = new IfPlural();
        Assertions.assertEquals("", a.format("Bob Bruce and Jolly Jumper"));
    }

    @Test
    public void testNoProperArgument() {
        ParamLayoutFormatter a = new IfPlural();
        a.setArgument("Eds.");
        Assertions.assertEquals("", a.format("Bob Bruce and Jolly Jumper"));
    }
}


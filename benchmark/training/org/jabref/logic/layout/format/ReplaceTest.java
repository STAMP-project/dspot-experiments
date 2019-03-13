package org.jabref.logic.layout.format;


import org.jabref.logic.layout.ParamLayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReplaceTest {
    @Test
    public void testSimpleText() {
        ParamLayoutFormatter a = new Replace();
        a.setArgument("Bob,Ben");
        Assertions.assertEquals("Ben Bruce", a.format("Bob Bruce"));
    }

    @Test
    public void testSimpleTextNoHit() {
        ParamLayoutFormatter a = new Replace();
        a.setArgument("Bob,Ben");
        Assertions.assertEquals("Jolly Jumper", a.format("Jolly Jumper"));
    }

    @Test
    public void testFormatNull() {
        ParamLayoutFormatter a = new Replace();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals(null, a.format(null));
    }

    @Test
    public void testFormatEmpty() {
        ParamLayoutFormatter a = new Replace();
        a.setArgument("Eds.,Ed.");
        Assertions.assertEquals("", a.format(""));
    }

    @Test
    public void testNoArgumentSet() {
        ParamLayoutFormatter a = new Replace();
        Assertions.assertEquals("Bob Bruce and Jolly Jumper", a.format("Bob Bruce and Jolly Jumper"));
    }

    @Test
    public void testNoProperArgument() {
        ParamLayoutFormatter a = new Replace();
        a.setArgument("Eds.");
        Assertions.assertEquals("Bob Bruce and Jolly Jumper", a.format("Bob Bruce and Jolly Jumper"));
    }
}


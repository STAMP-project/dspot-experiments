package de.westnordost.streetcomplete.data.osm.tql;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class StringWithCursorTest {
    @Test
    public void advance() {
        StringWithCursor x = new StringWithCursor("ab", Locale.US);
        Assert.assertEquals(0, x.getCursorPos());
        Assert.assertEquals('a', x.advance());
        Assert.assertEquals(1, x.getCursorPos());
        Assert.assertEquals('b', x.advance());
        Assert.assertEquals(2, x.getCursorPos());
        try {
            x.advance();
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void advanceBy() {
        StringWithCursor x = new StringWithCursor("wundertuete", Locale.US);
        Assert.assertEquals("wunder", x.advanceBy(6));
        Assert.assertEquals("", x.advanceBy(0));
        try {
            x.advanceBy((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
        Assert.assertEquals("tuete", x.advanceBy(99999));
    }

    @Test
    public void nextIsAndAdvance() {
        StringWithCursor x = new StringWithCursor("test123", Locale.US);
        Assert.assertTrue(x.nextIsAndAdvance("te"));
        Assert.assertEquals(2, x.getCursorPos());
        Assert.assertFalse(x.nextIsAndAdvance("te"));
        x.advanceBy(3);
        Assert.assertTrue(x.nextIsAndAdvance("23"));
        Assert.assertEquals(7, x.getCursorPos());
        Assert.assertTrue(x.isAtEnd());
    }

    @Test
    public void nextIsAndAdvanceChar() {
        StringWithCursor x = new StringWithCursor("test123", Locale.US);
        Assert.assertTrue(x.nextIsAndAdvance('t'));
        Assert.assertEquals(1, x.getCursorPos());
        Assert.assertFalse(x.nextIsAndAdvance('t'));
        x.advanceBy(3);
        Assert.assertTrue(x.nextIsAndAdvance('1'));
        Assert.assertEquals(5, x.getCursorPos());
    }

    @Test
    public void nextIsAndAdvanceIgnoreCase() {
        StringWithCursor x = new StringWithCursor("test123", Locale.US);
        Assert.assertTrue(x.nextIsAndAdvanceIgnoreCase("TE"));
        Assert.assertTrue(x.nextIsAndAdvanceIgnoreCase("st"));
    }

    @Test
    public void findNext() {
        StringWithCursor x = new StringWithCursor("abc abc", Locale.US);
        Assert.assertEquals("abc abc".length(), x.findNext("wurst"));
        Assert.assertEquals(0, x.findNext("abc"));
        x.advance();
        Assert.assertEquals(3, x.findNext("abc"));
    }

    @Test
    public void findNextChar() {
        StringWithCursor x = new StringWithCursor("abc abc", Locale.US);
        Assert.assertEquals("abc abc".length(), x.findNext('x'));
        Assert.assertEquals(0, x.findNext('a'));
        x.advance();
        Assert.assertEquals(3, x.findNext('a'));
    }

    @Test
    public void nextIsChar() {
        StringWithCursor x = new StringWithCursor("abc", Locale.US);
        Assert.assertTrue(x.nextIs('a'));
        Assert.assertFalse(x.nextIs('b'));
        x.advance();
        Assert.assertTrue(x.nextIs('b'));
        x.advance();
        Assert.assertTrue(x.nextIs('c'));
        x.advance();
        Assert.assertFalse(x.nextIs('c'));
    }

    @Test
    public void nextIsString() {
        StringWithCursor x = new StringWithCursor("abc", Locale.US);
        Assert.assertTrue(x.nextIs("abc"));
        Assert.assertTrue(x.nextIs("ab"));
        Assert.assertFalse(x.nextIs("bc"));
        x.advance();
        Assert.assertTrue(x.nextIs("bc"));
        x.advance();
        x.advance();
        Assert.assertFalse(x.nextIs("c"));
    }

    @Test
    public void nextIsStringIgnoreCase() {
        StringWithCursor x = new StringWithCursor("abc", Locale.US);
        Assert.assertTrue(x.nextIsIgnoreCase("A"));
        Assert.assertTrue(x.nextIsIgnoreCase("a"));
    }
}


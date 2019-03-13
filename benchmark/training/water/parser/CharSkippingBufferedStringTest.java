package water.parser;


import org.junit.Assert;
import org.junit.Test;


public class CharSkippingBufferedStringTest {
    private CharSkippingBufferedString charSkippingBufferedString;

    @Test
    public void toBufferedString() {
        final byte[] bytes = "abcd".getBytes();
        charSkippingBufferedString.set(bytes, 0, ((bytes.length) - 1));
        charSkippingBufferedString.skipIndex(((bytes.length) - 1));
        Assert.assertNotNull(charSkippingBufferedString.getBuffer());
        final BufferedString bufferedString = charSkippingBufferedString.toBufferedString();
        Assert.assertNotNull(bufferedString.getBuffer());
        Assert.assertEquals(3, bufferedString.length());
        Assert.assertEquals(0, bufferedString.getOffset());
        Assert.assertEquals("abc", bufferedString.toString());
    }

    @Test
    public void toBufferedString_nonZeroOffset() {
        final byte[] bytes = "abcdefgh".getBytes();
        charSkippingBufferedString.set(bytes, 4, 0);
        charSkippingBufferedString.skipIndex(4);
        charSkippingBufferedString.addChar();
        charSkippingBufferedString.addChar();
        charSkippingBufferedString.addChar();
        Assert.assertNotNull(charSkippingBufferedString.getBuffer());
        final BufferedString bufferedString = charSkippingBufferedString.toBufferedString();
        Assert.assertNotNull(bufferedString.getBuffer());
        Assert.assertEquals(3, bufferedString.length());
        Assert.assertEquals(0, bufferedString.getOffset());
        Assert.assertEquals("fgh", bufferedString.toString());
    }

    @Test
    public void toBufferedString_skipFirst() {
        final byte[] bytes = "efgh".getBytes();
        charSkippingBufferedString.set(bytes, 0, 0);
        charSkippingBufferedString.skipIndex(0);
        charSkippingBufferedString.addChar();
        charSkippingBufferedString.addChar();
        charSkippingBufferedString.addChar();
        Assert.assertNotNull(charSkippingBufferedString.getBuffer());
        final BufferedString bufferedString = charSkippingBufferedString.toBufferedString();
        Assert.assertNotNull(bufferedString.getBuffer());
        Assert.assertEquals(3, bufferedString.length());
        Assert.assertEquals(0, bufferedString.getOffset());
        Assert.assertEquals("fgh", bufferedString.toString());
    }

    @Test
    public void removeChar() {
        final byte[] bytes = "abcd".getBytes();
        charSkippingBufferedString.set(bytes, 0, bytes.length);
        charSkippingBufferedString.removeChar();
        Assert.assertEquals("abc", charSkippingBufferedString.toString());
    }

    @Test
    public void addChar() {
        final byte[] bytes = "abcd".getBytes();
        charSkippingBufferedString.set(bytes, 0, 0);
        charSkippingBufferedString.addChar();
        Assert.assertEquals("a", charSkippingBufferedString.toString());
    }

    @Test
    public void emptyStringBehavior() {
        final byte[] bytes = "".getBytes();
        charSkippingBufferedString.set(bytes, 0, 0);
        final String string = charSkippingBufferedString.toString();
        Assert.assertNotNull(string);
        Assert.assertTrue(string.isEmpty());
    }

    @Test
    public void testToString() {
        final byte[] bytes = "abcd".getBytes();
        charSkippingBufferedString.set(bytes, 0, 0);
        Assert.assertEquals(charSkippingBufferedString.toString(), charSkippingBufferedString.toBufferedString().toString());
    }
}


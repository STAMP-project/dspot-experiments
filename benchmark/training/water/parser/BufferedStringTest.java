package water.parser;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import water.AutoBuffer;


public class BufferedStringTest {
    @Test
    public void testWrite_impl() throws Exception {
        final String source = "this is not a string";
        BufferedString sut = new BufferedString(source);
        Assert.assertArrayEquals(source.getBytes(), sut.getBuffer());
        AutoBuffer ab = new AutoBuffer();
        sut.write_impl(ab);
        final byte[] expected = ("\u0015" + source).getBytes();
        final byte[] actual = ab.buf();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testRead_impl() throws Exception {
        final String source = "this is not a string";
        BufferedString sut1 = new BufferedString(source);
        AutoBuffer ab = new AutoBuffer();
        sut1.write_impl(ab);
        ab.flipForReading();
        BufferedString sut2 = new BufferedString();
        sut2.read_impl(ab);
        Assert.assertEquals(sut1, sut2);
    }

    @Test
    public void testCompareTo() throws Exception {
        final String source = "this is not a string";
        BufferedString sut1 = new BufferedString(source);
        Assert.assertEquals(0, sut1.compareTo(new BufferedString(source)));
        Assert.assertEquals(2, sut1.compareTo(new BufferedString("this is not a stri")));
    }

    @Test
    public void testAddChar() throws Exception {
        final String source = "abc";
        BufferedString sut1 = new BufferedString(source.getBytes(), 0, 2);
        Assert.assertEquals(2, sut1.length());
        sut1.addChar();
        Assert.assertEquals(3, sut1.length());
        String actual = sut1.toString();
        Assert.assertEquals(source, actual);
        byte[] bytes = sut1.getBuffer();
        Assert.assertArrayEquals(source.getBytes(), bytes);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEquals() throws Exception {
        BufferedString sut = new BufferedString("abc");
        Assert.assertEquals(sut, sut);
        Assert.assertEquals(sut, new BufferedString("abc"));
        Assert.assertFalse(sut.equals("abc"));
        Assert.assertFalse(sut.equals(new BufferedString("abcd")));
        Assert.assertFalse(sut.equals(new BufferedString("ABCD")));
        Assert.assertFalse(sut.equals(new BufferedString(" abc")));
        Assert.assertFalse(sut.equals(new BufferedString("abc ")));
        Assert.assertFalse(sut.equals(new BufferedString("abc\u0000")));
        Assert.assertFalse(sut.equals(new BufferedString("ab")));
        Assert.assertFalse(sut.equals(new BufferedString("")));
        Assert.assertFalse(new BufferedString("").equals(sut));
    }

    @Test
    public void testEqualsAsciiString() throws Exception {
        BufferedString sut1 = new BufferedString("abc");
        Assert.assertFalse(sut1.equalsAsciiString(null));
        Assert.assertTrue(sut1.equalsAsciiString("abc"));
        Assert.assertFalse(sut1.equalsAsciiString("ab"));
        Assert.assertFalse(sut1.equalsAsciiString("abd"));
        Assert.assertFalse(sut1.equalsAsciiString("abcd"));
        Assert.assertFalse(sut1.equalsAsciiString("abC"));
        Assert.assertFalse(sut1.equalsAsciiString("ab\u0441"));// this is Russian 'c' here

        Assert.assertFalse(sut1.equalsAsciiString("ab"));
        BufferedString sut2 = new BufferedString("");
        Assert.assertTrue(sut2.equalsAsciiString(""));
        Assert.assertFalse(sut1.equalsAsciiString(null));
        Assert.assertFalse(sut2.equalsAsciiString("a"));
        BufferedString sut3 = new BufferedString("a\u0100b");
        Assert.assertFalse(sut3.equalsAsciiString("a\u0100b"));
    }

    @Test
    public void testGetBuffer() throws Exception {
        final String source = "not a string\u00f0";
        BufferedString sut = new BufferedString(source);
        final byte[] expected = source.getBytes("UTF8");
        final byte[] actual = sut.getBuffer();
        Assert.assertArrayEquals(((("Failed. expected " + (Arrays.toString(expected))) + ", got ") + (Arrays.toString(actual))), expected, actual);
    }
}


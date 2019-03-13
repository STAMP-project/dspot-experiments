package com.twitter.elephantbird.mapreduce.input;


import com.twitter.elephantbird.mapreduce.io.DecodeException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class TestBase64Codec {
    private static final String TEST_STRING = "The quick brown fox jumps over the lazy dog.";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final byte[] PLAIN = TestBase64Codec.TEST_STRING.getBytes(TestBase64Codec.UTF8);

    private static final byte[] ENCODED = Base64Codec.encodeToByte(TestBase64Codec.PLAIN, false);

    @Test
    public void testDecode() throws DecodeException {
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decode(TestBase64Codec.ENCODED));
    }

    @Test
    public void testDecodeIllegal() throws DecodeException {
        byte[] illegal = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(TestBase64Codec.ENCODED, TestBase64Codec.concat(illegal, TestBase64Codec.ENCODED));
        // illegal characters in the middle are not ignored
        Assert.assertFalse(((TestBase64Codec.concat(TestBase64Codec.PLAIN, TestBase64Codec.PLAIN).length) == (Base64Codec.decode(merged).length)));
    }

    @Test
    public void testDecodeIllegalLeading() throws DecodeException {
        byte[] leading = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(leading, TestBase64Codec.ENCODED);
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decode(merged));
    }

    @Test
    public void testDecodeIllegalTrailing() throws DecodeException {
        byte[] trailing = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(TestBase64Codec.ENCODED, trailing);
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decode(merged));
    }

    @Test(expected = DecodeException.class)
    public void testDecodeInvalidLength() throws DecodeException {
        Base64Codec.decode(TestBase64Codec.ENCODED, 0, ((TestBase64Codec.ENCODED.length) + 1));// incorrect length

    }

    // tests for the fast decode version:
    @Test
    public void testDecodeFast() throws DecodeException {
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decodeFast(TestBase64Codec.ENCODED, TestBase64Codec.ENCODED.length));
    }

    @Test
    public void testDecodeFastIllegal() throws DecodeException {
        byte[] illegal = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(TestBase64Codec.ENCODED, TestBase64Codec.concat(illegal, TestBase64Codec.ENCODED));
        // illegal characters in the middle are not ignored
        Assert.assertFalse(((TestBase64Codec.concat(TestBase64Codec.PLAIN, TestBase64Codec.PLAIN).length) == (Base64Codec.decode(merged).length)));
    }

    @Test
    public void testDecodeFastIllegalLeading() throws DecodeException {
        byte[] leading = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(leading, TestBase64Codec.ENCODED);
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decodeFast(merged, merged.length));
    }

    @Test
    public void testDecodeFastIllegalTrailing() throws DecodeException {
        byte[] trailing = "%$%".getBytes(TestBase64Codec.UTF8);
        byte[] merged = TestBase64Codec.concat(TestBase64Codec.ENCODED, trailing);
        Assert.assertArrayEquals(TestBase64Codec.PLAIN, Base64Codec.decodeFast(merged, merged.length));
    }

    @Test
    public void testDecodeFastZeroLength() throws DecodeException {
        Assert.assertEquals(0, Base64Codec.decodeFast(new byte[0], 0).length);
    }

    @Test(expected = DecodeException.class)
    public void testDecodeFastNullWithNonZeroLength() throws DecodeException {
        Base64Codec.decodeFast(null, 100);
    }

    @Test(expected = DecodeException.class)
    public void testDecodeFastInvalidLength() throws DecodeException {
        Base64Codec.decodeFast(TestBase64Codec.ENCODED, ((TestBase64Codec.ENCODED.length) + 1));// incorrect length

    }
}


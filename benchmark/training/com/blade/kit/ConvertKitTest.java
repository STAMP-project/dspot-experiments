package com.blade.kit;


import MemoryConst.GB;
import MemoryConst.MB;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/20
 */
public class ConvertKitTest {
    private byte[] mBytes = new byte[]{ 0, 8, ((byte) (219)), 51, 69, ((byte) (171)), 2, 35 };

    private String hexString = "0008DB3345AB0223";

    private char[] mChars1 = new char[]{ '0', '1', '2' };

    private byte[] mBytes1 = new byte[]{ 48, 49, 50 };

    @Test
    public void bytes2HexString() throws Exception {
        Assert.assertEquals(hexString, ConvertKit.bytes2HexString(mBytes));
    }

    @Test
    public void hexString2Bytes() throws Exception {
        TestCase.assertTrue(Arrays.equals(mBytes, ConvertKit.hexString2Bytes(hexString)));
    }

    @Test
    public void chars2Bytes() throws Exception {
        TestCase.assertTrue(Arrays.equals(mBytes1, ConvertKit.chars2Bytes(mChars1)));
    }

    @Test
    public void bytes2Chars() throws Exception {
        TestCase.assertTrue(Arrays.equals(mChars1, ConvertKit.bytes2Chars(mBytes1)));
    }

    @Test
    public void byte2MemorySize() throws Exception {
        Assert.assertEquals(1024, ConvertKit.byte2MemorySize(GB, MB), 0.001);
    }

    @Test
    public void byte2FitMemorySize() throws Exception {
        Assert.assertEquals("3.098MB", ConvertKit.byte2FitMemorySize((((1024 * 1024) * 3) + (1024 * 100))));
    }

    @Test
    public void bytes2Bits_bits2Bytes() throws Exception {
        Assert.assertEquals("0111111111111010", ConvertKit.bytes2Bits(new byte[]{ 127, ((byte) (250)) }));
        Assert.assertEquals("0111111111111010", ConvertKit.bytes2Bits(ConvertKit.bits2Bytes("111111111111010")));
    }

    @Test
    public void inputStream2Bytes_bytes2InputStream() throws Exception {
        String string = "this is test string";
        TestCase.assertTrue(Arrays.equals(string.getBytes("UTF-8"), ConvertKit.inputStream2Bytes(ConvertKit.bytes2InputStream(string.getBytes("UTF-8")))));
    }

    @Test
    public void inputStream2String_string2InputStream() throws Exception {
        String string = "this is test string";
        Assert.assertEquals(string, ConvertKit.inputStream2String(ConvertKit.string2InputStream(string, "UTF-8"), "UTF-8"));
    }
}


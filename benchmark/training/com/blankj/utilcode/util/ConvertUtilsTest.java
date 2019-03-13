package com.blankj.utilcode.util;


import MemoryConstants.GB;
import MemoryConstants.MB;
import com.blankj.utilcode.constant.TimeConstants;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/13
 *     desc  : test ConvertUtils
 * </pre>
 */
public class ConvertUtilsTest extends BaseTest {
    private byte[] mBytes = new byte[]{ 0, 8, ((byte) (219)), 51, 69, ((byte) (171)), 2, 35 };

    private String hexString = "0008DB3345AB0223";

    private char[] mChars1 = new char[]{ '0', '1', '2' };

    private byte[] mBytes1 = new byte[]{ 48, 49, 50 };

    @Test
    public void bytes2HexString() {
        Assert.assertEquals(hexString, ConvertUtils.bytes2HexString(mBytes));
    }

    @Test
    public void hexString2Bytes() {
        Assert.assertTrue(Arrays.equals(mBytes, ConvertUtils.hexString2Bytes(hexString)));
    }

    @Test
    public void chars2Bytes() {
        Assert.assertTrue(Arrays.equals(mBytes1, ConvertUtils.chars2Bytes(mChars1)));
    }

    @Test
    public void bytes2Chars() {
        Assert.assertTrue(Arrays.equals(mChars1, ConvertUtils.bytes2Chars(mBytes1)));
    }

    @Test
    public void byte2MemorySize() {
        Assert.assertEquals(1024, ConvertUtils.byte2MemorySize(GB, MB), 0.001);
    }

    @Test
    public void byte2FitMemorySize() {
        Assert.assertEquals("3.098MB", ConvertUtils.byte2FitMemorySize((((1024 * 1024) * 3) + (1024 * 100))));
    }

    @Test
    public void millis2FitTimeSpan() {
        long millis = ((((6 * (TimeConstants.DAY)) + (6 * (TimeConstants.HOUR))) + (6 * (TimeConstants.MIN))) + (6 * (TimeConstants.SEC))) + 6;
        Assert.assertEquals("6?6??6??6?6??", ConvertUtils.millis2FitTimeSpan(millis, 7));
        Assert.assertEquals("6?6??6??6?", ConvertUtils.millis2FitTimeSpan(millis, 4));
        Assert.assertEquals("6?6??6??", ConvertUtils.millis2FitTimeSpan(millis, 3));
        Assert.assertEquals("25?24??24?24??", ConvertUtils.millis2FitTimeSpan((millis * 4), 5));
    }

    @Test
    public void bytes2Bits_bits2Bytes() {
        Assert.assertEquals("0111111111111010", ConvertUtils.bytes2Bits(new byte[]{ 127, ((byte) (250)) }));
        Assert.assertEquals("0111111111111010", ConvertUtils.bytes2Bits(ConvertUtils.bits2Bytes("111111111111010")));
    }

    @Test
    public void inputStream2Bytes_bytes2InputStream() throws Exception {
        String string = "this is test string";
        Assert.assertTrue(Arrays.equals(string.getBytes("UTF-8"), ConvertUtils.inputStream2Bytes(ConvertUtils.bytes2InputStream(string.getBytes("UTF-8")))));
    }

    @Test
    public void inputStream2String_string2InputStream() {
        String string = "this is test string";
        Assert.assertEquals(string, ConvertUtils.inputStream2String(ConvertUtils.string2InputStream(string, "UTF-8"), "UTF-8"));
    }
}


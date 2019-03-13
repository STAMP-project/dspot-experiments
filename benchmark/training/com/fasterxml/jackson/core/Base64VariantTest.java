package com.fasterxml.jackson.core;


import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for class {@link Base64Variant}.
 *
 * @unknown 2017-09-18
 * @see Base64Variant
 */
@SuppressWarnings("resource")
public class Base64VariantTest extends BaseTest {
    @Test
    public void testDecodeTaking2ArgumentsThrowsIllegalArgumentException() {
        Base64Variant base64Variant = new Base64Variant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", false, 'x', 'x');
        TestCase.assertEquals(120, base64Variant.getMaxLineLength());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.toString());
        TestCase.assertFalse(base64Variant.usesPadding());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.getName());
        TestCase.assertEquals(((byte) (120)), base64Variant.getPaddingByte());
        TestCase.assertEquals('x', base64Variant.getPaddingChar());
        ByteArrayBuilder byteArrayBuilder = new ByteArrayBuilder();
        try {
            base64Variant.decode("-%8en$9m=>$m", byteArrayBuilder);
            TestCase.fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            TestCase.assertEquals(Base64Variant.class.getName(), e.getStackTrace()[0].getClassName());
        }
    }

    @Test
    public void test_reportInvalidBase64ThrowsIllegalArgumentException() {
        Base64Variant base64Variant = new Base64Variant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", false, 'L', 3);
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.getName());
        TestCase.assertFalse(base64Variant.usesPadding());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.toString());
        TestCase.assertEquals(((byte) (76)), base64Variant.getPaddingByte());
        TestCase.assertEquals(3, base64Variant.getMaxLineLength());
        TestCase.assertEquals('L', base64Variant.getPaddingChar());
        try {
            base64Variant._reportInvalidBase64('L', 2274, "r68;3&@B");
            TestCase.fail("Expecting exception: IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            TestCase.assertEquals(Base64Variant.class.getName(), e.getStackTrace()[0].getClassName());
        }
    }

    @Test
    public void testEquals() {
        Base64Variant base64Variant = new Base64Variant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", true, ':', ':');
        TestCase.assertFalse(base64Variant.equals(new BufferRecycler()));
        TestCase.assertTrue(base64Variant.equals(base64Variant));
    }

    @Test
    public void testDecodeTaking2ArgumentsOne() {
        Base64Variant base64Variant = new Base64Variant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", false, 'R', 4);
        BufferRecycler bufferRecycler = new BufferRecycler();
        ByteArrayBuilder byteArrayBuilder = new ByteArrayBuilder(bufferRecycler);
        base64Variant.decode("PEM", byteArrayBuilder);
        TestCase.assertFalse(base64Variant.usesPadding());
        TestCase.assertEquals('R', base64Variant.getPaddingChar());
        TestCase.assertEquals(4, base64Variant.getMaxLineLength());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.toString());
        TestCase.assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", base64Variant.getName());
        TestCase.assertEquals(((byte) (82)), base64Variant.getPaddingByte());
    }

    @Test
    public void testEncodeTaking2ArgumentsWithTrue() {
        Base64Variant base64Variant = new Base64Variant("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", false, 'L', 3);
        byte[] byteArray = new byte[9];
        String encoded = base64Variant.encode(byteArray, true);
        Assert.assertArrayEquals(new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) }, byteArray);
        TestCase.assertEquals("\"AAAA\\nAAAA\\nAAAA\\n\"", encoded);
    }
}


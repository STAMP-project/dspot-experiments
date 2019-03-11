package com.baeldung.array.conversions;


import org.junit.Assert;
import org.junit.Test;


public class FloatToByteArrayUnitTest {
    @Test
    public void givenAFloat_thenConvertToByteArray() {
        Assert.assertArrayEquals(new byte[]{ 63, -116, -52, -51 }, FloatToByteArray.floatToByteArray(1.1F));
    }

    @Test
    public void givenAByteArray_thenConvertToFloat() {
        Assert.assertEquals(1.1F, FloatToByteArray.byteArrayToFloat(new byte[]{ 63, -116, -52, -51 }), 0);
    }

    @Test
    public void givenAFloat_thenConvertToByteArrayUsingByteBuffer() {
        Assert.assertArrayEquals(new byte[]{ 63, -116, -52, -51 }, FloatToByteArray.floatToByteArrayWithByteBuffer(1.1F));
    }

    @Test
    public void givenAByteArray_thenConvertToFloatUsingByteBuffer() {
        Assert.assertEquals(1.1F, FloatToByteArray.byteArrayToFloatWithByteBuffer(new byte[]{ 63, -116, -52, -51 }), 0);
    }

    @Test
    public void givenAFloat_thenConvertToByteArray_thenConvertToFloat() {
        float floatToConvert = 200.12F;
        byte[] byteArray = FloatToByteArray.floatToByteArray(floatToConvert);
        Assert.assertEquals(200.12F, FloatToByteArray.byteArrayToFloat(byteArray), 0);
    }

    @Test
    public void givenAFloat_thenConvertToByteArrayWithByteBuffer_thenConvertToFloatWithByteBuffer() {
        float floatToConvert = 30100.42F;
        byte[] byteArray = FloatToByteArray.floatToByteArrayWithByteBuffer(floatToConvert);
        Assert.assertEquals(30100.42F, FloatToByteArray.byteArrayToFloatWithByteBuffer(byteArray), 0);
    }
}


package com.baeldung.algorithms.conversion;


import org.apache.commons.codec.DecoderException;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayConverterUnitTest {
    private HexStringConverter hexStringConverter;

    @Test
    public void shouldEncodeByteArrayToHexStringUsingBigIntegerToString() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        if ((hexString.charAt(0)) == '0') {
            hexString = hexString.substring(1);
        }
        String output = hexStringConverter.encodeUsingBigIntegerToString(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldEncodeByteArrayToHexStringUsingBigIntegerStringFormat() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        String output = hexStringConverter.encodeUsingBigIntegerStringFormat(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldDecodeHexStringToByteArrayUsingBigInteger() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        byte[] output = hexStringConverter.decodeUsingBigInteger(hexString);
        Assert.assertArrayEquals(bytes, output);
    }

    @Test
    public void shouldEncodeByteArrayToHexStringUsingCharacterConversion() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        String output = hexStringConverter.encodeHexString(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldDecodeHexStringToByteArrayUsingCharacterConversion() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        byte[] output = hexStringConverter.decodeHexString(hexString);
        Assert.assertArrayEquals(bytes, output);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDecodeHexToByteWithInvalidHexCharacter() {
        hexStringConverter.hexToByte("fg");
    }

    @Test
    public void shouldEncodeByteArrayToHexStringDataTypeConverter() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        String output = hexStringConverter.encodeUsingDataTypeConverter(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldDecodeHexStringToByteArrayUsingDataTypeConverter() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        byte[] output = hexStringConverter.decodeUsingDataTypeConverter(hexString);
        Assert.assertArrayEquals(bytes, output);
    }

    @Test
    public void shouldEncodeByteArrayToHexStringUsingGuava() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        String output = hexStringConverter.encodeUsingGuava(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldDecodeHexStringToByteArrayUsingGuava() {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        byte[] output = hexStringConverter.decodeUsingGuava(hexString);
        Assert.assertArrayEquals(bytes, output);
    }

    @Test
    public void shouldEncodeByteArrayToHexStringUsingApacheCommons() throws DecoderException {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        String output = hexStringConverter.encodeUsingApacheCommons(bytes);
        Assert.assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(hexString));
    }

    @Test
    public void shouldDecodeHexStringToByteArrayUsingApacheCommons() throws DecoderException {
        byte[] bytes = getSampleBytes();
        String hexString = getSampleHexString();
        byte[] output = hexStringConverter.decodeUsingApacheCommons(hexString);
        Assert.assertArrayEquals(bytes, output);
    }
}


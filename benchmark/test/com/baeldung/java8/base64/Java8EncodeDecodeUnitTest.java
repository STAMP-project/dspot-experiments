package com.baeldung.java8.base64;


import java.io.UnsupportedEncodingException;
import java.util.Base64;
import org.junit.Assert;
import org.junit.Test;


public class Java8EncodeDecodeUnitTest {
    // tests
    @Test
    public void whenStringIsEncoded_thenOk() throws UnsupportedEncodingException {
        final String originalInput = "test input";
        final String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        Assert.assertNotNull(encodedString);
        Assert.assertNotEquals(originalInput, encodedString);
    }

    @Test
    public void whenStringIsEncoded_thenStringCanBeDecoded() throws UnsupportedEncodingException {
        final String originalInput = "test input";
        final String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        final byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        final String decodedString = new String(decodedBytes);
        Assert.assertNotNull(decodedString);
        Assert.assertEquals(originalInput, decodedString);
    }

    @Test
    public void whenStringIsEncodedWithoutPadding_thenOk() throws UnsupportedEncodingException {
        final String originalInput = "test input";
        final String encodedString = Base64.getEncoder().withoutPadding().encodeToString(originalInput.getBytes());
        Assert.assertNotNull(encodedString);
        Assert.assertNotEquals(originalInput, encodedString);
    }

    @Test
    public void whenStringIsEncodedWithoutPadding_thenStringCanBeDecoded() throws UnsupportedEncodingException {
        final String originalInput = "test input";
        final String encodedString = Base64.getEncoder().withoutPadding().encodeToString(originalInput.getBytes());
        final byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        final String decodedString = new String(decodedBytes);
        Assert.assertNotNull(decodedString);
        Assert.assertEquals(originalInput, decodedString);
    }

    @Test
    public void whenUrlIsEncoded_thenOk() throws UnsupportedEncodingException {
        final String originalUrl = "https://www.google.co.nz/?gfe_rd=cr&ei=dzbFVf&gws_rd=ssl#q=java";
        final String encodedUrl = Base64.getUrlEncoder().encodeToString(originalUrl.getBytes());
        Assert.assertNotNull(encodedUrl);
        Assert.assertNotEquals(originalUrl, encodedUrl);
    }

    @Test
    public void whenUrlIsEncoded_thenURLCanBeDecoded() throws UnsupportedEncodingException {
        final String originalUrl = "https://www.google.co.nz/?gfe_rd=cr&ei=dzbFVf&gws_rd=ssl#q=java";
        final String encodedUrl = Base64.getUrlEncoder().encodeToString(originalUrl.getBytes());
        final byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedUrl.getBytes());
        final String decodedUrl = new String(decodedBytes);
        Assert.assertNotNull(decodedUrl);
        Assert.assertEquals(originalUrl, decodedUrl);
    }

    @Test
    public void whenMimeIsEncoded_thenOk() throws UnsupportedEncodingException {
        final StringBuilder buffer = Java8EncodeDecodeUnitTest.getMimeBuffer();
        final byte[] forEncode = buffer.toString().getBytes();
        final String encodedMime = Base64.getMimeEncoder().encodeToString(forEncode);
        Assert.assertNotNull(encodedMime);
    }

    @Test
    public void whenMimeIsEncoded_thenItCanBeDecoded() throws UnsupportedEncodingException {
        final StringBuilder buffer = Java8EncodeDecodeUnitTest.getMimeBuffer();
        final byte[] forEncode = buffer.toString().getBytes();
        final String encodedMime = Base64.getMimeEncoder().encodeToString(forEncode);
        final byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedMime);
        final String decodedMime = new String(decodedBytes);
        Assert.assertNotNull(decodedMime);
    }
}


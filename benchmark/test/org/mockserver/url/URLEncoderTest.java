package org.mockserver.url;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class URLEncoderTest {
    @Test
    public void shouldEncodeCharacters() {
        Assert.assertEquals("%7B%7D%5B%5D%5E%C2%A3%5C%7C", new URLEncoder().encodeURL("{}[]^\u00a3\\|"));
    }

    @Test
    public void shouldNotEncodeAllowedCharacters() {
        String input = "abc-xyz_123~890.!$&\'()*,;=:@/?";
        Assert.assertEquals(input, URLEncoder.encodeURL(input));
    }

    @Test
    public void shouldAllowAlreadyEncodedCharacters() {
        String input = "%7B%7D%5B%5D%5E%C2%A3%5C%7C";
        Assert.assertEquals(input, URLEncoder.encodeURL(input));
    }

    @Test
    public void shouldNotEncodeWhenExceptionDuringDecoding() {
        String input = "%{";
        Assert.assertEquals(input, URLEncoder.encodeURL(input));
    }
}


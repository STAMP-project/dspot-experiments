package org.sufficientlysecure.keychain.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;


@RunWith(KeychainTestRunner.class)
public class CharsetVerifierTest {
    @Test
    public void testTypeImagePngAlwaysBinary() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "image/png", null);
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertTrue("image/png should be marked as definitely binary", charsetVerifier.isDefinitelyBinary());
        Assert.assertFalse("image/png should never be marked as, even if it is", charsetVerifier.isProbablyText());
        Assert.assertNull("charset should be null", charsetVerifier.getCharset());
    }

    @Test
    public void testUtf8SpecifiedButFaulty() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        bytes[4] = ((byte) (195));
        bytes[5] = ((byte) (40));
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/something", "utf-8");
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertFalse("text/plain should not be marked as binary, even if it is", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("text/plain should be marked as text, even if it isn't valid", charsetVerifier.isProbablyText());
        Assert.assertTrue("encoding contained illegal chars, so it should be marked as faulty", charsetVerifier.isCharsetFaulty());
        Assert.assertFalse("charset was specified and should not be marked as guessed", charsetVerifier.isCharsetGuessed());
        Assert.assertEquals("mimetype should be preserved", "text/something", charsetVerifier.getGuessedMimeType());
        Assert.assertEquals("charset should be utf-8 since it was given explicitly", "utf-8", charsetVerifier.getCharset());
        Assert.assertEquals("charset should be utf-8 since it was given explicitly", "utf-8", charsetVerifier.getMaybeFaultyCharset());
    }

    @Test
    public void testUtf8GuessedAndFaulty() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        bytes[4] = ((byte) (195));
        bytes[5] = ((byte) (40));
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/plain", null);
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertFalse("text/plain should not be marked as binary, even if it is", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("text/plain should be marked as text, even if it isn't valid", charsetVerifier.isProbablyText());
        Assert.assertTrue("encoding contained illegal chars, so it should be marked as faulty", charsetVerifier.isCharsetFaulty());
        Assert.assertTrue("charset was guessed and should be marked as such", charsetVerifier.isCharsetGuessed());
        Assert.assertNull("charset should be null since the guess was faulty", charsetVerifier.getCharset());
        Assert.assertEquals("mimetype should be set to text", "text/plain", charsetVerifier.getGuessedMimeType());
        Assert.assertEquals("maybe-faulty charset should be utf-8", "utf-8", charsetVerifier.getMaybeFaultyCharset());
    }

    @Test
    public void testGuessedEncoding() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "application/octet-stream", null);
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertFalse("application/octet-stream with text content is not definitely binary", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("application/octet-stream with text content should be probably text", charsetVerifier.isProbablyText());
        Assert.assertFalse("detected charset should not be faulty", charsetVerifier.isCharsetFaulty());
        Assert.assertTrue("charset was guessed and should be marked as such", charsetVerifier.isCharsetGuessed());
        Assert.assertEquals("mimetype should be set to text", "text/plain", charsetVerifier.getGuessedMimeType());
        Assert.assertEquals("guessed charset is utf-8", "utf-8", charsetVerifier.getCharset());
    }

    @Test
    public void testWindows1252Faulty() throws Exception {
        byte[] bytes = "bla bluh  ?".getBytes("windows-1252");
        bytes[2] = ((byte) (157));
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/plain", "windows-1252");
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertFalse("text/plain is never definitely binary", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("text/plain is always probably text", charsetVerifier.isProbablyText());
        Assert.assertTrue("charset contained faulty characters", charsetVerifier.isCharsetFaulty());
        Assert.assertFalse("charset was not guessed", charsetVerifier.isCharsetGuessed());
        Assert.assertEquals("charset is returned correctly", "windows-1252", charsetVerifier.getCharset());
    }

    @Test
    public void testWindows1252Good() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("windows-1252");
        // this is ? in windows-1252
        bytes[2] = ((byte) (135));
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/plain", "windows-1252");
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        Assert.assertFalse("text/plain is never definitely binary", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("text/plain is always probably text", charsetVerifier.isProbablyText());
        Assert.assertFalse("charset contained no faulty characters", charsetVerifier.isCharsetFaulty());
        Assert.assertFalse("charset was not guessed", charsetVerifier.isCharsetGuessed());
        Assert.assertEquals("charset is returned correctly", "windows-1252", charsetVerifier.getCharset());
    }

    @Test(expected = IllegalStateException.class)
    public void testReadAfterGetterShouldCrash() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/plain", null);
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
        charsetVerifier.isCharsetFaulty();
        charsetVerifier.readBytesFromBuffer(0, bytes.length);
    }

    @Test
    public void testStaggeredInput() throws Exception {
        byte[] bytes = "bla bluh ?".getBytes("utf-8");
        bytes[4] = ((byte) (195));
        bytes[5] = ((byte) (40));
        CharsetVerifier charsetVerifier = new CharsetVerifier(bytes, "text/plain", null);
        for (int i = 0; i < (bytes.length); i++) {
            charsetVerifier.readBytesFromBuffer(i, (i + 1));
        }
        Assert.assertFalse("text/plain should not be marked as binary, even if it is", charsetVerifier.isDefinitelyBinary());
        Assert.assertTrue("text/plain should be marked as text, even if it isn't valid", charsetVerifier.isProbablyText());
        Assert.assertTrue("encoding contained illegal chars, so it should be marked as faulty", charsetVerifier.isCharsetFaulty());
        Assert.assertTrue("charset was guessed and should be marked as such", charsetVerifier.isCharsetGuessed());
        Assert.assertNull("charset should be null since the guess was faulty", charsetVerifier.getCharset());
        Assert.assertEquals("maybe-faulty charset should be utf-8", "utf-8", charsetVerifier.getMaybeFaultyCharset());
    }
}


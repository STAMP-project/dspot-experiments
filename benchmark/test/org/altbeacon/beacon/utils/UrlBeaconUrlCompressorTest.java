package org.altbeacon.beacon.utils;


import java.net.MalformedURLException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class UrlBeaconUrlCompressorTest {
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * URLs to test:
     * <p/>
     * http://www.radiusnetworks.com
     * https://www.radiusnetworks.com
     * http://radiusnetworks.com
     * https://radiusnetworks.com
     * https://radiusnetworks.com/
     * https://radiusnetworks.com/v1/index.html
     * https://api.v1.radiusnetworks.com
     * https://www.api.v1.radiusnetworks.com
     */
    @Test
    public void testCompressURL() throws MalformedURLException {
        String testURL = "http://www.radiusnetworks.com";
        byte[] expectedBytes = new byte[]{ 0, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 7 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressHttpsURL() throws MalformedURLException {
        String testURL = "https://www.radiusnetworks.com";
        byte[] expectedBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 7 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithTrailingSlash() throws MalformedURLException {
        String testURL = "http://google.com/123";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', 0, '1', '2', '3' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithoutTLD() throws MalformedURLException {
        String testURL = "http://xxx";
        byte[] expectedBytes = new byte[]{ 2, 'x', 'x', 'x' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithSubdomains() throws MalformedURLException {
        String testURL = "http://www.forums.google.com";
        byte[] expectedBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 7 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithSubdomainsWithTrailingSlash() throws MalformedURLException {
        String testURL = "http://www.forums.google.com/";
        byte[] expectedBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 0 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithMoreSubdomains() throws MalformedURLException {
        String testURL = "http://www.forums.developer.google.com/123";
        byte[] expectedBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'd', 'e', 'v', 'e', 'l', 'o', 'p', 'e', 'r', '.', 'g', 'o', 'o', 'g', 'l', 'e', 0, '1', '2', '3' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithSubdomainsAndSlashesInPath() throws MalformedURLException {
        String testURL = "http://www.forums.google.com/123/456";
        byte[] expectedBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 0, '1', '2', '3', '/', '4', '5', '6' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithDotCaTLD() throws MalformedURLException {
        String testURL = "http://google.ca";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', '.', 'c', 'a' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithDotInfoTLD() throws MalformedURLException {
        String testURL = "http://google.info";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', 11 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithDotCaTLDWithSlash() throws MalformedURLException {
        String testURL = "http://google.ca/";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', '.', 'c', 'a', '/' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithDotCoTLD() throws MalformedURLException {
        String testURL = "http://google.co";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', '.', 'c', 'o' };
        String hexBytes = UrlBeaconUrlCompressorTest.bytesToHex(UrlBeaconUrlCompressor.compress(testURL));
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithShortenedURLContainingCaps() throws MalformedURLException {
        String testURL = "http://goo.gl/C2HC48";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', '.', 'g', 'l', '/', 'C', '2', 'H', 'C', '4', '8' };
        String hexBytes = UrlBeaconUrlCompressorTest.bytesToHex(UrlBeaconUrlCompressor.compress(testURL));
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithSchemeInCaps() throws MalformedURLException {
        String testURL = "HTTP://goo.gl/C2HC48";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', '.', 'g', 'l', '/', 'C', '2', 'H', 'C', '4', '8' };
        String hexBytes = UrlBeaconUrlCompressorTest.bytesToHex(UrlBeaconUrlCompressor.compress(testURL));
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressWithDomainInCaps() throws MalformedURLException {
        String testURL = "http://GOO.GL/C2HC48";
        byte[] expectedBytes = new byte[]{ 2, 'g', 'o', 'o', '.', 'g', 'l', '/', 'C', '2', 'H', 'C', '4', '8' };
        String hexBytes = UrlBeaconUrlCompressorTest.bytesToHex(UrlBeaconUrlCompressor.compress(testURL));
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressHttpsAndWWWInCaps() throws MalformedURLException {
        String testURL = "HTTPS://WWW.radiusnetworks.com";
        byte[] expectedBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 7 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressEntireURLInCaps() throws MalformedURLException {
        String testURL = "HTTPS://WWW.RADIUSNETWORKS.COM";
        byte[] expectedBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 7 };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testCompressEntireURLInCapsWithPath() throws MalformedURLException {
        String testURL = "HTTPS://WWW.RADIUSNETWORKS.COM/C2HC48";
        byte[] expectedBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 0, 'C', '2', 'H', 'C', '4', '8' };
        Assert.assertTrue(Arrays.equals(expectedBytes, UrlBeaconUrlCompressor.compress(testURL)));
    }

    @Test
    public void testDecompressWithDotCoTLD() {
        String testURL = "http://google.co";
        byte[] testBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', '.', 'c', 'o' };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testDecompressWithPath() {
        String testURL = "http://google.com/123";
        byte[] testBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', 0, '1', '2', '3' };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressHttpsURL() {
        String testURL = "https://www.radiusnetworks.com";
        byte[] testBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 7 };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressHttpsURLWithTrailingSlash() {
        String testURL = "https://www.radiusnetworks.com/";
        byte[] testBytes = new byte[]{ 1, 'r', 'a', 'd', 'i', 'u', 's', 'n', 'e', 't', 'w', 'o', 'r', 'k', 's', 0 };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressWithoutTLD() throws MalformedURLException {
        String testURL = "http://xxx";
        byte[] testBytes = new byte[]{ 2, 'x', 'x', 'x' };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressWithSubdomains() throws MalformedURLException {
        String testURL = "http://www.forums.google.com";
        byte[] testBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 7 };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressWithSubdomainsAndTrailingSlash() throws MalformedURLException {
        String testURL = "http://www.forums.google.com/";
        byte[] testBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 0 };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressWithSubdomainsAndSlashesInPath() throws MalformedURLException {
        String testURL = "http://www.forums.google.com/123/456";
        byte[] testBytes = new byte[]{ 0, 'f', 'o', 'r', 'u', 'm', 's', '.', 'g', 'o', 'o', 'g', 'l', 'e', 0, '1', '2', '3', '/', '4', '5', '6' };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }

    @Test
    public void testUncompressWithDotInfoTLD() throws MalformedURLException {
        String testURL = "http://google.info";
        byte[] testBytes = new byte[]{ 2, 'g', 'o', 'o', 'g', 'l', 'e', 11 };
        Assert.assertEquals(testURL, UrlBeaconUrlCompressor.uncompress(testBytes));
    }
}


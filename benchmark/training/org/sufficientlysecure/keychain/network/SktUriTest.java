package org.sufficientlysecure.keychain.network;


import android.annotation.SuppressLint;
import java.net.URISyntaxException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("WeakerAccess")
@SuppressLint("DefaultLocale")
public class SktUriTest {
    static final String HOST = "127.0.0.1";

    static final int PORT = 1234;

    static final byte[] PRESHARED_KEY = new byte[]{ 1, 2 };

    static final String SSID = "ssid";

    static final String ENCODED_SKT = String.format("OPGPSKT:%s/%d/%s/SSID:%s", SktUriTest.HOST, SktUriTest.PORT, Hex.toHexString(SktUriTest.PRESHARED_KEY), Hex.toHexString(SktUriTest.SSID.getBytes()));

    @Test
    public void testCreate() {
        SktUri sktUri = SktUri.create(SktUriTest.HOST, SktUriTest.PORT, SktUriTest.PRESHARED_KEY, null);
        Assert.assertEquals(SktUriTest.HOST, sktUri.getHost());
        Assert.assertEquals(SktUriTest.PORT, sktUri.getPort());
        Assert.assertArrayEquals(SktUriTest.PRESHARED_KEY, sktUri.getPresharedKey());
        Assert.assertEquals(null, sktUri.getWifiSsid());
    }

    @Test
    public void testCreateWithSsid() {
        SktUri sktUri = SktUri.create(SktUriTest.HOST, SktUriTest.PORT, SktUriTest.PRESHARED_KEY, SktUriTest.SSID);
        Assert.assertEquals(SktUriTest.HOST, sktUri.getHost());
        Assert.assertEquals(SktUriTest.PORT, sktUri.getPort());
        Assert.assertArrayEquals(SktUriTest.PRESHARED_KEY, sktUri.getPresharedKey());
        Assert.assertEquals(SktUriTest.SSID, sktUri.getWifiSsid());
    }

    @Test
    public void testCreate_isAllUppercase() {
        SktUri sktUri = SktUri.create(SktUriTest.HOST, SktUriTest.PORT, SktUriTest.PRESHARED_KEY, SktUriTest.SSID);
        String encodedSktUri = sktUri.toUriString();
        Assert.assertEquals(encodedSktUri.toUpperCase(), encodedSktUri);
    }

    @Test
    public void testParse() throws URISyntaxException {
        SktUri sktUri = SktUri.parse(SktUriTest.ENCODED_SKT);
        Assert.assertNotNull(sktUri);
        Assert.assertEquals(SktUriTest.HOST, sktUri.getHost());
        Assert.assertEquals(SktUriTest.PORT, sktUri.getPort());
        Assert.assertArrayEquals(SktUriTest.PRESHARED_KEY, sktUri.getPresharedKey());
        Assert.assertEquals(SktUriTest.SSID, sktUri.getWifiSsid());
    }

    @Test
    public void testBackAndForth() throws URISyntaxException {
        SktUri sktUri = SktUri.create(SktUriTest.HOST, SktUriTest.PORT, SktUriTest.PRESHARED_KEY, null);
        String encodedSktUri = sktUri.toUriString();
        SktUri decodedSktUri = SktUri.parse(encodedSktUri);
        Assert.assertEquals(sktUri, decodedSktUri);
    }

    @Test
    public void testBackAndForthWithSsid() throws URISyntaxException {
        SktUri sktUri = SktUri.create(SktUriTest.HOST, SktUriTest.PORT, SktUriTest.PRESHARED_KEY, SktUriTest.SSID);
        String encodedSktUri = sktUri.toUriString();
        SktUri decodedSktUri = SktUri.parse(encodedSktUri);
        Assert.assertEquals(sktUri, decodedSktUri);
    }

    @Test(expected = URISyntaxException.class)
    public void testParse_withBadScheme_shouldFail() throws URISyntaxException {
        SktUri.parse(String.format("XXXGPSKT:%s/%d/%s/SSID:%s", SktUriTest.HOST, SktUriTest.PORT, Hex.toHexString(SktUriTest.PRESHARED_KEY), Hex.toHexString(SktUriTest.SSID.getBytes())));
    }

    @Test(expected = URISyntaxException.class)
    public void testParse_withBadPsk_shouldFail() throws URISyntaxException {
        SktUri.parse(String.format("OPGPSKT:%s/%d/xx%s/SSID:%s", SktUriTest.HOST, SktUriTest.PORT, Hex.toHexString(SktUriTest.PRESHARED_KEY), Hex.toHexString(SktUriTest.SSID.getBytes())));
    }

    @Test(expected = URISyntaxException.class)
    public void testParse_withBadPort_shouldFail() throws URISyntaxException {
        SktUri.parse(String.format("OPGPSKT:%s/x%d/%s/SSID:%s", SktUriTest.HOST, SktUriTest.PORT, Hex.toHexString(SktUriTest.PRESHARED_KEY), Hex.toHexString(SktUriTest.SSID.getBytes())));
    }
}


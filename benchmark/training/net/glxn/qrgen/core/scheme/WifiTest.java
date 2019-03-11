package net.glxn.qrgen.core.scheme;


import Authentication.WPA;
import org.junit.Assert;
import org.junit.Test;


public class WifiTest {
    @Test
    public void parse() {
        Wifi wifi = Wifi.parse("WIFI:S:some weird SSID;T:WPA;P:aintNoSecret;H:true;");
        Assert.assertEquals("some weird SSID", wifi.getSsid());
        Assert.assertEquals("WPA", wifi.getAuthentication());
        Assert.assertEquals("aintNoSecret", wifi.getPsk());
        Assert.assertEquals(true, wifi.isHidden());
    }

    /**
     * The following characters need to be escaped with a backslash (\) in the
     * SSID and PSK strings: backslash (\), single-quote ('), double-quote ("),
     * dot (.), colon (:), comma (,), and semicolon (;)
     */
    @Test
    public void parseEscapeSsidAndPassword() {
        Wifi wifi = Wifi.parse("WIFI:S:s\\;o\\,\\\"me \\\'wei\\\\rd\\. SSID\\;;T:WPA;P:\\;a\\,\\\"intNo\\,Sec\\\\ret;false;");
        Assert.assertEquals("s;o,\"me \'wei\\rd. SSID;", wifi.getSsid());
        Assert.assertEquals("WPA", wifi.getAuthentication());
        Assert.assertEquals(";a,\"intNo,Sec\\ret", wifi.getPsk());
        Assert.assertEquals(false, wifi.isHidden());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNull() {
        Wifi.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseEmptyString() {
        Wifi.parse("");
    }

    @Test
    public void parseHeaderOnly() {
        Wifi wifi = Wifi.parse("WIFI:");
        Assert.assertNull(null, wifi.getSsid());
        Assert.assertNull(null, wifi.getAuthentication());
        Assert.assertNull(null, wifi.getPsk());
        Assert.assertEquals(false, wifi.isHidden());
    }

    @Test
    public void testToString() {
        Wifi wifi = new Wifi();
        wifi.setSsid("some weird SSID");
        wifi.setAuthentication(WPA);
        wifi.setPsk("aintNoSecret");
        wifi.setHidden(true);
        Assert.assertEquals("WIFI:S:some weird SSID;T:WPA;P:aintNoSecret;H:true;", wifi.toString());
    }

    @Test
    public void testToStringEscapeSsidAndPassword() {
        Wifi wifi = new Wifi();
        wifi.setSsid("s;o,\"me \'wei\\rd. SSID;");
        wifi.setAuthentication(WPA);
        wifi.setPsk(";a,\"intNo,Sec\\ret");
        wifi.setHidden(false);
        Assert.assertEquals("WIFI:S:s\\;o\\,\\\"me \\\'wei\\\\rd\\. SSID\\;;T:WPA;P:\\;a\\,\\\"intNo\\,Sec\\\\ret;H:false;", wifi.toString());
    }
}


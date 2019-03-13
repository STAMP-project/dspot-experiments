package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class EnterpriseWifiTest {
    @Test
    public void parse() {
        EnterpriseWifi wifi = EnterpriseWifi.parse("WIFI:S:some weird SSID;U:Spock;P:aintNoSecret;E:PEAP;PH:MS-CHAPv2;H:true;");
        Assert.assertEquals("some weird SSID", wifi.getSsid());
        Assert.assertEquals("Spock", wifi.getUser());
        Assert.assertEquals("aintNoSecret", wifi.getPsk());
        Assert.assertEquals("PEAP", wifi.getEap());
        Assert.assertEquals("MS-CHAPv2", wifi.getPhase());
        Assert.assertEquals(true, wifi.isHidden());
    }

    /**
     * The following characters need to be escaped with a backslash (\) in the
     * SSID and PSK strings: backslash (\), single-quote ('), double-quote ("),
     * dot (.), colon (:), comma (,), and semicolon (;)
     */
    @Test
    public void parseEscapeSsidAndAuth() {
        EnterpriseWifi wifi = EnterpriseWifi.parse("WIFI:S:s\\;o\\,\\\"me \\\'wei\\\\rd\\. SSID\\;;U:Sp\\;ock;P:\\;a\\,\\\"intNo\\,Sec\\\\ret;E:PEAP;PH:MS-CHAPv2;false;");
        Assert.assertEquals("s;o,\"me \'wei\\rd. SSID;", wifi.getSsid());
        Assert.assertEquals("Sp;ock", wifi.getUser());
        Assert.assertEquals(";a,\"intNo,Sec\\ret", wifi.getPsk());
        Assert.assertEquals("PEAP", wifi.getEap());
        Assert.assertEquals("MS-CHAPv2", wifi.getPhase());
        Assert.assertEquals(false, wifi.isHidden());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseNull() {
        EnterpriseWifi.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseEmptyString() {
        EnterpriseWifi.parse("");
    }

    @Test
    public void parseHeaderOnly() {
        EnterpriseWifi wifi = EnterpriseWifi.parse("WIFI:");
        Assert.assertNull(null, wifi.getSsid());
        Assert.assertNull(null, wifi.getUser());
        Assert.assertNull(null, wifi.getPsk());
        Assert.assertNull(null, wifi.getEap());
        Assert.assertNull(null, wifi.getPhase());
        Assert.assertEquals(false, wifi.isHidden());
    }

    @Test
    public void testToString() {
        EnterpriseWifi wifi = new EnterpriseWifi();
        wifi.setSsid("some weird SSID");
        wifi.setUser("Spock");
        wifi.setPsk("aintNoSecret");
        wifi.setEap("PEAP");
        wifi.setPhase("MS-CHAPv2");
        wifi.setHidden(true);
        Assert.assertEquals("WIFI:S:some weird SSID;U:Spock;P:aintNoSecret;E:PEAP;PH:MS-CHAPv2;H:true;", wifi.toString());
    }

    @Test
    public void testToStringEscapeUsernameAndPassword() {
        EnterpriseWifi wifi = new EnterpriseWifi();
        wifi.setSsid("s;o,\"me \'wei\\rd. SSID;");
        wifi.setUser("Sp;ock");
        wifi.setPsk(";a,\"intNo,Sec\\ret");
        wifi.setEap("PEAP");
        wifi.setPhase("MS-CHAPv2");
        wifi.setHidden(false);
        Assert.assertEquals("WIFI:S:s\\;o\\,\\\"me \\\'wei\\\\rd\\. SSID\\;;U:Sp\\;ock;P:\\;a\\,\\\"intNo\\,Sec\\\\ret;E:PEAP;PH:MS-CHAPv2;H:false;", wifi.toString());
    }
}


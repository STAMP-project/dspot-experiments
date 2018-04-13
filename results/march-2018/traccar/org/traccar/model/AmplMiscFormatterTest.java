package org.traccar.model;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class AmplMiscFormatterTest {
    @Test(timeout = 50000)
    public void testToString() throws Exception {
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString__6 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString__6);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
    }

    @Test(timeout = 50000)
    public void testToString_sd70() throws Exception {
        double __DSPOT_latitude_6 = 0.17708796212554356;
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd70__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd70__7);
        position.setLatitude(__DSPOT_latitude_6);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.17708796212554356, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd70__7);
    }

    @Test(timeout = 50000)
    public void testToString_sd72() throws Exception {
        Network __DSPOT_network_8 = new Network();
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd72__8 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd72__8);
        position.setNetwork(__DSPOT_network_8);
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd72__8);
        Assert.assertNull(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getHomeMobileCountryCode());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertNull(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getWifiAccessPoints());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getHomeMobileNetworkCode());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertFalse(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getConsiderIp());
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertNull(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getCarrier());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getCellTowers());
        Assert.assertEquals("gsm", ((org.traccar.model.Network)((org.traccar.model.Position)position).getNetwork()).getRadioType());
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
    }

    @Test(timeout = 50000)
    public void testToStringlitString17() throws Exception {
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToStringlitString17__6 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><b>2</b><a>3</a></info>", o_testToStringlitString17__6);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
    }

    @Test(timeout = 50000)
    public void testToString_sd67() throws Exception {
        double __DSPOT_course_3 = 0.20990498219948017;
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd67__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd67__7);
        position.setCourse(__DSPOT_course_3);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.20990498219948017, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd67__7);
    }

    @Test(timeout = 50000)
    public void testToString_sd78() throws Exception {
        boolean __DSPOT_valid_14 = true;
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd78__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd78__7);
        position.setValid(__DSPOT_valid_14);
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd78__7);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertTrue(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
    }

    @Test(timeout = 50000)
    public void testToString_sd69() throws Exception {
        Date __DSPOT_fixTime_5 = new Date(-439235821, -116913431, 1879249823, -41748166, -149948924);
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd69__8 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd69__8);
        position.setFixTime(__DSPOT_fixTime_5);
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd69__8);
        Assert.assertEquals("+140708286-06-16T09:41:51.616Z", ((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).toString());
        Assert.assertEquals(4440262474518111L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).getEpochSecond())));
        Assert.assertEquals("Wed Jun 16 11:41:51 CEST 140708286", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toString());
        Assert.assertEquals(4440262474518111616L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).toEpochMilli())));
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals("16 Jun 140708286 09:41:51 GMT", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toGMTString());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(41, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getMinutes())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertEquals(11, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getHours())));
        Assert.assertEquals(51, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getSeconds())));
        Assert.assertEquals(16, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getDate())));
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertEquals(-120, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getTimezoneOffset())));
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("Jun 16, 140708286 11:41:51 AM", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toLocaleString());
        Assert.assertEquals(5, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getMonth())));
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(2082022970, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).hashCode())));
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertEquals(616000000, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).getNano())));
        Assert.assertEquals(-759636303, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).hashCode())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(4440262474518111616L, ((long) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getTime())));
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertEquals(3, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getDay())));
        Assert.assertEquals(140706386, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getYear())));
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
    }

    @Test(timeout = 50000)
    public void testToString_sd74() throws Exception {
        String __DSPOT_protocol_10 = "i!rb0/|]6^FT)-ef&bk*";
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd74__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd74__7);
        position.setProtocol(__DSPOT_protocol_10);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertEquals("i!rb0/|]6^FT)-ef&bk*", ((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd74__7);
    }

    @Test(timeout = 50000)
    public void testToStringlitString10() throws Exception {
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "a");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToStringlitString10__6 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToStringlitString10__6);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
    }

    @Test(timeout = 50000)
    public void testToString_sd77() throws Exception {
        Date __DSPOT_time_13 = new Date(-1911680479, 920537097, -481834580);
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd77__8 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd77__8);
        position.setTime(__DSPOT_time_13);
        Assert.assertEquals("-82661905-06-16T18:17:34.848Z", ((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).toString());
        Assert.assertEquals(-2608619921041346L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).getEpochSecond())));
        Assert.assertEquals("Thu Oct 31 19:17:34 CET 82660209", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toString());
        Assert.assertEquals(82658309, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getYear())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals("31 Oct 82660209 18:17:34 GMT", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toGMTString());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(17, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getMinutes())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertEquals(19, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getHours())));
        Assert.assertEquals(19, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getHours())));
        Assert.assertEquals(34, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getSeconds())));
        Assert.assertEquals(31, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getDate())));
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals("31 Oct 82660209 18:17:34 GMT", ((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toGMTString());
        Assert.assertEquals(1738467200, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).hashCode())));
        Assert.assertEquals("Oct 31, 82660209 7:17:34 PM", ((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toLocaleString());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals(9, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getMonth())));
        Assert.assertEquals("-82661905-06-16T18:17:34.848Z", ((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toInstant()).toString());
        Assert.assertEquals(-1182908601, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).hashCode())));
        Assert.assertEquals(34, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getSeconds())));
        Assert.assertEquals(1738467200, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).hashCode())));
        Assert.assertEquals(-2608619921041345152L, ((long) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getTime())));
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertEquals(-60, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getTimezoneOffset())));
        Assert.assertEquals(4, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getDay())));
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertEquals(848000000, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toInstant()).getNano())));
        Assert.assertEquals(31, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getDate())));
        Assert.assertEquals(-2608619921041345152L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).toEpochMilli())));
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertEquals(-2608619921041345152L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toInstant()).toEpochMilli())));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertEquals(4, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getDay())));
        Assert.assertEquals(-60, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getTimezoneOffset())));
        Assert.assertEquals(-1182908601, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toInstant()).hashCode())));
        Assert.assertEquals(-2608619921041346L, ((long) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toInstant()).getEpochSecond())));
        Assert.assertEquals(-2608619921041345152L, ((long) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getTime())));
        Assert.assertEquals("Oct 31, 82660209 7:17:34 PM", ((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toLocaleString());
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(17, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getMinutes())));
        Assert.assertEquals("Thu Oct 31 19:17:34 CET 82660209", ((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).toString());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertEquals(848000000, ((int) (((java.time.Instant)((java.util.Date)((org.traccar.model.Position)position).getFixTime()).toInstant()).getNano())));
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(9, ((int) (((java.util.Date)((org.traccar.model.Position)position).getDeviceTime()).getMonth())));
        Assert.assertEquals(82658309, ((int) (((java.util.Date)((org.traccar.model.Position)position).getFixTime()).getYear())));
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd77__8);
    }

    @Test(timeout = 50000)
    public void testToString_sd66() throws Exception {
        double __DSPOT_altitude_2 = 0.5775214122591753;
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd66__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd66__7);
        position.setAltitude(__DSPOT_altitude_2);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.5775214122591753, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd66__7);
    }

    @Test(timeout = 50000)
    public void testToString_sd65() throws Exception {
        String __DSPOT_address_1 = "&zgYc TM1`_8;0L`A=SO";
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd65__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd65__7);
        position.setAddress(__DSPOT_address_1);
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd65__7);
        Assert.assertEquals("&zgYc TM1`_8;0L`A=SO", ((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
    }

    @Test(timeout = 50000)
    public void testToString_sd76() throws Exception {
        double __DSPOT_speed_12 = 0.8992170879254511;
        Position position = new Position();
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");
        String o_testToString_sd76__7 = MiscFormatter.toXmlString(position.getAttributes());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd76__7);
        position.setSpeed(__DSPOT_speed_12);
        Assert.assertNull(((org.traccar.model.Position)position).getAddress());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getDeviceId())));
        Assert.assertNull(((org.traccar.model.Position)position).getNetwork());
        Assert.assertNull(((org.traccar.model.Position)position).getProtocol());
        Assert.assertEquals(0L, ((long) (((org.traccar.model.Position)position).getId())));
        Assert.assertNull(((org.traccar.model.Position)position).getFixTime());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAccuracy())), 0.1);
        Assert.assertEquals(0.8992170879254511, ((double) (((org.traccar.model.Position)position).getSpeed())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getAltitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLatitude())), 0.1);
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getCourse())), 0.1);
        Assert.assertFalse(((org.traccar.model.Position)position).getOutdated());
        Assert.assertFalse(((org.traccar.model.Position)position).getValid());
        Assert.assertEquals(0.0, ((double) (((org.traccar.model.Position)position).getLongitude())), 0.1);
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("a"));
        Assert.assertEquals("3", ((org.traccar.model.Position)position).getAttributes().get("a"));
        Assert.assertTrue(((org.traccar.model.Position)position).getAttributes().containsKey("b"));
        Assert.assertEquals("2", ((org.traccar.model.Position)position).getAttributes().get("b"));
        Assert.assertNull(((org.traccar.model.Position)position).getDeviceTime());
        Assert.assertNull(((org.traccar.model.Position)position).getServerTime());
        Assert.assertNull(((org.traccar.model.Position)position).getType());
        Assert.assertEquals("<info><a>3</a><b>2</b></info>", o_testToString_sd76__7);
    }

    @Test(timeout = 50000)
    public void testToString_sd77_sd9483_sd15407_failAssert37() throws Exception {
        try {
            int __DSPOT_arg1_2088 = 1181285167;
            String __DSPOT_arg0_2087 = ".}JV9hEMEfrrwY4[=(I<";
            Date __DSPOT_time_13 = new Date(-1911680479, 920537097, -481834580);
            Position position = new Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            String o_testToString_sd77__8 = MiscFormatter.toXmlString(position.getAttributes());
            position.setTime(__DSPOT_time_13);
            ((org.traccar.model.Position)position).getAttributes().containsKey("a");
            ((org.traccar.model.Position)position).getAttributes().get("a");
            ((org.traccar.model.Position)position).getAttributes().containsKey("b");
            ((org.traccar.model.Position)position).getAttributes().get("b");
            String __DSPOT_invoc_166 = position.getProtocol();
            __DSPOT_invoc_166.startsWith(__DSPOT_arg0_2087, __DSPOT_arg1_2088);
            org.junit.Assert.fail("testToString_sd77_sd9483_sd15407 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testToString_sd77_sd9474_sd16203_failAssert10() throws Exception {
        try {
            int __DSPOT_arg3_2211 = 1250542834;
            byte[] __DSPOT_arg2_2210 = new byte[]{ 124, 97, 115, -49 };
            int __DSPOT_arg1_2209 = 782197997;
            int __DSPOT_arg0_2208 = 1877702784;
            Date __DSPOT_time_13 = new Date(-1911680479, 920537097, -481834580);
            Position position = new Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            String o_testToString_sd77__8 = MiscFormatter.toXmlString(position.getAttributes());
            position.setTime(__DSPOT_time_13);
            ((org.traccar.model.Position)position).getAttributes().containsKey("a");
            ((org.traccar.model.Position)position).getAttributes().get("a");
            ((org.traccar.model.Position)position).getAttributes().containsKey("b");
            ((org.traccar.model.Position)position).getAttributes().get("b");
            String __DSPOT_invoc_166 = position.getAddress();
            __DSPOT_invoc_166.getBytes(__DSPOT_arg0_2208, __DSPOT_arg1_2209, __DSPOT_arg2_2210, __DSPOT_arg3_2211);
            org.junit.Assert.fail("testToString_sd77_sd9474_sd16203 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testToString_sd77_add9505_sd13102_failAssert21() throws Exception {
        try {
            int __DSPOT_arg1_1721 = 2125470920;
            int __DSPOT_arg0_1720 = -712346150;
            Date __DSPOT_time_13 = new Date(-1911680479, 920537097, -481834580);
            Position position = new Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            String __DSPOT_invoc_42 = MiscFormatter.toXmlString(position.getAttributes());
            String o_testToString_sd77__8 = MiscFormatter.toXmlString(position.getAttributes());
            position.setTime(__DSPOT_time_13);
            ((org.traccar.model.Position)position).getAttributes().containsKey("a");
            ((org.traccar.model.Position)position).getAttributes().get("a");
            ((org.traccar.model.Position)position).getAttributes().containsKey("b");
            ((org.traccar.model.Position)position).getAttributes().get("b");
            __DSPOT_invoc_42.offsetByCodePoints(__DSPOT_arg0_1720, __DSPOT_arg1_1721);
            org.junit.Assert.fail("testToString_sd77_add9505_sd13102 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testToString_sd77_sd9481_sd16058_failAssert16() throws Exception {
        try {
            Collection<CellTower> __DSPOT_cellTowers_2174 = Collections.<CellTower>emptyList();
            Date __DSPOT_time_13 = new Date(-1911680479, 920537097, -481834580);
            Position position = new Position();
            position.set("a", "1");
            position.set("b", "2");
            position.set("a", "3");
            String o_testToString_sd77__8 = MiscFormatter.toXmlString(position.getAttributes());
            position.setTime(__DSPOT_time_13);
            ((org.traccar.model.Position)position).getAttributes().containsKey("a");
            ((org.traccar.model.Position)position).getAttributes().get("a");
            ((org.traccar.model.Position)position).getAttributes().containsKey("b");
            ((org.traccar.model.Position)position).getAttributes().get("b");
            Network __DSPOT_invoc_166 = position.getNetwork();
            __DSPOT_invoc_166.setCellTowers(__DSPOT_cellTowers_2174);
            org.junit.Assert.fail("testToString_sd77_sd9481_sd16058 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }
}


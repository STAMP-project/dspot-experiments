package org.traccar.events;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.traccar.BaseTest;
import org.traccar.model.CellTower;
import org.traccar.model.Event;
import org.traccar.model.Network;
import org.traccar.model.Position;
import org.traccar.model.WifiAccessPoint;


public class AmplIgnitionEventHandlerTest extends BaseTest {
    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16_mg354() throws Exception {
        WifiAccessPoint __DSPOT_wifiAccessPoint_121 = new WifiAccessPoint();
        Network __DSPOT_network_8 = new Network();
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
        __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_121);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg14_mg855() throws Exception {
        Network __DSPOT_network_359 = new Network(new CellTower());
        double __DSPOT_latitude_6 = 0.10089601329744369;
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setLatitude(__DSPOT_latitude_6);
        position.setNetwork(__DSPOT_network_359);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16_mg354_mg8795() throws Exception {
        WifiAccessPoint __DSPOT_wifiAccessPoint_3181 = new WifiAccessPoint();
        WifiAccessPoint __DSPOT_wifiAccessPoint_121 = new WifiAccessPoint();
        Network __DSPOT_network_8 = new Network();
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
        __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_121);
        __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_3181);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16_mg383_mg7626_failAssert22() throws Exception {
        try {
            WifiAccessPoint __DSPOT_wifiAccessPoint_2771 = new WifiAccessPoint();
            Collection<WifiAccessPoint> __DSPOT_wifiAccessPoints_131 = Collections.singletonList(new WifiAccessPoint());
            Network __DSPOT_network_8 = new Network();
            IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
            Position position = new Position();
            position.set(Position.KEY_IGNITION, true);
            position.setValid(true);
            Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
            position.setNetwork(__DSPOT_network_8);
            __DSPOT_network_8.setWifiAccessPoints(__DSPOT_wifiAccessPoints_131);
            __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_2771);
            org.junit.Assert.fail("testIgnitionEventHandler_mg16_mg383_mg7626 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16_mg362_mg8059_failAssert40() throws Exception {
        try {
            CellTower __DSPOT_cellTower_2924 = new CellTower();
            Collection<CellTower> __DSPOT_cellTowers_124 = Collections.<CellTower>emptyList();
            Network __DSPOT_network_8 = new Network();
            IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
            Position position = new Position();
            position.set(Position.KEY_IGNITION, true);
            position.setValid(true);
            Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
            position.setNetwork(__DSPOT_network_8);
            __DSPOT_network_8.setCellTowers(__DSPOT_cellTowers_124);
            __DSPOT_network_8.addCellTower(__DSPOT_cellTower_2924);
            org.junit.Assert.fail("testIgnitionEventHandler_mg16_mg362_mg8059 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg19_mg592_mg6883() throws Exception {
        Collection<WifiAccessPoint> __DSPOT_wifiAccessPoints_2469 = Collections.singletonList(new WifiAccessPoint());
        Network __DSPOT_network_244 = new Network(new CellTower());
        Date __DSPOT_serverTime_11 = new Date();
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setServerTime(__DSPOT_serverTime_11);
        position.setNetwork(__DSPOT_network_244);
        __DSPOT_network_244.setWifiAccessPoints(__DSPOT_wifiAccessPoints_2469);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }
}


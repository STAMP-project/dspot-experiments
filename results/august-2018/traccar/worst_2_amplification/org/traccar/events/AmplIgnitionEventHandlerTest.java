package org.traccar.events;


import java.util.Collection;
import java.util.Collections;
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
    public void testIgnitionEventHandler_mg19() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getServerTime();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg18() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getProtocol();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getNetwork();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg9() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getAddress();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg21() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getType();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31() throws Exception {
        Network __DSPOT_network_8 = new Network(new CellTower());
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg13() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getFixTime();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg12() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getDeviceTime();
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31_add1030() throws Exception {
        Network __DSPOT_network_8 = new Network(new CellTower());
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        ((IgnitionEventHandler) (ignitionEventHandler)).isSharable();
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg21_remove2011() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getType();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16null838() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getNetwork();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31_mg1040() throws Exception {
        WifiAccessPoint __DSPOT_wifiAccessPoint_248 = new WifiAccessPoint();
        Network __DSPOT_network_8 = new Network(new CellTower());
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
        __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_248);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg12null1258() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getDeviceTime();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_remove6_mg240() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getFixTime();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg18null2603() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getProtocol();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg9_remove796() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getAddress();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg19null2757() throws Exception {
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.getServerTime();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg16_rv830_failAssert3_mg5120() throws Exception {
        try {
            Integer __DSPOT_homeMobileCountryCode_1150 = 209228306;
            Collection<WifiAccessPoint> __DSPOT_wifiAccessPoints_188 = Collections.singletonList(new WifiAccessPoint());
            IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
            Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
            Position position = new Position();
            position.set(Position.KEY_IGNITION, true);
            position.setValid(true);
            Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
            Network __DSPOT_invoc_12 = position.getNetwork();
            __DSPOT_invoc_12.setWifiAccessPoints(__DSPOT_wifiAccessPoints_188);
            org.junit.Assert.fail("testIgnitionEventHandler_mg16_rv830 should have thrown NullPointerException");
            __DSPOT_invoc_12.setHomeMobileCountryCode(__DSPOT_homeMobileCountryCode_1150);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31_mg1093_mg13510() throws Exception {
        WifiAccessPoint __DSPOT_wifiAccessPoint_3190 = new WifiAccessPoint();
        Collection<CellTower> __DSPOT_cellTowers_266 = Collections.singletonList(new CellTower());
        Network __DSPOT_network_8 = new Network(new CellTower());
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_8);
        __DSPOT_network_8.setCellTowers(__DSPOT_cellTowers_266);
        __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_3190);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31_mg1093_mg13506_failAssert23() throws Exception {
        try {
            CellTower __DSPOT_cellTower_3188 = new CellTower();
            Collection<CellTower> __DSPOT_cellTowers_266 = Collections.singletonList(new CellTower());
            Network __DSPOT_network_8 = new Network(new CellTower());
            IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
            Position position = new Position();
            position.set(Position.KEY_IGNITION, true);
            position.setValid(true);
            Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
            position.setNetwork(__DSPOT_network_8);
            __DSPOT_network_8.setCellTowers(__DSPOT_cellTowers_266);
            __DSPOT_network_8.addCellTower(__DSPOT_cellTower_3188);
            org.junit.Assert.fail("testIgnitionEventHandler_mg31_mg1093_mg13506 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg31_mg1117_mg13222_failAssert21() throws Exception {
        try {
            WifiAccessPoint __DSPOT_wifiAccessPoint_3099 = new WifiAccessPoint();
            Collection<WifiAccessPoint> __DSPOT_wifiAccessPoints_271 = Collections.<WifiAccessPoint>emptyList();
            Network __DSPOT_network_8 = new Network(new CellTower());
            IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
            Position position = new Position();
            position.set(Position.KEY_IGNITION, true);
            position.setValid(true);
            Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
            position.setNetwork(__DSPOT_network_8);
            __DSPOT_network_8.setWifiAccessPoints(__DSPOT_wifiAccessPoints_271);
            __DSPOT_network_8.addWifiAccessPoint(__DSPOT_wifiAccessPoint_3099);
            org.junit.Assert.fail("testIgnitionEventHandler_mg31_mg1117_mg13222 should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}


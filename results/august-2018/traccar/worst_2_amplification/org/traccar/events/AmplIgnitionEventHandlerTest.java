package org.traccar.events;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.traccar.BaseTest;
import org.traccar.model.Event;
import org.traccar.model.Network;
import org.traccar.model.Position;
import org.traccar.model.WifiAccessPoint;


public class AmplIgnitionEventHandlerTest extends BaseTest {
    @Test(timeout = 10000)
    public void testIgnitionEventHandler_mg11_mg647() throws Exception {
        Network __DSPOT_network_227 = new Network(new org.traccar.model.CellTower());
        double __DSPOT_course_3 = 0.1467843967493232;
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setCourse(__DSPOT_course_3);
        position.setNetwork(__DSPOT_network_227);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandler_add4_add268_mg4508() throws Exception {
        Network __DSPOT_network_611 = new Network(new org.traccar.model.CellTower());
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_611);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }

    @Test(timeout = 10000)
    public void testIgnitionEventHandlerlitBool2_mg533_mg5473() throws Exception {
        WifiAccessPoint __DSPOT_wifiAccessPoint_1576 = new WifiAccessPoint();
        Network __DSPOT_network_113 = new Network();
        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler();
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(false);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        position.setNetwork(__DSPOT_network_113);
        __DSPOT_network_113.addWifiAccessPoint(__DSPOT_wifiAccessPoint_1576);
        Assert.assertTrue(((IgnitionEventHandler) (ignitionEventHandler)).isSharable());
    }
}


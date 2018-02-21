package org.traccar.events;


public class AmplIgnitionEventHandlerTest extends org.traccar.BaseTest {
    @org.junit.Test
    public void testIgnitionEventHandler() throws java.lang.Exception {
        org.traccar.events.IgnitionEventHandler ignitionEventHandler = new org.traccar.events.IgnitionEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_IGNITION, true);
        position.setValid(true);
        java.util.Collection<org.traccar.model.Event> events = ignitionEventHandler.analyzePosition(position);
        org.junit.Assert.assertEquals(events, null);
    }
}


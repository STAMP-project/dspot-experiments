

package org.traccar.events;


public class AmplCommandResultEventHandlerTest extends org.traccar.BaseTest {
    @org.junit.Test
    public void testCommandResultEventHandler() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 10000)
    public void testCommandResultEventHandler_cf8() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        org.traccar.model.Event vc_1 = new org.traccar.model.Event();
        // AssertGenerator replace invocation
        java.util.Date o_testCommandResultEventHandler_cf8__13 = // StatementAdderMethod cloned existing statement
vc_1.getServerTime();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testCommandResultEventHandler_cf8__13);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 10000)
    public void testCommandResultEventHandler_cf13() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // AssertGenerator replace invocation
        long o_testCommandResultEventHandler_cf13__11 = // StatementAdderMethod cloned existing statement
event.getPositionId();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testCommandResultEventHandler_cf13__11, 0L);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 10000)
    public void testCommandResultEventHandler_cf10() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // AssertGenerator replace invocation
        long o_testCommandResultEventHandler_cf10__11 = // StatementAdderMethod cloned existing statement
event.getGeofenceId();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testCommandResultEventHandler_cf10__11, 0L);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 10000)
    public void testCommandResultEventHandler_cf11() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        org.traccar.model.Event vc_3 = new org.traccar.model.Event();
        // AssertGenerator replace invocation
        long o_testCommandResultEventHandler_cf11__13 = // StatementAdderMethod cloned existing statement
vc_3.getGeofenceId();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testCommandResultEventHandler_cf11__13, 0L);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }
}


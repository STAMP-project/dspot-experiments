

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
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf17() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        long vc_8 = -5499534280882810483L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -5499534280882810483L);
        // StatementAdderOnAssert create random local variable
        org.traccar.model.Event vc_7 = new org.traccar.model.Event();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Event)vc_7).getPositionId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Event)vc_7).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Event)vc_7).getGeofenceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Event)vc_7).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Event)vc_7).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Event)vc_7).getType());
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_2051256467 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_2051256467, ((org.traccar.model.Event)vc_7).getAttributes());;
        // StatementAdderMethod cloned existing statement
        vc_7.setGeofenceId(vc_8);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf23() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create null value
        java.util.Date vc_14 = (java.util.Date)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // StatementAdderMethod cloned existing statement
        event.setServerTime(vc_14);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
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
        junit.framework.Assert.assertEquals(o_testCommandResultEventHandler_cf10__11, 0L);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf23_cf622_literalMutation6037() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create null value
        java.util.Date vc_14 = (java.util.Date)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // StatementAdderMethod cloned existing statement
        event.setServerTime(vc_14);
        // AssertGenerator replace invocation
        java.util.Date o_testCommandResultEventHandler_cf23_cf622__17 = // StatementAdderMethod cloned existing statement
event.getServerTime();
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testCommandResultEventHandler_cf23_cf622__17);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf19_literalMutation477_cf5397() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        long vc_11 = 8869001208729428908L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // StatementAdderMethod cloned existing statement
        event.setPositionId(vc_11);
        // AssertGenerator replace invocation
        long o_testCommandResultEventHandler_cf19_literalMutation477_cf5397__20 = // StatementAdderMethod cloned existing statement
event.getGeofenceId();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCommandResultEventHandler_cf19_literalMutation477_cf5397__20, 0L);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf16_cf366_cf5142_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
            java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
            org.junit.Assert.assertNotNull(events);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
            // StatementAdderOnAssert create random local variable
            long vc_8 = -5499534280882810483L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_8, -5499534280882810483L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_8, -5499534280882810483L);
            // StatementAdderMethod cloned existing statement
            event.setGeofenceId(vc_8);
            // AssertGenerator replace invocation
            long o_testCommandResultEventHandler_cf16_cf366__17 = // StatementAdderMethod cloned existing statement
event.getGeofenceId();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCommandResultEventHandler_cf16_cf366__17, -5499534280882810483L);
            // StatementAdderOnAssert create null value
            org.traccar.model.Event vc_2144 = (org.traccar.model.Event)null;
            // StatementAdderMethod cloned existing statement
            vc_2144.getServerTime();
            // MethodAssertGenerator build local variable
            Object o_27_0 = event.getType();
            org.junit.Assert.fail("testCommandResultEventHandler_cf16_cf366_cf5142 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.events.CommandResultEventHandlerTest#testCommandResultEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testCommandResultEventHandler_cf19_cf492_cf5789() throws java.lang.Exception {
        org.traccar.events.CommandResultEventHandler commandResultEventHandler = new org.traccar.events.CommandResultEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_RESULT, "Test Result");
        java.util.Collection<org.traccar.model.Event> events = commandResultEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        long vc_11 = 8869001208729428908L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_11, 8869001208729428908L);
        // StatementAdderMethod cloned existing statement
        event.setPositionId(vc_11);
        // AssertGenerator replace invocation
        long o_testCommandResultEventHandler_cf19_cf492__17 = // StatementAdderMethod cloned existing statement
event.getPositionId();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCommandResultEventHandler_cf19_cf492__17, 8869001208729428908L);
        // StatementAdderMethod cloned existing statement
        event.setGeofenceId(vc_11);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_COMMAND_RESULT, event.getType());
    }
}


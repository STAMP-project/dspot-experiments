

package org.traccar.events;


public class AmplAlertEventHandlerTest extends org.traccar.BaseTest {
    @org.junit.Test
    public void testAlertEventHandler() throws java.lang.Exception {
        org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
        java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_ALARM, event.getType());
    }

    /* amplification of org.traccar.events.AlertEventHandlerTest#testAlertEventHandler */
    @org.junit.Test(timeout = 10000)
    public void testAlertEventHandler_cf14() throws java.lang.Exception {
        org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
        java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        org.traccar.model.Position vc_3 = new org.traccar.model.Position();
        // StatementAdderOnAssert create random local variable
        org.traccar.events.AlertEventHandler vc_1 = new org.traccar.events.AlertEventHandler();
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Event> o_testAlertEventHandler_cf14__15 = // StatementAdderMethod cloned existing statement
vc_1.analyzePosition(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testAlertEventHandler_cf14__15);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_ALARM, event.getType());
    }

    /* amplification of org.traccar.events.AlertEventHandlerTest#testAlertEventHandler */
    /* amplification of org.traccar.events.AlertEventHandlerTest#testAlertEventHandler_cf11 */
    @org.junit.Test(timeout = 10000)
    public void testAlertEventHandler_cf11_cf26_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
            java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_3 = new org.traccar.model.Position();
            // AssertGenerator replace invocation
            java.util.Collection<org.traccar.model.Event> o_testAlertEventHandler_cf11__13 = // StatementAdderMethod cloned existing statement
alertEventHandler.analyzePosition(vc_3);
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testAlertEventHandler_cf11__13;
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_6 = (org.traccar.model.Position)null;
            // StatementAdderOnAssert create random local variable
            org.traccar.events.AlertEventHandler vc_5 = new org.traccar.events.AlertEventHandler();
            // StatementAdderMethod cloned existing statement
            vc_5.analyzePosition(vc_6);
            // MethodAssertGenerator build local variable
            Object o_23_0 = event.getType();
            org.junit.Assert.fail("testAlertEventHandler_cf11_cf26 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


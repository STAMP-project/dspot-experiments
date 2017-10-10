

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
    @org.junit.Test(timeout = 1000)
    public void testAlertEventHandler_cf17() throws java.lang.Exception {
        org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
        java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        // StatementAdderOnAssert create random local variable
        org.traccar.model.Position vc_3 = new org.traccar.model.Position();
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getProtocol());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getDeviceTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getSpeed(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getAltitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getType());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getCourse(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getLatitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_3).getOutdated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getNetwork());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_3).getValid());
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_525916187 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_525916187, ((org.traccar.model.Position)vc_3).getAttributes());;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getAddress());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getLongitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_3).getAccuracy(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_3).getFixTime());
        // StatementAdderOnAssert create random local variable
        org.traccar.events.AlertEventHandler vc_1 = new org.traccar.events.AlertEventHandler();
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Event> o_testAlertEventHandler_cf17__15 = // StatementAdderMethod cloned existing statement
vc_1.analyzePosition(vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testAlertEventHandler_cf17__15);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_ALARM, event.getType());
    }

    /* amplification of org.traccar.events.AlertEventHandlerTest#testAlertEventHandler */
    @org.junit.Test(timeout = 1)
    public void testAlertEventHandler_cf7_failAssert4_literalMutation88() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
            java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
            org.junit.Assert.assertNotNull(events);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
            // StatementAdderOnAssert create null value
            org.traccar.events.AlertEventHandler vc_0 = (org.traccar.events.AlertEventHandler)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_0);
            // StatementAdderMethod cloned existing statement
            vc_0.analyzePosition(position);
            // MethodAssertGenerator build local variable
            Object o_15_0 = event.getType();
            org.junit.Assert.fail("testAlertEventHandler_cf7 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.events.AlertEventHandlerTest#testAlertEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testAlertEventHandler_literalMutation2_failAssert0_cf29_add2973() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.AlertEventHandler alertEventHandler = new org.traccar.events.AlertEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            // MethodCallAdder
            position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
            position.set(org.traccar.model.Position.KEY_ALARM, org.traccar.model.Position.ALARM_GENERAL);
            java.util.Collection<org.traccar.model.Event> events = alertEventHandler.analyzePosition(position);
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_7 = new org.traccar.model.Position();
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getOutdated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getFixTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAccuracy(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getDeviceTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_677729751 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_677729751, ((org.traccar.model.Position)vc_7).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLongitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getOutdated());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getFixTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getAccuracy(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_7).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getDeviceTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_1189364663 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1189364663, ((org.traccar.model.Position)vc_7).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLongitude(), 0.0D);
            // AssertGenerator replace invocation
            java.util.Collection<org.traccar.model.Event> o_testAlertEventHandler_literalMutation2_failAssert0_cf29__12 = // StatementAdderMethod cloned existing statement
alertEventHandler.analyzePosition(vc_7);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_testAlertEventHandler_literalMutation2_failAssert0_cf29__12);
            org.junit.Assert.assertNotNull(events);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[1]));
            // MethodAssertGenerator build local variable
            Object o_12_0 = event.getType();
            org.junit.Assert.fail("testAlertEventHandler_literalMutation2 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }
}


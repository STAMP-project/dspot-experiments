

package org.traccar.events;


public class AmplMotionEventHandlerTest extends org.traccar.BaseTest {
    @org.junit.Test
    public void testMotionEventHandler() throws java.lang.Exception {
        org.traccar.events.MotionEventHandler motionEventHandler = new org.traccar.events.MotionEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.setSpeed(10.0);
        position.setValid(true);
        java.util.Collection<org.traccar.model.Event> events = motionEventHandler.analyzePosition(position);
        org.junit.Assert.assertNotNull(events);
        org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_DEVICE_MOVING, event.getType());
    }

    /* amplification of org.traccar.events.MotionEventHandlerTest#testMotionEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testMotionEventHandler_cf14() throws java.lang.Exception {
        org.traccar.events.MotionEventHandler motionEventHandler = new org.traccar.events.MotionEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.setSpeed(10.0);
        position.setValid(true);
        java.util.Collection<org.traccar.model.Event> events = motionEventHandler.analyzePosition(position);
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
        java.util.LinkedHashMap map_1526597110 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1526597110, ((org.traccar.model.Position)vc_3).getAttributes());;
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
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Event> o_testMotionEventHandler_cf14__14 = // StatementAdderMethod cloned existing statement
motionEventHandler.analyzePosition(vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testMotionEventHandler_cf14__14);
        org.junit.Assert.assertEquals(org.traccar.model.Event.TYPE_DEVICE_MOVING, event.getType());
    }

    /* amplification of org.traccar.events.MotionEventHandlerTest#testMotionEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testMotionEventHandler_literalMutation3_failAssert0_cf35() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.MotionEventHandler motionEventHandler = new org.traccar.events.MotionEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.setSpeed(10.0);
            position.setValid(true);
            java.util.Collection<org.traccar.model.Event> events = motionEventHandler.analyzePosition(position);
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
            java.util.LinkedHashMap map_2027227032 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_2027227032, ((org.traccar.model.Position)vc_7).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_7).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_7).getLongitude(), 0.0D);
            // StatementAdderOnAssert create random local variable
            org.traccar.events.MotionEventHandler vc_5 = new org.traccar.events.MotionEventHandler();
            // AssertGenerator replace invocation
            java.util.Collection<org.traccar.model.Event> o_testMotionEventHandler_literalMutation3_failAssert0_cf35__15 = // StatementAdderMethod cloned existing statement
vc_5.analyzePosition(vc_7);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_testMotionEventHandler_literalMutation3_failAssert0_cf35__15);
            org.junit.Assert.assertNotNull(events);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[1]));
            // MethodAssertGenerator build local variable
            Object o_13_0 = event.getType();
            org.junit.Assert.fail("testMotionEventHandler_literalMutation3 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of org.traccar.events.MotionEventHandlerTest#testMotionEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testMotionEventHandler_cf7_failAssert3_cf90_cf379() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.events.MotionEventHandler motionEventHandler = new org.traccar.events.MotionEventHandler();
            org.traccar.model.Position position = new org.traccar.model.Position();
            position.setSpeed(10.0);
            position.setValid(true);
            java.util.Collection<org.traccar.model.Event> events = motionEventHandler.analyzePosition(position);
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_19 = new org.traccar.model.Position();
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_19).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getDeviceTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getFixTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_19).getOutdated());
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_1742656593 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1742656593, ((org.traccar.model.Position)vc_19).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getLongitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getAccuracy(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_19).getValid());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getType());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getNetwork());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getDeviceTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getLatitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getServerTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getDeviceId(), 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getAltitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getFixTime());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getSpeed(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(((org.traccar.model.Position)vc_19).getOutdated());
            // AssertGenerator add assertion
            java.util.LinkedHashMap map_1403526666 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1403526666, ((org.traccar.model.Position)vc_19).getAttributes());;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getAddress());
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(((org.traccar.model.Position)vc_19).getProtocol());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getCourse(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getLongitude(), 0.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.model.Position)vc_19).getAccuracy(), 0.0D);
            // StatementAdderOnAssert create random local variable
            org.traccar.events.MotionEventHandler vc_17 = new org.traccar.events.MotionEventHandler();
            // AssertGenerator replace invocation
            java.util.Collection<org.traccar.model.Event> o_testMotionEventHandler_cf7_failAssert3_cf90__15 = // StatementAdderMethod cloned existing statement
vc_17.analyzePosition(vc_19);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(o_testMotionEventHandler_cf7_failAssert3_cf90__15);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_82 = (org.traccar.model.Position)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_82);
            // StatementAdderOnAssert create null value
            org.traccar.events.MotionEventHandler vc_80 = (org.traccar.events.MotionEventHandler)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_80);
            // StatementAdderMethod cloned existing statement
            vc_80.analyzePosition(vc_82);
            org.junit.Assert.assertNotNull(events);
            org.traccar.model.Event event = ((org.traccar.model.Event) (events.toArray()[0]));
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_2 = (org.traccar.model.Position)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // StatementAdderOnAssert create null value
            org.traccar.events.MotionEventHandler vc_0 = (org.traccar.events.MotionEventHandler)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_0);
            // StatementAdderMethod cloned existing statement
            vc_0.analyzePosition(vc_2);
            // MethodAssertGenerator build local variable
            Object o_18_0 = event.getType();
            org.junit.Assert.fail("testMotionEventHandler_cf7 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


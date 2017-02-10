

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

    /* amplification of org.traccar.events.IgnitionEventHandlerTest#testIgnitionEventHandler */
    @org.junit.Test(timeout = 1000)
    public void testIgnitionEventHandler_cf14() throws java.lang.Exception {
        org.traccar.events.IgnitionEventHandler ignitionEventHandler = new org.traccar.events.IgnitionEventHandler();
        org.traccar.model.Position position = new org.traccar.model.Position();
        position.set(org.traccar.model.Position.KEY_IGNITION, true);
        position.setValid(true);
        java.util.Collection<org.traccar.model.Event> events = ignitionEventHandler.analyzePosition(position);
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
        java.util.LinkedHashMap map_1701035045 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1701035045, ((org.traccar.model.Position)vc_3).getAttributes());;
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
        org.traccar.events.IgnitionEventHandler vc_1 = new org.traccar.events.IgnitionEventHandler();
        // AssertGenerator replace invocation
        java.util.Collection<org.traccar.model.Event> o_testIgnitionEventHandler_cf14__13 = // StatementAdderMethod cloned existing statement
vc_1.analyzePosition(vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testIgnitionEventHandler_cf14__13);
        org.junit.Assert.assertEquals(events, null);
    }
}


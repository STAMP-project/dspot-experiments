

package org.traccar;


public class AmplFilterHandlerTest extends org.traccar.BaseTest {
    private org.traccar.FilterHandler filtingHandler;

    private org.traccar.FilterHandler passingHandler;

    @org.junit.Before
    public void setUp() {
        passingHandler = new org.traccar.FilterHandler();
        filtingHandler = new org.traccar.FilterHandler();
        filtingHandler.setFilterInvalid(true);
        filtingHandler.setFilterZero(true);
        filtingHandler.setFilterDuplicate(true);
        filtingHandler.setFilterApproximate(true);
        filtingHandler.setFilterStatic(true);
        filtingHandler.setFilterDistance(10);
        filtingHandler.setFilterLimit(10);
        filtingHandler.setFilterFuture((5 * 60));
    }

    @org.junit.After
    public void tearDown() {
        filtingHandler = null;
        passingHandler = null;
    }

    private org.traccar.model.Position createPosition(long deviceId, java.util.Date time, boolean valid, double latitude, double longitude, double altitude, double speed, double course) {
        org.traccar.model.Position p = new org.traccar.model.Position();
        p.setDeviceId(deviceId);
        p.setTime(time);
        p.setValid(valid);
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        p.setAltitude(altitude);
        p.setSpeed(speed);
        p.setCourse(course);
        return p;
    }

    @org.junit.Test
    public void testFilterInvalid() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 10000)
    public void testFilterInvalid_cf94_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_4_0 = filtingHandler.decode(null, null, position);
            // MethodAssertGenerator build local variable
            Object o_6_0 = passingHandler.decode(null, null, position);
            position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_11_0 = filtingHandler.decode(null, null, position);
            // MethodAssertGenerator build local variable
            Object o_13_0 = passingHandler.decode(null, null, position);
            position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_18_0 = filtingHandler.decode(null, null, position);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_2 = (org.traccar.model.Position)null;
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_1 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_1.handlePosition(vc_2);
            // MethodAssertGenerator build local variable
            Object o_26_0 = passingHandler.decode(null, null, position);
            org.junit.Assert.fail("testFilterInvalid_cf94 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 10000)
    public void testFilterInvalid_cf99_cf223_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_4_0 = filtingHandler.decode(null, null, position);
            // MethodAssertGenerator build local variable
            Object o_6_0 = passingHandler.decode(null, null, position);
            position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_11_0 = filtingHandler.decode(null, null, position);
            // MethodAssertGenerator build local variable
            Object o_13_0 = passingHandler.decode(null, null, position);
            position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
            // MethodAssertGenerator build local variable
            Object o_18_0 = filtingHandler.decode(null, null, position);
            // StatementAdderOnAssert create literal from method
            boolean boolean_vc_0 = true;
            // MethodAssertGenerator build local variable
            Object o_22_0 = boolean_vc_0;
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_5 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_5.setFilterApproximate(boolean_vc_0);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_30 = (org.traccar.model.Position)null;
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_29 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_29.handlePosition(vc_30);
            // MethodAssertGenerator build local variable
            Object o_34_0 = passingHandler.decode(null, null, position);
            org.junit.Assert.fail("testFilterInvalid_cf99_cf223 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


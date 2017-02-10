

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
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf7_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            // StatementAddOnAssert local variable replacement
            org.traccar.model.Position lastPosition = org.traccar.Context.getIdentityManager().getLastPosition(position.getDeviceId());
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_1 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_1.handlePosition(lastPosition);
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            org.junit.Assert.fail("testFilterInvalid_cf7 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf19() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_2 = true;
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_2);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_11 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_11.setFilterDuplicate(boolean_vc_2);
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf8() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
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
        java.util.LinkedHashMap map_419994247 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_419994247, ((org.traccar.model.Position)vc_3).getAttributes());;
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
        org.traccar.FilterHandler vc_1 = new org.traccar.FilterHandler();
        // AssertGenerator replace invocation
        org.traccar.model.Position o_testFilterInvalid_cf8__24 = // StatementAdderMethod cloned existing statement
vc_1.handlePosition(vc_3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getNetwork());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getFixTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getOutdated());
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getValid());
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_658799445 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_658799445, ((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getAttributes());;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getType());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getProtocol());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getCourse(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getAccuracy(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getAddress());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getDeviceTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getSpeed(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getLatitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getAltitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf8__24).getLongitude(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testFilterInvalid_cf8__24.equals(vc_3));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf32_cf2224() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        // StatementAdderOnAssert create random local variable
        long vc_21 = 1374456570953599299L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_21, 1374456570953599299L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_21, 1374456570953599299L);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_20 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_20.setFilterLimit(vc_21);
        // StatementAdderOnAssert create random local variable
        long vc_883 = -940359942044553523L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_883, -940359942044553523L);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_882 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_882.setFilterFuture(vc_883);
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf5_failAssert4_cf307() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_2 = (org.traccar.model.Position)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_1 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_1.handlePosition(vc_2);
            // StatementAdderOnAssert create random local variable
            int vc_149 = -2130656791;
            // StatementAdderMethod cloned existing statement
            vc_1.setFilterDistance(vc_149);
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            org.junit.Assert.fail("testFilterInvalid_cf5 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf23_cf1520_failAssert1_cf3736() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
            org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
            // StatementAddOnAssert local variable replacement
            long deviceId = position.getDeviceId();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(deviceId, 0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(deviceId, 0L);
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_14 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_14.setFilterFuture(deviceId);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_618 = (org.traccar.model.Position)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_618);
            // StatementAdderMethod cloned existing statement
            vc_14.handlePosition(vc_618);
            // StatementAdderOnAssert create random local variable
            org.traccar.model.Position vc_1431 = new org.traccar.model.Position();
            // StatementAdderOnAssert create random local variable
            org.traccar.FilterHandler vc_1429 = new org.traccar.FilterHandler();
            // StatementAdderMethod cloned existing statement
            vc_1429.handlePosition(vc_1431);
            org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
            org.junit.Assert.fail("testFilterInvalid_cf23_cf1520 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf35_cf2437_cf5807() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_4 = true;
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_4);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_23 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_23.setFilterStatic(boolean_vc_4);
        // StatementAdderOnAssert create random local variable
        int vc_961 = 744805883;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_961, 744805883);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_961, 744805883);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_960 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_960.setFilterDistance(vc_961);
        // StatementAdderMethod cloned existing statement
        vc_23.setFilterInvalid(boolean_vc_4);
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }

    /* amplification of org.traccar.FilterHandlerTest#testFilterInvalid */
    @org.junit.Test(timeout = 1000)
    public void testFilterInvalid_cf39_cf2767_cf17672() throws java.lang.Exception {
        org.traccar.model.Position position = createPosition(0, new java.util.Date(), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNotNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(java.lang.Long.MAX_VALUE), true, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
        position = createPosition(0, new java.util.Date(), false, 10, 10, 10, 10, 10);
        org.junit.Assert.assertNull(filtingHandler.decode(null, null, position));
        // StatementAdderOnAssert create literal from method
        boolean boolean_vc_5 = true;
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_5);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(boolean_vc_5);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_26 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_26.setFilterZero(boolean_vc_5);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_1081 = new org.traccar.FilterHandler();
        // StatementAdderMethod cloned existing statement
        vc_1081.setFilterInvalid(boolean_vc_5);
        // StatementAdderOnAssert create random local variable
        org.traccar.FilterHandler vc_6273 = new org.traccar.FilterHandler();
        // AssertGenerator replace invocation
        org.traccar.model.Position o_testFilterInvalid_cf39_cf2767_cf17672__36 = // StatementAdderMethod cloned existing statement
vc_6273.handlePosition(position);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getAccuracy(), 0.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getType());
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getServerTime());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getDeviceId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getNetwork());
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testFilterInvalid_cf39_cf2767_cf17672__36.equals(position));
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getProtocol());
        // AssertGenerator add assertion
        java.util.LinkedHashMap map_1813576913 = new java.util.LinkedHashMap<Object, Object>();	junit.framework.Assert.assertEquals(map_1813576913, ((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getAttributes());;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getCourse(), 10.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getId(), 0L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getSpeed(), 10.0D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(((org.traccar.model.Position)o_testFilterInvalid_cf39_cf2767_cf17672__36).getAddress());
        org.junit.Assert.assertNotNull(passingHandler.decode(null, null, position));
    }
}


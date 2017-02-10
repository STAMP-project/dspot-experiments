

package org.traccar.geofence;


public class AmplGeofencePolylineTest {
    @org.junit.Test
    public void testPolylineWkt() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    @org.junit.Test
    public void testContainsPolyline1Interval() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (56.83777 60.59833, 56.83766 60.5968)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 35);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(56.83801, 60.59748));
        ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83801, 60.59748))));
    }

    @org.junit.Test
    public void testContainsPolyline3Intervals() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(56.83847, 60.60458));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83764, 60.59725))));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83861, 60.60822))));
    }

    @org.junit.Test
    public void testContainsPolylineNear180() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (66.9494 179.838, 66.9508 -179.8496)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.95, 180.0));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.96, 180.0))));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.9509, (-179.83)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test
    public void testContainsPolyline1Interval_literalMutation3_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.83777 60.59833, 56.83766*60.5968)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 35);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_literalMutation3 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test
    public void testContainsPolyline1Interval_literalMutation2_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 35);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_literalMutation2 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test
    public void testContainsPolyline1Interval_literalMutation11_literalMutation232_failAssert11() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.83}77 60.59833, 56.83766 60.5968)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 36);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)geofenceGeometry).toWkt(), "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
            // MethodAssertGenerator build local variable
            Object o_7_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_literalMutation11_literalMutation232 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolyline1Interval_cf29_cf423() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (56.83777 60.59833, 56.83766 60.5968)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 35);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(56.83801, 60.59748));
        ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "LINESTRING (56.83777 60.59833, 56.83766 60.5968)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_1);
        // AssertGenerator replace invocation
        java.lang.String o_testContainsPolyline1Interval_cf29_cf423__13 = // StatementAdderMethod cloned existing statement
geofenceGeometry.toWkt();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testContainsPolyline1Interval_cf29_cf423__13, "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83801, 60.59748))));
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test
    public void testContainsPolyline1Interval_literalMutation3_failAssert1_literalMutation52_literalMutation3061() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "S@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*Odwpa";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "S@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*Odwpa");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "S@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-ef&bk*201yCi*Odwpa");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 34);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_literalMutation3 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test
    public void testContainsPolyline1Interval_literalMutation4_failAssert2_literalMutation62_literalMutation968() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 60);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_literalMutation4 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline1Interval */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolyline1Interval_cf32_add471_cf2727_failAssert48() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.83777 60.59833, 56.83766 60.5968)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 35);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83801, 60.59748);
            ((org.traccar.geofence.GeofencePolyline) (geofenceGeometry)).setDistance(15);
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_6 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_6.fromWkt(test);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_6).toWkt(), "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(test);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_6).toWkt(), "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_6).toWkt(), "LINESTRING (56.83777 60.59833, 56.83766 60.5968)");
            // StatementAdderOnAssert create null value
            org.traccar.geofence.GeofencePolyline vc_495 = (org.traccar.geofence.GeofencePolyline)null;
            // StatementAdderMethod cloned existing statement
            vc_495.toWkt();
            // MethodAssertGenerator build local variable
            Object o_24_0 = !(geofenceGeometry.containsPoint(56.83801, 60.59748));
            org.junit.Assert.fail("testContainsPolyline1Interval_cf32_add471_cf2727 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test
    public void testContainsPolyline3Intervals_literalMutation4264_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60].60811, 56.83782 60.5988)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83847, 60.60458);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(56.83764, 60.59725));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(56.83861, 60.60822));
            org.junit.Assert.fail("testContainsPolyline3Intervals_literalMutation4264 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test
    public void testContainsPolyline3Intervals_literalMutation4265_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.836 60.6126, 56$8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83847, 60.60458);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(56.83764, 60.59725));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(56.83861, 60.60822));
            org.junit.Assert.fail("testContainsPolyline3Intervals_literalMutation4265 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test
    public void testContainsPolyline3Intervals_literalMutation4263_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83847, 60.60458);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(56.83764, 60.59725));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(56.83861, 60.60822));
            org.junit.Assert.fail("testContainsPolyline3Intervals_literalMutation4263 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolyline3Intervals_cf4294_add4750() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(56.83847, 60.60458));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83764, 60.59725))));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_196 = "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_196, "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_196, "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolyline vc_786 = new org.traccar.geofence.GeofencePolyline();
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_786.fromWkt(String_vc_196);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
        // StatementAdderMethod cloned existing statement
        vc_786.fromWkt(String_vc_196);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(56.83861, 60.60822))));
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolyline3Intervals_cf4292_add4709_cf7309_failAssert28() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 15);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83847, 60.60458);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(56.83764, 60.59725));
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_786 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_786.fromWkt(test);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
            // StatementAdderMethod cloned existing statement
            vc_786.fromWkt(test);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_786).toWkt(), "LINESTRING (56.836 60.6126, 56.8393 60.6114, 56.83887 60.60811, 56.83782 60.5988)");
            // StatementAdderOnAssert create null value
            java.lang.String vc_1303 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_786.fromWkt(vc_1303);
            // MethodAssertGenerator build local variable
            Object o_26_0 = !(geofenceGeometry.containsPoint(56.83861, 60.60822));
            org.junit.Assert.fail("testContainsPolyline3Intervals_cf4292_add4709_cf7309 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolyline3Intervals */
    @org.junit.Test
    public void testContainsPolyline3Intervals_literalMutation4266_failAssert3_literalMutation4369_literalMutation7199() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 14);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(56.83847, 60.60458);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(56.83764, 60.59725));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(56.83861, 60.60822));
            org.junit.Assert.fail("testContainsPolyline3Intervals_literalMutation4266 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test
    public void testContainsPolylineNear180_literalMutation7531_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (66.9494 179.838,S66.9508 -179.8496)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(66.95, 180.0);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(66.96, 180.0));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(66.9509, (-179.83)));
            org.junit.Assert.fail("testContainsPolylineNear180_literalMutation7531 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test
    public void testContainsPolylineNear180_literalMutation7528_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(66.95, 180.0);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(66.96, 180.0));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(66.9509, (-179.83)));
            org.junit.Assert.fail("testContainsPolylineNear180_literalMutation7528 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test
    public void testContainsPolylineNear180_literalMutation7530_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (66.9494 K179.838, 66.9508 -179.8496)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(66.95, 180.0);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(66.96, 180.0));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(66.9509, (-179.83)));
            org.junit.Assert.fail("testContainsPolylineNear180_literalMutation7530 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test
    public void testContainsPolylineNear180_literalMutation7529_failAssert1_literalMutation7599() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "etFYCDOR]<.$/D.G;=N/s8uuu[g!ILJ4M#{|<kqs}$OfXk;";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "etFYCDOR]<.$/D.G;=N/s8uuu[g!ILJ4M#{|<kqs}$OfXk;");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
            // MethodAssertGenerator build local variable
            Object o_4_0 = geofenceGeometry.containsPoint(66.95, 180.0);
            // MethodAssertGenerator build local variable
            Object o_6_0 = !(geofenceGeometry.containsPoint(66.96, 180.0));
            // MethodAssertGenerator build local variable
            Object o_9_0 = !(geofenceGeometry.containsPoint(66.9509, 359.66));
            org.junit.Assert.fail("testContainsPolylineNear180_literalMutation7529 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test
    public void testContainsPolylineNear180_literalMutation7536_literalMutation7763_literalMutation9217_failAssert9() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINE7TRING (66.9494 179.838, 66.9508 -179.8496)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 50);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)geofenceGeometry).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)geofenceGeometry).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
            // MethodAssertGenerator build local variable
            Object o_9_0 = geofenceGeometry.containsPoint(66.95, 180.0);
            // MethodAssertGenerator build local variable
            Object o_11_0 = !(geofenceGeometry.containsPoint(66.96, 180.0));
            // MethodAssertGenerator build local variable
            Object o_14_0 = !(geofenceGeometry.containsPoint(66.9509, 89.915));
            org.junit.Assert.fail("testContainsPolylineNear180_literalMutation7536_literalMutation7763_literalMutation9217 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testContainsPolylineNear180 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolylineNear180_cf7563_literalMutation8093_add11097() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (66.9494 179.838, 66.9508 -179.8496)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline(test, 25);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.95, 180.0));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.96, 180.0))));
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolyline vc_1338 = new org.traccar.geofence.GeofencePolyline();
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_1338.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_1338).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
        // StatementAdderMethod cloned existing statement
        vc_1338.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_1338).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_1338).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_1338).toWkt(), "LINESTRING (66.9494 179.838, 66.9508 -179.8496)");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.9509, (-179.83)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test
    public void testPolylineWkt_literalMutation11874_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_literalMutation11874 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11893() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_510);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test
    public void testPolylineWkt_literalMutation11875_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.75474 37.61823, 55i75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_literalMutation11875 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test
    public void testPolylineWkt_literalMutation11876_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 !37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_literalMutation11876 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11898_literalMutation12127_failAssert20() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "[C]<[QAnOmBKz(8nVg=[d2#BuH``AKvJ$e=t%!boI]dz:RW+*Es_9pK!,DKBL\\v-\\F./kFjZXM4!/ZWFW;9/Q";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            vc_2046.fromWkt(String_vc_510);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // MethodAssertGenerator build local variable
            Object o_15_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_cf11898_literalMutation12127 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test
    public void testPolylineWkt_literalMutation11876_failAssert2_literalMutation11917() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 !37.61888 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "LINESTRING (55.75474 37.61823, 55.75513 !37.61888 55.7535 37.6222, 55.75315 37.62165)");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_literalMutation11876 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11898_literalMutation12129_failAssert28() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.7547X4 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            vc_2046.fromWkt(String_vc_510);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // MethodAssertGenerator build local variable
            Object o_15_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_cf11898_literalMutation12129 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11896_add12088() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
        // StatementAdderMethod cloned existing statement
        vc_2046.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11888_failAssert8_literalMutation12022_failAssert10() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 P37.6222, 55.75315 37.62165)";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolyline vc_2045 = (org.traccar.geofence.GeofencePolyline)null;
                // StatementAdderMethod cloned existing statement
                vc_2045.fromWkt(String_vc_510);
                // MethodAssertGenerator build local variable
                Object o_11_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolylineWkt_cf11888 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolylineWkt_cf11888_failAssert8_literalMutation12022 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11898_cf12141_failAssert60_literalMutation14685_failAssert33() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888,u55.7535 37.6222, 55.75315 37.62165)";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
                // StatementAdderMethod cloned existing statement
                vc_2046.fromWkt(String_vc_510);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_2108 = new java.lang.String();
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolyline vc_2105 = (org.traccar.geofence.GeofencePolyline)null;
                // StatementAdderMethod cloned existing statement
                vc_2105.fromWkt(vc_2108);
                // MethodAssertGenerator build local variable
                Object o_21_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolylineWkt_cf11898_cf12141 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolylineWkt_cf11898_cf12141_failAssert60_literalMutation14685 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11902_cf12207_cf13080() throws java.text.ParseException {
        java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create random local variable
        double vc_2051 = 0.936201197893095;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2051, 0.936201197893095D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2051, 0.936201197893095D);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2051, 0.936201197893095D);
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolyline vc_2050 = new org.traccar.geofence.GeofencePolyline();
        // StatementAdderMethod cloned existing statement
        vc_2050.setDistance(vc_2051);
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolyline vc_2262 = new org.traccar.geofence.GeofencePolyline();
        // StatementAdderMethod cloned existing statement
        vc_2262.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2262).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11898_cf12151_failAssert12_literalMutation12667() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888,S 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "LINESTRING (55.75474 37.61823, 55.75513 37.61888,S 55.7535 37.6222, 55.75315 37.62165)");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            vc_2046.fromWkt(String_vc_510);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_2108 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofencePolyline vc_2106 = new org.traccar.geofence.GeofencePolyline();
            // StatementAdderMethod cloned existing statement
            vc_2106.fromWkt(vc_2108);
            // MethodAssertGenerator build local variable
            Object o_21_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_cf11898_cf12151 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11893_cf12062_literalMutation13136_failAssert37() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62t65)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_510);
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_15_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolylineWkt_cf11893_cf12062_literalMutation13136 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolylineTest#testPolylineWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolylineWkt_cf11898_cf12134_failAssert51_literalMutation14200_failAssert11() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "M^$[fN);Oe]7=\\%wP&w) s1}kEJ0+>1vgcmiqKxuaiwSCx5,2@h\\(f #4F{5;X$a1bT?^b:=x#}9@Clh4p?IK";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolyline();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_510 = "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_510, "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolyline vc_2046 = new org.traccar.geofence.GeofencePolyline();
                // StatementAdderMethod cloned existing statement
                vc_2046.fromWkt(String_vc_510);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolyline)vc_2046).toWkt(), "LINESTRING (55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165)");
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolyline vc_2103 = (org.traccar.geofence.GeofencePolyline)null;
                // StatementAdderMethod cloned existing statement
                vc_2103.toWkt();
                // MethodAssertGenerator build local variable
                Object o_19_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolylineWkt_cf11898_cf12134 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolylineWkt_cf11898_cf12134_failAssert51_literalMutation14200 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }
}


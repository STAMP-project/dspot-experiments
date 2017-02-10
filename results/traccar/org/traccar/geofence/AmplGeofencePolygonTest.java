

package org.traccar.geofence;


public class AmplGeofencePolygonTest {
    @org.junit.Test
    public void testPolygonWkt() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    @org.junit.Test
    public void testContainsPolygon() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(55.75476, 37.61915));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(55.75545, 37.61921))));
    }

    @org.junit.Test
    public void testContainsPolygon180() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, (-180.0096)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, 179.991));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.8368, (-179.8792)))));
    }

    @org.junit.Test
    public void testContainsPolygon0() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, (-0.0165)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, 0.018));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(50.9477, 0.5836))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon */
    @org.junit.Test
    public void testContainsPolygon_literalMutation2_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75476, 37.61915);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsPolygon_literalMutation2 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon */
    @org.junit.Test
    public void testContainsPolygon_literalMutation3_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 3*.6222, 55.75315 37.62165))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75476, 37.61915);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsPolygon_literalMutation3 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon */
    @org.junit.Test
    public void testContainsPolygon_literalMutation5_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75}513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75476, 37.61915);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsPolygon_literalMutation5 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon_cf27_add192() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(55.75476, 37.61915));
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_6 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_6.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(55.75545, 37.61921))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon_cf29_cf250_add1271() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(55.75476, 37.61915));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_6 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_6.fromWkt(String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderMethod cloned existing statement
        vc_6.fromWkt(String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_6).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(55.75545, 37.61921))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test
    public void testContainsPolygon0_literalMutation1466_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((51.1Z966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(51.0466, (-0.0165));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(51.0466, 0.018);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(50.9477, 0.5836));
            org.junit.Assert.fail("testContainsPolygon0_literalMutation1466 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test
    public void testContainsPolygon0_literalMutation1465_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(51.0466, (-0.0165));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(51.0466, 0.018);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(50.9477, 0.5836));
            org.junit.Assert.fail("testContainsPolygon0_literalMutation1465 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test
    public void testContainsPolygon0_literalMutation1468_failAssert4() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377T0.5136, 50.8675 -0.6082))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(51.0466, (-0.0165));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(51.0466, 0.018);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(50.9477, 0.5836));
            org.junit.Assert.fail("testContainsPolygon0_literalMutation1468 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon0_cf1491_add1692() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, (-0.0165)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, 0.018));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_55 = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_55, "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_55, "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_55);
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(50.9477, 0.5836))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon0_cf1496_cf1830_add2499() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, (-0.0165)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(51.0466, 0.018));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_55 = "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_55, "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_55, "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_55, "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_249 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_249.fromWkt(String_vc_55);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_249).toWkt(), "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_249).toWkt(), "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_249).toWkt(), "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_285 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_285.fromWkt(String_vc_55);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_285).toWkt(), "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_285).toWkt(), "POLYGON ((51.1966 -0.6207, 51.1897 0.4147, 50.9377 0.5136, 50.8675 -0.6082))");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(50.9477, 0.5836))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon0 */
    @org.junit.Test
    public void testContainsPolygon0_literalMutation1464_failAssert0_literalMutation1500_literalMutation2263() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "I";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "I");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "I");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(51.0466, -0.9835);
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(51.0466, 0.018);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(50.9477, 0.5836));
            org.junit.Assert.fail("testContainsPolygon0_literalMutation1464 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test
    public void testContainsPolygon180_literalMutation3116_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((66.94943179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(66.9015, (-180.0096));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(66.9015, 179.991);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(66.8368, (-179.8792)));
            org.junit.Assert.fail("testContainsPolygon180_literalMutation3116 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test
    public void testContainsPolygon180_literalMutation3115_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((66.9T494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(66.9015, (-180.0096));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(66.9015, 179.991);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(66.8368, (-179.8792)));
            org.junit.Assert.fail("testContainsPolygon180_literalMutation3115 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test
    public void testContainsPolygon180_literalMutation3113_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = ":||.5o(ul,#^ !f#N&7F0o,./FMu#h}RT_Vw|GUZW%l.rTN}^}=4)/68Sy=-Sdtge";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(66.9015, (-180.0096));
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(66.9015, 179.991);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(66.8368, (-179.8792)));
            org.junit.Assert.fail("testContainsPolygon180_literalMutation3113 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon180_cf3150_add3569() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, (-180.0096)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, 179.991));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_89 = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_402 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_402.fromWkt(String_vc_89);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.8368, (-179.8792)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon180_cf3145_literalMutation3468() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, (-180.0096)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, 179.991));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_89 = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_89);
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(89.9396, (-179.8792)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon180_cf3145_literalMutation3466_literalMutation4221() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, (-180.0096)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, 179.991));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_89 = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_89, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_89);
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(359.7584, (-179.8792)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test
    public void testContainsPolygon180_literalMutation3114_failAssert1_literalMutation3178_literalMutation4310() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(66.9015, 60.59725);
            // MethodAssertGenerator build local variable
            Object o_8_0 = geofenceGeometry.containsPoint(66.9015, 179.991);
            // MethodAssertGenerator build local variable
            Object o_10_0 = !(geofenceGeometry.containsPoint(66.8368, 178.8792));
            org.junit.Assert.fail("testContainsPolygon180_literalMutation3114 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testContainsPolygon180 */
    @org.junit.Test(timeout = 1000)
    public void testContainsPolygon180_cf3148_add3529_cf4063() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, (-180.0096)));
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(66.9015, 179.991));
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_402 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_402.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // StatementAdderMethod cloned existing statement
        vc_402.fromWkt(test);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_402).toWkt(), "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        // AssertGenerator replace invocation
        java.lang.String o_testContainsPolygon180_cf3148_add3529_cf4063__23 = // StatementAdderMethod cloned existing statement
vc_402.toWkt();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testContainsPolygon180_cf3148_add3529_cf4063__23, "POLYGON ((66.9494 179.838, 66.9508 -179.8496, 66.8406 -180.0014))");
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(66.8368, (-179.8792)))));
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test
    public void testPolygonWkt_literalMutation5164_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.75474 37.6T823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolygonWkt_literalMutation5164 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test
    public void testPolygonWkt_literalMutation5165_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7-535 37.6222, 55.75315 37.62165))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolygonWkt_literalMutation5165 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5181() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_130);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test
    public void testPolygonWkt_literalMutation5166_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "QkrhR4pBh]li$&pz/(;NtScgM5G##t7oCVTvN<ouT!sw0@E:LsKJHTRroV@bx(+iqw.p)mRh5dfw=(r?tLP,";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolygonWkt_literalMutation5166 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5181_literalMutation5326_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.754l74 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_130);
            // MethodAssertGenerator build local variable
            Object o_11_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolygonWkt_cf5181_literalMutation5326 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5176_failAssert8_literalMutation5300_failAssert25() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "POLYGON ((55.75474:37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolygon vc_590 = (org.traccar.geofence.GeofencePolygon)null;
                // StatementAdderMethod cloned existing statement
                vc_590.fromWkt(String_vc_130);
                // MethodAssertGenerator build local variable
                Object o_11_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolygonWkt_cf5176 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolygonWkt_cf5176_failAssert8_literalMutation5300 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5186_cf5418() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_591 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_591.fromWkt(String_vc_130);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_135 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_135, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderMethod cloned existing statement
        geofenceGeometry.fromWkt(String_vc_135);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5181_literalMutation5326_failAssert2_literalMutation5609() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 3C.61888, 55.7535 37.6222, 55.75315 37.62165))";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "POLYGON ((55.75474 37.61823, 55.75513 3C.61888, 55.7535 37.6222, 55.75315 37.62165))");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_130);
            // MethodAssertGenerator build local variable
            Object o_11_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testPolygonWkt_cf5181_literalMutation5326 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5186_cf5420_failAssert34_literalMutation6412_failAssert10() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "POLYGON ((55.754c4 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolygon vc_591 = new org.traccar.geofence.GeofencePolygon();
                // StatementAdderMethod cloned existing statement
                vc_591.fromWkt(String_vc_130);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create null value
                java.lang.String vc_637 = (java.lang.String)null;
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolygon vc_636 = new org.traccar.geofence.GeofencePolygon();
                // StatementAdderMethod cloned existing statement
                vc_636.fromWkt(vc_637);
                // MethodAssertGenerator build local variable
                Object o_21_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolygonWkt_cf5186_cf5420 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolygonWkt_cf5186_cf5420_failAssert34_literalMutation6412 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5186_add5392_literalMutation5938() throws java.text.ParseException {
        java.lang.String test = "POLYGON ((55.75474 37.61823, 55.7551 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(test, "POLYGON ((55.75474 37.61823, 55.7551 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
        // MethodCallAdder
        geofenceGeometry.fromWkt(test);
        geofenceGeometry.fromWkt(test);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // StatementAdderOnAssert create random local variable
        org.traccar.geofence.GeofencePolygon vc_591 = new org.traccar.geofence.GeofencePolygon();
        // StatementAdderMethod cloned existing statement
        vc_591.fromWkt(String_vc_130);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5186_cf5411_failAssert30_literalMutation6293_failAssert21() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "}?2G^}2<85)!KZa]sC&l w{R,6\\7GeA_@R_&=$>yF2g+eX%k#mSk1i8P` @{ed-aNUomo$O+{R>s%hIgqEFl";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolygon vc_591 = new org.traccar.geofence.GeofencePolygon();
                // StatementAdderMethod cloned existing statement
                vc_591.fromWkt(String_vc_130);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolygon vc_635 = (org.traccar.geofence.GeofencePolygon)null;
                // StatementAdderMethod cloned existing statement
                vc_635.fromWkt(test);
                // MethodAssertGenerator build local variable
                Object o_19_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolygonWkt_cf5186_cf5411 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolygonWkt_cf5186_cf5411_failAssert30_literalMutation6293 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofencePolygonTest#testPolygonWkt */
    @org.junit.Test(timeout = 1000)
    public void testPolygonWkt_cf5186_cf5410_failAssert22_literalMutation6177_failAssert30() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.62222 55.75315 37.62165))";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofencePolygon();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_130 = "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_130, "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofencePolygon vc_591 = new org.traccar.geofence.GeofencePolygon();
                // StatementAdderMethod cloned existing statement
                vc_591.fromWkt(String_vc_130);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofencePolygon)vc_591).toWkt(), "POLYGON ((55.75474 37.61823, 55.75513 37.61888, 55.7535 37.6222, 55.75315 37.62165))");
                // StatementAdderOnAssert create null value
                java.lang.String vc_637 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofencePolygon vc_635 = (org.traccar.geofence.GeofencePolygon)null;
                // StatementAdderMethod cloned existing statement
                vc_635.fromWkt(vc_637);
                // MethodAssertGenerator build local variable
                Object o_21_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testPolygonWkt_cf5186_cf5410 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPolygonWkt_cf5186_cf5410_failAssert22_literalMutation6177 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }
}


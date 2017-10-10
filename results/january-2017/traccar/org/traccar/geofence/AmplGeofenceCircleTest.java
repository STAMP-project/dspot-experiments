

package org.traccar.geofence;


public class AmplGeofenceCircleTest {
    @org.junit.Test
    public void testCircleWkt() throws java.text.ParseException {
        java.lang.String test = "CIRCLE (55.75414 37.6204, 100)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertEquals(geofenceGeometry.toWkt(), test);
    }

    @org.junit.Test
    public void testContainsCircle() throws java.text.ParseException {
        java.lang.String test = "CIRCLE (55.75414 37.6204, 100)";
        org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
        geofenceGeometry.fromWkt(test);
        org.junit.Assert.assertTrue(geofenceGeometry.containsPoint(55.75477, 37.62025));
        org.junit.Assert.assertTrue((!(geofenceGeometry.containsPoint(55.75545, 37.61921))));
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test
    public void testCircleWkt_literalMutation5_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.7}5414 37.6204, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_literalMutation5 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test
    public void testCircleWkt_literalMutation4_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE 55.75414 37.6204, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_literalMutation4 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test
    public void testCircleWkt_literalMutation2_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_literalMutation2 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf26_literalMutation242_failAssert23() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.754u4 37.6204, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(String_vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // MethodAssertGenerator build local variable
            Object o_17_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf26_literalMutation242 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf26_literalMutation241_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6204, 10!0)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(String_vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // MethodAssertGenerator build local variable
            Object o_17_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf26_literalMutation241 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf21_literalMutation166_failAssert30() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6204V, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_0);
            // MethodAssertGenerator build local variable
            Object o_11_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf21_literalMutation166 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf26_add232_literalMutation844_failAssert46() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6204, !100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_6.fromWkt(String_vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(String_vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // MethodAssertGenerator build local variable
            Object o_28_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf26_add232_literalMutation844 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf26_cf247_literalMutation588_failAssert59() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.620S, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(String_vc_0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator replace invocation
            java.lang.String o_testCircleWkt_cf26_cf247__17 = // StatementAdderMethod cloned existing statement
geofenceGeometry.toWkt();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCircleWkt_cf26_cf247__17, "CIRCLE (55.75414 37.6204, 100)");
            // MethodAssertGenerator build local variable
            Object o_27_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf26_cf247_literalMutation588 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf24_cf224_literalMutation1519_failAssert64() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "&P{pcD`f*8U&lNc$=$JI<B[&L]p+q/";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            vc_6.fromWkt(test);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_4 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_4, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_4);
            // MethodAssertGenerator build local variable
            Object o_23_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf24_cf224_literalMutation1519 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf21_cf183_failAssert14_literalMutation987() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 3).6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "CIRCLE (55.75414 3).6204, 100)");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(String_vc_0);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_35 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            geofenceGeometry.fromWkt(vc_35);
            // MethodAssertGenerator build local variable
            Object o_15_0 = geofenceGeometry.toWkt();
            org.junit.Assert.fail("testCircleWkt_cf21_cf183 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testCircleWkt */
    @org.junit.Test(timeout = 1000)
    public void testCircleWkt_cf26_cf246_failAssert39_literalMutation1571_failAssert29() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String test = "CIRCLE (B55.75414 37.6204, 100)";
                org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
                geofenceGeometry.fromWkt(test);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_0 = "CIRCLE (55.75414 37.6204, 100)";
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(String_vc_0, "CIRCLE (55.75414 37.6204, 100)");
                // StatementAdderOnAssert create random local variable
                org.traccar.geofence.GeofenceCircle vc_6 = new org.traccar.geofence.GeofenceCircle();
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (0.0 0.0, 0)");
                // StatementAdderMethod cloned existing statement
                vc_6.fromWkt(String_vc_0);
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_6).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
                // StatementAdderOnAssert create null value
                org.traccar.geofence.GeofenceCircle vc_48 = (org.traccar.geofence.GeofenceCircle)null;
                // StatementAdderMethod cloned existing statement
                vc_48.toWkt();
                // MethodAssertGenerator build local variable
                Object o_21_0 = geofenceGeometry.toWkt();
                org.junit.Assert.fail("testCircleWkt_cf26_cf246 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testCircleWkt_cf26_cf246_failAssert39_literalMutation1571 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test
    public void testContainsCircle_literalMutation1847_failAssert1() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1847 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test
    public void testContainsCircle_literalMutation1846_failAssert0() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6@04, 100)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1846 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test
    public void testContainsCircle_literalMutation1850_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6204, 10B0)";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1850 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test
    public void testContainsCircle_literalMutation1849_failAssert2() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "3G%$s5Za_=(n+lTbV|h.|Jj6/;,`)R";
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1849 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test
    public void testContainsCircle_literalMutation1846_failAssert0_literalMutation1876() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1846 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test(timeout = 1000)
    public void testContainsCircle_literalMutation1848_cf1912_cf2414_failAssert29() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CIRCLE (55.75414 37.6204,100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "CIRCLE (55.75414 37.6204,100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "CIRCLE (55.75414 37.6204,100)");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_9_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_36 = "CIRCLE (55.75414 37.6204,100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_36, "CIRCLE (55.75414 37.6204,100)");
            // StatementAdderOnAssert create random local variable
            org.traccar.geofence.GeofenceCircle vc_312 = new org.traccar.geofence.GeofenceCircle();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_312).toWkt(), "CIRCLE (0.0 0.0, 0)");
            // StatementAdderMethod cloned existing statement
            vc_312.fromWkt(String_vc_36);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((org.traccar.geofence.GeofenceCircle)vc_312).toWkt(), "CIRCLE (55.75414 37.6204, 100)");
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_404 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            vc_312.fromWkt(vc_404);
            // MethodAssertGenerator build local variable
            Object o_27_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1848_cf1912_cf2414 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }

    /* amplification of org.traccar.geofence.GeofenceCircleTest#testContainsCircle */
    @org.junit.Test(timeout = 1000)
    public void testContainsCircle_literalMutation1846_failAssert0_literalMutation1877_add3455() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String test = "CRCLE (55.75414 37.6@04, 100)";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "CRCLE (55.75414 37.6@04, 100)");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(test, "CRCLE (55.75414 37.6@04, 100)");
            org.traccar.geofence.GeofenceGeometry geofenceGeometry = new org.traccar.geofence.GeofenceCircle();
            // MethodCallAdder
            geofenceGeometry.fromWkt(test);
            geofenceGeometry.fromWkt(test);
            // MethodAssertGenerator build local variable
            Object o_5_0 = geofenceGeometry.containsPoint(55.75477, 37.62025);
            // MethodAssertGenerator build local variable
            Object o_7_0 = !(geofenceGeometry.containsPoint(55.75545, 37.61921));
            org.junit.Assert.fail("testContainsCircle_literalMutation1846 should have thrown ParseException");
        } catch (java.text.ParseException eee) {
        }
    }
}


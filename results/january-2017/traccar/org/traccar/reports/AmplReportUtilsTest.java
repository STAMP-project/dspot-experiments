

package org.traccar.reports;


public class AmplReportUtilsTest {
    @org.junit.Test
    public void testCalculateDistance() {
        org.traccar.model.Position startPosition = new org.traccar.model.Position();
        startPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 500.0);
        org.traccar.model.Position endPosition = new org.traccar.model.Position();
        endPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 700.0);
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition), 200.0, 10);
        startPosition.set(org.traccar.model.Position.KEY_ODOMETER, 50000);
        endPosition.set(org.traccar.model.Position.KEY_ODOMETER, 51000);
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition), 1000.0, 10);
    }

    @org.junit.Test
    public void testCalculateSpentFuel() {
        org.traccar.model.Position startPosition = new org.traccar.model.Position();
        org.traccar.model.Position endPosition = new org.traccar.model.Position();
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition), "-");
        startPosition.setProtocol("meitrack");
        startPosition.set(org.traccar.model.Position.KEY_FUEL, 0.07);
        endPosition.set(org.traccar.model.Position.KEY_FUEL, 0.05);
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition), "0.02 %");
        startPosition.setProtocol("galileo");
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition), "0.02 %");
        startPosition.setProtocol("noran");
        org.junit.Assert.assertEquals(org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition), "0.02 %");
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateDistance */
    @org.junit.Test(timeout = 1000)
    public void testCalculateDistance_cf48_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            startPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 500.0);
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            endPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 700.0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            startPosition.set(org.traccar.model.Position.KEY_ODOMETER, 50000);
            endPosition.set(org.traccar.model.Position.KEY_ODOMETER, 51000);
            // StatementAdderOnAssert create random local variable
            long vc_18 = -6383755665539627073L;
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_16 = (org.traccar.reports.ReportUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_16.getSpeedUnit(vc_18);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateDistance_cf48 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateDistance */
    @org.junit.Test(timeout = 1000)
    public void testCalculateDistance_cf44_failAssert6_literalMutation85_add280() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            startPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 500.0);
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            endPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 700.0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_7_0, 200.0D);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_7_0, 200.0D);
            startPosition.set(org.traccar.model.Position.KEY_ODOMETER, 50000);
            endPosition.set(org.traccar.model.Position.KEY_ODOMETER, 2147483647);
            // StatementAdderOnAssert create random local variable
            long vc_15 = 949216287749063632L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_15, 949216287749063632L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_15, 949216287749063632L);
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_13 = (org.traccar.reports.ReportUtils)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_13);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_13.getDistanceUnit(vc_15);
            // StatementAdderMethod cloned existing statement
            vc_13.getDistanceUnit(vc_15);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateDistance_cf44 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateSpentFuel */
    @org.junit.Test(timeout = 1000)
    public void testCalculateSpentFuel_cf434_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            startPosition.setProtocol("meitrack");
            startPosition.set(org.traccar.model.Position.KEY_FUEL, 0.07);
            endPosition.set(org.traccar.model.Position.KEY_FUEL, 0.05);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            startPosition.setProtocol("galileo");
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            startPosition.setProtocol("noran");
            // StatementAdderOnAssert create random local variable
            long vc_41 = -1156232160733171433L;
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_39 = (org.traccar.reports.ReportUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_39.getSpeedUnit(vc_41);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateSpentFuel_cf434 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateSpentFuel */
    @org.junit.Test(timeout = 1000)
    public void testCalculateSpentFuel_cf430_failAssert17_literalMutation534_add1145() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_5_0, "-");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_5_0, "-");
            startPosition.setProtocol("meitrack");
            startPosition.set(org.traccar.model.Position.KEY_FUEL, 0.07);
            endPosition.set(org.traccar.model.Position.KEY_FUEL, 0.05);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_10_0, "0.02 %");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_10_0, "0.02 %");
            // MethodCallAdder
            startPosition.setProtocol("galileo");
            startPosition.setProtocol("galileo");
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_13_0, "0.02 %");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_13_0, "0.02 %");
            startPosition.setProtocol("noran");
            // StatementAdderOnAssert create random local variable
            long vc_38 = 3040465591510048211L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_38, 3040465591510048211L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_38, 3040465591510048211L);
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_36 = (org.traccar.reports.ReportUtils)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_36);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_36);
            // StatementAdderMethod cloned existing statement
            vc_36.getDistanceUnit(vc_38);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateSpentFuel_cf430 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


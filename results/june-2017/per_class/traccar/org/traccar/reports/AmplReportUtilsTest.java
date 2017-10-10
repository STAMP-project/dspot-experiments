

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
    @org.junit.Test(timeout = 10000)
    public void testCalculateDistance_cf45_failAssert13() {
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
            long vc_18 = 7024455863740152300L;
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_16 = (org.traccar.reports.ReportUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_16.getSpeedUnit(vc_18);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateDistance_cf45 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateDistance */
    @org.junit.Test(timeout = 10000)
    public void testCalculateDistance_cf43_failAssert12_literalMutation73() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            startPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 500.0);
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            endPosition.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, 700.0);
            // MethodAssertGenerator build local variable
            Object o_7_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, 200.0);
            startPosition.set(org.traccar.model.Position.KEY_ODOMETER, 50000);
            endPosition.set(org.traccar.model.Position.KEY_ODOMETER, 50999);
            // StatementAdderOnAssert create random local variable
            long vc_15 = 4123787237744426743L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_15, 4123787237744426743L);
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_13 = (org.traccar.reports.ReportUtils)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13);
            // StatementAdderMethod cloned existing statement
            vc_13.getDistanceUnit(vc_15);
            // MethodAssertGenerator build local variable
            Object o_17_0 = org.traccar.reports.ReportUtils.calculateDistance(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateDistance_cf43 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateSpentFuel */
    @org.junit.Test(timeout = 10000)
    public void testCalculateSpentFuel_cf4841_failAssert26() {
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
            long vc_1375 = 1674679514834051769L;
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_1373 = (org.traccar.reports.ReportUtils)null;
            // StatementAdderMethod cloned existing statement
            vc_1373.getSpeedUnit(vc_1375);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateSpentFuel_cf4841 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.reports.ReportUtilsTest#testCalculateSpentFuel */
    @org.junit.Test(timeout = 10000)
    public void testCalculateSpentFuel_cf4839_failAssert25_literalMutation4881() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.model.Position startPosition = new org.traccar.model.Position();
            org.traccar.model.Position endPosition = new org.traccar.model.Position();
            // MethodAssertGenerator build local variable
            Object o_5_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, "-");
            startPosition.setProtocol("meitrack");
            startPosition.set(org.traccar.model.Position.KEY_FUEL, 0.07);
            endPosition.set(org.traccar.model.Position.KEY_FUEL, 0.05);
            // MethodAssertGenerator build local variable
            Object o_10_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, "0.02 %");
            startPosition.setProtocol("galileo");
            // MethodAssertGenerator build local variable
            Object o_13_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, "0.02 %");
            startPosition.setProtocol("noran");
            // StatementAdderOnAssert create random local variable
            long vc_1372 = 9223372036854775807L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1372, 9223372036854775807L);
            // StatementAdderOnAssert create null value
            org.traccar.reports.ReportUtils vc_1370 = (org.traccar.reports.ReportUtils)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1370);
            // StatementAdderMethod cloned existing statement
            vc_1370.getDistanceUnit(vc_1372);
            // MethodAssertGenerator build local variable
            Object o_22_0 = org.traccar.reports.ReportUtils.calculateFuel(startPosition, endPosition);
            org.junit.Assert.fail("testCalculateSpentFuel_cf4839 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


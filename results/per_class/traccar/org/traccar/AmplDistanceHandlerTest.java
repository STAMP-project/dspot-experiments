

package org.traccar;


public class AmplDistanceHandlerTest {
    @org.junit.Test
    public void testCalculateDistance() throws java.lang.Exception {
        org.traccar.DistanceHandler distanceHandler = new org.traccar.DistanceHandler();
        org.traccar.model.Position position = distanceHandler.calculateDistance(new org.traccar.model.Position());
        org.junit.Assert.assertEquals(0.0, position.getAttributes().get(org.traccar.model.Position.KEY_DISTANCE));
        org.junit.Assert.assertEquals(0.0, position.getAttributes().get(org.traccar.model.Position.KEY_TOTAL_DISTANCE));
    }

    /* amplification of org.traccar.DistanceHandlerTest#testCalculateDistance */
    @org.junit.Test(timeout = 10000)
    public void testCalculateDistance_cf13_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.DistanceHandler distanceHandler = new org.traccar.DistanceHandler();
            org.traccar.model.Position position = distanceHandler.calculateDistance(new org.traccar.model.Position());
            // MethodAssertGenerator build local variable
            Object o_6_0 = position.getAttributes().get(org.traccar.model.Position.KEY_DISTANCE);
            // StatementAdderOnAssert create null value
            org.traccar.model.Position vc_6 = (org.traccar.model.Position)null;
            // StatementAdderMethod cloned existing statement
            distanceHandler.handlePosition(vc_6);
            // MethodAssertGenerator build local variable
            Object o_13_0 = position.getAttributes().get(org.traccar.model.Position.KEY_TOTAL_DISTANCE);
            org.junit.Assert.fail("testCalculateDistance_cf13 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


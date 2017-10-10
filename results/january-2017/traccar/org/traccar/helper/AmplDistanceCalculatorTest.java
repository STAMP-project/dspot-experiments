

package org.traccar.helper;


public class AmplDistanceCalculatorTest {
    @org.junit.Test
    public void testDistance() {
        org.junit.Assert.assertEquals(org.traccar.helper.DistanceCalculator.distance(0.0, 0.0, 0.05, 0.05), 7863.0, 10.0);
    }

    @org.junit.Test
    public void testDistanceToLine() {
        org.junit.Assert.assertEquals(org.traccar.helper.DistanceCalculator.distanceToLine(56.83801, 60.59748, 56.83777, 60.59833, 56.83766, 60.5968), 33.0, 5.0);
        org.junit.Assert.assertEquals(org.traccar.helper.DistanceCalculator.distanceToLine(56.83753, 60.59508, 56.83777, 60.59833, 56.83766, 60.5968), 105.0, 5.0);
    }
}


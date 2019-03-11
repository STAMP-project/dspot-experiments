package org.opentripplanner.routing.util.elevation;


import org.junit.Assert;
import org.junit.Test;


public class ToblersHikingFunctionTest {
    private static final double CUT_OFF_LIMIT = 3.2;

    @Test
    public void calculateHorizontalWalkingDistanceMultiplier() {
        ToblersHikingFunctionTest.TestCase[] testCases = new ToblersHikingFunctionTest.TestCase[]{ ToblersHikingFunctionTest.TestCase.tc(35, ToblersHikingFunctionTest.CUT_OFF_LIMIT), ToblersHikingFunctionTest.TestCase.tc(31.4, 3.0), ToblersHikingFunctionTest.TestCase.tc(30, 2.86), ToblersHikingFunctionTest.TestCase.tc(25, 2.4), ToblersHikingFunctionTest.TestCase.tc(20, 2.02), ToblersHikingFunctionTest.TestCase.tc(15, 1.69), ToblersHikingFunctionTest.TestCase.tc(10, 1.42), ToblersHikingFunctionTest.TestCase.tc(5, 1.19), ToblersHikingFunctionTest.TestCase.tc(0, 1.0), ToblersHikingFunctionTest.TestCase.tc((-5), 0.84), ToblersHikingFunctionTest.TestCase.tc((-10), 1.0), ToblersHikingFunctionTest.TestCase.tc((-15), 1.19), ToblersHikingFunctionTest.TestCase.tc((-20), 1.42), ToblersHikingFunctionTest.TestCase.tc((-25), 1.69), ToblersHikingFunctionTest.TestCase.tc((-30), 2.02), ToblersHikingFunctionTest.TestCase.tc((-35), 2.4), ToblersHikingFunctionTest.TestCase.tc((-40), 2.86), ToblersHikingFunctionTest.TestCase.tc((-45), ToblersHikingFunctionTest.CUT_OFF_LIMIT) };
        ToblersHikingFunction f = new ToblersHikingFunction(ToblersHikingFunctionTest.CUT_OFF_LIMIT);
        for (ToblersHikingFunctionTest.TestCase it : testCases) {
            double distMultiplier = f.calculateHorizontalWalkingDistanceMultiplier(it.dx, it.dh);
            Assert.assertEquals(it.describe(), it.expected, distMultiplier, 0.01);
        }
    }

    static class TestCase {
        final double slopeAnglePercentage;

        final double dx;

        final double dh;

        final double expected;

        TestCase(double slopeAnglePercentage, double expected) {
            this.slopeAnglePercentage = slopeAnglePercentage;
            // Set the horizontal distance to 300 meters
            this.dx = 300.0;
            // Calculate the height:
            this.dh = ((dx) * slopeAnglePercentage) / 100.0;
            this.expected = expected;
        }

        static ToblersHikingFunctionTest.TestCase tc(double slopeAngle, double expected) {
            return new ToblersHikingFunctionTest.TestCase(slopeAngle, expected);
        }

        String describe() {
            return String.format("Multiplier at %.1f%% slope angle with dx %.1f and dh %.1f.", slopeAnglePercentage, dx, dh);
        }
    }
}


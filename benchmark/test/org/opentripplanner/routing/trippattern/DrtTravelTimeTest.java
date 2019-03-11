package org.opentripplanner.routing.trippattern;


import DrtTravelTime.ERROR_MSG;
import junit.framework.TestCase;


public class DrtTravelTimeTest extends TestCase {
    public void testConstant() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("20");
        TestCase.assertEquals(0.0, tt.getCoefficient());
        TestCase.assertEquals(1200.0, tt.getConstant());
        TestCase.assertEquals(1200.0, tt.process(100));
    }

    public void testCoefficient() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("3t");
        TestCase.assertEquals(3.0, tt.getCoefficient());
        TestCase.assertEquals(0.0, tt.getConstant());
        TestCase.assertEquals(180.0, tt.process(60.0));
    }

    public void testArithmeticFunction() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("2.5t+5");
        TestCase.assertEquals(2.5, tt.getCoefficient());
        TestCase.assertEquals(300.0, tt.getConstant());
        TestCase.assertEquals(1800.0, tt.process(600));
    }

    public void testTwoDigitConstant() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("1t+12");
        TestCase.assertEquals(1.0, tt.getCoefficient());
        TestCase.assertEquals(720.0, tt.getConstant());
        TestCase.assertEquals(1320.0, tt.process(600));
    }

    public void testDecimalsEverywhere() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("3.0t+5.00");
        TestCase.assertEquals(3.0, tt.getCoefficient());
        TestCase.assertEquals(300.0, tt.getConstant());
        TestCase.assertEquals(2100.0, tt.process(600));
    }

    public void testLarge() {
        DrtTravelTime tt = DrtTravelTime.fromSpec("2880.0");
        TestCase.assertEquals(0.0, tt.getCoefficient());
        TestCase.assertEquals(172800.0, tt.getConstant());
        TestCase.assertEquals(172800.0, tt.process(20));
        TestCase.assertEquals(172800.0, tt.process(88));
    }

    public void testBadSpec() {
        try {
            DrtTravelTime.fromSpec("not to spec");
            TestCase.fail("Missing exception");
        } catch (IllegalArgumentException e) {
            TestCase.assertEquals(e.getMessage(), ERROR_MSG);
        }
    }
}


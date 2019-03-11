package org.opentripplanner.common.geometry;


import junit.framework.TestCase;


public class TestDistanceLib extends TestCase {
    public void testMetersToDegree() {
        // Note: 111194.926644559 is 1 degree at the equator, given the earth radius used in the lib
        double degree;
        degree = SphericalDistanceLibrary.metersToDegrees(111194.926644559);
        TestCase.assertTrue(((Math.abs((degree - 1.0))) < 1.0E-5));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 0);
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(1))))))) < 1.0E-5));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 1.0);
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(2))))))) < 1.0E-5));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, (-1.0));
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(2))))))) < 1.0E-5));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 45.0);
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(46))))))) < 1.0E-5));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, (-45.0));
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(46))))))) < 1.0E-5));
        // Further north, solutions get degenerated.
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 80);
        TestCase.assertTrue(((Math.abs((degree - (1.0 / (Math.cos(Math.toRadians(81))))))) < 1.0E-4));
        degree = SphericalDistanceLibrary.metersToLonDegrees(111.194926, 45);
        TestCase.assertTrue(((Math.abs((degree - ((1.0 / (Math.cos(Math.toRadians(44.999)))) / 1000)))) < 1.0E-5));
    }

    public void testPointToLineStringFastDistance() {
        // Note: the meridian length of 1 degree of latitude on the sphere is around 111.2 km
        runOneTestPointToLineStringFastDistance(0, 0, 45, 0, 44.9, 0, 45.1, 0);
        runOneTestPointToLineStringFastDistance(0, 0, 45, 0, 45, (-0.1), 45, 0.1);
        // Mid-range lat
        runOneTestPointToLineStringFastDistance(7862, 7863, 45, 0.1, 44.9, 0, 45.1, 0);
        runOneTestPointToLineStringFastDistance(11119, 11120, 45.1, 0.0, 45, (-0.1), 45, 0.1);
        // Equator
        runOneTestPointToLineStringFastDistance(11119, 11120, 0, 0.1, (-0.1), 0, 0.1, 0);
        runOneTestPointToLineStringFastDistance(11119, 11120, 0.1, 0.0, 0, (-0.1), 0, 0.1);
        runOneTestPointToLineStringFastDistance(0, 0, 45.1, 0.1, 44.9, (-0.1), 45.1, 0.1);
        runOneTestPointToLineStringFastDistance(12854, 12855, 44.9, 0.1, 44.9, (-0.1), 45.1, 0.1);
        // Test corners
        runOneTestPointToLineStringFastDistance(1361, 1362, 44.99, 0.01, 45, (-0.1), 45, 0, 45.1, 0);
        runOneTestPointToLineStringFastDistance(1111, 1112, 44.99, (-0.05), 45, (-0.1), 45, 0, 45.1, 0);
        /* Note: the two following case do not have the exact same distance as we project on point
        location and their latitude differ a bit.
         */
        runOneTestPointToLineStringFastDistance(786, 787, 45.01, (-0.01), 45, (-0.1), 45, 0, 45.1, 0);
        runOneTestPointToLineStringFastDistance(785, 786, 45.05, (-0.01), 45, (-0.1), 45, 0, 45.1, 0);
    }

    public void testLineStringFastLenght() {
        // Note: the meridian length of 1 degree of latitude on the sphere is around 111.2 km
        // a ~= 111.2 km
        double a = runOneTestLineStringFastLength(11119, 11120, 45, 0, 45.1, 0);
        // b ~= a . cos(45)
        double b = runOneTestLineStringFastLength(7862, 7863, 45, 0, 45, 0.1);
        // c^2 ~= a^2 + b^2
        double c = runOneTestLineStringFastLength(13614, 13615, 45, 0, 45.1, 0.1);
        // d ~= a + b
        double d = runOneTestLineStringFastLength(18975, 18976, 45, 0, 45.1, 0, 45.1, 0.1);
        // fast, but imprecise: error is less than 10 meters for a distance of ~20 kms
        TestCase.assertTrue(((Math.abs((b - (a * (Math.cos(Math.toRadians(45))))))) < 1.0));
        TestCase.assertTrue(((Math.abs((c - (Math.sqrt(((a * a) + (b * b))))))) < 10.0));
        TestCase.assertTrue(((Math.abs((d - (a + b)))) < 10.0));
    }
}


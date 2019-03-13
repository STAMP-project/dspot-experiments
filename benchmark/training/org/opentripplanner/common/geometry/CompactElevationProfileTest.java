package org.opentripplanner.common.geometry;


import junit.framework.TestCase;
import org.locationtech.jts.geom.Coordinate;


public class CompactElevationProfileTest extends TestCase {
    public final void testEncodingDecoding() {
        runOneTest(null);
        runOneTest(new Coordinate[]{ new Coordinate(0.0, 0.0) });
        runOneTest(new Coordinate[]{ new Coordinate(0.0, 1.0) });
        runOneTest(new Coordinate[]{ new Coordinate(0.0, 10000.0) });// More than Mt Everest

        // elevation
        runOneTest(new Coordinate[]{ new Coordinate(100000.0, 0.0) });// A long street segment

        // length
        runOneTest(new Coordinate[]{ new Coordinate(0.0, 10.0), new Coordinate(8.0, (-10.0)), new Coordinate(120.0, 20.0), new Coordinate(150.0, 0.0) });
        runOneTest(new Coordinate[]{ new Coordinate(0.0, 0.12345678), new Coordinate(1.1111111111, (-0.987654321)), new Coordinate(2.222222222222, 1.23E-5), new Coordinate(3.33333333333, 6789.987654321) });
    }
}


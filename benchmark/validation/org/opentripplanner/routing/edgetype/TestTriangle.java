package org.opentripplanner.routing.edgetype;


import TraverseMode.BICYCLE;
import junit.framework.TestCase;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.common.geometry.PackedCoordinateSequence;
import org.opentripplanner.routing.core.OptimizeType;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.util.ElevationUtils;
import org.opentripplanner.routing.util.SlopeCosts;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.opentripplanner.util.NonLocalizedString;

import static StreetTraversalPermission.ALL;


public class TestTriangle extends TestCase {
    public void testTriangle() {
        Coordinate c1 = new Coordinate((-122.575033), 45.456773);
        Coordinate c2 = new Coordinate((-122.576668), 45.451426);
        StreetVertex v1 = new org.opentripplanner.routing.vertextype.IntersectionVertex(null, "v1", c1.x, c1.y, ((NonLocalizedString) (null)));
        StreetVertex v2 = new org.opentripplanner.routing.vertextype.IntersectionVertex(null, "v2", c2.x, c2.y, ((NonLocalizedString) (null)));
        GeometryFactory factory = new GeometryFactory();
        LineString geometry = factory.createLineString(new Coordinate[]{ c1, c2 });
        double length = 650.0;
        StreetWithElevationEdge testStreet = new StreetWithElevationEdge(v1, v2, geometry, "Test Lane", length, ALL, false);
        testStreet.setBicycleSafetyFactor(0.74F);// a safe street

        Coordinate[] profile = new Coordinate[]{ new Coordinate(0, 0)// slope = 0.1
        , new Coordinate((length / 2), (length / 20.0)), new Coordinate(length, 0)// slope = -0.1
         };
        PackedCoordinateSequence elev = new PackedCoordinateSequence.Double(profile);
        testStreet.setElevationProfile(elev, false);
        SlopeCosts costs = ElevationUtils.getSlopeCosts(elev, true);
        double trueLength = (costs.lengthMultiplier) * length;
        double slopeWorkLength = testStreet.getSlopeWorkCostEffectiveLength();
        double slopeSpeedLength = testStreet.getSlopeSpeedEffectiveLength();
        RoutingRequest options = new RoutingRequest(TraverseMode.BICYCLE);
        options.optimize = OptimizeType.TRIANGLE;
        options.bikeSpeed = 6.0;
        options.walkReluctance = 1;
        options.setTriangleSafetyFactor(0);
        options.setTriangleSlopeFactor(0);
        options.setTriangleTimeFactor(1);
        State startState = new State(v1, options);
        State result = testStreet.traverse(startState);
        double timeWeight = result.getWeight();
        double expectedTimeWeight = slopeSpeedLength / (options.getSpeed(BICYCLE));
        TestCase.assertTrue(((Math.abs((expectedTimeWeight - timeWeight))) < 1.0E-5));
        options.setTriangleSafetyFactor(0);
        options.setTriangleSlopeFactor(1);
        options.setTriangleTimeFactor(0);
        startState = new State(v1, options);
        result = testStreet.traverse(startState);
        double slopeWeight = result.getWeight();
        double expectedSlopeWeight = slopeWorkLength / (options.getSpeed(BICYCLE));
        TestCase.assertTrue(((Math.abs((expectedSlopeWeight - slopeWeight))) < 1.0E-5));
        TestCase.assertTrue((((length * 1.5) / (options.getSpeed(BICYCLE))) < slopeWeight));
        TestCase.assertTrue(((((length * 1.5) * 10) / (options.getSpeed(BICYCLE))) > slopeWeight));
        options.setTriangleSafetyFactor(1);
        options.setTriangleSlopeFactor(0);
        options.setTriangleTimeFactor(0);
        startState = new State(v1, options);
        result = testStreet.traverse(startState);
        double safetyWeight = result.getWeight();
        double slopeSafety = costs.slopeSafetyCost;
        double expectedSafetyWeight = ((trueLength * 0.74) + slopeSafety) / (options.getSpeed(BICYCLE));
        TestCase.assertTrue(((Math.abs((expectedSafetyWeight - safetyWeight))) < 1.0E-5));
        final double ONE_THIRD = 1 / 3.0;
        options.setTriangleSafetyFactor(ONE_THIRD);
        options.setTriangleSlopeFactor(ONE_THIRD);
        options.setTriangleTimeFactor(ONE_THIRD);
        startState = new State(v1, options);
        result = testStreet.traverse(startState);
        double averageWeight = result.getWeight();
        TestCase.assertTrue(((Math.abs(((((safetyWeight * ONE_THIRD) + (slopeWeight * ONE_THIRD)) + (timeWeight * ONE_THIRD)) - averageWeight))) < 1.0E-8));
    }
}


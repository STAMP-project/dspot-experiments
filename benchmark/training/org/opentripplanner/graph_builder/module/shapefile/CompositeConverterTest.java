package org.opentripplanner.graph_builder.module.shapefile;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.BICYCLE;
import StreetTraversalPermission.CAR;
import StreetTraversalPermission.NONE;
import StreetTraversalPermission.PEDESTRIAN;
import junit.framework.TestCase;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;


public class CompositeConverterTest extends TestCase {
    private CompositeStreetTraversalPermissionConverter converter;

    private StubSimpleFeature stubFeature;

    private CaseBasedTraversalPermissionConverter caseConverter1;

    private CaseBasedTraversalPermissionConverter caseConverter2;

    public void testPermissionAggregationAnd() throws Exception {
        caseConverter1.addPermission("1", ALL, ALL);
        caseConverter2.addPermission("nicestreet", ALL, NONE);
        P2<StreetTraversalPermission> result = converter.convert(stubFeature);
        TestCase.assertEquals(new P2<StreetTraversalPermission>(StreetTraversalPermission.ALL, StreetTraversalPermission.NONE), result);
    }

    public void testPermissionAggregationOr() throws Exception {
        converter.setOrPermissions(true);
        caseConverter1.addPermission("1", NONE, ALL);
        caseConverter2.addPermission("nicestreet", ALL, ALL);
        P2<StreetTraversalPermission> result = converter.convert(stubFeature);
        TestCase.assertEquals(new P2<StreetTraversalPermission>(StreetTraversalPermission.ALL, StreetTraversalPermission.ALL), result);
    }

    public void testWalkingBiking() throws Exception {
        converter.setOrPermissions(true);
        caseConverter1.addPermission("1", PEDESTRIAN, PEDESTRIAN);
        caseConverter2.addPermission("nicestreet", BICYCLE, BICYCLE);
        P2<StreetTraversalPermission> result = converter.convert(stubFeature);
        TestCase.assertEquals(new P2<StreetTraversalPermission>(StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE, StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE), result);
    }

    public void testAllCars() throws Exception {
        caseConverter1.addPermission("1", ALL, ALL);
        caseConverter2.addPermission("nicestreet", CAR, CAR);
        P2<StreetTraversalPermission> result = converter.convert(stubFeature);
        TestCase.assertEquals(new P2<StreetTraversalPermission>(StreetTraversalPermission.CAR, StreetTraversalPermission.CAR), result);
    }
}


package org.opentripplanner.graph_builder.module.shapefile;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.PEDESTRIAN;
import junit.framework.TestCase;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;


public class TestCaseBasedTraversalPermissionConverter extends TestCase {
    /* Test for ticket #273: ttp://opentripplanner.org/ticket/273 */
    public void testDefaultValueForNullEntry() throws Exception {
        StubSimpleFeature feature = new StubSimpleFeature();
        feature.addAttribute("DIRECTION", null);
        CaseBasedTraversalPermissionConverter converter = new CaseBasedTraversalPermissionConverter();
        converter.setDefaultPermission(PEDESTRIAN);
        converter.addPermission("FOO", ALL, ALL);
        TestCase.assertEquals(new org.opentripplanner.common.model.P2<StreetTraversalPermission>(StreetTraversalPermission.PEDESTRIAN, StreetTraversalPermission.PEDESTRIAN), converter.convert(feature));
    }
}


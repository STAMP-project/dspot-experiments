package org.opentripplanner.openstreetmap.model;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.BICYCLE;
import StreetTraversalPermission.BICYCLE_AND_CAR;
import StreetTraversalPermission.CAR;
import StreetTraversalPermission.PEDESTRIAN;
import StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;


public class OSMWayTest {
    @Test
    public void testIsBicycleDismountForced() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isBicycleDismountForced());
        way.addTag("bicycle", "dismount");
        Assert.assertTrue(way.isBicycleDismountForced());
    }

    @Test
    public void testIsSteps() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isSteps());
        way.addTag("highway", "primary");
        Assert.assertFalse(way.isSteps());
        way.addTag("highway", "steps");
        Assert.assertTrue(way.isSteps());
    }

    @Test
    public void testIsRoundabout() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isRoundabout());
        way.addTag("junction", "dovetail");
        Assert.assertFalse(way.isRoundabout());
        way.addTag("junction", "roundabout");
        Assert.assertTrue(way.isRoundabout());
    }

    @Test
    public void testIsOneWayDriving() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isOneWayForwardDriving());
        Assert.assertFalse(way.isOneWayReverseDriving());
        way.addTag("oneway", "notatagvalue");
        Assert.assertFalse(way.isOneWayForwardDriving());
        Assert.assertFalse(way.isOneWayReverseDriving());
        way.addTag("oneway", "1");
        Assert.assertTrue(way.isOneWayForwardDriving());
        Assert.assertFalse(way.isOneWayReverseDriving());
        way.addTag("oneway", "-1");
        Assert.assertFalse(way.isOneWayForwardDriving());
        Assert.assertTrue(way.isOneWayReverseDriving());
    }

    @Test
    public void testIsOneWayBicycle() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isOneWayForwardBicycle());
        Assert.assertFalse(way.isOneWayReverseBicycle());
        way.addTag("oneway:bicycle", "notatagvalue");
        Assert.assertFalse(way.isOneWayForwardBicycle());
        Assert.assertFalse(way.isOneWayReverseBicycle());
        way.addTag("oneway:bicycle", "1");
        Assert.assertTrue(way.isOneWayForwardBicycle());
        Assert.assertFalse(way.isOneWayReverseBicycle());
        way.addTag("oneway:bicycle", "-1");
        Assert.assertFalse(way.isOneWayForwardBicycle());
        Assert.assertTrue(way.isOneWayReverseBicycle());
    }

    @Test
    public void testIsOneDirectionSidepath() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isForwardDirectionSidepath());
        Assert.assertFalse(way.isReverseDirectionSidepath());
        way.addTag("bicycle:forward", "use_sidepath");
        Assert.assertTrue(way.isForwardDirectionSidepath());
        Assert.assertFalse(way.isReverseDirectionSidepath());
        way.addTag("bicycle:backward", "use_sidepath");
        Assert.assertTrue(way.isForwardDirectionSidepath());
        Assert.assertTrue(way.isReverseDirectionSidepath());
    }

    @Test
    public void testIsOpposableCycleway() {
        OSMWay way = new OSMWay();
        Assert.assertFalse(way.isOpposableCycleway());
        way.addTag("cycleway", "notatagvalue");
        Assert.assertFalse(way.isOpposableCycleway());
        way.addTag("cycleway", "oppo");
        Assert.assertFalse(way.isOpposableCycleway());
        way.addTag("cycleway", "opposite");
        Assert.assertTrue(way.isOpposableCycleway());
        way.addTag("cycleway", "nope");
        way.addTag("cycleway:left", "opposite_side");
        Assert.assertTrue(way.isOpposableCycleway());
    }

    /**
     * Tests if cars can drive on unclassified highways with bicycleDesignated
     *
     * Check for bug #1878 and PR #1880
     */
    @Test
    public void testCarPermission() {
        OSMWay way = new OSMWay();
        way.addTag("highway", "unclassified");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
        way.addTag("bicycle", "designated");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
    }

    /**
     * Tests that motorcar/bicycle/foot private don't add permissions
     * but yes add permission if access is no
     */
    @Test
    public void testMotorCarTagAllowedPermissions() {
        OSMWay way = new OSMWay();
        way.addTag("highway", "residential");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
        way.addTag("access", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        way.addTag("motorcar", "private");
        way.addTag("bicycle", "private");
        way.addTag("foot", "private");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        way.addTag("motorcar", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(CAR));
        way.addTag("bicycle", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(BICYCLE_AND_CAR));
        way.addTag("foot", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
    }

    /**
     * Tests that motorcar/bicycle/foot private don't add permissions
     * but no remove permission if access is yes
     */
    @Test
    public void testMotorCarTagDeniedPermissions() {
        OSMWay way = new OSMWay();
        way.addTag("highway", "residential");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
        way.addTag("motorcar", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(PEDESTRIAN_AND_BICYCLE));
        way.addTag("bicycle", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(PEDESTRIAN));
        way.addTag("foot", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        // normal road with specific mode of transport private only is doubtful
        /* way.addTag("motorcar", "private");
        way.addTag("bicycle", "private");
        way.addTag("foot", "private");
        permissionPair = getWayProperties(way);
        assertTrue(permissionPair.first.allowsNothing());
         */
    }

    /**
     * Tests that motor_vehicle/bicycle/foot private don't add permissions
     * but yes add permission if access is no
     *
     * Support for motor_vehicle was added in #1881
     */
    @Test
    public void testMotorVehicleTagAllowedPermissions() {
        OSMWay way = new OSMWay();
        way.addTag("highway", "residential");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
        way.addTag("access", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        way.addTag("motor_vehicle", "private");
        way.addTag("bicycle", "private");
        way.addTag("foot", "private");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        way.addTag("motor_vehicle", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(CAR));
        way.addTag("bicycle", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(BICYCLE_AND_CAR));
        way.addTag("foot", "yes");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
    }

    /**
     * Tests that motor_vehicle/bicycle/foot private don't add permissions
     * but no remove permission if access is yes
     *
     * Support for motor_vehicle was added in #1881
     */
    @Test
    public void testMotorVehicleTagDeniedPermissions() {
        OSMWay way = new OSMWay();
        way.addTag("highway", "residential");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(ALL));
        way.addTag("motor_vehicle", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(PEDESTRIAN_AND_BICYCLE));
        way.addTag("bicycle", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(PEDESTRIAN));
        way.addTag("foot", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allowsNothing());
        // normal road with specific mode of transport private only is doubtful
        /* way.addTag("motor_vehicle", "private");
        way.addTag("bicycle", "private");
        way.addTag("foot", "private");
        permissionPair = getWayProperties(way);
        assertTrue(permissionPair.first.allowsNothing());
         */
    }

    @Test
    public void testSidepathPermissions() {
        OSMWay way = new OSMWay();
        way.addTag("bicycle", "use_sidepath");
        way.addTag("highway", "primary");
        way.addTag("lanes", "2");
        way.addTag("maxspeed", "70");
        way.addTag("oneway", "yes");
        P2<StreetTraversalPermission> permissionPair = getWayProperties(way);
        Assert.assertFalse(permissionPair.first.allows(BICYCLE));
        Assert.assertFalse(permissionPair.second.allows(BICYCLE));
        Assert.assertTrue(permissionPair.first.allows(CAR));
        Assert.assertFalse(permissionPair.second.allows(CAR));
        way = new OSMWay();
        way.addTag("bicycle:forward", "use_sidepath");
        way.addTag("highway", "tertiary");
        permissionPair = getWayProperties(way);
        Assert.assertFalse(permissionPair.first.allows(BICYCLE));
        Assert.assertTrue(permissionPair.second.allows(BICYCLE));
        Assert.assertTrue(permissionPair.first.allows(CAR));
        Assert.assertTrue(permissionPair.second.allows(CAR));
        way = new OSMWay();
        way.addTag("bicycle:backward", "use_sidepath");
        way.addTag("highway", "tertiary");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(BICYCLE));
        Assert.assertFalse(permissionPair.second.allows(BICYCLE));
        Assert.assertTrue(permissionPair.first.allows(CAR));
        Assert.assertTrue(permissionPair.second.allows(CAR));
        way = new OSMWay();
        way.addTag("highway", "tertiary");
        way.addTag("oneway", "yes");
        way.addTag("oneway:bicycle", "no");
        permissionPair = getWayProperties(way);
        Assert.assertTrue(permissionPair.first.allows(BICYCLE));
        Assert.assertTrue(permissionPair.second.allows(BICYCLE));
        Assert.assertTrue(permissionPair.first.allows(CAR));
        Assert.assertFalse(permissionPair.second.allows(CAR));
        way.addTag("bicycle:forward", "use_sidepath");
        permissionPair = getWayProperties(way);
        Assert.assertFalse(permissionPair.first.allows(BICYCLE));
        Assert.assertTrue(permissionPair.second.allows(BICYCLE));
        Assert.assertTrue(permissionPair.first.allows(CAR));
        Assert.assertFalse(permissionPair.second.allows(CAR));
    }
}


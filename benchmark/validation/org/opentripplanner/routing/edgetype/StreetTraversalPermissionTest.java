package org.opentripplanner.routing.edgetype;


import StreetTraversalPermission.ALL;
import StreetTraversalPermission.BICYCLE;
import StreetTraversalPermission.CAR;
import StreetTraversalPermission.NONE;
import StreetTraversalPermission.PEDESTRIAN;
import StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE;
import TraverseMode.TRANSIT;
import TraverseMode.WALK;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;

import static StreetTraversalPermission.ALL;
import static StreetTraversalPermission.BICYCLE;
import static StreetTraversalPermission.BICYCLE_AND_CAR;
import static StreetTraversalPermission.CAR;
import static StreetTraversalPermission.NONE;
import static StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE;


public class StreetTraversalPermissionTest {
    @Test
    public void testGetCode() {
        StreetTraversalPermission perm1 = BICYCLE_AND_CAR;
        StreetTraversalPermission perm2 = StreetTraversalPermission.get(perm1.code);
        Assert.assertEquals(perm1, perm2);
        StreetTraversalPermission perm3 = BICYCLE;
        Assert.assertFalse(perm1.equals(perm3));
    }

    @Test
    public void testRemove() {
        StreetTraversalPermission perm1 = CAR;
        StreetTraversalPermission none = perm1.remove(CAR);
        Assert.assertEquals(NONE, none);
    }

    @Test
    public void testAllowsStreetTraversalPermission() {
        StreetTraversalPermission perm1 = ALL;
        Assert.assertTrue(perm1.allows(CAR));
        Assert.assertTrue(perm1.allows(BICYCLE));
        Assert.assertTrue(perm1.allows(PEDESTRIAN));
        Assert.assertTrue(perm1.allows(PEDESTRIAN_AND_BICYCLE));
    }

    @Test
    public void testAllowsTraverseMode() {
        StreetTraversalPermission perm1 = ALL;
        Assert.assertTrue(perm1.allows(TraverseMode.CAR));
        Assert.assertTrue(perm1.allows(WALK));
        // StreetTraversalPermission is not used for public transit.
        Assert.assertFalse(perm1.allows(TRANSIT));
    }

    @Test
    public void testAllowsTraverseModeSet() {
        StreetTraversalPermission perm1 = BICYCLE_AND_CAR;
        Assert.assertTrue(perm1.allows(TraverseModeSet.allModes()));
        Assert.assertTrue(perm1.allows(new TraverseModeSet(TraverseMode.CAR, TraverseMode.BICYCLE)));
        Assert.assertTrue(perm1.allows(new TraverseModeSet(TraverseMode.BICYCLE, TraverseMode.RAIL, TraverseMode.FERRY)));
        Assert.assertFalse(perm1.allows(new TraverseModeSet(TraverseMode.WALK)));
    }

    @Test
    public void testAllowsAnythingNothing() {
        StreetTraversalPermission perm = CAR;
        Assert.assertTrue(perm.allowsAnything());
        Assert.assertFalse(perm.allowsNothing());
        perm = NONE;
        Assert.assertFalse(perm.allowsAnything());
        Assert.assertTrue(perm.allowsNothing());
    }

    @Test
    public void testIntersect() {
        StreetTraversalPermission perm = ALL;
        StreetTraversalPermission bike_walk = PEDESTRIAN_AND_BICYCLE;
        StreetTraversalPermission combined = perm.intersection(bike_walk);
        Assert.assertTrue(perm.allows(ALL));
        Assert.assertTrue(combined.allows(PEDESTRIAN_AND_BICYCLE));
        Assert.assertFalse(combined.allows(CAR));
        Assert.assertTrue(bike_walk.allows(PEDESTRIAN_AND_BICYCLE));
    }
}


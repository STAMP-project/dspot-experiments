package org.opentripplanner.routing.core;


import org.junit.Assert;
import org.junit.Test;

import static TraverseMode.BICYCLE;
import static TraverseMode.CAR;
import static TraverseMode.WALK;


public class TraverseModeSetTest {
    @Test
    public void testCarMode() {
        TraverseModeSet modeSet = new TraverseModeSet(CAR);
        Assert.assertTrue(modeSet.getCar());
        Assert.assertFalse(modeSet.isTransit());
        Assert.assertFalse(modeSet.getRail());
        Assert.assertFalse(modeSet.getTram());
        Assert.assertFalse(modeSet.getSubway());
        Assert.assertFalse(modeSet.getFunicular());
        Assert.assertFalse(modeSet.getGondola());
        Assert.assertFalse(modeSet.getWalk());
        Assert.assertFalse(modeSet.getBicycle());
    }

    @Test
    public void testWalkMode() {
        TraverseModeSet modeSet = new TraverseModeSet(WALK);
        Assert.assertTrue(modeSet.getWalk());
        Assert.assertFalse(modeSet.getCar());
        Assert.assertFalse(modeSet.isTransit());
        Assert.assertFalse(modeSet.getRail());
        Assert.assertFalse(modeSet.getTram());
        Assert.assertFalse(modeSet.getSubway());
        Assert.assertFalse(modeSet.getFunicular());
        Assert.assertFalse(modeSet.getGondola());
        Assert.assertFalse(modeSet.getBicycle());
    }

    @Test
    public void testBikeMode() {
        TraverseModeSet modeSet = new TraverseModeSet(BICYCLE);
        Assert.assertTrue(modeSet.getBicycle());
        Assert.assertFalse(modeSet.getWalk());
        Assert.assertFalse(modeSet.getCar());
        Assert.assertFalse(modeSet.isTransit());
        Assert.assertFalse(modeSet.getRail());
        Assert.assertFalse(modeSet.getTram());
        Assert.assertFalse(modeSet.getSubway());
        Assert.assertFalse(modeSet.getFunicular());
        Assert.assertFalse(modeSet.getGondola());
        Assert.assertFalse(modeSet.getWalk());
    }
}


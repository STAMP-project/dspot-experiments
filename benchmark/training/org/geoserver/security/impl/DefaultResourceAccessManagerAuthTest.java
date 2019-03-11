/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import AccessMode.READ;
import AccessMode.WRITE;
import CatalogMode.CHALLENGE;
import CatalogMode.HIDE;
import CatalogMode.MIXED;
import org.junit.Assert;
import org.junit.Test;


public class DefaultResourceAccessManagerAuthTest extends AbstractAuthorizationTest {
    @Test
    public void testWideOpen() throws Exception {
        ResourceAccessManager manager = buildAccessManager("wideOpen.properties");
        checkUserAccessFlat(manager, anonymous, true, true);
    }

    @Test
    public void testLockedDown() throws Exception {
        ResourceAccessManager manager = buildAccessManager("lockedDown.properties");
        checkUserAccessFlat(manager, anonymous, false, false);
        checkUserAccessFlat(manager, roUser, false, false);
        checkUserAccessFlat(manager, rwUser, true, true);
        checkUserAccessFlat(manager, root, true, true);
    }

    @Test
    public void testPublicRead() throws Exception {
        ResourceAccessManager manager = buildAccessManager("publicRead.properties");
        checkUserAccessFlat(manager, anonymous, true, false);
        checkUserAccessFlat(manager, roUser, true, false);
        checkUserAccessFlat(manager, rwUser, true, true);
        checkUserAccessFlat(manager, root, true, true);
    }

    @Test
    public void testComplex() throws Exception {
        ResourceAccessManager wo = buildAccessManager("complex.properties");
        // check non configured ws inherits root configuration, auth read, nobody write
        Assert.assertFalse(canAccess(wo, anonymous, nurcWs, READ));
        Assert.assertFalse(canAccess(wo, anonymous, nurcWs, WRITE));
        Assert.assertTrue(canAccess(wo, roUser, nurcWs, READ));
        Assert.assertFalse(canAccess(wo, rwUser, nurcWs, WRITE));
        Assert.assertTrue(canAccess(wo, root, nurcWs, WRITE));
        // check access to the topp workspace (everybody read, nobody for write)
        Assert.assertTrue(canAccess(wo, anonymous, toppWs, READ));
        Assert.assertFalse(canAccess(wo, anonymous, toppWs, WRITE));
        Assert.assertTrue(canAccess(wo, roUser, toppWs, READ));
        Assert.assertFalse(canAccess(wo, rwUser, toppWs, WRITE));
        // check non configured layer in topp ws inherits topp security attributes
        Assert.assertTrue(canAccess(wo, anonymous, roadsLayer, READ));
        Assert.assertFalse(canAccess(wo, anonymous, roadsLayer, WRITE));
        Assert.assertTrue(canAccess(wo, roUser, roadsLayer, READ));
        Assert.assertFalse(canAccess(wo, rwUser, roadsLayer, WRITE));
        // check states uses its own config (auth for read, auth for write)
        Assert.assertFalse(canAccess(wo, anonymous, statesLayer, READ));
        Assert.assertFalse(canAccess(wo, anonymous, statesLayer, WRITE));
        Assert.assertTrue(canAccess(wo, roUser, statesLayer, READ));
        Assert.assertFalse(canAccess(wo, roUser, statesLayer, WRITE));
        Assert.assertTrue(canAccess(wo, rwUser, statesLayer, WRITE));
        Assert.assertTrue(canAccess(wo, rwUser, statesLayer, WRITE));
        // check landmarks uses its own config (all can for read, auth for write)
        Assert.assertTrue(canAccess(wo, anonymous, landmarksLayer, READ));
        Assert.assertFalse(canAccess(wo, anonymous, landmarksLayer, WRITE));
        Assert.assertTrue(canAccess(wo, roUser, landmarksLayer, READ));
        Assert.assertFalse(canAccess(wo, roUser, landmarksLayer, WRITE));
        Assert.assertTrue(canAccess(wo, rwUser, landmarksLayer, READ));
        Assert.assertTrue(canAccess(wo, rwUser, statesLayer, WRITE));
        // check military is off limits for anyone but the military users
        Assert.assertFalse(canAccess(wo, anonymous, basesLayer, READ));
        Assert.assertFalse(canAccess(wo, anonymous, basesLayer, WRITE));
        Assert.assertFalse(canAccess(wo, roUser, basesLayer, READ));
        Assert.assertFalse(canAccess(wo, roUser, basesLayer, WRITE));
        Assert.assertFalse(canAccess(wo, rwUser, basesLayer, READ));
        Assert.assertFalse(canAccess(wo, rwUser, basesLayer, WRITE));
        Assert.assertTrue(canAccess(wo, milUser, basesLayer, READ));
        Assert.assertTrue(canAccess(wo, milUser, basesLayer, WRITE));
        // check the layer with dots
        Assert.assertFalse(canAccess(wo, anonymous, arcGridLayer, READ));
        Assert.assertFalse(canAccess(wo, anonymous, arcGridLayer, WRITE));
        Assert.assertFalse(canAccess(wo, roUser, arcGridLayer, READ));
        Assert.assertFalse(canAccess(wo, roUser, arcGridLayer, WRITE));
        Assert.assertFalse(canAccess(wo, rwUser, arcGridLayer, READ));
        Assert.assertFalse(canAccess(wo, rwUser, arcGridLayer, WRITE));
        Assert.assertTrue(canAccess(wo, milUser, arcGridLayer, READ));
        Assert.assertTrue(canAccess(wo, milUser, arcGridLayer, WRITE));
    }

    @Test
    public void testDefaultMode() throws Exception {
        DefaultResourceAccessManager wo = buildAccessManager("lockedDown.properties");
        Assert.assertEquals(HIDE, wo.getMode());
    }

    @Test
    public void testHideMode() throws Exception {
        DefaultResourceAccessManager wo = buildAccessManager("lockedDownHide.properties");
        Assert.assertEquals(HIDE, wo.getMode());
    }

    @Test
    public void testChallengeMode() throws Exception {
        DefaultResourceAccessManager wo = buildAccessManager("lockedDownChallenge.properties");
        Assert.assertEquals(CHALLENGE, wo.getMode());
    }

    @Test
    public void testMixedMode() throws Exception {
        DefaultResourceAccessManager wo = buildAccessManager("lockedDownMixed.properties");
        Assert.assertEquals(MIXED, wo.getMode());
    }

    @Test
    public void testUnknownMode() throws Exception {
        DefaultResourceAccessManager wo = buildAccessManager("lockedDownUnknown.properties");
        // should fall back on the default and complain in the logger
        Assert.assertEquals(HIDE, wo.getMode());
    }

    @Test
    public void testOverride() throws Exception {
        DefaultResourceAccessManager manager = buildAccessManager("override-ws.properties");
        // since the use can read states, it can read its container ws oo
        Assert.assertTrue(canAccess(manager, roUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, toppWs, READ));
        // no access for this one
        Assert.assertFalse(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertFalse(canAccess(manager, milUser, toppWs, READ));
    }

    @Test
    public void testWmsNamedTreeAMilitaryOnly() throws Exception {
        setupRequestThreadLocal("WMS");
        DefaultResourceAccessManager manager = buildAccessManager("namedTreeAMilitaryOnly.properties");
        Assert.assertFalse(canAccess(manager, roUser, namedTreeA, READ));
        // only contained in the hidden group and in a "single mode" one
        Assert.assertFalse(canAccess(manager, roUser, statesLayer, READ));
        // contained also in containerTreeB
        Assert.assertTrue(canAccess(manager, roUser, roadsLayer, READ));
        // the other layers in groups are also available
        Assert.assertTrue(canAccess(manager, roUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, roUser, nestedContainerE, READ));
        Assert.assertTrue(canAccess(manager, roUser, forestsLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, singleGroupC, READ));
        // check the mil user sees everything instead
        Assert.assertTrue(canAccess(manager, milUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, roadsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, milUser, nestedContainerE, READ));
        Assert.assertTrue(canAccess(manager, milUser, forestsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, singleGroupC, READ));
    }

    @Test
    public void testContainerGroupBMilitaryOnly() throws Exception {
        setupRequestThreadLocal("WMS");
        DefaultResourceAccessManager manager = buildAccessManager("containerTreeGroupBMilitaryOnly.properties");
        // layer group A and its contents should be visible
        Assert.assertTrue(canAccess(manager, roUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, roUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, roadsLayer, READ));
        // layer group B and landmarks should not
        Assert.assertFalse(canAccess(manager, roUser, containerTreeB, READ));
        Assert.assertFalse(canAccess(manager, roUser, landmarksLayer, READ));
        // nor the nested group
        Assert.assertFalse(canAccess(manager, roUser, nestedContainerE, READ));
        Assert.assertFalse(canAccess(manager, roUser, forestsLayer, READ));
        // layer group C should be available
        Assert.assertTrue(canAccess(manager, roUser, singleGroupC, READ));
        // now switch to the military user, that should see everything instead
        Assert.assertTrue(canAccess(manager, milUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, roadsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, milUser, nestedContainerE, READ));
        Assert.assertTrue(canAccess(manager, milUser, forestsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, singleGroupC, READ));
    }

    @Test
    public void testWmsbothGroupABMilitaryOnly() throws Exception {
        setupRequestThreadLocal("WMS");
        DefaultResourceAccessManager manager = buildAccessManager("bothGroupABMilitaryOnly.properties");
        Assert.assertFalse(canAccess(manager, roUser, namedTreeA, READ));
        // only contained in the hidden group and in a "single mode" one
        Assert.assertFalse(canAccess(manager, roUser, statesLayer, READ));
        // contained also in containerTreeB, which is also denied
        Assert.assertFalse(canAccess(manager, roUser, roadsLayer, READ));
        // the other layers in groups are also available
        Assert.assertFalse(canAccess(manager, roUser, containerTreeB, READ));
        Assert.assertFalse(canAccess(manager, roUser, landmarksLayer, READ));
        Assert.assertFalse(canAccess(manager, roUser, nestedContainerE, READ));
        Assert.assertFalse(canAccess(manager, roUser, forestsLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, singleGroupC, READ));
        // check the mil user sees everything instead
        Assert.assertTrue(canAccess(manager, milUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, roadsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, landmarksLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, milUser, nestedContainerE, READ));
        Assert.assertTrue(canAccess(manager, milUser, forestsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, singleGroupC, READ));
    }

    @Test
    public void testSingleGroupCMilitaryOnly() throws Exception {
        setupRequestThreadLocal("WMS");
        DefaultResourceAccessManager manager = buildAccessManager("singleGroupCMilitaryOnly.properties");
        // layer group A and its contents should be visible
        Assert.assertTrue(canAccess(manager, roUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, roUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, roadsLayer, READ));
        // layer group B and landmarks should also be visible
        Assert.assertTrue(canAccess(manager, roUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, roUser, landmarksLayer, READ));
        // layer group C should not be available, but the layers in it, states and bases, should
        Assert.assertFalse(canAccess(manager, roUser, singleGroupC, READ));
        Assert.assertTrue(canAccess(manager, roUser, basesLayer, READ));
        // now switch to the military user, that should see everything instead
        Assert.assertTrue(canAccess(manager, milUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, roadsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, milUser, singleGroupC, READ));
    }

    @Test
    public void testWsContainerGroupDMilitaryOnly() throws Exception {
        setupRequestThreadLocal("WMS");
        DefaultResourceAccessManager manager = buildAccessManager("wsContainerGroupDMilitaryOnly.properties");
        // layer group A and its contents should be visible
        Assert.assertTrue(canAccess(manager, roUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, roUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, roUser, roadsLayer, READ));
        // layer group B and landmarks should also be visible
        Assert.assertTrue(canAccess(manager, roUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, roUser, landmarksLayer, READ));
        // layer group C should available, but the layers in it, states and bases, should
        Assert.assertTrue(canAccess(manager, roUser, singleGroupC, READ));
        Assert.assertTrue(canAccess(manager, roUser, basesLayer, READ));
        // layer group D and its exclusive contents are not visible
        Assert.assertFalse(canAccess(manager, roUser, wsContainerD, READ));
        Assert.assertFalse(canAccess(manager, roUser, arcGridLayer, READ));
        // now switch to the military user, that should see everything instead
        Assert.assertTrue(canAccess(manager, milUser, namedTreeA, READ));
        Assert.assertTrue(canAccess(manager, milUser, statesLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, roadsLayer, READ));
        Assert.assertTrue(canAccess(manager, milUser, containerTreeB, READ));
        Assert.assertTrue(canAccess(manager, milUser, singleGroupC, READ));
        Assert.assertTrue(canAccess(manager, milUser, wsContainerD, READ));
        Assert.assertTrue(canAccess(manager, milUser, arcGridLayer, READ));
    }
}


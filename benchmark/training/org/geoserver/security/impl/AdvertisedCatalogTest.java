/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.impl;


import Dispatcher.REQUEST;
import LayerGroupVisibilityPolicy.HIDE_EMPTY;
import LayerGroupVisibilityPolicy.HIDE_NEVER;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.impl.AdvertisedCatalog;
import org.junit.Assert;
import org.junit.Test;


public class AdvertisedCatalogTest extends AbstractAuthorizationTest {
    @Test
    public void testNotAdvertisedLayersInGroupWithHideGroupIfEmptyPolicy() throws Exception {
        AdvertisedCatalog sc = new AdvertisedCatalog(catalog);
        sc.setLayerGroupVisibilityPolicy(HIDE_EMPTY);
        Assert.assertNull(sc.getLayerByName("topp:states"));
        Assert.assertNull(sc.getLayerByName("topp:roads"));
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");
        Assert.assertNull(layerGroup);
    }

    @Test
    public void testNotAdvertisedLayersInGroupWithNeverHideGroupPolicy() throws Exception {
        AdvertisedCatalog sc = new AdvertisedCatalog(catalog);
        sc.setLayerGroupVisibilityPolicy(HIDE_NEVER);
        Assert.assertNull(sc.getLayerByName("topp:states"));
        Assert.assertNull(sc.getLayerByName("topp:roads"));
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");
        Assert.assertNotNull(layerGroup);
        Assert.assertEquals(0, layerGroup.getLayers().size());
    }

    @Test
    public void testLayerSpecificCapabilities() throws Exception {
        AdvertisedCatalog sc = new AdvertisedCatalog(catalog);
        REQUEST.get().setContext("topp/states");
        Assert.assertNotNull(sc.getLayerByName("topp:states"));
    }
}


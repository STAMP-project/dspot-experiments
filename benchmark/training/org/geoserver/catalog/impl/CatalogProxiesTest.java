/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import org.geoserver.catalog.LayerInfo;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class CatalogProxiesTest extends GeoServerSystemTestSupport {
    @Test
    public void testDanglingReferenceOnModificationProxy() {
        LayerInfo li = getCatalog().getLayerByName(getLayerId(SystemTestData.BUILDINGS));
        Assert.assertNull(li.getDefaultStyle());
    }

    @Test
    public void testDanglingReferenceEqualsHashcode() {
        LayerInfo li = getCatalog().getLayerByName(getLayerId(SystemTestData.BUILDINGS));
        // this would have failed with an exception, also check for stable hash code
        Assert.assertEquals(li.hashCode(), li.hashCode());
        // despite the dangling reference, the layer is equal to itself
        Assert.assertTrue(li.equals(li));
    }
}


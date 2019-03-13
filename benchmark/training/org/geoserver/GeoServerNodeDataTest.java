/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver;


import org.junit.Assert;
import org.junit.Test;


public class GeoServerNodeDataTest {
    @Test
    public void testCreate() {
        GeoServerNodeData data = GeoServerNodeData.createFromString("id:foo");
        Assert.assertEquals("foo", data.getId());
        Assert.assertNotNull(data.getIdStyle());
    }
}


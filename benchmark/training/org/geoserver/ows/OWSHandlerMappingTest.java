/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;


import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class OWSHandlerMappingTest extends GeoServerSystemTestSupport {
    private OWSHandlerMapping mapping = null;

    @Test
    public void testLookupHandler_WithoutWorkspace() throws Exception {
        Assert.assertNotNull(this.mapping.lookupHandler("/test", null));
    }

    @Test
    public void testLookupHandler_WorkspaceExists() throws Exception {
        Assert.assertNotNull(this.mapping.lookupHandler("/cite/test", null));
    }

    @Test
    public void testLookupHandler_WorkspaceMissing() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/ws/test", null));
    }

    @Test
    public void testLookupHandler_LayerExists() throws Exception {
        Assert.assertNotNull(this.mapping.lookupHandler("/cite/BasicPolygons/test", null));
    }

    @Test
    public void testLookupHandler_LayerMissing() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/cite/MissingLayer/test", null));
    }

    @Test
    public void testLookupHandler_LayerMissingInWorkspace() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/cite/Fifteen/test", null));
    }

    @Test
    public void testLookupHandler_WorkspacedLayerGroupExists() throws Exception {
        Assert.assertNotNull(this.mapping.lookupHandler("/cite/LayerGroup2/test", null));
    }

    @Test
    public void testLookupHandler_WorkspacedLayerGroupMissing() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/cite/lg/test", null));
    }

    @Test
    public void testLookupHandler_LayerGroupExists() throws Exception {
        Assert.assertNotNull(this.mapping.lookupHandler("/LayerGroup1/test", null));
    }

    @Test
    public void testLookupHandler_LayerGroupMissing() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/lg/test", null));
    }

    @Test
    public void testLookupHandler_NotAGlobalLayerGroup() throws Exception {
        Assert.assertNull(this.mapping.lookupHandler("/cite:LayerGroup2/test", null));
    }
}


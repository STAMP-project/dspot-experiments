/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;


import org.geoserver.platform.ServiceException;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class LocalWorkspaceCallbackTest extends GeoServerSystemTestSupport {
    private LocalWorkspaceCallback callback = null;

    private Request request = null;

    @Test(expected = ServiceException.class)
    public void testInitRequest_WithoutWorkspace() {
        this.request.setContext("ows");
        this.callback.init(this.request);
    }

    @Test
    public void testInitRequest_WorkspaceExists() {
        this.request.setContext("cite/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNull(LocalPublished.get());
    }

    @Test(expected = ServiceException.class)
    public void testInitRequest_WorkspaceMissing() {
        this.request.setContext("ws/ows");
        this.callback.init(this.request);
    }

    @Test
    public void testInitRequest_LayerExists() {
        this.request.setContext("cite/BasicPolygons/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNotNull(LocalPublished.get());
    }

    @Test
    public void testInitRequest_LayerMissing() {
        this.request.setContext("cite/MissingLayer/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNull(LocalPublished.get());
    }

    @Test
    public void testInitRequest_LayerMissingInWorkspace() {
        this.request.setContext("cite/Fifteen/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNull(LocalPublished.get());
    }

    @Test
    public void testInitRequest_WorkspacedLayerGroupExists() {
        this.request.setContext("cite/LayerGroup2/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNotNull(LocalPublished.get());
    }

    @Test
    public void testInitRequest_WorkspacedLayerGroupMissing() {
        this.request.setContext("cite/lg/ows");
        this.callback.init(this.request);
        Assert.assertNotNull(LocalWorkspace.get());
        Assert.assertNull(LocalPublished.get());
    }

    @Test
    public void testInitRequest_LayerGroupExists() {
        this.request.setContext("LayerGroup1/ows");
        this.callback.init(this.request);
        Assert.assertNull(LocalWorkspace.get());
        Assert.assertNotNull(LocalPublished.get());
    }

    @Test(expected = ServiceException.class)
    public void testInitRequest_LayerGroupMissing() {
        this.request.setContext("lg/ows");
        this.callback.init(this.request);
    }

    @Test(expected = ServiceException.class)
    public void testInitRequest_NotAGlobalLayerGroup() {
        this.request.setContext("cite:LayerGroup2/ows");
        this.callback.init(this.request);
    }
}


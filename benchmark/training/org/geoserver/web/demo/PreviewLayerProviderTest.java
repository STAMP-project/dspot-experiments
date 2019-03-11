/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.demo;


import LayerGroupInfo.Mode.CONTAINER;
import LayerGroupInfo.Mode.OPAQUE_CONTAINER;
import LayerGroupInfo.Mode.SINGLE;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class PreviewLayerProviderTest extends GeoServerWicketTestSupport {
    @Test
    public void testNonAdvertisedLayer() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        try {
            // now you see me
            PreviewLayerProvider provider = new PreviewLayerProvider();
            PreviewLayer pl = getPreviewLayer(provider, layerId);
            Assert.assertNotNull(pl);
            // now you don't!
            layer.setAdvertised(false);
            getCatalog().save(layer);
            pl = getPreviewLayer(provider, layerId);
            Assert.assertNull(pl);
        } finally {
            layer.setAdvertised(true);
            getCatalog().save(layer);
        }
    }

    @Test
    public void testSingleLayerGroup() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        LayerGroupInfo group = getCatalog().getFactory().createLayerGroup();
        group.setName("testSingleLayerGroup");
        group.setMode(SINGLE);
        group.getLayers().add(layer);
        group.setTitle("This is the title");
        group.setAbstract("This is the abstract");
        getCatalog().add(group);
        try {
            PreviewLayerProvider provider = new PreviewLayerProvider();
            PreviewLayer pl = getPreviewLayer(provider, group.prefixedName());
            Assert.assertNotNull(pl);
            Assert.assertEquals("This is the title", pl.getTitle());
            Assert.assertEquals("This is the abstract", pl.getAbstract());
        } finally {
            getCatalog().remove(group);
        }
    }

    @Test
    public void testOpaqueContainerLayerGroup() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        LayerGroupInfo group = getCatalog().getFactory().createLayerGroup();
        group.setName("testOpaqueContainerLayerGroup");
        group.setMode(OPAQUE_CONTAINER);
        group.getLayers().add(layer);
        group.setTitle("This is the title");
        group.setAbstract("This is the abstract");
        getCatalog().add(group);
        try {
            PreviewLayerProvider provider = new PreviewLayerProvider();
            PreviewLayer pl = getPreviewLayer(provider, group.prefixedName());
            Assert.assertNotNull(pl);
            Assert.assertEquals("This is the title", pl.getTitle());
            Assert.assertEquals("This is the abstract", pl.getAbstract());
        } finally {
            getCatalog().remove(group);
        }
    }

    @Test
    public void testWorkspacedLayerGroup() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        WorkspaceInfo ws = getCatalog().getWorkspaceByName("cite");
        LayerGroupInfo group = getCatalog().getFactory().createLayerGroup();
        group.setName("testWorkspacedLayerGroup");
        group.setMode(SINGLE);
        group.setWorkspace(ws);
        group.getLayers().add(layer);
        getCatalog().add(group);
        try {
            PreviewLayerProvider provider = new PreviewLayerProvider();
            PreviewLayer pl = getPreviewLayer(provider, group.prefixedName());
            Assert.assertNotNull(pl);
            Assert.assertEquals("cite:testWorkspacedLayerGroup", pl.getName());
        } finally {
            getCatalog().remove(group);
        }
    }

    @Test
    public void testContainerLayerGroup() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        LayerGroupInfo group = getCatalog().getFactory().createLayerGroup();
        group.setName("testContainerLayerGroup");
        group.setMode(CONTAINER);
        group.getLayers().add(layer);
        getCatalog().add(group);
        try {
            PreviewLayerProvider provider = new PreviewLayerProvider();
            PreviewLayer pl = getPreviewLayer(provider, group.prefixedName());
            Assert.assertNull(pl);
        } finally {
            getCatalog().remove(group);
        }
    }

    @Test
    public void testNestedContainerLayerGroup() throws Exception {
        String layerId = getLayerId(MockData.BUILDINGS);
        LayerInfo layer = getCatalog().getLayerByName(layerId);
        LayerGroupInfo containerGroup = getCatalog().getFactory().createLayerGroup();
        containerGroup.setName("testContainerLayerGroup");
        containerGroup.setMode(SINGLE);
        containerGroup.getLayers().add(layer);
        getCatalog().add(containerGroup);
        LayerGroupInfo singleGroup = getCatalog().getFactory().createLayerGroup();
        singleGroup.setName("testSingleLayerGroup");
        singleGroup.setMode(SINGLE);
        singleGroup.getLayers().add(containerGroup);
        getCatalog().add(singleGroup);
        try {
            PreviewLayerProvider provider = new PreviewLayerProvider();
            Assert.assertNotNull(getPreviewLayer(provider, singleGroup.prefixedName()));
            Assert.assertNotNull(getPreviewLayer(provider, layer.prefixedName()));
        } finally {
            getCatalog().remove(singleGroup);
            getCatalog().remove(containerGroup);
        }
    }

    @Test
    public void testKewordsFilterSizeCache() {
        PreviewLayerProvider provider = new PreviewLayerProvider();
        Assert.assertEquals(29, provider.size());
        provider.setKeywords(new String[]{ "cite" });
        Assert.assertEquals(12, provider.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetItems() throws Exception {
        // Ensure that the method getItems is no more called
        PreviewLayerProvider provider = new PreviewLayerProvider();
        provider.getItems();
    }
}


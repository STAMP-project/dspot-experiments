/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import ModificationType.DELETE;
import ModificationType.EXTRA_STYLE_REMOVED;
import ModificationType.GROUP_CHANGED;
import ModificationType.STYLE_RESET;
import java.util.ArrayList;
import java.util.List;
import org.geoserver.catalog.CascadeRemovalReporter;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.data.test.MockData;
import org.junit.Assert;
import org.junit.Test;


public class CascadeRemovalReporterTest extends CascadeVisitorAbstractTest {
    @Test
    public void testCascadeLayer() {
        Catalog catalog = getCatalog();
        CascadeRemovalReporter visitor = new CascadeRemovalReporter(catalog);
        String name = getLayerId(MockData.LAKES);
        LayerInfo layer = catalog.getLayerByName(name);
        Assert.assertNotNull(layer);
        visitor.visit(layer);
        // layer.accept(visitor);
        // we expect a layer, a resource and two groups
        Assert.assertEquals(4, visitor.getObjects(null).size());
        // check the layer and resource have been marked to delete (and
        Assert.assertEquals(catalog.getLayerByName(name), visitor.getObjects(LayerInfo.class, DELETE).get(0));
        Assert.assertEquals(catalog.getResourceByName(name, ResourceInfo.class), visitor.getObjects(ResourceInfo.class, DELETE).get(0));
        // the groups have been marked to update?
        Assert.assertTrue(visitor.getObjects(LayerGroupInfo.class, GROUP_CHANGED).contains(catalog.getLayerGroupByName(CascadeVisitorAbstractTest.LAKES_GROUP)));
        Assert.assertTrue(visitor.getObjects(LayerGroupInfo.class, GROUP_CHANGED).contains(catalog.getLayerGroupByName(CascadeVisitorAbstractTest.NEST_GROUP)));
    }

    @Test
    public void testCascadeStore() {
        Catalog catalog = getCatalog();
        CascadeRemovalReporter visitor = new CascadeRemovalReporter(catalog);
        String citeStore = MockData.CITE_PREFIX;
        StoreInfo store = catalog.getStoreByName(citeStore, StoreInfo.class);
        String buildings = getLayerId(MockData.BUILDINGS);
        String lakes = getLayerId(MockData.LAKES);
        LayerInfo bl = catalog.getLayerByName(buildings);
        ResourceInfo br = catalog.getResourceByName(buildings, ResourceInfo.class);
        LayerInfo ll = catalog.getLayerByName(lakes);
        ResourceInfo lr = catalog.getResourceByName(lakes, ResourceInfo.class);
        visitor.visit(((DataStoreInfo) (store)));
        Assert.assertEquals(store, visitor.getObjects(StoreInfo.class, DELETE).get(0));
        List<LayerInfo> layers = visitor.getObjects(LayerInfo.class, DELETE);
        Assert.assertTrue(layers.contains(bl));
        Assert.assertTrue(layers.contains(ll));
        List<ResourceInfo> resources = visitor.getObjects(ResourceInfo.class, DELETE);
        Assert.assertTrue(resources.contains(br));
        Assert.assertTrue(resources.contains(lr));
    }

    @Test
    public void testCascadeWorkspace() {
        Catalog catalog = getCatalog();
        CascadeRemovalReporter visitor = new CascadeRemovalReporter(catalog);
        WorkspaceInfo ws = catalog.getWorkspaceByName(MockData.CITE_PREFIX);
        Assert.assertNotNull(ws);
        List<StoreInfo> stores = getCatalog().getStoresByWorkspace(ws, StoreInfo.class);
        List<StyleInfo> styles = getCatalog().getStylesByWorkspace(ws);
        List<LayerGroupInfo> layerGroups = getCatalog().getLayerGroupsByWorkspace(ws);
        List<LayerGroupInfo> changedLayerGroups = new ArrayList<LayerGroupInfo>();
        // Added another check for Layergroups which are not in the ws but contain
        // Layers belonging to this ws
        List<LayerGroupInfo> totalLayerGroups = getCatalog().getLayerGroups();
        for (LayerGroupInfo info : totalLayerGroups) {
            List<PublishedInfo> layers = info.getLayers();
            int size = countStores(info, stores);
            if (size == (layers.size())) {
                if (!(layerGroups.contains(info))) {
                    layerGroups.add(info);
                }
            } else {
                changedLayerGroups.add(info);
            }
        }
        ws.accept(visitor);
        Assert.assertTrue(stores.containsAll(visitor.getObjects(StoreInfo.class, DELETE)));
        Assert.assertTrue(styles.containsAll(visitor.getObjects(StyleInfo.class, DELETE)));
        Assert.assertTrue(layerGroups.containsAll(visitor.getObjects(LayerGroupInfo.class, DELETE)));
        Assert.assertTrue(changedLayerGroups.containsAll(visitor.getObjects(LayerGroupInfo.class, GROUP_CHANGED)));
    }

    @Test
    public void testCascadeStyle() {
        Catalog catalog = getCatalog();
        CascadeRemovalReporter visitor = new CascadeRemovalReporter(catalog);
        StyleInfo style = catalog.getStyleByName(MockData.LAKES.getLocalPart());
        LayerInfo buildings = catalog.getLayerByName(getLayerId(MockData.BUILDINGS));
        LayerInfo lakes = catalog.getLayerByName(getLayerId(MockData.LAKES));
        visitor.visit(style);
        // test style reset
        Assert.assertEquals(style, visitor.getObjects(StyleInfo.class, DELETE).get(0));
        Assert.assertEquals(lakes, visitor.getObjects(LayerInfo.class, STYLE_RESET).get(0));
        Assert.assertEquals(buildings, visitor.getObjects(LayerInfo.class, EXTRA_STYLE_REMOVED).get(0));
    }
}


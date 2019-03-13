/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import Filter.INCLUDE;
import java.util.List;
import org.geoserver.catalog.CascadeDeleteVisitor;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.data.test.CiteTestData;
import org.junit.Assert;
import org.junit.Test;


public class CascadeDeleteVisitorTest extends CascadeVisitorAbstractTest {
    @Test
    public void testCascadeLayer() {
        Catalog catalog = getCatalog();
        String name = toString(CiteTestData.LAKES);
        LayerInfo layer = catalog.getLayerByName(name);
        Assert.assertNotNull(layer);
        CascadeDeleteVisitor visitor = new CascadeDeleteVisitor(catalog);
        visitor.visit(layer);
        LayerGroupInfo group = catalog.getLayerGroupByName(CascadeVisitorAbstractTest.LAKES_GROUP);
        Assert.assertEquals(2, group.getLayers().size());
        Assert.assertFalse(group.getLayers().contains(layer));
    }

    @Test
    public void testCascadeLayerGroup() {
        Catalog catalog = getCatalog();
        LayerGroupInfo layerGroup = catalog.getLayerGroupByName(CascadeVisitorAbstractTest.LAKES_GROUP);
        Assert.assertNotNull(layerGroup);
        CascadeDeleteVisitor visitor = new CascadeDeleteVisitor(catalog);
        visitor.visit(layerGroup);
        LayerGroupInfo nestedGroup = catalog.getLayerGroupByName(CascadeVisitorAbstractTest.NEST_GROUP);
        Assert.assertNotNull(nestedGroup);
        Assert.assertEquals(1, nestedGroup.getLayers().size());
        Assert.assertEquals(1, nestedGroup.getStyles().size());
    }

    @Test
    public void testCascadeLayerDuplicate() {
        Catalog catalog = getCatalog();
        String name = toString(CiteTestData.LAKES);
        LayerInfo layer = catalog.getLayerByName(name);
        Assert.assertNotNull(layer);
        LayerGroupInfo group = catalog.getLayerGroupByName(CascadeVisitorAbstractTest.LAKES_GROUP);
        group.getLayers().add(layer);
        group.getStyles().add(null);
        catalog.save(group);
        CascadeDeleteVisitor visitor = new CascadeDeleteVisitor(catalog);
        visitor.visit(layer);
        group = catalog.getLayerGroupByName(CascadeVisitorAbstractTest.LAKES_GROUP);
        Assert.assertEquals(2, group.getLayers().size());
        Assert.assertFalse(group.getLayers().contains(layer));
    }

    @Test
    public void testCascadeStore() {
        Catalog catalog = getCatalog();
        DataStoreInfo store = ((DataStoreInfo) (catalog.getLayerByName(getLayerId(CiteTestData.LAKES)).getResource().getStore()));
        new CascadeDeleteVisitor(catalog).visit(store);
        // that store actually holds all layers, so check we got empty
        Assert.assertEquals(0, catalog.count(LayerInfo.class, INCLUDE));
        Assert.assertEquals(0, catalog.count(ResourceInfo.class, INCLUDE));
        Assert.assertEquals(0, catalog.count(StoreInfo.class, INCLUDE));
        List<LayerGroupInfo> groups = catalog.getLayerGroups();
        Assert.assertEquals(0, catalog.count(LayerGroupInfo.class, INCLUDE));
    }

    @Test
    public void testCascadeWorkspace() {
        Catalog catalog = getCatalog();
        WorkspaceInfo ws = catalog.getWorkspaceByName(CiteTestData.CITE_PREFIX);
        new CascadeDeleteVisitor(catalog).visit(ws);
        // check the namespace is also gone
        Assert.assertNull(catalog.getNamespaceByPrefix(CiteTestData.CITE_PREFIX));
        // that workspace actually holds all layers, so check we got empty
        Assert.assertEquals(0, catalog.count(LayerInfo.class, INCLUDE));
        Assert.assertEquals(0, catalog.count(ResourceInfo.class, INCLUDE));
        Assert.assertEquals(0, catalog.count(StoreInfo.class, INCLUDE));
        Assert.assertEquals(0, catalog.count(LayerGroupInfo.class, INCLUDE));
        // the workspace specific style is also gone
        Assert.assertEquals(0, catalog.getStylesByWorkspace(CiteTestData.CITE_PREFIX).size());
        Assert.assertNull(catalog.getStyleByName(CascadeVisitorAbstractTest.WS_STYLE));
    }

    @Test
    public void testCascadeStyle() {
        Catalog catalog = getCatalog();
        StyleInfo style = catalog.getStyleByName(CiteTestData.LAKES.getLocalPart());
        Assert.assertNotNull(style);
        new CascadeDeleteVisitor(catalog).visit(style);
        Assert.assertNull(catalog.getStyleByName(CiteTestData.LAKES.getLocalPart()));
        LayerInfo layer = catalog.getLayerByName(getLayerId(CiteTestData.LAKES));
        Assert.assertEquals("polygon", layer.getDefaultStyle().getName());
    }
}


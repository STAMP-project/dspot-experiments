/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;


import Dispatcher.REQUEST;
import Mode.NAMED;
import java.util.List;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.SettingsInfo;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.ows.LocalPublished;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.ows.Request;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LocalWorkspaceLayersTest extends GeoServerSystemTestSupport {
    static final String GLOBAL_GROUP = "globalGroup";

    static final String GLOBAL_GROUP2 = "globalGroup2";

    static final String NESTED_GROUP = "nestedGroup";

    static final String LOCAL_GROUP = "localGroup";

    Catalog catalog;

    @Test
    public void testGroupLayerInWorkspace() {
        // System.out.println(catalog.getLayerGroups());
        WorkspaceInfo workspace = catalog.getWorkspaceByName("sf");
        WorkspaceInfo workspace2 = catalog.getWorkspaceByName("cite");
        CatalogFactory factory = catalog.getFactory();
        LayerGroupInfo globalGroup = factory.createLayerGroup();
        globalGroup.setName("globalGroup");
        globalGroup.setWorkspace(workspace2);
        globalGroup.getLayers().add(catalog.getLayerByName("Lakes"));
        globalGroup.getStyles().add(null);
        catalog.add(globalGroup);
        LayerGroupInfo localGroup = factory.createLayerGroup();
        localGroup.setName("localGroup");
        localGroup.setWorkspace(workspace);
        localGroup.getLayers().add(catalog.getLayerByName("GenericEntity"));
        localGroup.getStyles().add(null);
        catalog.add(localGroup);
        String localName = localGroup.prefixedName();
        Assert.assertEquals("sf:localGroup", localName);
        Assert.assertEquals(2, catalog.getLayerGroups().size());
        LocalWorkspace.set(workspace2);
        Assert.assertNull(catalog.getLayerGroupByName("localGroup"));
        LocalWorkspace.remove();
        LocalWorkspace.set(workspace);
        Assert.assertNotNull(catalog.getLayerGroupByName("localGroup"));
        Assert.assertEquals(1, catalog.getLayerGroups().size());
        Assert.assertEquals("localGroup", catalog.getLayerGroupByName("localGroup").prefixedName());
        GeoServer gs = getGeoServer();
        SettingsInfo settings = gs.getFactory().createSettings();
        settings.setLocalWorkspaceIncludesPrefix(true);
        settings.setWorkspace(workspace);
        gs.add(settings);
        Assert.assertEquals("sf:localGroup", catalog.getLayerGroupByName("localGroup").prefixedName());
        Assert.assertEquals("sf:localGroup", catalog.getLayerGroups().get(0).prefixedName());
        gs.remove(settings);
        LocalWorkspace.remove();
    }

    @Test
    public void testLayersInLocalWorkspace() {
        WorkspaceInfo sf = catalog.getWorkspaceByName("sf");
        WorkspaceInfo cite = catalog.getWorkspaceByName("cite");
        CatalogFactory factory = catalog.getFactory();
        DataStoreInfo citeStore = factory.createDataStore();
        citeStore.setEnabled(true);
        citeStore.setName("globalStore");
        citeStore.setWorkspace(cite);
        catalog.add(citeStore);
        FeatureTypeInfo citeFeatureType = factory.createFeatureType();
        citeFeatureType.setName("citeLayer");
        citeFeatureType.setStore(citeStore);
        citeFeatureType.setNamespace(catalog.getNamespaceByPrefix("cite"));
        catalog.add(citeFeatureType);
        LayerInfo citeLayer = factory.createLayer();
        citeLayer.setResource(citeFeatureType);
        citeLayer.setEnabled(true);
        // citeLayer.setName("citeLayer");
        catalog.add(citeLayer);
        Assert.assertNotNull(catalog.getLayerByName("citeLayer"));
        Assert.assertEquals("cite:citeLayer", catalog.getLayerByName("citeLayer").prefixedName());
        DataStoreInfo sfStore = factory.createDataStore();
        sfStore.setEnabled(true);
        sfStore.setName("localStore");
        sfStore.setWorkspace(sf);
        catalog.add(sfStore);
        FeatureTypeInfo sfFeatureType = factory.createFeatureType();
        sfFeatureType.setName("sfLayer");
        sfFeatureType.setStore(sfStore);
        sfFeatureType.setNamespace(catalog.getNamespaceByPrefix("sf"));
        catalog.add(sfFeatureType);
        LayerInfo sfLayer = factory.createLayer();
        sfLayer.setResource(sfFeatureType);
        sfLayer.setEnabled(true);
        // sfLayer.setName("sfLayer");
        catalog.add(sfLayer);
        Assert.assertNotNull(catalog.getLayerByName("citeLayer"));
        Assert.assertNotNull(catalog.getLayerByName("sfLayer"));
        LocalWorkspace.set(sf);
        Assert.assertNull(catalog.getLayerByName("citeLayer"));
        Assert.assertNotNull(catalog.getLayerByName("sfLayer"));
        Assert.assertEquals("sfLayer", catalog.getLayerByName("sfLayer").prefixedName());
        LocalWorkspace.remove();
        LocalWorkspace.set(cite);
        Assert.assertNull(catalog.getLayerByName("sfLayer"));
        Assert.assertNotNull(catalog.getLayerByName("citeLayer"));
        Assert.assertEquals("citeLayer", catalog.getLayerByName("citeLayer").prefixedName());
        LocalWorkspace.remove();
    }

    @Test
    public void testGlobalGroupSpecificRequest() {
        CatalogFactory factory = catalog.getFactory();
        LayerGroupInfo globalGroup = factory.createLayerGroup();
        globalGroup.setName(LocalWorkspaceLayersTest.GLOBAL_GROUP);
        globalGroup.getLayers().add(getBuildingsLayer());
        globalGroup.getLayers().add(getAggregateGeoFeatureLayer());
        globalGroup.getStyles().add(null);
        globalGroup.getStyles().add(null);
        catalog.add(globalGroup);
        LayerGroupInfo globalGroup2 = factory.createLayerGroup();
        globalGroup2.setName(LocalWorkspaceLayersTest.GLOBAL_GROUP2);
        globalGroup2.getLayers().add(getBridgesLayer());
        globalGroup2.getStyles().add(null);
        catalog.add(globalGroup2);
        LocalPublished.set(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        // some direct access tests, generic request
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNull(getBridgesLayer());
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP2));
        Assert.assertNotNull(getBuildingsLayer());
        Assert.assertNotNull(getAggregateGeoFeatureLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        List<LayerInfo> layers = catalog.getLayers();
        Assert.assertEquals(2, layers.size());
        Assert.assertThat(layers, Matchers.containsInAnyOrder(getBuildingsLayer(), getAggregateGeoFeatureLayer()));
        // now simulate WMS getCaps, the layers should not appear in the caps document
        Request request = new Request();
        request.setService("WMS");
        request.setRequest("GetCapabilities");
        REQUEST.set(request);
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNull(getBridgesLayer());
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP2));
        Assert.assertNull(getBuildingsLayer());
        Assert.assertNull(getAggregateGeoFeatureLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertEquals(0, catalog.getLayers().size());
        LocalPublished.remove();
    }

    @Test
    public void testNestedGroupSpecificRequest() {
        CatalogFactory factory = catalog.getFactory();
        LayerGroupInfo nestedGroup = factory.createLayerGroup();
        nestedGroup.setName(LocalWorkspaceLayersTest.NESTED_GROUP);
        nestedGroup.getLayers().add(getBridgesLayer());
        nestedGroup.getStyles().add(null);
        catalog.add(nestedGroup);
        LayerGroupInfo globalGroup = factory.createLayerGroup();
        globalGroup.setName(LocalWorkspaceLayersTest.GLOBAL_GROUP);
        globalGroup.getLayers().add(getBuildingsLayer());
        globalGroup.getLayers().add(getAggregateGeoFeatureLayer());
        globalGroup.getLayers().add(nestedGroup);
        globalGroup.getStyles().add(null);
        globalGroup.getStyles().add(null);
        globalGroup.getStyles().add(null);
        catalog.add(globalGroup);
        LocalPublished.set(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        // some direct access tests, generic request, everything nested
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNotNull(getBridgesLayer());
        Assert.assertNotNull(getBuildingsLayer());
        Assert.assertNotNull(getAggregateGeoFeatureLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.NESTED_GROUP));
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertThat(catalog.getLayers(), Matchers.containsInAnyOrder(getBuildingsLayer(), getAggregateGeoFeatureLayer(), getBridgesLayer()));
        // now simulate WMS getCaps, the layers should not appear in the caps document
        Request request = new Request();
        request.setService("WMS");
        request.setRequest("GetCapabilities");
        REQUEST.set(request);
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNull(getBridgesLayer());
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.NESTED_GROUP));
        Assert.assertNull(getBuildingsLayer());
        Assert.assertNull(getAggregateGeoFeatureLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertEquals(0, catalog.getLayers().size());
        // and then change the mode of the group to tree mode, contents will show up in caps too
        globalGroup = catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP);
        globalGroup.setMode(NAMED);
        catalog.save(globalGroup);
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNotNull(getBridgesLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.NESTED_GROUP));
        Assert.assertNotNull(getBuildingsLayer());
        Assert.assertNotNull(getAggregateGeoFeatureLayer());
        Assert.assertNotNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertThat(catalog.getLayers(), Matchers.containsInAnyOrder(getBuildingsLayer(), getAggregateGeoFeatureLayer(), getBridgesLayer()));
        LocalPublished.remove();
    }

    @Test
    public void testWorkspaceGroupSpecificRequest() {
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo citeWs = catalog.getWorkspaceByName("cite");
        addLocalGroup(factory, citeWs);
        LayerGroupInfo globalGroup = factory.createLayerGroup();
        globalGroup.setName(LocalWorkspaceLayersTest.GLOBAL_GROUP);
        globalGroup.getLayers().add(getAggregateGeoFeatureLayer());
        globalGroup.getStyles().add(null);
        catalog.add(globalGroup);
        LocalWorkspace.set(citeWs);
        LocalPublished.set(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.LOCAL_GROUP));
        // some direct access tests, generic request
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNull(getAggregateGeoFeatureLayer());
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertNotNull(getBridgesLayer());
        Assert.assertNotNull(getBuildingsLayer());
        List<LayerInfo> layers = catalog.getLayers();
        Assert.assertEquals(2, layers.size());
        Assert.assertThat(layers, Matchers.containsInAnyOrder(getBuildingsLayer(), getBridgesLayer()));
        // now simulate WMS getCaps, the layers should not appear in the caps document
        Request request = new Request();
        request.setService("WMS");
        request.setRequest("GetCapabilities");
        REQUEST.set(request);
        Assert.assertNull(catalog.getLayerByName(getLayerId(SystemTestData.BASIC_POLYGONS)));
        Assert.assertNull(getBridgesLayer());
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertNull(getBuildingsLayer());
        Assert.assertNull(getAggregateGeoFeatureLayer());
        Assert.assertEquals(0, catalog.getLayers().size());
        LocalPublished.remove();
        LocalWorkspace.remove();
    }

    @Test
    public void testLayerLocalWithContainingGroup() throws Exception {
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo citeWs = catalog.getWorkspaceByName("cite");
        addLocalGroup(factory, citeWs);
        // set a local layer that's in the group
        final LayerInfo buildingsLayer = getBuildingsLayer();
        LocalPublished.set(buildingsLayer);
        Assert.assertNotNull(catalog.getLayerByName(buildingsLayer.prefixedName()));
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.LOCAL_GROUP));
        Assert.assertEquals(1, catalog.getLayers().size());
        Assert.assertThat(catalog.getLayerGroups(), Matchers.empty());
    }

    @Test
    public void testLayerLocalWithNonContainingGroup() throws Exception {
        CatalogFactory factory = catalog.getFactory();
        WorkspaceInfo citeWs = catalog.getWorkspaceByName("cite");
        addLocalGroup(factory, citeWs);
        // set a local layer that's not in the group
        final LayerInfo dividedRoutes = catalog.getLayerByName(getLayerId(SystemTestData.DIVIDED_ROUTES));
        LocalPublished.set(dividedRoutes);
        Assert.assertNotNull(catalog.getLayerByName(dividedRoutes.prefixedName()));
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.GLOBAL_GROUP));
        Assert.assertNull(catalog.getLayerGroupByName(LocalWorkspaceLayersTest.LOCAL_GROUP));
        Assert.assertEquals(1, catalog.getLayers().size());
        Assert.assertThat(catalog.getLayerGroups(), Matchers.empty());
    }
}


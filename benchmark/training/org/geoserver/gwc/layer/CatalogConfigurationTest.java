/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import PublishedType.VECTOR;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.GWCTestHelpers;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.ows.LocalWorkspace;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opengis.feature.type.FeatureType;


/**
 * Test class for the GWCCatalogListener
 *
 * @author groldan
 */
public class CatalogConfigurationTest {
    private Catalog catalog;

    private TileLayerCatalog tileLayerCatalog;

    private GridSetBroker gridSetBroker;

    private CatalogConfiguration config;

    private LayerInfo layer1;

    private LayerInfo layer2;

    private LayerInfo layerWithNoTileLayer;

    private LayerGroupInfo group1;

    private LayerGroupInfo group2;

    private LayerGroupInfo groupWithNoTileLayer;

    private GeoServerTileLayerInfo layerInfo1;

    private GeoServerTileLayerInfo layerInfo2;

    private GeoServerTileLayerInfo groupInfo1;

    private GeoServerTileLayerInfo groupInfo2;

    private GWC mockMediator;

    private GWCConfig defaults;

    @Test
    public void testGoofyMethods() {
        Assert.assertEquals("GeoServer Catalog Configuration", config.getIdentifier());
    }

    @Test
    public void testInitialize() {
        config.afterPropertiesSet();
    }

    @Test
    public void testGetTileLayerCount() {
        Assert.assertEquals(4, config.getLayerCount());
    }

    @Test
    public void testGetTileLayerNames() {
        Set<String> expected = ImmutableSet.of(GWC.tileLayerName(layer1), GWC.tileLayerName(layer2), GWC.tileLayerName(group1), GWC.tileLayerName(group2));
        Set<String> actual = config.getLayerNames();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetLayers() {
        Iterable<TileLayer> layers = config.getLayers();
        testGetLayers(layers);
    }

    @Test
    public void testDeprecatedGetTileLayers() {
        @SuppressWarnings("deprecation")
        Iterable<TileLayer> layers = config.getLayers();
        testGetLayers(layers);
    }

    @Test
    public void testGetTileLayer() {
        String layerName = GWC.tileLayerName(layerWithNoTileLayer);
        Assert.assertFalse(config.getLayer(layerName).isPresent());
        Assert.assertFalse(config.getLayer(GWC.tileLayerName(groupWithNoTileLayer)).isPresent());
        Assert.assertTrue(config.getLayer(GWC.tileLayerName(layer1)).isPresent());
        Assert.assertTrue(config.getLayer(GWC.tileLayerName(layer2)).isPresent());
        Assert.assertTrue(config.getLayer(GWC.tileLayerName(group1)).isPresent());
        Assert.assertTrue(config.getLayer(GWC.tileLayerName(group2)).isPresent());
        Assert.assertFalse(config.getLayer("anythingElse").isPresent());
    }

    @Test
    public void testModifyLayer() {
        try {
            config.modifyLayer(null);
            Assert.fail("expected precondition exception");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("is null"));
        }
        try {
            config.modifyLayer(Mockito.mock(TileLayer.class));
            Assert.fail("expected precondition exception");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Can't save TileLayer of type"));
        }
        GeoServerTileLayer tileLayer1 = ((GeoServerTileLayer) (config.getLayer(GWC.tileLayerName(layer1)).get()));
        GeoServerTileLayer tileLayer2 = ((GeoServerTileLayer) (config.getLayer(GWC.tileLayerName(group1)).get()));
        testModifyLayer(tileLayer1);
        testModifyLayer(tileLayer2);
    }

    @Test
    public void testRemoveLayer() {
        try {
            config.removeLayer(null);
            Assert.fail("expected precondition violation exception");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
        try {
            config.removeLayer(GWC.tileLayerName(layerWithNoTileLayer));
            Assert.fail("expected precondition violation exception");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
        try {
            config.removeLayer(GWC.tileLayerName(groupWithNoTileLayer));
            Assert.fail("expected precondition violation exception");
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
        String layerName;
        layerName = GWC.tileLayerName(layer1);
        Assert.assertNotNull(config.getLayer(layerName));
        final int initialCount = config.getLayerCount();
        config.removeLayer(layerName);
        // Update mocks
        Mockito.when(tileLayerCatalog.getLayerByName(layerName)).thenReturn(null);
        Mockito.when(tileLayerCatalog.getLayerId(layerName)).thenReturn(null);
        Mockito.when(tileLayerCatalog.getLayerNames()).thenReturn(ImmutableSet.of(GWC.tileLayerName(layer2), GWC.tileLayerName(group1), GWC.tileLayerName(group2)));
        Mockito.when(tileLayerCatalog.getLayerIds()).thenReturn(ImmutableSet.of(layer2.getId(), group1.getId(), group2.getId()));
        Assert.assertFalse(config.getLayer(layerName).isPresent());
        Assert.assertFalse(config.getLayerNames().contains(layerName));
        Assert.assertEquals((initialCount - 1), config.getLayerCount());
        layerName = GWC.tileLayerName(group1);
        Assert.assertNotNull(config.getLayer(layerName));
        config.removeLayer(layerName);
        // Update mocks
        Mockito.when(tileLayerCatalog.getLayerByName(layerName)).thenReturn(null);
        Mockito.when(tileLayerCatalog.getLayerId(layerName)).thenReturn(null);
        Mockito.when(tileLayerCatalog.getLayerNames()).thenReturn(ImmutableSet.of(GWC.tileLayerName(layer2), GWC.tileLayerName(group2)));
        Mockito.when(tileLayerCatalog.getLayerIds()).thenReturn(ImmutableSet.of(layer2.getId(), group2.getId()));
        Assert.assertFalse(config.getLayer(layerName).isPresent());
        Assert.assertEquals((initialCount - 2), config.getLayerCount());
    }

    @Test
    public void testSaveRename() {
        GeoServerTileLayerInfo originalState = layerInfo1;
        GeoServerTileLayerInfo forceState1 = TileLayerInfoUtil.loadOrCreate(layer1, defaults);
        Mockito.when(tileLayerCatalog.save(ArgumentMatchers.same(forceState1))).thenReturn(originalState);
        forceState1.setName("newName");
        config.modifyLayer(new GeoServerTileLayer(layer1, gridSetBroker, forceState1));
        Mockito.verify(mockMediator, Mockito.never()).layerRemoved(ArgumentMatchers.anyString());
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.same(forceState1));
        Mockito.verify(mockMediator, Mockito.times(1)).layerRenamed(ArgumentMatchers.eq(layerInfo1.getName()), ArgumentMatchers.eq("newName"));
    }

    @Test
    public void testSave() {
        // delete layer
        Mockito.when(tileLayerCatalog.delete(ArgumentMatchers.eq(layerInfo2.getId()))).thenReturn(layerInfo2);
        config.removeLayer(layerInfo2.getName());
        // failing delete
        Mockito.when(tileLayerCatalog.delete(ArgumentMatchers.eq(groupInfo1.getId()))).thenReturn(groupInfo1);
        config.removeLayer(groupInfo1.getName());
        Mockito.doThrow(new IllegalArgumentException("failedDelete")).when(tileLayerCatalog).delete(ArgumentMatchers.eq(group1.getId()));
        // modify two layers, one will fail
        GeoServerTileLayerInfo forceState1 = TileLayerInfoUtil.loadOrCreate(layer1, defaults);
        forceState1.setName("newName");
        GeoServerTileLayerInfo forceState2 = TileLayerInfoUtil.loadOrCreate(group2, defaults);
        Mockito.when(tileLayerCatalog.save(ArgumentMatchers.same(forceState1))).thenReturn(layerInfo1);
        config.modifyLayer(new GeoServerTileLayer(layer1, gridSetBroker, forceState1));
        config.modifyLayer(new GeoServerTileLayer(group2, gridSetBroker, forceState2));
        // make this last one fail
        Mockito.doThrow(new IllegalArgumentException("failedSave")).when(tileLayerCatalog).save(ArgumentMatchers.eq(forceState2));
        GeoServerTileLayerInfo addedState1 = TileLayerInfoUtil.loadOrCreate(layerWithNoTileLayer, defaults);
        config.addLayer(new GeoServerTileLayer(layerWithNoTileLayer, gridSetBroker, addedState1));
        Mockito.doThrow(new IllegalArgumentException("callback exception")).when(mockMediator).layerAdded(ArgumentMatchers.eq(addedState1.getName()));
        GeoServerTileLayerInfo addedState2 = TileLayerInfoUtil.loadOrCreate(groupWithNoTileLayer, defaults);
        config.addLayer(new GeoServerTileLayer(groupWithNoTileLayer, gridSetBroker, addedState2));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).delete(ArgumentMatchers.eq(group1.getId()));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).delete(ArgumentMatchers.eq(layer2.getId()));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.same(forceState1));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.same(forceState2));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.same(addedState1));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.same(addedState2));
        Mockito.verify(mockMediator, Mockito.times(1)).layerRemoved(ArgumentMatchers.eq(layerInfo2.getName()));
        Mockito.verify(mockMediator, Mockito.times(1)).layerRenamed(ArgumentMatchers.eq(layerInfo1.getName()), ArgumentMatchers.eq("newName"));
        Mockito.verify(mockMediator, Mockito.times(1)).layerAdded(ArgumentMatchers.eq(addedState1.getName()));
        Mockito.verify(mockMediator, Mockito.times(1)).layerAdded(ArgumentMatchers.eq(addedState2.getName()));
    }

    @Test
    public void testCanSave() {
        // Create mock layer not transient and ensure that the Layer cannot be saved
        GeoServerTileLayer l = Mockito.mock(GeoServerTileLayer.class);
        Mockito.when(l.isTransientLayer()).thenReturn(true);
        Assert.assertFalse(config.canSave(l));
    }

    @Test
    public void testNoGeometry() throws Exception {
        FeatureType featureTypeWithNoGeometry = Mockito.mock(FeatureType.class);
        Mockito.when(featureTypeWithNoGeometry.getGeometryDescriptor()).thenReturn(null);
        FeatureTypeInfo resourceWithNoGeometry = Mockito.mock(FeatureTypeInfo.class);
        Mockito.when(resourceWithNoGeometry.getFeatureType()).thenReturn(featureTypeWithNoGeometry);
        LayerInfo layerWithNoGeometry = GWCTestHelpers.mockLayer("layerWithNoGeometry", new String[]{  }, VECTOR);
        layerWithNoGeometry.setResource(resourceWithNoGeometry);
        GeoServerTileLayer tl = Mockito.mock(GeoServerTileLayer.class);
        GeoServerTileLayerInfo info = new GeoServerTileLayerInfoImpl();
        info.setId("layerWithNoGeometry");
        info.setName("layerWithNoGeometry");
        Mockito.when(tl.getId()).thenReturn("layerWithNoGeometry");
        Mockito.when(tl.isTransientLayer()).thenReturn(false);
        Mockito.when(tl.getInfo()).thenReturn(info);
        Mockito.when(tl.getLayerInfo()).thenReturn(layerWithNoGeometry);
        Mockito.when(catalog.getLayer(layerWithNoGeometry.getId())).thenReturn(layerWithNoGeometry);
        Mockito.when(catalog.getLayerByName(ArgumentMatchers.eq(GWC.tileLayerName(layerWithNoGeometry)))).thenReturn(layerWithNoGeometry);
        config.addLayer(tl);
        Mockito.verify(this.tileLayerCatalog, Mockito.never()).save(info);
    }

    @Test
    public void testConfigurationDeadlock() throws Exception {
        // to make it reproducible with some reliability on my machine
        // 100000 loops need to be attempted. With the fix it works, but runs for
        // a minute and a half, so not suitable for actual builds.
        // For in-build tests I've thus settled down for 1000 loops instead
        final int LOOPS = 1000;
        ExecutorService service = Executors.newFixedThreadPool(8);
        Runnable reloader = new Runnable() {
            @Override
            public void run() {
                config.setGridSetBroker(gridSetBroker);
                config.afterPropertiesSet();
            }
        };
        Runnable tileLayerFetcher = new Runnable() {
            @Override
            public void run() {
                config.getLayer(layer1.getName());
                config.getLayer(layer2.getName());
                config.getLayer(group1.getName());
                config.getLayer(group2.getName());
            }
        };
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < LOOPS; i++) {
                futures.add(service.submit(reloader));
                futures.add(service.submit(tileLayerFetcher));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            service.shutdown();
        }
    }

    @Test
    public void getLayerByIdWithLocalWorkspace() {
        try {
            // create test workspaces
            WorkspaceInfo testWorkspace = new WorkspaceInfoImpl();
            testWorkspace.setName("test");
            WorkspaceInfo otherWorkspace = new WorkspaceInfoImpl();
            otherWorkspace.setName("other");
            // setting the local workspace equal to layers workspace
            LocalWorkspace.set(testWorkspace);
            Assert.assertThat(config.getTileLayerById(layer1.getId()), Matchers.notNullValue());
            Assert.assertThat(config.getTileLayerById(layer2.getId()), Matchers.notNullValue());
            // setting the local workspace different from layers workspaces
            LocalWorkspace.set(otherWorkspace);
            Assert.assertThat(config.getTileLayerById(layer1.getId()), Matchers.nullValue());
            Assert.assertThat(config.getTileLayerById(layer2.getId()), Matchers.nullValue());
        } finally {
            // cleaning
            LocalWorkspace.set(null);
        }
    }

    @Test
    public void getLayersWithLocalWorkspace() {
        try {
            // create test workspaces
            WorkspaceInfo testWorkspace = new WorkspaceInfoImpl();
            testWorkspace.setName("test");
            WorkspaceInfo otherWorkspace = new WorkspaceInfoImpl();
            otherWorkspace.setName("other");
            // setting the local workspace equal to layers workspace
            LocalWorkspace.set(testWorkspace);
            Assert.assertThat(Iterators.size(config.getLayers().iterator()), Matchers.is(2));
            // setting the local workspace different from layers workspaces
            LocalWorkspace.set(otherWorkspace);
            Assert.assertThat(Iterators.size(config.getLayers().iterator()), Matchers.is(0));
        } finally {
            // cleaning
            LocalWorkspace.set(null);
        }
    }
}


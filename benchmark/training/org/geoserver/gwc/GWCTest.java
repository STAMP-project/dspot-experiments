/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import Filter.INCLUDE;
import PublishedType.GROUP;
import PublishedType.RASTER;
import PublishedType.REMOTE;
import PublishedType.VECTOR;
import PublishedType.WMS;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.gwc.config.GWCConfigPersister;
import org.geoserver.gwc.layer.CatalogLayerEventListener;
import org.geoserver.gwc.layer.CatalogStyleChangeListener;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geoserver.gwc.layer.TileLayerInfoUtil;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.util.CaseInsensitiveMap;
import org.geoserver.platform.GeoServerEnvironment;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.kvp.PaletteManager;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.ConfigurationException;
import org.geowebcache.config.ConfigurationPersistenceException;
import org.geowebcache.config.FileBlobStoreInfo;
import org.geowebcache.config.TileLayerConfiguration;
import org.geowebcache.config.XMLConfiguration;
import org.geowebcache.config.XMLGridSet;
import org.geowebcache.diskquota.DiskQuotaMonitor;
import org.geowebcache.diskquota.QuotaStore;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.seed.GWCTask;
import org.geowebcache.seed.SeedRequest;
import org.geowebcache.seed.TileBreeder;
import org.geowebcache.service.Service;
import org.geowebcache.storage.BlobStoreAggregator;
import org.geowebcache.storage.CompositeBlobStore;
import org.geowebcache.storage.DefaultStorageFinder;
import org.geowebcache.storage.StorageBroker;
import org.geowebcache.storage.StorageException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.locationtech.jts.geom.Envelope;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opengis.filter.Filter;


/**
 * Unit test suite for the {@link GWC} mediator.
 */
/**
 *
 *
 * @author groldan
 */
public class GWCTest {
    private GWC mediator;

    private GWCConfig defaults;

    private GWCConfigPersister gwcConfigPersister;

    private XMLConfiguration xmlConfig;

    private StorageBroker storageBroker;

    private GridSetBroker gridSetBroker;

    private TileLayerConfiguration config;

    private TileLayerDispatcher tld;

    private TileBreeder tileBreeder;

    private QuotaStore quotaStore;

    private DiskQuotaMonitor diskQuotaMonitor;

    private ConfigurableQuotaStoreProvider diskQuotaStoreProvider;

    private Dispatcher owsDispatcher;

    private Catalog catalog;

    LayerInfo layer;

    LayerGroupInfo layerGroup;

    GeoServerTileLayerInfo tileLayerInfo;

    GeoServerTileLayerInfo tileLayerGroupInfo;

    GeoServerTileLayer tileLayer;

    GeoServerTileLayer tileLayerGroup;

    private DefaultStorageFinder storageFinder;

    private JDBCConfigurationStorage jdbcStorage;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private BlobStoreAggregator blobStoreAggregator;

    @Test
    public void testAddTileLayer() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("fake")).when(tld).addLayer(ArgumentMatchers.same(tileLayer));
        Mockito.doNothing().when(tld).addLayer(ArgumentMatchers.same(tileLayerGroup));
        try {
            mediator.add(tileLayer);
            Assert.fail("Expected IAE");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        mediator.add(tileLayerGroup);
    }

    @Test
    public void testModifyTileLayer() throws Exception {
        try {
            mediator.save(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        Mockito.doThrow(new IllegalArgumentException()).when(tld).modify(ArgumentMatchers.same(tileLayer));
        try {
            mediator.save(tileLayer);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayerGroup));
        mediator.save(tileLayerGroup);
        Mockito.verify(tld, Mockito.times(1)).modify(ArgumentMatchers.same(tileLayerGroup));
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayer));
        Mockito.doThrow(new ConfigurationPersistenceException(new IOException())).when(config).modifyLayer(tileLayer);
        try {
            mediator.save(tileLayer);
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRemoveTileLayers() throws Exception {
        try {
            mediator.removeTileLayers(null);
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        Mockito.doNothing().when(tld).removeLayer(ArgumentMatchers.eq(tileLayer.getName()));
        Mockito.doNothing().when(tld).removeLayer(ArgumentMatchers.eq(tileLayerGroup.getName()));
        List<String> layerNames = Arrays.asList(tileLayer.getName(), tileLayerGroup.getName());
        mediator.removeTileLayers(layerNames);
        Mockito.verify(tld, Mockito.times(1)).removeLayer(ArgumentMatchers.eq(tileLayer.getName()));
        Mockito.verify(tld, Mockito.times(1)).removeLayer(ArgumentMatchers.eq(tileLayerGroup.getName()));
    }

    @Test
    public void testAddGridset() throws Exception {
        try {
            mediator.addGridSet(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        GridSet gset = Mockito.mock(GridSet.class);
        GridSet gset2 = Mockito.mock(GridSet.class);
        Mockito.doThrow(new IOException("fake")).when(tld).addGridSet(ArgumentMatchers.same(gset));
        try {
            mediator.addGridSet(gset);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("fake", e.getMessage());
        }
        mediator.addGridSet(gset2);
        Mockito.verify(tld, Mockito.times(1)).addGridSet(ArgumentMatchers.same(gset2));
    }

    @Test
    public void testModifyGridsetPreconditions() throws Exception {
        GridSet oldGridset = gridSetBroker.get("EPSG:4326");
        try {
            mediator.modifyGridSet(null, oldGridset);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        try {
            mediator.modifyGridSet("oldname", null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq("wrongOldName"))).thenReturn(Optional.empty());
        try {
            mediator.modifyGridSet("wrongOldName", oldGridset);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    @Test
    public void testModifyGridsetNoNeedToTruncate() throws Exception {
        final String oldName = "TEST";
        final String newName = "TEST_CHANGED";
        final GridSet oldGridset = gridSetBroker.get(oldName);
        final GridSet newGridset;
        newGridset = namedGridsetCopy(newName, oldGridset);
        Assert.assertNotNull(tileLayer.getGridSubset(oldName));
        Assert.assertNotNull(tileLayerGroup.getGridSubset(oldName));
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(newName))).thenReturn(Optional.empty());
        Mockito.when(xmlConfig.canSave(ArgumentMatchers.eq(newGridset))).thenReturn(true);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayer))).thenReturn(config);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayerGroup))).thenReturn(config);
        mediator.modifyGridSet(oldName, newGridset);
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(oldName))).thenReturn(Optional.empty());
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(newName))).thenReturn(Optional.of(newGridset));
        Assert.assertNull(tileLayer.getGridSubset(oldName));
        Assert.assertNull(tileLayerGroup.getGridSubset(oldName));
        Assert.assertNotNull(tileLayer.getGridSubset(newName));
        Assert.assertNotNull(tileLayerGroup.getGridSubset(newName));
        Mockito.verify(xmlConfig, Mockito.times(1)).removeGridSet(ArgumentMatchers.eq(oldName));
        Mockito.verify(xmlConfig, Mockito.times(1)).addGridSet(ArgumentMatchers.eq(newGridset));
        Assert.assertNull(gridSetBroker.get(oldName));
        Assert.assertEquals(newGridset, gridSetBroker.get(newName));
    }

    @Test
    public void testModifyGridsetTruncates() throws Exception {
        final String oldName = "TEST";
        final String newName = "TEST_CHANGED";
        final GridSet oldGridset = gridSetBroker.get(oldName);
        final GridSet newGridset;
        {
            XMLGridSet xmlGridSet = new XMLGridSet(oldGridset);
            xmlGridSet.setName(newName);
            // make it so the gridset forces truncation
            xmlGridSet.setAlignTopLeft((!(xmlGridSet.getAlignTopLeft())));
            newGridset = xmlGridSet.makeGridSet();
        }
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(newName))).thenReturn(Optional.empty());
        Mockito.when(xmlConfig.canSave(ArgumentMatchers.eq(newGridset))).thenReturn(true);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayer))).thenReturn(config);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayerGroup))).thenReturn(config);
        mediator.modifyGridSet(oldName, newGridset);
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(oldName))).thenReturn(Optional.empty());
        Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq(newName))).thenReturn(Optional.of(newGridset));
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq(oldName));
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayerGroup.getName()), ArgumentMatchers.eq(oldName));
    }

    @Test
    public void testRemoveGridsets() throws Exception {
        try {
            mediator.removeGridSets(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }
        {
            final GridSet oldGridset = gridSetBroker.get("TEST");
            final GridSet newGridset = this.namedGridsetCopy("My4326", oldGridset);
            Mockito.when(xmlConfig.getGridSet(ArgumentMatchers.eq("My4326"))).thenReturn(Optional.of(newGridset));
        }
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayer))).thenReturn(config);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayerGroup))).thenReturn(config);
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayer));
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayerGroup));
        mediator.removeGridSets(ImmutableSet.of("My4326", "TEST"));
        Assert.assertEquals(ImmutableSet.of("EPSG:4326", "EPSG:900913"), tileLayer.getGridSubsets());
        Assert.assertEquals(ImmutableSet.of("EPSG:4326", "EPSG:900913"), tileLayerGroup.getGridSubsets());
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("TEST"));
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayerGroup.getName()), ArgumentMatchers.eq("TEST"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("EPSG:900913"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("EPSG:4326"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("My4326"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayerGroup.getName()), ArgumentMatchers.eq("EPSG:900913"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayerGroup.getName()), ArgumentMatchers.eq("EPSG:4326"));
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.eq(tileLayerGroup.getName()), ArgumentMatchers.eq("My4326"));
        Mockito.verify(tld, Mockito.times(1)).modify(ArgumentMatchers.same(tileLayer));
        Mockito.verify(tld, Mockito.times(1)).modify(ArgumentMatchers.same(tileLayerGroup));
    }

    @Test
    public void testRemoveAllLayerGridsetsDisablesLayer() throws Exception {
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayer))).thenReturn(config);
        Mockito.when(tld.getConfiguration(ArgumentMatchers.same(tileLayerGroup))).thenReturn(config);
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayer));
        Mockito.doNothing().when(tld).modify(ArgumentMatchers.same(tileLayerGroup));
        // sanity check before modification
        Assert.assertTrue(tileLayer.getInfo().isEnabled());
        Assert.assertTrue(tileLayer.getInfo().isEnabled());
        // Defaults can't be removed from the broker so remove them from the layers first
        tileLayer.removeGridSubset("EPSG:900913");
        tileLayer.removeGridSubset("EPSG:4326");
        tileLayerGroup.removeGridSubset("EPSG:900913");
        tileLayerGroup.removeGridSubset("EPSG:4326");
        mediator.save(tileLayer);
        mediator.save(tileLayerGroup);
        mediator.removeGridSets(ImmutableSet.of("TEST"));
        Mockito.verify(tld, Mockito.times(2)).modify(ArgumentMatchers.same(tileLayer));// all other checks are in testRemoveGridsets

        Mockito.verify(tld, Mockito.times(2)).modify(ArgumentMatchers.same(tileLayerGroup));
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("TEST"));
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq(tileLayer.getName()), ArgumentMatchers.eq("TEST"));
        Assert.assertTrue(tileLayer.getGridSubsets().isEmpty());
        Assert.assertTrue(tileLayerGroup.getGridSubsets().isEmpty());
        // layers ended up with no gridsubsets, shall have been disabled
        Assert.assertFalse(tileLayer.getInfo().isEnabled());
        Assert.assertFalse(tileLayerGroup.getInfo().isEnabled());
    }

    @Test
    public void testAutoConfigureLayers() throws Exception {
        {
            GWCConfig insaneDefaults = new GWCConfig();
            insaneDefaults.setMetaTilingX((-1));
            Assert.assertFalse(insaneDefaults.isSane());
            try {
                mediator.autoConfigureLayers(Arrays.asList(tileLayer.getName()), insaneDefaults);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(true);
            }
        }
        try {
            mediator.autoConfigureLayers(Arrays.asList(tileLayer.getName()), defaults);
            Assert.fail("expected IAE, layer exists");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        LayerInfo layer2 = GWCTestHelpers.mockLayer("layer2", new String[]{  }, RASTER);
        LayerGroupInfo group2 = GWCTestHelpers.mockGroup("group2", layer, layer2);
        Mockito.when(catalog.getLayerByName(ArgumentMatchers.eq(GWC.tileLayerName(layer2)))).thenReturn(layer2);
        Mockito.when(catalog.getLayerGroupByName(ArgumentMatchers.eq(GWC.tileLayerName(group2)))).thenReturn(group2);
        List<String> layerNames = Arrays.asList(GWC.tileLayerName(layer2), GWC.tileLayerName(group2));
        Mockito.doNothing().when(tld).addLayer(ArgumentMatchers.any(GeoServerTileLayer.class));
        mediator.autoConfigureLayers(layerNames, defaults);
        GeoServerTileLayerInfo expected1 = getInfo();
        GeoServerTileLayerInfo expected2 = getInfo();
        ArgumentCaptor<GeoServerTileLayer> addCaptor = ArgumentCaptor.forClass(GeoServerTileLayer.class);
        Mockito.verify(tld, Mockito.times(2)).addLayer(addCaptor.capture());
        GeoServerTileLayerInfo actual1 = getInfo();
        GeoServerTileLayerInfo actual2 = getInfo();
        Assert.assertEquals(expected1, actual1);
        Assert.assertEquals(expected2, actual2);
    }

    @Test
    public void testIsInternalGridset() {
        Set<String> embeddedNames = gridSetBroker.getEmbeddedNames();
        for (String name : embeddedNames) {
            Assert.assertTrue(mediator.isInternalGridSet(name));
        }
        Assert.assertFalse(mediator.isInternalGridSet("somethingelse"));
    }

    @Test
    public void testDeleteCacheByGridSetId() throws Exception {
        Mockito.when(storageBroker.deleteByGridSetId(ArgumentMatchers.eq("layer"), ArgumentMatchers.eq("gset1"))).thenThrow(new StorageException("fake"));
        try {
            mediator.deleteCacheByGridSetId("layer", "gset1");
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
        mediator.deleteCacheByGridSetId("layer", "gset2");
        Mockito.verify(storageBroker, Mockito.times(1)).deleteByGridSetId(ArgumentMatchers.eq("layer"), ArgumentMatchers.eq("gset2"));
    }

    @Test
    public void testDestroy() throws Exception {
        mediator.destroy();
        ArgumentCaptor<CatalogListener> captor = ArgumentCaptor.forClass(CatalogListener.class);
        Mockito.verify(catalog, Mockito.times(2)).removeListener(captor.capture());
        for (CatalogListener captured : captor.getAllValues()) {
            Assert.assertTrue(((captured instanceof CatalogLayerEventListener) || (captured instanceof CatalogStyleChangeListener)));
        }
    }

    @Test
    public void testTruncateLayerFully() throws Exception {
        Mockito.when(tld.getTileLayer(ArgumentMatchers.eq(tileLayerGroup.getName()))).thenThrow(new GeoWebCacheException("fake"));
        mediator.truncate(tileLayerGroup.getName());
        Mockito.verify(storageBroker, Mockito.never()).deleteByGridSetId(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        mediator.truncate(tileLayer.getName());
        Mockito.verify(storageBroker, Mockito.times(tileLayer.getGridSubsets().size())).deleteByGridSetId(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testTruncateByLayerAndStyle() throws Exception {
        String layerName = tileLayer.getName();
        String styleName = "notACachedStyle";
        mediator.truncateByLayerAndStyle(layerName, styleName);
        Mockito.verify(tileBreeder, Mockito.never()).dispatchTasks(ArgumentMatchers.any(GWCTask[].class));
        styleName = layer.getDefaultStyle().prefixedName();
        mediator.truncateByLayerAndStyle(layerName, styleName);
        int expected = (tileLayer.getGridSubsets().size()) * (tileLayer.getMimeTypes().size());
        Mockito.verify(tileBreeder, Mockito.times(expected)).dispatchTasks(ArgumentMatchers.any());
    }

    @Test
    public void testTruncateByBounds() throws Exception {
        String layerName = tileLayer.getName();
        Mockito.when(tileBreeder.findTileLayer(layerName)).thenReturn(tileLayer);
        final Set<Map<String, String>> cachedParameters = tileLayer.getInfo().cachedStyles().stream().map(( style) -> Collections.singletonMap("STYLES", style)).collect(Collectors.toSet());
        Mockito.when(storageBroker.getCachedParameters(layerName)).thenReturn(cachedParameters);
        ReferencedEnvelope bounds;
        // bounds outside layer bounds (which are -180,0,0,90)
        bounds = new ReferencedEnvelope(10, 20, 10, 20, DefaultGeographicCRS.WGS84);
        BoundingBox layerBounds = tileLayer.getGridSubset("EPSG:4326").getOriginalExtent();
        Assert.assertFalse(bounds.intersects(layerBounds.getMinX(), layerBounds.getMinY()));
        Assert.assertFalse(bounds.intersects(layerBounds.getMaxX(), layerBounds.getMaxY()));
        mediator.truncate(layerName, bounds);
        Mockito.verify(tileBreeder, Mockito.never()).dispatchTasks(ArgumentMatchers.any(GWCTask[].class));
        // bounds intersecting layer bounds
        bounds = new ReferencedEnvelope((-10), (-10), 10, 10, DefaultGeographicCRS.WGS84);
        mediator.truncate(layerName, bounds);
        int numGridsets = tileLayer.getGridSubsets().size();
        int numFormats = tileLayer.getMimeTypes().size();
        int numStyles = 1/* default */
         + (tileLayer.getInfo().cachedStyles().size());
        final int expected = (numGridsets * numFormats) * numStyles;
        Mockito.verify(tileBreeder, Mockito.times(expected)).seed(ArgumentMatchers.eq(layerName), ArgumentMatchers.any(SeedRequest.class));
        Mockito.reset(tileBreeder);
        Mockito.when(tileBreeder.findTileLayer(layerName)).thenReturn(tileLayer);
        bounds = bounds.transform(CRS.decode("EPSG:900913"), true);
        mediator.truncate(layerName, bounds);
        Mockito.verify(tileBreeder, Mockito.times(expected)).seed(ArgumentMatchers.eq(layerName), ArgumentMatchers.any(SeedRequest.class));
        Mockito.reset(tileBreeder);
        Mockito.when(tileBreeder.findTileLayer(layerName)).thenReturn(tileLayer);
        bounds = mediator.getAreaOfValidity(CRS.decode("EPSG:2083"));// Terra del Fuego Does not intersect subset

        mediator.truncate(layerName, bounds);
        Mockito.verify(tileBreeder, Mockito.times(0)).seed(ArgumentMatchers.eq(layerName), ArgumentMatchers.any(SeedRequest.class));
        Mockito.reset(tileBreeder);
        Mockito.when(tileBreeder.findTileLayer(layerName)).thenReturn(tileLayer);
        bounds = mediator.getAreaOfValidity(CRS.decode("EPSG:26986"));// Massachussets

        mediator.truncate(layerName, bounds);
        Mockito.verify(tileBreeder, Mockito.times(expected)).seed(ArgumentMatchers.eq(layerName), ArgumentMatchers.any(SeedRequest.class));
    }

    @Test
    public void testTruncateByBoundsWithDimension() throws Exception {
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(tileLayerInfo, "TIME", true);
        Collection<String> cachedTimes = Arrays.asList("time1", "time2");
        String layerName = tileLayer.getName();
        Mockito.when(tileBreeder.findTileLayer(layerName)).thenReturn(tileLayer);
        final Set<Map<String, String>> cachedParameters = tileLayer.getInfo().cachedStyles().stream().flatMap(( style) -> cachedTimes.stream().map(( time) -> {
            Map<String, String> map = new HashMap();
            map.put("STYLE", style);
            map.put("TIME", time);
            return map;
        })).collect(Collectors.toSet());
        Mockito.when(storageBroker.getCachedParameters(layerName)).thenReturn(cachedParameters);
        ReferencedEnvelope bounds;
        // bounds outside layer bounds (which are -180,0,0,90)
        bounds = new ReferencedEnvelope(10, 20, 10, 20, DefaultGeographicCRS.WGS84);
        BoundingBox layerBounds = tileLayer.getGridSubset("EPSG:4326").getGridSet().getOriginalExtent();
        Assert.assertFalse(bounds.intersects(layerBounds.getMinX(), layerBounds.getMinY()));
        Assert.assertFalse(bounds.intersects(layerBounds.getMaxX(), layerBounds.getMaxY()));
        mediator.truncate(layerName, bounds);
        Mockito.verify(tileBreeder, Mockito.never()).dispatchTasks(ArgumentMatchers.any(GWCTask[].class));
        // bounds intersecting layer bounds
        bounds = new ReferencedEnvelope((-10), (-10), 10, 10, DefaultGeographicCRS.WGS84);
        mediator.truncate(layerName, bounds);
        int numGridsets = tileLayer.getGridSubsets().size();
        int numFormats = tileLayer.getMimeTypes().size();
        int numStyles = tileLayer.getInfo().cachedStyles().size();
        int numTimes = cachedTimes.size();
        int numParameters = (numStyles * numTimes) + 1;
        final int expected = (numGridsets * numFormats) * numParameters;
        Mockito.verify(tileBreeder, Mockito.times(expected)).seed(ArgumentMatchers.eq(layerName), ArgumentMatchers.any(SeedRequest.class));
    }

    @Test
    public void testLayerRemoved() throws Exception {
        mediator.layerRemoved("someLayer");
        Mockito.verify(storageBroker, Mockito.times(1)).delete(ArgumentMatchers.eq("someLayer"));
        Mockito.doThrow(new StorageException("fake")).when(storageBroker).delete(ArgumentMatchers.eq("anotherLayer"));
        try {
            mediator.layerRemoved("anotherLayer");
            Assert.fail("Expected RTE");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof StorageException));
        }
    }

    @Test
    public void testLayerAdded() throws Exception {
        Mockito.when(diskQuotaMonitor.isEnabled()).thenReturn(false);
        mediator.layerAdded("someLayer");
        Mockito.verify(quotaStore, Mockito.never()).createLayer(ArgumentMatchers.anyString());
        Mockito.when(diskQuotaMonitor.isEnabled()).thenReturn(true);
        mediator.layerAdded("someLayer");
        Mockito.verify(quotaStore, Mockito.times(1)).createLayer(ArgumentMatchers.eq("someLayer"));
        Mockito.doThrow(new InterruptedException("fake")).when(quotaStore).createLayer(ArgumentMatchers.eq("someLayer"));
        try {
            mediator.layerAdded("someLayer");
            Assert.fail("Expected RTE");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof InterruptedException));
        }
    }

    @Test
    public void testLayerRenamed() throws Exception {
        mediator.layerRenamed("old", "new");
        Mockito.verify(storageBroker, Mockito.times(1)).rename(ArgumentMatchers.eq("old"), ArgumentMatchers.eq("new"));
        Mockito.doThrow(new StorageException("target directory already exists")).when(storageBroker).rename(ArgumentMatchers.eq("old"), ArgumentMatchers.eq("new"));
        try {
            mediator.layerRenamed("old", "new");
            Assert.fail("Expected RTE");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof StorageException));
        }
    }

    @Test
    public void testReload() throws Exception {
        mediator.reload();
        Mockito.verify(tld, Mockito.times(1)).reInit();
        Mockito.verify(diskQuotaStoreProvider, Mockito.times(1)).reloadQuotaStore();
        RuntimeException expected = new RuntimeException("expected");
        Mockito.doThrow(expected).when(tld).reInit();
        try {
            mediator.reload();
            Assert.fail("Expected RTE");
        } catch (RuntimeException e) {
            Assert.assertSame(expected, e);
        }
    }

    @Test
    public void testReloadAndLayerRemovedExternally() throws Exception {
        final String removedLayer = tileLayer.getName();
        final String remainingLayer = tileLayerGroup.getName();
        final Set<String> layerNames = Sets.newHashSet(removedLayer, remainingLayer);
        Mockito.when(tld.getLayerNames()).thenReturn(layerNames);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                layerNames.remove(removedLayer);
                return null;
            }
        }).when(tld).reInit();
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doReturn(true).when(mediator).layerRemoved(argCaptor.capture());
        mediator.reload();
        Mockito.verify(tld, Mockito.times(1)).reInit();
        Assert.assertEquals(1, argCaptor.getAllValues().size());
        Assert.assertEquals(removedLayer, argCaptor.getValue());
    }

    @Test
    public void testIsServiceEnabled() {
        Service service = Mockito.mock(Service.class);
        Mockito.when(service.getPathName()).thenReturn("wms");
        defaults.setWMSCEnabled(true);
        Assert.assertTrue(mediator.isServiceEnabled(service));
        defaults.setWMSCEnabled(false);
        Assert.assertFalse(mediator.isServiceEnabled(service));
        Mockito.when(service.getPathName()).thenReturn("tms");
        defaults.setTMSEnabled(true);
        Assert.assertTrue(mediator.isServiceEnabled(service));
        defaults.setTMSEnabled(false);
        Assert.assertFalse(mediator.isServiceEnabled(service));
        Mockito.when(service.getPathName()).thenReturn("somethingElse");
        Assert.assertTrue(mediator.isServiceEnabled(service));
    }

    @Test
    public void testDispatchGetMapDoesntMatchTileCache() throws Exception {
        GetMapRequest request = new GetMapRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> rawKvp = new CaseInsensitiveMap(new HashMap<String, String>());
        request.setRawKvp(rawKvp);
        rawKvp.put("layers", "more,than,one,layer");
        assertDispatchMismatch(request, "more than one layer requested");
        rawKvp.put("layers", "SomeNonCachedLayer");
        Mockito.when(tld.getTileLayer(ArgumentMatchers.eq("SomeNonCachedLayer"))).thenThrow(new GeoWebCacheException("layer not found"));
        assertDispatchMismatch(request, "not a tile layer");
        rawKvp.put("layers", tileLayer.getName());
        request.setFormat("badFormat");
        assertDispatchMismatch(request, "not a GWC supported format");
        request.setFormat("image/gif");
        assertDispatchMismatch(request, "no tile cache for requested format");
        request.setFormat(tileLayer.getMimeTypes().get(0).getMimeType());
        request.setSRS("EPSG:4326");
        request.setBbox(new Envelope(10, 10, 20, 20));
        assertDispatchMismatch(request, "request does not align to grid");
        request.setSRS("EPSG:23036");
        assertDispatchMismatch(request, "no cache exists for requested CRS");
        request.setSRS("badCRS");
        assertDispatchMismatch(request, "exception occurred");
        request.setSRS("EPSG:4326");
        request.setWidth(128);
        request.setHeight(256);
        assertDispatchMismatch(request, "request does not align to grid");
        request.setWidth(256);
        request.setHeight(128);
        assertDispatchMismatch(request, "request does not align to grid");
        request.setSRS("EPSG:4326");
        request.setWidth(256);
        request.setHeight(256);
        assertDispatchMismatch(request, "request does not align to grid");
    }

    @Test
    public void testDispatchGetMapNonMatchingParameterFilter() throws Exception {
        GetMapRequest request = new GetMapRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> rawKvp = new CaseInsensitiveMap(new HashMap<String, String>());
        request.setRawKvp(rawKvp);
        rawKvp.put("layers", tileLayer.getName());
        tileLayer.setEnabled(false);
        assertDispatchMismatch(request, "tile layer disabled");
        tileLayer.setEnabled(true);
        Assert.assertTrue(layer.enabled());
        request.setRemoteOwsURL(new URL("http://example.com"));
        assertDispatchMismatch(request, "remote OWS");
        request.setRemoteOwsURL(null);
        request.setRemoteOwsType("WFS");
        assertDispatchMismatch(request, "remote OWS");
        request.setRemoteOwsType(null);
        request.setEnv(ImmutableMap.of("envVar", "envValue"));
        assertDispatchMismatch(request, "no parameter filter exists for ENV");
        request.setEnv(null);
        request.setFormatOptions(ImmutableMap.of("optKey", "optVal"));
        assertDispatchMismatch(request, "no parameter filter exists for FORMAT_OPTIONS");
        request.setFormatOptions(null);
        request.setAngle(45);
        assertDispatchMismatch(request, "no parameter filter exists for ANGLE");
        request.setAngle(0);
        rawKvp.put("BGCOLOR", "0xAA0000");
        assertDispatchMismatch(request, "no parameter filter exists for BGCOLOR");
        rawKvp.remove("BGCOLOR");
        request.setBuffer(10);
        assertDispatchMismatch(request, "no parameter filter exists for BUFFER");
        request.setBuffer(0);
        request.setCQLFilter(Arrays.asList(CQL.toFilter("ATT = 1")));
        assertDispatchMismatch(request, "no parameter filter exists for CQL_FILTER");
        request.setCQLFilter(null);
        request.setElevation(10.0);
        assertDispatchMismatch(request, "no parameter filter exists for ELEVATION");
        request.setElevation(Collections.emptyList());
        request.setFeatureId(Arrays.asList(new FeatureIdImpl("someid")));
        assertDispatchMismatch(request, "no parameter filter exists for FEATUREID");
        request.setFeatureId(null);
        request.setFilter(Arrays.asList(CQL.toFilter("ATT = 1")));
        assertDispatchMismatch(request, "no parameter filter exists for FILTER");
        request.setFilter(null);
        request.setPalette(PaletteManager.getPalette("SAFE"));
        assertDispatchMismatch(request, "no parameter filter exists for PALETTE");
        request.setPalette(null);
        request.setStartIndex(10);
        assertDispatchMismatch(request, "no parameter filter exists for STARTINDEX");
        request.setStartIndex(null);
        request.setMaxFeatures(1);
        assertDispatchMismatch(request, "no parameter filter exists for MAXFEATURES");
        request.setMaxFeatures(null);
        request.setTime(Arrays.asList(((Object) (1)), ((Object) (2))));
        assertDispatchMismatch(request, "no parameter filter exists for TIME");
        request.setTime(Collections.emptyList());
        List<Map<String, String>> viewParams = ImmutableList.of(((Map<String, String>) (ImmutableMap.of("paramKey", "paramVal"))));
        request.setViewParams(viewParams);
        assertDispatchMismatch(request, "no parameter filter exists for VIEWPARAMS");
        request.setViewParams(null);
        request.setFeatureVersion("@version");
        assertDispatchMismatch(request, "no parameter filter exists for FEATUREVERSION");
        request.setFeatureVersion(null);
    }

    /**
     * See GEOS-5003
     */
    @Test
    public void testNullsInDimensionAndTimeParameters() throws Exception {
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(tileLayerInfo, "ELEVATION", true);
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(tileLayerInfo, "TIME", true);
        tileLayer = new GeoServerTileLayer(layer, gridSetBroker, tileLayerInfo);
        GetMapRequest request = new GetMapRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> rawKvp = new CaseInsensitiveMap(new HashMap<String, String>());
        request.setRawKvp(rawKvp);
        StringBuilder target = new StringBuilder();
        boolean cachingPossible;
        request.setElevation(Arrays.asList(((Object) (null))));
        cachingPossible = mediator.isCachingPossible(tileLayer, request, target);
        Assert.assertTrue(cachingPossible);
        Assert.assertEquals(0, target.length());
        request.setElevation(Collections.emptyList());
        request.setTime(Arrays.asList(((Object) (null))));
        cachingPossible = mediator.isCachingPossible(tileLayer, request, target);
        Assert.assertTrue(cachingPossible);
        Assert.assertEquals(0, target.length());
    }

    /**
     * Since GeoServer sets a new FILTER parameter equal to an input CQL_FILTER parameter (if
     * present) for each WMS requests (using direct WMS integration), this may result in a caching
     * error. This test ensures that no error is thrown and caching is allowed.
     */
    @Test
    public void testCQLFILTERParameters() throws Exception {
        // Define a CQL_FILTER
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(tileLayerInfo, "CQL_FILTER", true);
        tileLayer = new GeoServerTileLayer(layer, gridSetBroker, tileLayerInfo);
        // Create the new GetMapRequest
        GetMapRequest request = new GetMapRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> rawKvp = new CaseInsensitiveMap(new HashMap<String, String>());
        rawKvp.put("CQL_FILTER", "include");
        request.setRawKvp(rawKvp);
        StringBuilder target = new StringBuilder();
        // Setting CQL FILTER
        List<Filter> cqlFilters = Arrays.asList(CQL.toFilter("include"));
        request.setCQLFilter(cqlFilters);
        // Checking if caching is possible
        Assert.assertTrue(mediator.isCachingPossible(tileLayer, request, target));
        // Ensure No error is logged
        Assert.assertEquals(0, target.length());
        // Setting FILTER parameter equal to CQL FILTER (GeoServer does it internally)
        request.setFilter(cqlFilters);
        // Checking if caching is possible
        Assert.assertTrue(mediator.isCachingPossible(tileLayer, request, target));
        // Ensure No error is logged
        Assert.assertEquals(0, target.length());
        // Ensure that if another filter is set an error is thrown
        List filters = new ArrayList(cqlFilters);
        filters.add(INCLUDE);
        request.setFilter(filters);
        // Ensuring caching is not possible
        Assert.assertFalse(mediator.isCachingPossible(tileLayer, request, target));
        // Ensure No error is logged
        Assert.assertFalse((0 == (target.length())));
    }

    @Test
    public void testDispatchGetMapMultipleCrsMatchingGridSubsets() throws Exception {
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "EPSG:4326", new long[]{ 1, 1, 1 });
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "EPSG:4326", new long[]{ 10, 10, 10 });
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "GlobalCRS84Scale", new long[]{ 1, 1, 1 });
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "GlobalCRS84Scale", new long[]{ 10, 10, 10 });
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "GlobalCRS84Scale", new long[]{ 1, 1, 1 });
        testMultipleCrsMatchingGridSubsets("EPSG:4326", "GlobalCRS84Scale", new long[]{ 10, 10, 10 });
    }

    @Test
    public void testDispatchGetMapWithMatchingParameterFilters() throws Exception {
        GetMapRequest request = new GetMapRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> rawKvp = new CaseInsensitiveMap(new HashMap<String, String>());
        request.setRawKvp(rawKvp);
        request.setFormat("image/png");
        request.setSRS("EPSG:900913");
        request.setWidth(256);
        request.setHeight(256);
        rawKvp.put("layers", tileLayer.getName());
        // tileLayer = mockTileLayer("mockLayer", ImmutableList.of("EPSG:900913", "EPSG:4326"));
        // make the request match a tile in the expected gridset
        BoundingBox bounds;
        bounds = tileLayer.getGridSubset("EPSG:900913").boundsFromIndex(new long[]{ 0, 0, 1 });
        Envelope reqBbox = new Envelope(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());
        request.setBbox(reqBbox);
        Assert.assertTrue(((tileLayer.getInfo().cachedStyles().size()) > 0));
        for (String style : tileLayer.getInfo().cachedStyles()) {
            if ((style != null) && (style.equals("default"))) {
                // if no styles are provided WMTS assumes a default style named default
                continue;
            }
            String rawKvpParamName = "styles";
            String rawKvpParamValue = style;
            testParameterFilter(request, rawKvp, rawKvpParamName, rawKvpParamValue);
        }
        request.setEnv(ImmutableMap.of("envKey", "envValue"));
        TileLayerInfoUtil.updateStringParameterFilter(tileLayerInfo, "ENV", true, "def:devVal", "envKey:envValue", "envKey2:envValue2");
        testParameterFilter(request, rawKvp, "env", "envKey:envValue");
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(tileLayerInfo, "ANGLE", true);
        request.setAngle(60);
        testParameterFilter(request, rawKvp, "angle", "60.0");
        request.setAngle(61.1);
        testParameterFilter(request, rawKvp, "angle", "61.1");
    }

    @Test
    public void testGetDefaultAdvertisedCachedFormats() {
        // from src/main/resources/org/geoserver/gwc/advertised_formats.properties
        Set<String> defaultFormats = ImmutableSet.of("image/png", "image/png8", "image/jpeg", "image/gif", "image/vnd.jpeg-png", "image/vnd.jpeg-png8");
        Sets.SetView<String> formatsWithUtfGrid = Sets.union(defaultFormats, Collections.singleton("application/json;type=utfgrid"));
        Assert.assertEquals(formatsWithUtfGrid, GWC.getAdvertisedCachedFormats(VECTOR));
        Assert.assertEquals(formatsWithUtfGrid, GWC.getAdvertisedCachedFormats(REMOTE));
        Assert.assertEquals(defaultFormats, GWC.getAdvertisedCachedFormats(RASTER));
        Assert.assertEquals(defaultFormats, GWC.getAdvertisedCachedFormats(WMS));
        Assert.assertEquals(formatsWithUtfGrid, GWC.getAdvertisedCachedFormats(GROUP));
    }

    @Test
    public void testGetPluggabledAdvertisedCachedFormats() throws IOException {
        List<URL> urls;
        try {
            // load the default and test resources separately so they are named differently and we
            // don't get the ones for testing listed in the UI when running from eclipse
            String defaultResource = "org/geoserver/gwc/advertised_formats.properties";
            String testResource = "org/geoserver/gwc/advertised_formats_unittesting.properties";
            ClassLoader classLoader = GWC.class.getClassLoader();
            urls = Lists.newArrayList(Iterators.forEnumeration(classLoader.getResources(defaultResource)));
            urls.addAll(Lists.newArrayList(Iterators.forEnumeration(classLoader.getResources(testResource))));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        // from src/main/resources/org/geoserver/gwc/advertised_formats.properties
        Set<String> defaultFormats = ImmutableSet.of("image/png", "image/png8", "image/jpeg", "image/gif", "image/vnd.jpeg-png", "image/vnd.jpeg-png8");
        // see src/test/resources/org/geoserver/gwc/advertised_formats.properties
        Set<String> expectedVector = Sets.union(defaultFormats, ImmutableSet.of("test/vector1", "test/vector2", "application/json;type=utfgrid"));
        Set<String> expectedRaster = Sets.union(defaultFormats, ImmutableSet.of("test/raster1", "test/raster2;type=test"));
        Set<String> expectedGroup = Sets.union(defaultFormats, ImmutableSet.of("test/group1", "test/group2", "application/json;type=utfgrid"));
        Assert.assertEquals(expectedVector, GWC.getAdvertisedCachedFormats(VECTOR, urls));
        Assert.assertEquals(expectedVector, GWC.getAdvertisedCachedFormats(REMOTE, urls));
        Assert.assertEquals(expectedRaster, GWC.getAdvertisedCachedFormats(RASTER, urls));
        Assert.assertEquals(expectedRaster, GWC.getAdvertisedCachedFormats(WMS, urls));
        Assert.assertEquals(expectedGroup, GWC.getAdvertisedCachedFormats(GROUP, urls));
    }

    @Test
    public void testSetBlobStoresNull() throws ConfigurationException {
        expected.expect(NullPointerException.class);
        expected.expectMessage("stores is null");
        mediator.setBlobStores(null);
    }

    @Test
    public void testSetBlobStoresSavesConfig() throws Exception {
        Mockito.when(xmlConfig.getBlobStores()).thenReturn(ImmutableList.<BlobStoreInfo>of());
        CompositeBlobStore composite = Mockito.mock(CompositeBlobStore.class);
        Mockito.doReturn(composite).when(mediator).getCompositeBlobStore();
        List<BlobStoreInfo> configList = Lists.newArrayList(Mockito.mock(BlobStoreInfo.class), Mockito.mock(BlobStoreInfo.class));
        Mockito.when(configList.get(0).getName()).thenReturn("store0");
        Mockito.when(configList.get(1).getName()).thenReturn("store1");
        Mockito.when(blobStoreAggregator.getBlobStores()).thenReturn(configList);
        Mockito.when(blobStoreAggregator.getBlobStoreNames()).thenReturn(Arrays.asList("store0", "store1"));
        BlobStoreInfo config = new FileBlobStoreInfo("TestBlobStore");
        List<BlobStoreInfo> newStores = ImmutableList.<BlobStoreInfo>of(config);
        mediator.setBlobStores(newStores);
        Mockito.verify(blobStoreAggregator, Mockito.times(1)).removeBlobStore(ArgumentMatchers.eq(configList.get(0).getName()));
        Mockito.verify(blobStoreAggregator, Mockito.times(1)).removeBlobStore(ArgumentMatchers.eq(configList.get(1).getName()));
        Mockito.verify(blobStoreAggregator, Mockito.times(1)).addBlobStore(ArgumentMatchers.eq(config));
    }

    @Test
    public void testSetBlobStoresRestoresRuntimeStoresOnSaveFailure() throws Exception {
        Mockito.when(blobStoreAggregator.getBlobStores()).thenReturn(ImmutableList.<BlobStoreInfo>of());
        CompositeBlobStore composite = Mockito.mock(CompositeBlobStore.class);
        Mockito.doReturn(composite).when(mediator).getCompositeBlobStore();
        BlobStoreInfo config = new FileBlobStoreInfo("TestStore");
        Mockito.doThrow(new ConfigurationPersistenceException(new IOException("expected"))).when(blobStoreAggregator).addBlobStore(config);
        List<BlobStoreInfo> oldStores = Lists.newArrayList(Mockito.mock(BlobStoreInfo.class), Mockito.mock(BlobStoreInfo.class));
        Mockito.when(blobStoreAggregator.getBlobStores()).thenReturn(oldStores);
        List<BlobStoreInfo> newStores = ImmutableList.<BlobStoreInfo>of(config);
        try {
            mediator.setBlobStores(newStores);
            Assert.fail("Expected ConfigurationException");
        } catch (ConfigurationException e) {
            Assert.assertTrue(e.getMessage().contains("Error saving config"));
        }
    }

    @Test
    public void testGeoServerEnvParametrization() throws Exception {
        if (GeoServerEnvironment.ALLOW_ENV_PARAMETRIZATION) {
            Assert.assertTrue("H2".equals(jdbcStorage.getJDBCDiskQuotaConfig().clone(true).getDialect()));
        }
    }
}


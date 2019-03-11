/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.config;


import GWCInitializer.WMS_INTEGRATION_ENABLED_KEY;
import PublishedType.RASTER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerFacade;
import org.geoserver.gwc.GWCTestHelpers;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geoserver.gwc.layer.GeoServerTileLayerInfoImpl;
import org.geoserver.gwc.layer.LegacyTileLayerInfoLoader;
import org.geoserver.gwc.layer.TileLayerCatalog;
import org.geoserver.gwc.layer.TileLayerInfoUtil;
import org.geoserver.gwc.wmts.WMTSInfo;
import org.geoserver.gwc.wmts.WMTSInfoImpl;
import org.geoserver.platform.resource.Files;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSInfoImpl;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GWCInitializerTest {
    private GWCInitializer initializer;

    private GWCConfigPersister configPersister;

    private GeoServer geoServer;

    private Catalog rawCatalog;

    private TileLayerCatalog tileLayerCatalog;

    private GeoServerFacade geoServerFacade;

    private WMTSInfo wmtsInfo = new WMTSInfoImpl();

    @Test
    public void testInitializeLayersToOldDefaults() throws Exception {
        // no gwc-gs.xml exists
        Mockito.when(configPersister.findConfigFile()).thenReturn(null);
        // ignore the upgrade of the direct wms integration flag on this test
        Mockito.when(geoServer.getService(ArgumentMatchers.eq(WMSInfo.class))).thenReturn(null);
        // let the catalog have something to initialize
        LayerInfo layer = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        LayerGroupInfo group = GWCTestHelpers.mockGroup("testGroup", layer);
        Mockito.when(rawCatalog.getLayers()).thenReturn(Lists.newArrayList(layer));
        Mockito.when(rawCatalog.getLayerGroups()).thenReturn(Lists.newArrayList(group));
        // run layer initialization
        initializer.initialize(geoServer);
        // make sure default tile layers were created
        GWCConfig oldDefaults = GWCConfig.getOldDefaults();
        GeoServerTileLayerInfo tileLayer = TileLayerInfoUtil.loadOrCreate(layer, oldDefaults);
        GeoServerTileLayerInfo tileLayerGroup = TileLayerInfoUtil.loadOrCreate(group, oldDefaults);
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(tileLayer));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(tileLayerGroup));
    }

    @Test
    public void testUpgradeDirectWMSIntegrationFlag() throws Exception {
        // no gwc-gs.xml exists, so that initialization runs
        Mockito.when(configPersister.findConfigFile()).thenReturn(null);
        // no catalog layers for this test
        List<LayerInfo> layers = ImmutableList.of();
        List<LayerGroupInfo> groups = ImmutableList.of();
        Mockito.when(rawCatalog.getLayers()).thenReturn(layers);
        Mockito.when(rawCatalog.getLayerGroups()).thenReturn(groups);
        WMSInfoImpl wmsInfo = new WMSInfoImpl();
        // initialize wmsInfo with a value for the old direct wms integration flag
        wmsInfo.getMetadata().put(WMS_INTEGRATION_ENABLED_KEY, Boolean.TRUE);
        // make sure WMSInfo exists
        Mockito.when(geoServer.getService(ArgumentMatchers.eq(WMSInfo.class))).thenReturn(wmsInfo);
        ArgumentCaptor<GWCConfig> captor = ArgumentCaptor.forClass(GWCConfig.class);
        // run layer initialization
        initializer.initialize(geoServer);
        Mockito.verify(configPersister, Mockito.times(3)).save(captor.capture());
        Assert.assertTrue(captor.getAllValues().get(0).isDirectWMSIntegrationEnabled());
        Assert.assertFalse(wmsInfo.getMetadata().containsKey(WMS_INTEGRATION_ENABLED_KEY));
        Mockito.verify(geoServer).save(ArgumentMatchers.same(wmsInfo));
    }

    @Test
    public void testUpgradeFromTileLayerInfosToTileLayerCatalog() throws Exception {
        // do have gwc-gs.xml, so it doesn't go through the createDefaultTileLayerInfos path
        Resource fakeConfig = Files.asResource(new File("target", "gwc-gs.xml"));
        Mockito.when(configPersister.findConfigFile()).thenReturn(fakeConfig);
        GWCConfig defaults = GWCConfig.getOldDefaults();
        defaults.setCacheLayersByDefault(true);
        Mockito.when(configPersister.getConfig()).thenReturn(defaults);
        // let the catalog have something to initialize
        LayerInfo layer = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        LayerGroupInfo group = GWCTestHelpers.mockGroup("testGroup", layer);
        Mockito.when(rawCatalog.getLayers()).thenReturn(Lists.newArrayList(layer));
        Mockito.when(rawCatalog.getLayerGroups()).thenReturn(Lists.newArrayList(group));
        GeoServerTileLayerInfoImpl layerInfo = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        GeoServerTileLayerInfoImpl groupInfo = TileLayerInfoUtil.loadOrCreate(group, defaults);
        LegacyTileLayerInfoLoader.save(layerInfo, layer.getMetadata());
        LegacyTileLayerInfoLoader.save(groupInfo, group.getMetadata());
        // run layer initialization
        initializer.initialize(geoServer);
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(layerInfo));
        Assert.assertFalse(LegacyTileLayerInfoLoader.hasTileLayerDef(layer.getMetadata()));
        Mockito.verify(rawCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(layer));
        Mockito.verify(tileLayerCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(groupInfo));
        Assert.assertFalse(LegacyTileLayerInfoLoader.hasTileLayerDef(group.getMetadata()));
        Mockito.verify(rawCatalog, Mockito.times(1)).save(ArgumentMatchers.eq(group));
    }

    @Test
    public void testUpgradeWithWmtsEnablingInfo() throws Exception {
        // force configuration initialisation
        Mockito.when(configPersister.findConfigFile()).thenReturn(null);
        Assert.assertTrue(wmtsInfo.isEnabled());
        // run layer initialization
        initializer.initialize(geoServer);
        // checking that the configuration was saved
        Mockito.verify(geoServer).save(ArgumentMatchers.same(wmtsInfo));
        Mockito.verify(configPersister, Mockito.times(2)).save(configPersister.getConfig());
        // checking that the service info have been updated with gwc configuration value
        Assert.assertFalse(wmtsInfo.isEnabled());
    }
}


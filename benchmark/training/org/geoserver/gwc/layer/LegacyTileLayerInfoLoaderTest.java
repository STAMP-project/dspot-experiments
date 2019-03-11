/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import PublishedType.RASTER;
import com.google.common.collect.ImmutableSet;
import junit.framework.Assert;
import org.geoserver.catalog.impl.LayerGroupInfoImpl;
import org.geoserver.catalog.impl.LayerInfoImpl;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.GWCTestHelpers;
import org.geoserver.gwc.config.GWCConfig;
import org.junit.Test;


public class LegacyTileLayerInfoLoaderTest {
    private GWCConfig defaults;

    private GeoServerTileLayerInfo defaultVectorInfo;

    @Test
    public void testLoadLayerInfo() {
        LayerInfoImpl layer = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        Assert.assertNull(LegacyTileLayerInfoLoader.load(layer));
        TileLayerInfoUtil.checkAutomaticStyles(layer, defaultVectorInfo);
        LegacyTileLayerInfoLoader.save(defaultVectorInfo, layer.getMetadata());
        GeoServerTileLayerInfo info2 = LegacyTileLayerInfoLoader.load(layer);
        defaultVectorInfo.setId(layer.getId());
        defaultVectorInfo.setName(GWC.tileLayerName(layer));
        Assert.assertEquals(defaultVectorInfo, info2);
    }

    @Test
    public void testLoadLayerInfoExtraStyles() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setAutoCacheStyles(false);
        TileLayerInfoUtil.setCachedStyles(info, "default", ImmutableSet.of("style1"));
        LayerInfoImpl layer = GWCTestHelpers.mockLayer("testLayer", new String[]{ "style1", "style2" }, RASTER);
        TileLayerInfoUtil.checkAutomaticStyles(layer, info);
        Assert.assertNull(LegacyTileLayerInfoLoader.load(layer));
        LegacyTileLayerInfoLoader.save(info, layer.getMetadata());
        GeoServerTileLayerInfo actual;
        actual = LegacyTileLayerInfoLoader.load(layer);
        info.setId(layer.getId());
        info.setName(GWC.tileLayerName(layer));
        Assert.assertEquals(info, actual);
        layer.setDefaultStyle(null);
        TileLayerInfoUtil.setCachedStyles(info, null, ImmutableSet.of("style1"));
        LegacyTileLayerInfoLoader.save(info, layer.getMetadata());
        actual = LegacyTileLayerInfoLoader.load(layer);
        Assert.assertEquals(ImmutableSet.of("style1"), actual.cachedStyles());
    }

    @Test
    public void testLoadLayerInfoAutoCacheStyles() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setAutoCacheStyles(true);
        LayerInfoImpl layer = GWCTestHelpers.mockLayer("testLayer", new String[]{ "style1", "style2" }, RASTER);
        Assert.assertNull(LegacyTileLayerInfoLoader.load(layer));
        TileLayerInfoUtil.checkAutomaticStyles(layer, defaultVectorInfo);
        LegacyTileLayerInfoLoader.save(info, layer.getMetadata());
        GeoServerTileLayerInfo actual;
        actual = LegacyTileLayerInfoLoader.load(layer);
        TileLayerInfoUtil.setCachedStyles(info, "default", ImmutableSet.of("style1", "style2"));
        info.setId(layer.getId());
        info.setName(GWC.tileLayerName(layer));
        Assert.assertEquals(info, actual);
        layer.setDefaultStyle(null);
        TileLayerInfoUtil.setCachedStyles(info, null, ImmutableSet.of("style1", "style2"));
        actual = LegacyTileLayerInfoLoader.load(layer);
        Assert.assertEquals(ImmutableSet.of("style1", "style2"), actual.cachedStyles());
    }

    @Test
    public void testLoadLayerGroup() {
        LayerGroupInfoImpl lg = GWCTestHelpers.mockGroup("tesGroup", GWCTestHelpers.mockLayer("L1", new String[]{  }, RASTER), GWCTestHelpers.mockLayer("L2", new String[]{  }, RASTER));
        Assert.assertNull(LegacyTileLayerInfoLoader.load(lg));
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.getMimeFormats().clear();
        info.getMimeFormats().addAll(defaults.getDefaultOtherCacheFormats());
        LegacyTileLayerInfoLoader.save(info, lg.getMetadata());
        GeoServerTileLayerInfo actual;
        actual = LegacyTileLayerInfoLoader.load(lg);
        info.setId(lg.getId());
        info.setName(GWC.tileLayerName(lg));
        Assert.assertEquals(info, actual);
    }

    @Test
    public void testClear() {
        LayerGroupInfoImpl lg = GWCTestHelpers.mockGroup("tesGroup", GWCTestHelpers.mockLayer("L1", new String[]{  }, RASTER), GWCTestHelpers.mockLayer("L2", new String[]{  }, RASTER));
        Assert.assertNull(LegacyTileLayerInfoLoader.load(lg));
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.getMimeFormats().clear();
        info.getMimeFormats().addAll(defaults.getDefaultOtherCacheFormats());
        LegacyTileLayerInfoLoader.save(info, lg.getMetadata());
        GeoServerTileLayerInfo actual;
        actual = LegacyTileLayerInfoLoader.load(lg);
        Assert.assertNotNull(actual);
        LegacyTileLayerInfoLoader.clear(lg.getMetadata());
        Assert.assertNull(LegacyTileLayerInfoLoader.load(lg));
    }
}


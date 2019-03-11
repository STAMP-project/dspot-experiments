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
import org.geowebcache.filter.parameters.FloatParameterFilter;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.junit.Test;


/**
 * Unit test suite for {@link TileLayerInfoUtil}
 */
public class TileLayerInfoUtilTest {
    private GWCConfig defaults;

    private GeoServerTileLayerInfo defaultVectorInfo;

    @Test
    public void testCreateLayerInfo() {
        LayerInfoImpl layer = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        defaultVectorInfo.setId(layer.getId());
        defaultVectorInfo.setName(GWC.tileLayerName(layer));
        Assert.assertNotNull(info);
        Assert.assertEquals(defaultVectorInfo, info);
    }

    @Test
    public void testCreateLayerGroupInfo() {
        LayerGroupInfoImpl group = GWCTestHelpers.mockGroup("testGroup", GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER));
        defaults.getDefaultOtherCacheFormats().clear();
        defaults.getDefaultOtherCacheFormats().add("image/png8");
        defaults.getDefaultOtherCacheFormats().add("image/jpeg");
        GeoServerTileLayerInfo expected = TileLayerInfoUtil.create(defaults);
        expected.setId(group.getId());
        expected.setName(GWC.tileLayerName(group));
        GeoServerTileLayerInfo info = TileLayerInfoUtil.loadOrCreate(group, defaults);
        Assert.assertNotNull(info);
        Assert.assertEquals(expected, info);
    }

    @Test
    public void testCreateLayerInfoAutoCacheStyles() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setAutoCacheStyles(true);
        defaults.setCacheNonDefaultStyles(true);
        LayerInfoImpl layer = GWCTestHelpers.mockLayer("testLayer", new String[]{ "style1", "style2" }, RASTER);
        GeoServerTileLayerInfo actual;
        actual = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        TileLayerInfoUtil.checkAutomaticStyles(layer, info);
        TileLayerInfoUtil.setCachedStyles(info, "default", ImmutableSet.of("style1", "style2"));
        layer.setDefaultStyle(null);
        TileLayerInfoUtil.setCachedStyles(info, "", ImmutableSet.of("style1", "style2"));
        actual = TileLayerInfoUtil.loadOrCreate(layer, defaults);
        Assert.assertEquals(ImmutableSet.of("style1", "style2"), actual.cachedStyles());
    }

    @Test
    public void testCreateLayerGroup() {
        LayerGroupInfoImpl lg = GWCTestHelpers.mockGroup("tesGroup", GWCTestHelpers.mockLayer("L1", new String[]{  }, RASTER), GWCTestHelpers.mockLayer("L2", new String[]{  }, RASTER));
        GeoServerTileLayerInfo info = defaultVectorInfo;
        info.setId(lg.getId());
        info.setName(GWC.tileLayerName(lg));
        info.getMimeFormats().clear();
        info.getMimeFormats().addAll(defaults.getDefaultOtherCacheFormats());
        GeoServerTileLayerInfo actual;
        actual = TileLayerInfoUtil.loadOrCreate(lg, defaults);
        Assert.assertEquals(info, actual);
    }

    @Test
    public void testUpdateAcceptAllRegExParameterFilter() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        // If createParam is false and there isn't already a filter, don't create one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", false);
        Assert.assertNull(TileLayerInfoUtil.findParameterFilter("ENV", info.getParameterFilters()));
        // If createParam is true and there isn't already a filter, create one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", true);
        ParameterFilter filter = TileLayerInfoUtil.findParameterFilter("ENV", info.getParameterFilters());
        Assert.assertTrue((filter instanceof RegexParameterFilter));
        Assert.assertEquals(".*", getRegex());
        // If createParam is true and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", true);
        ParameterFilter filter2 = TileLayerInfoUtil.findParameterFilter("ENV", info.getParameterFilters());
        Assert.assertNotSame(filter, filter2);
        Assert.assertEquals(filter, filter2);
        // If createParam is false and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllRegExParameterFilter(info, "ENV", false);
        ParameterFilter filter3 = TileLayerInfoUtil.findParameterFilter("ENV", info.getParameterFilters());
        Assert.assertNotSame(filter2, filter3);
        Assert.assertEquals(filter, filter3);
    }

    @Test
    public void testUpdateAcceptAllFloatParameterFilter() {
        GeoServerTileLayerInfo info = defaultVectorInfo;
        // If createParam is false and there isn't already a filter, don't create one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", false);
        Assert.assertNull(TileLayerInfoUtil.findParameterFilter("ELEVATION", info.getParameterFilters()));
        // If createParam is true and there isn't already a filter, create one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", true);
        ParameterFilter filter = TileLayerInfoUtil.findParameterFilter("ELEVATION", info.getParameterFilters());
        Assert.assertTrue((filter instanceof FloatParameterFilter));
        Assert.assertEquals(0, getValues().size());
        // If createParam is true and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", true);
        ParameterFilter filter2 = TileLayerInfoUtil.findParameterFilter("ELEVATION", info.getParameterFilters());
        Assert.assertNotSame(filter, filter2);
        Assert.assertEquals(filter, filter2);
        // If createParam is false and there is already a filter, replace it with a new one
        TileLayerInfoUtil.updateAcceptAllFloatParameterFilter(info, "ELEVATION", false);
        ParameterFilter filter3 = TileLayerInfoUtil.findParameterFilter("ELEVATION", info.getParameterFilters());
        Assert.assertNotSame(filter2, filter3);
        Assert.assertEquals(filter, filter3);
    }
}


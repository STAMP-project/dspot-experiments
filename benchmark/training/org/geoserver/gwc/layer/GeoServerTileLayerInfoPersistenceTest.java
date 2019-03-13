/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.layer;


import PublishedType.RASTER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.gwc.GWCTestHelpers;
import org.geoserver.gwc.config.GWCConfig;
import org.geowebcache.config.XMLGridSubset;
import org.geowebcache.filter.parameters.FloatParameterFilter;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.geowebcache.filter.parameters.StringParameterFilter;
import org.geowebcache.grid.BoundingBox;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GeoServerTileLayerInfoPersistenceTest {
    private GeoServerTileLayerInfo info;

    private GWCConfig defaults;

    private GeoServerTileLayerInfo defaultVectorInfo;

    @Test
    public void testMarshallingDefaults() throws Exception {
        GWCConfig oldDefaults = GWCConfig.getOldDefaults();
        LayerInfo layerInfo = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        info = TileLayerInfoUtil.loadOrCreate(layerInfo, oldDefaults);
        testMarshaling(info);
    }

    @Test
    public void testMarshallingBlobStoreId() throws Exception {
        GWCConfig oldDefaults = GWCConfig.getOldDefaults();
        LayerInfo layerInfo = GWCTestHelpers.mockLayer("testLayer", new String[]{  }, RASTER);
        info = TileLayerInfoUtil.loadOrCreate(layerInfo, oldDefaults);
        info.setBlobStoreId("myBlobStore");
        GeoServerTileLayerInfo unmarshalled = testMarshaling(info);
        Assert.assertThat(unmarshalled, Matchers.hasProperty("blobStoreId", Matchers.is("myBlobStore")));
    }

    @Test
    public void testMarshallingGridSubsets() throws Exception {
        List<XMLGridSubset> subsets = new ArrayList<XMLGridSubset>();
        XMLGridSubset subset;
        subset = new XMLGridSubset();
        subset.setGridSetName("EPSG:4326");
        subset.setZoomStart(1);
        subset.setZoomStop(10);
        subset.setExtent(new BoundingBox(0, 0, 180, 90));
        subsets.add(subset);
        subset = new XMLGridSubset();
        subset.setGridSetName("EPSG:900913");
        subsets.add(subset);
        subset = new XMLGridSubset();
        subset.setGridSetName("GlobalCRS84Scale");
        subset.setZoomStart(4);
        subset.setExtent(new BoundingBox((-100), (-40), 100, 40));
        subsets.add(subset);
        info.getGridSubsets().add(subsets.get(0));
        testMarshaling(info);
        info.getGridSubsets().clear();
        info.getGridSubsets().add(subsets.get(1));
        testMarshaling(info);
        info.getGridSubsets().clear();
        info.getGridSubsets().add(subsets.get(2));
        testMarshaling(info);
        info.getGridSubsets().addAll(subsets);
        testMarshaling(info);
    }

    @Test
    public void testMarshallingParameterFilters() throws Exception {
        StringParameterFilter strParam = new StringParameterFilter();
        strParam.setKey("TIME");
        strParam.setDefaultValue("now");
        List<String> strValues = new ArrayList(strParam.getValues());
        strValues.addAll(Arrays.asList("today", "yesterday", "tomorrow"));
        strParam.setValues(strValues);
        RegexParameterFilter regExParam = new RegexParameterFilter();
        regExParam.setKey("CQL_FILTER");
        regExParam.setDefaultValue("INCLUDE");
        regExParam.setRegex(".*");
        FloatParameterFilter floatParam = new FloatParameterFilter();
        floatParam.setKey("ENV");
        floatParam.setThreshold(Float.valueOf(1.0E-4F));
        List<Float> floatValues = new ArrayList(floatParam.getValues());
        floatValues.addAll(Arrays.asList(1.0F, 1.5F, 2.0F, 2.5F));
        floatParam.setValues(floatValues);
        info.getParameterFilters().clear();
        testMarshaling(info);
        info.getParameterFilters().clear();
        info.getParameterFilters().add(strParam);
        testMarshaling(info);
        info.getParameterFilters().clear();
        info.getParameterFilters().add(regExParam);
        testMarshaling(info);
        info.getParameterFilters().clear();
        info.getParameterFilters().add(floatParam);
        testMarshaling(info);
        info.getParameterFilters().clear();
        info.getParameterFilters().add(strParam);
        info.getParameterFilters().add(regExParam);
        info.getParameterFilters().add(floatParam);
        testMarshaling(info);
        StringParameterFilter strParam2 = new StringParameterFilter();
        strParam2.setKey("ELEVATION");
        strParam2.setDefaultValue("1");
        List<String> strValues2 = new ArrayList(strParam2.getValues());
        strValues2.addAll(Arrays.asList("1", "2", "3"));
        strParam2.setValues(strValues2);
        info.getParameterFilters().add(strParam2);
        testMarshaling(info);
    }
}


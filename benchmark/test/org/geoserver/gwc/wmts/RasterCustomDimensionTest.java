/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import DimensionsUtils.NO_LIMIT;
import Filter.INCLUDE;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import java.util.List;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.catalog.testreader.CustomFormat;
import org.geoserver.gwc.wmts.dimensions.Dimension;
import org.geoserver.gwc.wmts.dimensions.DimensionsUtils;
import org.junit.Assert;
import org.junit.Test;

import static ResourceInfo.CUSTOM_DIMENSION_PREFIX;


/**
 * This class contains tests that check that custom dimensions values are correctly extracted from
 * rasters. Custom dimensions are only supported in rasters.
 */
public class RasterCustomDimensionTest extends TestsSupport {
    @Test
    public void testDisabledDimension() throws Exception {
        // enable a custom dimension
        DimensionInfo dimensionInfo = new DimensionInfoImpl();
        dimensionInfo.setEnabled(true);
        CoverageInfo rasterInfo = getCoverageInfo();
        rasterInfo.getMetadata().put(((CUSTOM_DIMENSION_PREFIX) + (CustomFormat.CUSTOM_DIMENSION_NAME)), dimensionInfo);
        getCatalog().save(rasterInfo);
        // check that we correctly retrieve the custom dimension
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(1));
        // disable the custom dimension
        dimensionInfo.setEnabled(false);
        rasterInfo.getMetadata().put(((CUSTOM_DIMENSION_PREFIX) + (CustomFormat.CUSTOM_DIMENSION_NAME)), dimensionInfo);
        getCatalog().save(rasterInfo);
        // no dimensions should be available
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(0));
    }

    @Test
    public void testGetDefaultValue() {
        testDefaultValueStrategy(MINIMUM, "CustomDimValueA");
        testDefaultValueStrategy(MAXIMUM, "CustomDimValueC");
    }

    @Test
    public void testGetDomainsValues() throws Exception {
        testDomainsValuesRepresentation(0, "CustomDimValueA--CustomDimValueC");
        testDomainsValuesRepresentation(NO_LIMIT, "CustomDimValueA", "CustomDimValueB", "CustomDimValueC");
    }

    @Test
    public void testGetHistogram() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, null);
        Assert.assertThat(histogram.first, is("CustomDimValueA,CustomDimValueB,CustomDimValueC"));
        Assert.assertThat(histogram.second, containsInAnyOrder(1, 1, 1));
    }
}


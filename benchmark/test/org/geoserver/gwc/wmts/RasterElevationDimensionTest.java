/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import DimensionsUtils.NO_LIMIT;
import Filter.INCLUDE;
import ResourceInfo.ELEVATION;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import java.util.List;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.gwc.wmts.dimensions.Dimension;
import org.geoserver.gwc.wmts.dimensions.DimensionsUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class contains tests that check that elevation dimensions values are correctly extracted
 * from rasters.
 */
public class RasterElevationDimensionTest extends TestsSupport {
    @Test
    public void testDisabledDimension() throws Exception {
        // enable a elevation dimension
        DimensionInfo dimensionInfo = new DimensionInfoImpl();
        dimensionInfo.setEnabled(true);
        CoverageInfo rasterInfo = getCoverageInfo();
        rasterInfo.getMetadata().put(ELEVATION, dimensionInfo);
        getCatalog().save(rasterInfo);
        // check that we correctly retrieve the elevation dimension
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), Matchers.is(1));
        // disable the elevation dimension
        dimensionInfo.setEnabled(false);
        rasterInfo.getMetadata().put(ELEVATION, dimensionInfo);
        getCatalog().save(rasterInfo);
        // no dimensions should be available
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), Matchers.is(0));
    }

    @Test
    public void testGetDefaultValue() {
        testDefaultValueStrategy(MINIMUM, "0.0");
        testDefaultValueStrategy(MAXIMUM, "100.0");
    }

    @Test
    public void testGetDomainsValues() throws Exception {
        testDomainsValuesRepresentation(NO_LIMIT, "0", "100");
        testDomainsValuesRepresentation(1, "0--100");
    }

    @Test
    public void testGetHistogram() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, "50");
        Assert.assertThat(histogram.first, Matchers.is("0.0/150.0/50.0"));
        Assert.assertThat(histogram.second, Matchers.containsInAnyOrder(2, 0, 2));
    }
}


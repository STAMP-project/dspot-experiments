/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import Filter.INCLUDE;
import ResourceInfo.ELEVATION;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import java.util.Arrays;
import java.util.List;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.gwc.wmts.dimensions.Dimension;
import org.geoserver.gwc.wmts.dimensions.DimensionsUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class contains tests that check that elevation dimensions values are correctly extracted
 * from vector data.
 */
public class VectorElevationDimensionTest extends TestsSupport {
    @Test
    public void testDisabledDimension() throws Exception {
        // enable a elevation dimension
        DimensionInfo dimensionInfo = new DimensionInfoImpl();
        dimensionInfo.setEnabled(true);
        FeatureTypeInfo vectorInfo = getVectorInfo();
        vectorInfo.getMetadata().put(ELEVATION, dimensionInfo);
        getCatalog().save(vectorInfo);
        // check that we correctly retrieve the elevation dimension
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(1));
        // disable the elevation dimension
        dimensionInfo.setEnabled(false);
        vectorInfo.getMetadata().put(ELEVATION, dimensionInfo);
        getCatalog().save(vectorInfo);
        // no dimensions should be available
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(0));
    }

    @Test
    public void testGetDefaultValue() {
        testDefaultValueStrategy(MINIMUM, "1.0");
        testDefaultValueStrategy(MAXIMUM, "5.0");
    }

    @Test
    public void testGetDomainsValues() throws Exception {
        testDomainsValuesRepresentation(2, "1.0--5.0");
        testDomainsValuesRepresentation(4, "1.0", "2.0", "3.0", "5.0");
        testDomainsValuesRepresentation(7, "1.0", "2.0", "3.0", "5.0");
    }

    @Test
    public void testGetHistogram() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, "1");
        Assert.assertThat(histogram.first, is("1.0/6.0/1.0"));
        Assert.assertThat(histogram.second, equalTo(Arrays.asList(1, 1, 1, 0, 1)));
    }

    @Test
    public void testGetHistogramMisaligned() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, "0.75");
        Assert.assertThat(histogram.first, is("1.0/5.0/0.75"));
        Assert.assertThat(histogram.second, equalTo(Arrays.asList(1, 1, 1, 0, 0, 1)));
    }
}


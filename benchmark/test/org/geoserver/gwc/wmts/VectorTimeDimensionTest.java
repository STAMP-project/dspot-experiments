/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.wmts;


import DimensionsUtils.NO_LIMIT;
import Filter.INCLUDE;
import ResourceInfo.TIME;
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
 * This class contains tests that check that time dimensions values are correctly extracted from
 * vector data.
 */
public class VectorTimeDimensionTest extends TestsSupport {
    @Test
    public void testDisabledDimension() throws Exception {
        // enable a time dimension
        DimensionInfo dimensionInfo = new DimensionInfoImpl();
        dimensionInfo.setEnabled(true);
        FeatureTypeInfo vectorInfo = getVectorInfo();
        vectorInfo.getMetadata().put(TIME, dimensionInfo);
        getCatalog().save(vectorInfo);
        // check that we correctly retrieve the time dimension
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(1));
        // disable the time dimension
        dimensionInfo.setEnabled(false);
        vectorInfo.getMetadata().put(TIME, dimensionInfo);
        getCatalog().save(vectorInfo);
        // no dimensions should be available
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), is(0));
    }

    @Test
    public void testGetDefaultValue() {
        testDefaultValueStrategy(MINIMUM, "2012-02-11T00:00:00Z");
        testDefaultValueStrategy(MAXIMUM, "2012-02-12T00:00:00Z");
    }

    @Test
    public void testGetDomainsValues() throws Exception {
        testDomainsValuesRepresentation(NO_LIMIT, "2012-02-11T00:00:00.000Z", "2012-02-12T00:00:00.000Z");
        testDomainsValuesRepresentation(0, "2012-02-11T00:00:00.000Z--2012-02-12T00:00:00.000Z");
    }

    @Test
    public void testGetHistogram() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, "P1D");
        Assert.assertThat(histogram.first, is("2012-02-11T00:00:00.000Z/2012-02-13T00:00:00.000Z/P1D"));
        Assert.assertThat(histogram.second, equalTo(Arrays.asList(3, 1)));
    }
}


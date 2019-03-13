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
import java.util.Date;
import java.util.List;
import org.geoserver.catalog.impl.DimensionInfoImpl;
import org.geoserver.gwc.wmts.dimensions.Dimension;
import org.geoserver.gwc.wmts.dimensions.DimensionsUtils;
import org.geotools.feature.type.DateUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class contains tests that check that time dimension values are correctly extracted from
 * rasters. Some domain values are dynamically created based on the current time, this matches what
 * is done in WMS dimensions tests.
 *
 * <p>Note, there is some inconsistency between ISO formatter and GeoTools data serializer string
 * output, this is something that needs to be fixed (or maybe not) at GeoServer and GeoTools level.
 * In the mean time this tests will use the two formatter were needed.
 */
public class RasterTimeDimensionTest extends TestsSupport {
    // sorted time domain values as date objects
    protected static Date[] DATE_VALUES = new Date[]{ DateUtil.deserializeDateTime("2008-10-31T00:00:00Z"), DateUtil.deserializeDateTime("2008-11-01T00:00:00Z"), RasterTimeDimensionTest.getGeneratedMinValue(), RasterTimeDimensionTest.getGeneratedMiddleValue(), RasterTimeDimensionTest.getGeneratedMaxValue() };

    // sorted time domain values as strings formatted with ISO8601 formatter
    protected static String[] STRING_VALUES = new String[]{ RasterTimeDimensionTest.formatDate(RasterTimeDimensionTest.DATE_VALUES[0]), RasterTimeDimensionTest.formatDate(RasterTimeDimensionTest.DATE_VALUES[1]), RasterTimeDimensionTest.formatDate(RasterTimeDimensionTest.DATE_VALUES[2]), RasterTimeDimensionTest.formatDate(RasterTimeDimensionTest.DATE_VALUES[3]), RasterTimeDimensionTest.formatDate(RasterTimeDimensionTest.DATE_VALUES[4]) };

    @Test
    public void testDisabledDimension() throws Exception {
        // enable a time dimension
        DimensionInfo dimensionInfo = new DimensionInfoImpl();
        dimensionInfo.setEnabled(true);
        CoverageInfo rasterInfo = getCoverageInfo();
        rasterInfo.getMetadata().put(TIME, dimensionInfo);
        getCatalog().save(rasterInfo);
        // check that we correctly retrieve the time dimension
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), Matchers.is(1));
        // disable the time dimension
        dimensionInfo.setEnabled(false);
        rasterInfo.getMetadata().put(TIME, dimensionInfo);
        getCatalog().save(rasterInfo);
        // no dimensions should be available
        Assert.assertThat(DimensionsUtils.extractDimensions(wms, getLayerInfo(), MultiDimensionalExtension.ALL_DOMAINS).size(), Matchers.is(0));
    }

    @Test
    public void testGetDefaultValue() {
        testDefaultValueStrategy(MINIMUM, DateUtil.serializeDateTime(RasterTimeDimensionTest.DATE_VALUES[0].getTime(), true));
        testDefaultValueStrategy(MAXIMUM, DateUtil.serializeDateTime(RasterTimeDimensionTest.DATE_VALUES[4].getTime(), true));
    }

    @Test
    public void testGetDomainsValues() throws Exception {
        testDomainsValuesRepresentation(NO_LIMIT, RasterTimeDimensionTest.STRING_VALUES);
        testDomainsValuesRepresentation(2, (((RasterTimeDimensionTest.STRING_VALUES[0]) + "--") + (RasterTimeDimensionTest.STRING_VALUES[4])));
    }

    @Test
    public void testGetHistogram() {
        DimensionInfo dimensionInfo = TestsSupport.createDimension(true, null);
        Dimension dimension = buildDimension(dimensionInfo);
        Tuple<String, List<Integer>> histogram = dimension.getHistogram(INCLUDE, "P1Y");
        Assert.assertThat(histogram.first, Matchers.is((("2008-10-31T00:00:00.000Z/" + (RasterTimeDimensionTest.STRING_VALUES[4])) + "/P1Y")));
        // watertemp has 4 files, the test setup adds 3 to them, to a total o f 7 is expected
        Assert.assertThat(histogram.second.stream().reduce(0, ( total, value) -> total + value), Matchers.is(7));
    }
}


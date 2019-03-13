/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import javax.xml.namespace.QName;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.catalog.testreader.CustomFormat;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the WMS default value support for a custom dimension for both vector and raster layers.
 *
 * @author Ilkka Rinne <ilkka.rinne@spatineo.com>
 */
public class RasterCustomDimensionDefaultValueTest extends WMSTestSupport {
    private static final QName WATTEMP_CUSTOM = new QName(MockData.SF_URI, "watertemp_custom", MockData.SF_PREFIX);

    private static final String COVERAGE_DIMENSION_NAME = CustomFormat.CUSTOM_DIMENSION_NAME;

    WMS wms;

    @Test
    public void testDefaultCustomDimValueVectorSelector() throws Exception {
        // Use default default value strategy:
        setupCoverageMyDimension(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM, null);
        CoverageInfo customCoverage = getCatalog().getCoverageByName(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM.getLocalPart());
        String expected = "CustomDimValueA";
        String def = wms.getDefaultCustomDimensionValue(RasterCustomDimensionDefaultValueTest.COVERAGE_DIMENSION_NAME, customCoverage, String.class);
        Assert.assertTrue("Default dimension value is null", (def != null));
        Assert.assertTrue("Default dimension value should be the smallest one", expected.equals(def));
    }

    @Test
    public void testExplicitMinCustomDimValueVectorSelector() throws Exception {
        // Use default explicit value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupCoverageMyDimension(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM, defaultValueSetting);
        CoverageInfo customCoverage = getCatalog().getCoverageByName(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM.getLocalPart());
        String expected = "CustomDimValueA";
        String def = wms.getDefaultCustomDimensionValue(RasterCustomDimensionDefaultValueTest.COVERAGE_DIMENSION_NAME, customCoverage, String.class);
        Assert.assertTrue("Default dimension value is null", (def != null));
        Assert.assertTrue("Default dimension value should be the smallest one", expected.equals(def));
    }

    @Test
    public void testExplicitMaxCustomDimValueVectorSelector() throws Exception {
        // Use default explicit value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupCoverageMyDimension(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM, defaultValueSetting);
        CoverageInfo customCoverage = getCatalog().getCoverageByName(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM.getLocalPart());
        String expected = "CustomDimValueC";
        String def = wms.getDefaultCustomDimensionValue(RasterCustomDimensionDefaultValueTest.COVERAGE_DIMENSION_NAME, customCoverage, String.class);
        Assert.assertTrue("Default dimension value is null", (def != null));
        Assert.assertTrue("Default dimension value should be the biggest one", expected.equals(def));
    }

    @Test
    public void testExplicitNearestToGivenValueCustomDimValueVectorSelector() throws Exception {
        // Use default explicit value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        String referenceValue = "CustomDimValueD";
        defaultValueSetting.setReferenceValue(referenceValue);
        setupCoverageMyDimension(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM, defaultValueSetting);
        CoverageInfo customCoverage = getCatalog().getCoverageByName(RasterCustomDimensionDefaultValueTest.WATTEMP_CUSTOM.getLocalPart());
        String expected = "CustomDimValueC";
        String def = wms.getDefaultCustomDimensionValue(RasterCustomDimensionDefaultValueTest.COVERAGE_DIMENSION_NAME, customCoverage, String.class);
        Assert.assertTrue("Default dimension value is null", (def != null));
        Assert.assertTrue("Default dimension value should be the closest one", expected.equals(def));
    }
}


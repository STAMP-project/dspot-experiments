/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import Strategy.FIXED;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import javax.xml.namespace.QName;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.util.Range;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the WMS default value support for ELEVATION dimension for both vector and raster layers.
 *
 * @author Ilkka Rinne <ilkka.rinne@spatineo.com>
 */
public class RasterElevationDimensionDefaultValueTest extends WMSTestSupport {
    static final QName WATTEMP = new QName(MockData.SF_URI, "watertemp", MockData.SF_PREFIX);

    WMS wms;

    @Test
    public void testDefaultElevationCoverageSelector() throws Exception {
        // Use default default value strategy:
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, null);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double expected = Double.valueOf(0.0);
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the smallest one", ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitMinElevationCoverageSelector() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double expected = Double.valueOf(0.0);
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the smallest one", ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitMaxElevationCoverageSelector() throws Exception {
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double expected = Double.valueOf(100.0);
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the biggest one", ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitFixedElevationCoverageSelector() throws Exception {
        String fixedElevationStr = "550";
        // Use explicit default value strategy:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue(fixedElevationStr);
        Double fixedElevation = Double.parseDouble(fixedElevationStr);
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (fixedElevation.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitNearestToGivenTimeCoverageSelector() throws Exception {
        String referenceElevationStr = "55";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(referenceElevationStr);
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        // From src/test/resources/org/geoserver/wms/watertemp.zip:
        Double expected = Double.valueOf(100.0);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitNearestToGivenTimeCoverageSelector2() throws Exception {
        String referenceElevationStr = "45";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(referenceElevationStr);
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        // From src/test/resources/org/geoserver/wms/watertemp.zip:
        Double expected = Double.valueOf(0.0);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Double e = ((Double) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testFixedRangeElevation() throws Exception {
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("-100/0");
        setupCoverageElevationDimension(RasterElevationDimensionDefaultValueTest.WATTEMP, defaultValueSetting);
        CoverageInfo elevatedCoverage = getCatalog().getCoverageByName(RasterElevationDimensionDefaultValueTest.WATTEMP.getLocalPart());
        Range<Double> defaultRange = ((Range<Double>) (wms.getDefaultElevation(elevatedCoverage)));
        Assert.assertTrue("Default elevation is null", (defaultRange != null));
        Assert.assertEquals((-100), defaultRange.getMinValue(), 0.0);
        Assert.assertEquals(0, defaultRange.getMaxValue(), 0.0);
    }
}


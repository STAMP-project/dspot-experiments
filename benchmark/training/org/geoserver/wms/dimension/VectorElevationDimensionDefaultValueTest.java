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
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.catalog.FeatureTypeInfo;
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
public class VectorElevationDimensionDefaultValueTest extends WMSTestSupport {
    static final QName ELEVATION_WITH_START_END = new QName(MockData.SF_URI, "ElevationWithStartEnd", MockData.SF_PREFIX);

    WMS wms;

    @Test
    public void testExplicitMinElevationVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupFeatureElevationDimension(defaultValueSetting);
        FeatureTypeInfo elevationWithStartEnd = getCatalog().getFeatureTypeByName(VectorElevationDimensionDefaultValueTest.ELEVATION_WITH_START_END.getLocalPart());
        Double originallySmallest = Double.valueOf(1.0);
        Double e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the smallest one", ((Math.abs(((e.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-5));
        addFeatureWithElevation((fid++), 10.0);
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the smallest one", ((Math.abs(((e.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-5));
        Double smaller = Double.valueOf(((originallySmallest.doubleValue()) - 1));
        addFeatureWithElevation((fid++), smaller.doubleValue());
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the smallest one", ((Math.abs(((e.doubleValue()) - (smaller.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitMaxElevationVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupFeatureElevationDimension(defaultValueSetting);
        FeatureTypeInfo elevationWithStartEnd = getCatalog().getFeatureTypeByName(VectorElevationDimensionDefaultValueTest.ELEVATION_WITH_START_END.getLocalPart());
        Double originallyBiggest = Double.valueOf(2.0);
        Double e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the biggest one", ((Math.abs(((e.doubleValue()) - (originallyBiggest.doubleValue())))) < 1.0E-5));
        Double smaller = Double.valueOf(((originallyBiggest.doubleValue()) - 1));
        addFeatureWithElevation((fid++), smaller.doubleValue());
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the biggest one", ((Math.abs(((e.doubleValue()) - (originallyBiggest.doubleValue())))) < 1.0E-5));
        Double bigger = Double.valueOf(((originallyBiggest.doubleValue()) + 1));
        addFeatureWithElevation((fid++), bigger.doubleValue());
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the biggest one", ((Math.abs(((e.doubleValue()) - (bigger.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitFixedElevationVectorSelector() throws Exception {
        int fid = 1000;
        String fixedElevationStr = "550";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue(fixedElevationStr);
        Double fixedElevation = Double.parseDouble(fixedElevationStr);
        setupFeatureElevationDimension(defaultValueSetting);
        FeatureTypeInfo elevationWithStartEnd = getCatalog().getFeatureTypeByName(VectorElevationDimensionDefaultValueTest.ELEVATION_WITH_START_END.getLocalPart());
        Double originallyBiggest = Double.valueOf(3.0);
        Double e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (fixedElevation.doubleValue())))) < 1.0E-5));
        Double smaller = Double.valueOf(((originallyBiggest.doubleValue()) - 1));
        addFeatureWithElevation((fid++), smaller.doubleValue());
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (fixedElevation.doubleValue())))) < 1.0E-5));
        Double bigger = Double.valueOf(((originallyBiggest.doubleValue()) + 1));
        addFeatureWithElevation((fid++), bigger.doubleValue());
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue("Default elevation should be the fixed one", ((Math.abs(((e.doubleValue()) - (fixedElevation.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testExplicitNearestToGivenElevationVectorSelector() throws Exception {
        int fid = 1000;
        String referenceElevationStr = "1.6";
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        defaultValueSetting.setReferenceValue(referenceElevationStr);
        Double referenceElevation = Double.parseDouble(referenceElevationStr);
        setupFeatureElevationDimension(defaultValueSetting);
        FeatureTypeInfo elevationWithStartEnd = getCatalog().getFeatureTypeByName(VectorElevationDimensionDefaultValueTest.ELEVATION_WITH_START_END.getLocalPart());
        Double expected = Double.valueOf(2.0);
        Double e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue(("Default elevation should be the nearest one to " + (referenceElevation.doubleValue())), ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
        expected = Double.valueOf(1.8);
        addFeatureWithElevation((fid++), expected);
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue(("Default elevation should be the nearest one to " + (referenceElevation.doubleValue())), ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
        addFeatureWithElevation((fid++), 1.3);
        e = ((Double) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (e != null));
        Assert.assertTrue(("Default elevation should be the nearest one to " + (referenceElevation.doubleValue())), ((Math.abs(((e.doubleValue()) - (expected.doubleValue())))) < 1.0E-5));
    }

    @Test
    public void testFixedRangeElevation() throws Exception {
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        defaultValueSetting.setReferenceValue("-100/0");
        setupFeatureElevationDimension(defaultValueSetting);
        FeatureTypeInfo elevationWithStartEnd = getCatalog().getFeatureTypeByName(VectorElevationDimensionDefaultValueTest.ELEVATION_WITH_START_END.getLocalPart());
        Range<Double> defaultRange = ((Range<Double>) (wms.getDefaultElevation(elevationWithStartEnd)));
        Assert.assertTrue("Default elevation is null", (defaultRange != null));
        Assert.assertEquals((-100), defaultRange.getMinValue(), 0.0);
        Assert.assertEquals(0, defaultRange.getMaxValue(), 0.0);
    }
}


/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import Strategy.FIXED;
import Strategy.MAXIMUM;
import Strategy.MINIMUM;
import Strategy.NEAREST;
import java.sql.Date;
import javax.xml.namespace.QName;
import org.geoserver.catalog.DimensionDefaultValueSetting;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.feature.type.DateUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the WMS default value support for a custom dimension for both vector and raster layers.
 *
 * @author Ilkka Rinne <ilkka.rinne@spatineo.com>
 */
public class VectorCustomDimensionDefaultValueTest extends WMSTestSupport {
    private static final QName TIME_ELEVATION_CUSTOM = new QName(MockData.SF_URI, "TimeElevationCustom", MockData.SF_PREFIX);

    private static final String REFERENCE_TIME_DIMENSION = "REFERENCE_TIME";

    private static final String SCANNING_ANGLE_DIMENSION = "SCANNING_ANGLE";

    WMS wms;

    @Test
    public void testDefaultCustomDimDateValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use default value DimensionInfo setup, should return the minimum value
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, "referenceTime", null);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Date originallySmallest = Date.valueOf("2011-04-20");
        java.util.Date d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (originallySmallest.getTime())));
        Date biggest = Date.valueOf("2021-01-01");
        addFeatureWithReferenceTime((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (originallySmallest.getTime())));
        Date smaller = Date.valueOf("2010-01-01");
        addFeatureWithReferenceTime((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (smaller.getTime())));
    }

    @Test
    public void testDefaultCustomDimDoubleValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use default value DimensionInfo setup, should return the minimum value
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, "scanningAngle", null);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Double originallySmallest = Double.valueOf(0.0);
        Double d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-4));
        Double biggest = Double.valueOf(2.1);
        addFeatureWithScanningAngle((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-4));
        Double smaller = Double.valueOf((-1.0));
        addFeatureWithScanningAngle((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (smaller.doubleValue())))) < 1.0E-4));
    }

    @Test
    public void testExplicitMinCustomDimDateValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, "referenceTime", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Date originallySmallest = Date.valueOf("2011-04-20");
        java.util.Date d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (originallySmallest.getTime())));
        Date biggest = Date.valueOf("2021-01-01");
        addFeatureWithReferenceTime((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (originallySmallest.getTime())));
        Date smaller = Date.valueOf("2010-01-01");
        addFeatureWithReferenceTime((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((d.getTime()) == (smaller.getTime())));
    }

    @Test
    public void testExplicitMinCustomDimDoubleValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MINIMUM);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, "scanningAngle", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Double originallySmallest = Double.valueOf(0.0);
        Double d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-4));
        Double biggest = Double.valueOf(2.1);
        addFeatureWithScanningAngle((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (originallySmallest.doubleValue())))) < 1.0E-4));
        Double smaller = Double.valueOf((-1.0));
        addFeatureWithScanningAngle((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (smaller.doubleValue())))) < 1.0E-4));
    }

    @Test
    public void testExplicitMaxCustomDimDateValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, "referenceTime", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Date originallyBiggest = Date.valueOf("2011-04-23");
        java.util.Date d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the biggest one", ((d.getTime()) == (originallyBiggest.getTime())));
        Date biggest = Date.valueOf("2021-01-01");
        addFeatureWithReferenceTime((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the biggest one", ((d.getTime()) == (biggest.getTime())));
        Date smaller = Date.valueOf("2014-01-01");
        addFeatureWithReferenceTime((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the biggest one", ((d.getTime()) == (biggest.getTime())));
    }

    @Test
    public void testExplicitMaxCustomDimDoubleValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(MAXIMUM);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, "scanningAngle", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        // From src/test/resources/org/geoserver/wms/TimeElevationCustom.properties:
        Double originallyBiggest = Double.valueOf(1.5);
        Double d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (originallyBiggest.doubleValue())))) < 1.0E-4));
        Double biggest = Double.valueOf(2.1);
        addFeatureWithScanningAngle((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (biggest.doubleValue())))) < 1.0E-4));
        Double smaller = Double.valueOf(1.8);
        addFeatureWithScanningAngle((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - (biggest.doubleValue())))) < 1.0E-4));
    }

    @Test
    public void testExplicitFixedCustomDimDateValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        String fixedStr = "2014-01-20T00:00:00Z";
        defaultValueSetting.setReferenceValue(fixedStr);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, "referenceTime", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        long fixed = DateUtil.parseDateTime(fixedStr);
        java.util.Date d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the fixed one", ((d.getTime()) == fixed));
        Date biggest = Date.valueOf("2021-01-01");
        addFeatureWithReferenceTime((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the fixed one", ((d.getTime()) == fixed));
        Date smaller = Date.valueOf("2010-01-01");
        addFeatureWithReferenceTime((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the fixed one", ((d.getTime()) == fixed));
    }

    @Test
    public void testExplicitFixedCustomDimDoubleValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(FIXED);
        String fixedStr = "42.1";
        defaultValueSetting.setReferenceValue(fixedStr);
        double fixed = Double.parseDouble(fixedStr);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, "scanningAngle", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        Double d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - fixed))) < 1.0E-4));
        Double biggest = Double.valueOf(2.1);
        addFeatureWithScanningAngle((fid++), biggest);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - fixed))) < 1.0E-4));
        Double smaller = Double.valueOf(1.8);
        addFeatureWithScanningAngle((fid++), smaller);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the smallest one", ((Math.abs(((d.doubleValue()) - fixed))) < 1.0E-4));
    }

    @Test
    public void testExplicitNearestToGivenValueCustomDimDateValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        String referenceStr = "2014-01-20T00:00:00Z";
        defaultValueSetting.setReferenceValue(referenceStr);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, "referenceTime", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        Date originallyBiggest = Date.valueOf("2011-04-23");
        java.util.Date d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((d.getTime()) == (originallyBiggest.getTime())));
        Date biggerThanOriginal = Date.valueOf("2012-01-01");
        addFeatureWithReferenceTime((fid++), biggerThanOriginal);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((d.getTime()) == (biggerThanOriginal.getTime())));
        Date biggerThanReference = Date.valueOf("2014-06-01");
        addFeatureWithReferenceTime((fid++), biggerThanReference);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.REFERENCE_TIME_DIMENSION, timeElevationCustom, java.util.Date.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((d.getTime()) == (biggerThanReference.getTime())));
    }

    @Test
    public void testExplicitNearestToGivenValueCustomDimDoubleValueVectorSelector() throws Exception {
        int fid = 1000;
        // Use explicit default value DimensionInfo setup:
        DimensionDefaultValueSetting defaultValueSetting = new DimensionDefaultValueSetting();
        defaultValueSetting.setStrategyType(NEAREST);
        String referenceStr = "2.3";
        defaultValueSetting.setReferenceValue(referenceStr);
        setupFeatureCustomDimension(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, "scanningAngle", defaultValueSetting);
        FeatureTypeInfo timeElevationCustom = getCatalog().getFeatureTypeByName(VectorCustomDimensionDefaultValueTest.TIME_ELEVATION_CUSTOM.getLocalPart());
        double originallyBiggest = Double.valueOf(1.5);
        Double d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((Math.abs(((d.doubleValue()) - originallyBiggest))) < 1.0E-4));
        double biggerThanOriginal = 1.7;
        addFeatureWithScanningAngle((fid++), biggerThanOriginal);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((Math.abs(((d.doubleValue()) - biggerThanOriginal))) < 1.0E-4));
        double biggerThanReference = 2.45;
        addFeatureWithScanningAngle((fid++), biggerThanReference);
        d = wms.getDefaultCustomDimensionValue(VectorCustomDimensionDefaultValueTest.SCANNING_ANGLE_DIMENSION, timeElevationCustom, Double.class);
        Assert.assertTrue("Default value is null", (d != null));
        Assert.assertTrue("Default value should be the closest one", ((Math.abs(((d.doubleValue()) - biggerThanReference))) < 1.0E-4));
    }
}


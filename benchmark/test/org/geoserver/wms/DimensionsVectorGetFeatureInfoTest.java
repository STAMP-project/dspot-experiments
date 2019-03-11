/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;


import org.geoserver.catalog.ResourceInfo;
import org.geoserver.wms.dimension.DefaultValueConfiguration.DefaultValuePolicy;
import org.junit.Assert;
import org.junit.Test;


public class DimensionsVectorGetFeatureInfoTest extends WMSDynamicDimensionTestSupport {
    String baseFeatureInfo;

    @Test
    public void testNoDimension() throws Exception {
        Assert.assertEquals("TimeElevation.0", getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertEquals("TimeElevation.3", getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testBothDimensionsStaticDefaults() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testTimeDynamicRestriction() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.TIME, DefaultValuePolicy.LIMIT_DOMAIN));
        String request = (baseFeatureInfo) + "&elevation=1.0";
        Assert.assertNull(getFeatureAt(request, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(request, 60, 10));
        Assert.assertNull(getFeatureAt(request, 20, 30));
        Assert.assertNull(getFeatureAt(request, 60, 30));
    }

    @Test
    public void testTimeExpressionFull() throws Exception {
        // setup both dimensions, there is no equalSstrpimrinmatch records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.ELEVATION, DefaultValuePolicy.LIMIT_DOMAIN), new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.TIME, "Concatenate('2011-05-0', round(elevation + 1))"));
        Assert.assertEquals("TimeElevation.0", getFeatureAt(baseFeatureInfo, 20, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 10));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 20, 30));
        Assert.assertNull(getFeatureAt(baseFeatureInfo, 60, 30));
    }

    @Test
    public void testTimeExpressionSingleElevation() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.TIME, "Concatenate('2011-05-0', round(elevation + 1))"));
        String request = (baseFeatureInfo) + "&elevation=1.0";
        Assert.assertNull(getFeatureAt(request, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(request, 60, 10));
        Assert.assertNull(getFeatureAt(request, 20, 30));
        Assert.assertNull(getFeatureAt(request, 60, 30));
    }

    @Test
    public void testElevationDynamicRestriction() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.ELEVATION, DefaultValuePolicy.LIMIT_DOMAIN));
        String request = (baseFeatureInfo) + "&time=2011-05-02";
        Assert.assertNull(getFeatureAt(request, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(request, 60, 10));
        Assert.assertNull(getFeatureAt(request, 20, 30));
        Assert.assertNull(getFeatureAt(request, 60, 30));
    }

    @Test
    public void testExplicitDefaultTime() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.TIME, DefaultValuePolicy.LIMIT_DOMAIN));
        String request = (baseFeatureInfo) + "&elevation=1.0&time=current";
        Assert.assertNull(getFeatureAt(request, 20, 10));
        Assert.assertEquals("TimeElevation.1", getFeatureAt(request, 60, 10));
        Assert.assertNull(getFeatureAt(request, 20, 30));
        Assert.assertNull(getFeatureAt(request, 60, 30));
    }

    @Test
    public void testExplicitDefaultElevation() throws Exception {
        // setup both dimensions, there is no match records to the static defaults
        setupVectorDimension(ResourceInfo.ELEVATION, "elevation", DimensionPresentation.LIST, null, UNITS, UNIT_SYMBOL);
        setupVectorDimension(ResourceInfo.TIME, "time", DimensionPresentation.LIST, null, null, null);
        setupDynamicDimensions("TimeElevation", new org.geoserver.wms.dimension.DefaultValueConfiguration(ResourceInfo.ELEVATION, DefaultValuePolicy.LIMIT_DOMAIN));
        String request = (baseFeatureInfo) + "&elevation=&time=2011-05-03";
        Assert.assertNull(getFeatureAt(request, 20, 10));
        Assert.assertNull(getFeatureAt(request, 60, 10));
        Assert.assertEquals("TimeElevation.2", getFeatureAt(request, 20, 30));
        Assert.assertNull(getFeatureAt(request, 60, 30));
    }
}


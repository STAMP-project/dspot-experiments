/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.dimension;


import Filter.INCLUDE;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import org.geoserver.catalog.testreader.CustomFormat;
import org.geoserver.data.test.MockData;
import org.geoserver.wms.GetMapRequest;
import org.geoserver.wms.MapLayerInfo;
import org.geoserver.wms.WMS;
import org.geoserver.wms.WMSTestSupport;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.util.NumberRange;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;


/**
 * Tests the WMS default value support for a custom dimension that uses the java.util.Date class
 * rather than Strings and java.lang.Double class rather than Strings
 */
public class CustomDimensionTimeAndNumberTest extends WMSTestSupport {
    private static final QName WATTEMP_TIME = new QName(MockData.SF_URI, "watertemp_time", MockData.SF_PREFIX);

    private static final QName WATTEMP_DEPTH = new QName(MockData.SF_URI, "watertemp_depth", MockData.SF_PREFIX);

    private static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    static {
        CustomDimensionTimeAndNumberTest.DF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    WMS wms;

    @Test
    public void testTimeDimension() throws Exception {
        MapLayerInfo mapLayerInfo = new MapLayerInfo(getCatalog().getLayerByName(CustomDimensionTimeAndNumberTest.WATTEMP_TIME.getLocalPart()));
        final GridCoverage2DReader reader = ((GridCoverage2DReader) (mapLayerInfo.getCoverageReader()));
        GetMapRequest req = new GetMapRequest();
        req.setRawKvp(new HashMap<String, String>());
        req.getRawKvp().put(("DIM_" + (CustomFormat.CUSTOM_DIMENSION_NAME)), "2001-05-01T00:00:00.000Z, 2001-05-02T00:00:00.000Z");
        GeneralParameterValue[] readParam = wms.getWMSReadParameters(req, mapLayerInfo, INCLUDE, null, null, reader, false);
        @SuppressWarnings("unchecked")
        ParameterValue<List<Date>> val = ((ParameterValue<List<Date>>) (readParam[((readParam.length) - 1)]));
        Assert.assertEquals(2, val.getValue().size());
        Assert.assertEquals(CustomDimensionTimeAndNumberTest.DF.parse("2001-05-01 00:00:00"), val.getValue().get(0));
        Assert.assertEquals(CustomDimensionTimeAndNumberTest.DF.parse("2001-05-02 00:00:00"), val.getValue().get(1));
    }

    @Test
    public void testCustomDepthIntervalDimension() throws Exception {
        MapLayerInfo mapLayerInfo = new MapLayerInfo(getCatalog().getLayerByName(CustomDimensionTimeAndNumberTest.WATTEMP_DEPTH.getLocalPart()));
        final GridCoverage2DReader reader = ((GridCoverage2DReader) (mapLayerInfo.getCoverageReader()));
        GetMapRequest req = new GetMapRequest();
        req.setRawKvp(new HashMap<String, String>());
        req.getRawKvp().put(("DIM_" + (CustomFormat.CUSTOM_DIMENSION_NAME)), "10/50");
        GeneralParameterValue[] readParam = wms.getWMSReadParameters(req, mapLayerInfo, INCLUDE, null, null, reader, false);
        @SuppressWarnings("unchecked")
        ParameterValue<List<NumberRange>> val = ((ParameterValue<List<NumberRange>>) (readParam[((readParam.length) - 1)]));
        Assert.assertEquals(new NumberRange<Double>(Double.class, 10.0, 50.0), val.getValue().get(0));
    }

    @Test
    public void testCustomDepthListDimension() throws Exception {
        MapLayerInfo mapLayerInfo = new MapLayerInfo(getCatalog().getLayerByName(CustomDimensionTimeAndNumberTest.WATTEMP_DEPTH.getLocalPart()));
        final GridCoverage2DReader reader = ((GridCoverage2DReader) (mapLayerInfo.getCoverageReader()));
        GetMapRequest req = new GetMapRequest();
        req.setRawKvp(new HashMap<String, String>());
        req.getRawKvp().put(("DIM_" + (CustomFormat.CUSTOM_DIMENSION_NAME)), "10,50");
        GeneralParameterValue[] readParam = wms.getWMSReadParameters(req, mapLayerInfo, INCLUDE, null, null, reader, false);
        @SuppressWarnings("unchecked")
        ParameterValue<List<Double>> val = ((ParameterValue<List<Double>>) (readParam[((readParam.length) - 1)]));
        Assert.assertEquals(10, ((Double) (val.getValue().get(0))), 1.0E-6);
        Assert.assertEquals(50, ((Double) (val.getValue().get(1))), 1.0E-6);
    }
}


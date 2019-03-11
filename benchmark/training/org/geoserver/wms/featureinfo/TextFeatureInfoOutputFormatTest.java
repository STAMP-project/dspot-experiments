/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.featureinfo;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import net.opengis.wfs.FeatureCollectionType;
import org.geoserver.wms.GetFeatureInfoRequest;
import org.geoserver.wms.WMSTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class TextFeatureInfoOutputFormatTest extends WMSTestSupport {
    private TextFeatureInfoOutputFormat outputFormat;

    private FeatureCollectionType fcType;

    Map<String, Object> parameters;

    GetFeatureInfoRequest getFeatureInfoRequest;

    /**
     * Test null geometry is correctly handled (GEOS-6829).
     *
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void testNullGeometry() throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        outputFormat.write(fcType, getFeatureInfoRequest, outStream);
        String result = new String(outStream.toByteArray());
        Assert.assertFalse(result.contains("java.lang.NullPointerException"));
        Assert.assertTrue(result.contains("pointProperty = null"));
    }
}


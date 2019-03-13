/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.json;


import JSONType.CALLBACK_FUNCTION;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Assert;
import org.junit.Test;

import static JSONType.CALLBACK_FUNCTION_KEY;
import static JSONType.json;
import static JSONType.jsonp;


/**
 *
 *
 * @author carlo cancellieri - GeoSolutions
 */
public class GeoJsonDescribeTest extends WFSTestSupport {
    @Test
    public void testDescribePrimitiveGeoFeatureJSON() throws Exception {
        String output = getAsString(((("wfs?service=WFS&request=DescribeFeatureType&version=1.0.0&outputFormat=" + (json)) + "&typeName=") + (getLayerId(SystemTestData.PRIMITIVEGEOFEATURE))));
        testOutput(output);
    }

    @Test
    public void testDescribePrimitiveGeoFeatureJSONP() throws Exception {
        JSONType.setJsonpEnabled(true);
        String output = getAsString(((("wfs?service=WFS&request=DescribeFeatureType&version=1.0.0&outputFormat=" + (jsonp)) + "&typeName=") + (getLayerId(SystemTestData.PRIMITIVEGEOFEATURE))));
        JSONType.setJsonpEnabled(false);
        // removing specific parts
        output = output.substring(0, ((output.length()) - 2));
        output = output.substring(((CALLBACK_FUNCTION.length()) + 1), output.length());
        testOutput(output);
    }

    @Test
    public void testDescribePrimitiveGeoFeatureJSONPCustom() throws Exception {
        JSONType.setJsonpEnabled(true);
        String output = getAsString((((((("wfs?service=WFS&request=DescribeFeatureType&version=1.0.0&outputFormat=" + (jsonp)) + "&typeName=") + (getLayerId(SystemTestData.PRIMITIVEGEOFEATURE))) + "&format_options=") + (CALLBACK_FUNCTION_KEY)) + ":custom"));
        JSONType.setJsonpEnabled(false);
        // removing specific parts
        Assert.assertTrue(output.startsWith("custom("));
        output = output.substring(0, ((output.length()) - 2));
        output = output.substring((("custom".length()) + 1), output.length());
        testOutput(output);
    }
}


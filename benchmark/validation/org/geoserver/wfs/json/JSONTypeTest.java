/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.json;


import JSONType.CALLBACK_FUNCTION;
import JSONType.CALLBACK_FUNCTION_KEY;
import JSONType.JSON;
import JSONType.JSONP;
import JSONType.json;
import JSONType.jsonp;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;

import static JSONType.JSON;
import static JSONType.JSONP;


public class JSONTypeTest extends TestCase {
    @Test
    public void testMimeType() {
        // MimeType
        TestCase.assertNotSame(json, jsonp);
        TestCase.assertTrue(JSONType.isJsonMimeType(json));
        // enable JsonP programmatically
        JSONType.setJsonpEnabled(true);
        // check jsonp is enabled
        TestCase.assertTrue(JSONType.useJsonp(jsonp));
        // disable JsonP
        JSONType.setJsonpEnabled(false);
        TestCase.assertFalse(JSONType.useJsonp(jsonp));
    }

    @Test
    public void testJSONType() {
        // ENUM type
        JSONType json = JSON;
        TestCase.assertEquals(JSON, json);
        JSONType jsonp = JSONP;
        TestCase.assertEquals(JSONP, jsonp);
        TestCase.assertEquals(JSON, JSONType.getJSONType(json));
        TestCase.assertEquals(JSONP, JSONType.getJSONType(jsonp));
    }

    @Test
    public void testCallbackFunction() {
        Map<String, Map<String, String>> kvp = new HashMap<String, Map<String, String>>();
        TestCase.assertEquals(CALLBACK_FUNCTION, JSONType.getCallbackFunction(kvp));
        Map<String, String> formatOpts = new HashMap<String, String>();
        kvp.put("FORMAT_OPTIONS", formatOpts);
        TestCase.assertEquals(CALLBACK_FUNCTION, JSONType.getCallbackFunction(kvp));
        formatOpts.put(CALLBACK_FUNCTION_KEY, "functionName");
        TestCase.assertEquals("functionName", JSONType.getCallbackFunction(kvp));
    }
}


/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.featureinfo;


import JSONType.CALLBACK_FUNCTION;
import JSONType.json;
import JSONType.jsonp;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.wfs.json.JSONType;
import org.geoserver.wms.wms_1_1_1.GetFeatureInfoTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GetFeatureInfoJSONTest extends GetFeatureInfoTest {
    /**
     * Tests JSONP outside of expected polygon
     */
    @Test
    public void testSimpleJSONP() throws Exception {
        String layer = getLayerId(MockData.FORESTS);
        String request = (((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.jsonp);
        // JSONP
        JSONType.setJsonpEnabled(true);
        MockHttpServletResponse response = getAsServletResponse(request, "");
        JSONType.setJsonpEnabled(false);
        // MimeType
        Assert.assertEquals(jsonp, response.getContentType());
        // Check if the character encoding is the one expected
        Assert.assertTrue("UTF-8".equals(response.getCharacterEncoding()));
        // Content
        String result = response.getContentAsString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith(CALLBACK_FUNCTION));
        Assert.assertTrue(result.endsWith(")"));
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        result = result.substring(0, ((result.length()) - 1));
        result = result.substring(((CALLBACK_FUNCTION.length()) + 1), result.length());
        JSONObject rootObject = JSONObject.fromObject(result);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "the_geom");
    }

    /**
     * Tests jsonp with custom callback function
     */
    @Test
    public void testCustomJSONP() throws Exception {
        String layer = getLayerId(MockData.FORESTS);
        String request = ((((((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.jsonp)) + "&format_options=") + (JSONType.CALLBACK_FUNCTION_KEY)) + ":custom";
        // JSONP
        JSONType.setJsonpEnabled(true);
        MockHttpServletResponse response = getAsServletResponse(request, "");
        JSONType.setJsonpEnabled(false);
        // MimeType
        Assert.assertEquals(jsonp, response.getContentType());
        // Check if the character encoding is the one expected
        Assert.assertTrue("UTF-8".equals(response.getCharacterEncoding()));
        // Content
        String result = response.getContentAsString();
        // System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("custom("));
        Assert.assertTrue(result.endsWith(")"));
        Assert.assertTrue(((result.indexOf("Green Forest")) > 0));
        result = result.substring(0, ((result.length()) - 1));
        result = result.substring((("custom".length()) + 1), result.length());
        JSONObject rootObject = JSONObject.fromObject(result);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "the_geom");
    }

    /**
     * Tests JSON outside of expected polygon
     */
    @Test
    public void testSimpleJSON() throws Exception {
        String layer = getLayerId(MockData.FORESTS);
        String request = (((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.json);
        // JSON
        MockHttpServletResponse response = getAsServletResponse(request, "");
        // MimeType
        Assert.assertEquals(json, response.getContentType());
        // Check if the character encoding is the one expected
        Assert.assertTrue("UTF-8".equals(response.getCharacterEncoding()));
        // Content
        String result = response.getContentAsString();
        Assert.assertNotNull(result);
        JSONObject rootObject = JSONObject.fromObject(result);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertEquals(aFeature.getString("geometry_name"), "the_geom");
    }

    @Test
    public void testPropertySelection() throws Exception {
        String layer = getLayerId(MockData.FORESTS);
        String request = ((((((("wms?service=wms&version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.json)) + "&propertyName=NAME";
        // JSON
        MockHttpServletResponse response = getAsServletResponse(request, "");
        // MimeType
        Assert.assertEquals(json, response.getContentType());
        // Check if the character encoding is the one expected
        Assert.assertTrue("UTF-8".equals(response.getCharacterEncoding()));
        // Content
        String result = response.getContentAsString();
        Assert.assertNotNull(result);
        JSONObject rootObject = JSONObject.fromObject(result);
        // print(rootObject);
        Assert.assertEquals(rootObject.get("type"), "FeatureCollection");
        JSONArray featureCol = rootObject.getJSONArray("features");
        JSONObject aFeature = featureCol.getJSONObject(0);
        Assert.assertTrue(aFeature.getJSONObject("geometry").isNullObject());
        JSONObject properties = aFeature.getJSONObject("properties");
        Assert.assertTrue(properties.getJSONObject("FID").isNullObject());
        Assert.assertEquals("Green Forest", properties.get("NAME"));
    }

    @Test
    public void testReprojectedLayer() throws Exception {
        String layer = getLayerId(MockData.MPOLYGONS);
        String request = (((((("wms?version=1.1.1&bbox=500525,500025,500575,500050&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.json);
        // JSON
        JSONObject json = ((JSONObject) (getAsJSON(request)));
        JSONObject feature = ((JSONObject) (json.getJSONArray("features").get(0)));
        JSONObject geom = feature.getJSONObject("geometry");
        // unroll the geometry and get the first coordinate
        JSONArray coords = geom.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0).getJSONArray(0);
        Assert.assertTrue(new org.geotools.util.NumberRange<Double>(Double.class, 500525.0, 500575.0).contains(((Number) (coords.getDouble(0)))));
        Assert.assertTrue(new org.geotools.util.NumberRange<Double>(Double.class, 500025.0, 500050.0).contains(((Number) (coords.getDouble(1)))));
    }

    /**
     * Tests CQL filter
     */
    @Test
    public void testCQLFilter() throws Exception {
        String layer = getLayerId(MockData.FORESTS);
        String request = (((((("wms?version=1.1.1&bbox=-0.002,-0.002,0.002,0.002&styles=&format=jpeg" + "&request=GetFeatureInfo&layers=") + layer) + "&query_layers=") + layer) + "&width=20&height=20&x=10&y=10") + "&info_format=") + (JSONType.json);
        JSONObject json = ((JSONObject) (getAsJSON(request)));
        JSONArray features = json.getJSONArray("features");
        Assert.assertTrue(((features.size()) > 0));
        // Add CQL filter
        FeatureTypeInfo info = getCatalog().getFeatureTypeByName(layer);
        try {
            info.setCqlFilter("NAME LIKE 'Red%'");
            getCatalog().save(info);
            json = ((JSONObject) (getAsJSON(request)));
            features = json.getJSONArray("features");
            Assert.assertEquals(0, features.size());
        } finally {
            info = getCatalog().getFeatureTypeByName(layer);
            info.setCqlFilter(null);
            getCatalog().save(info);
        }
    }
}


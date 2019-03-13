/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 */
package org.geoserver.ows;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SimulateIntegrationTest extends GeoServerSystemTestSupport {
    @Test
    public void testGetFeature() throws Exception {
        JSONObject rsp = ((JSONObject) (getAsJSON(("wfs?" + (String.join("&", "service=wfs", "request=GetFeature", "version=1.0.0", "srsName=EPSG:4326", "bbox=-170,-80,170,80", ("typename=" + (getLayerId(POINTS))), "simulate=true"))))));
        print(rsp);
        assertService(rsp, "wfs", "1.0.0");
        JSONObject req = rsp.getJSONObject("operation").getJSONObject("request");
        JSONArray queries = req.getJSONArray("query");
        Assert.assertEquals(1, queries.size());
        JSONObject query = queries.getJSONObject(0);
        JSONObject typeName = query.getJSONArray("type_name").getJSONObject(0);
        Assert.assertEquals(POINTS.getLocalPart(), typeName.getString("local_part"));
        Assert.assertEquals(POINTS.getPrefix(), typeName.getString("prefix"));
        Assert.assertEquals(POINTS.getNamespaceURI(), typeName.getString("namespace_uri"));
    }

    @Test
    public void testGetMap() throws Exception {
        JSONObject rsp = ((JSONObject) (getAsJSON(("wms?" + (String.join("&", "service=wms", "request=GetMap", "version=1.1.1", ("layers=" + (getLayerId(BASIC_POLYGONS))), "styles=", "bbox=-170,-80,170,80", "srs=EPSG:4326", "width=256", "height=256", "format=image/png", "simulate=true"))))));
        print(rsp);
        assertService(rsp, "wms", "1.1.1");
        JSONObject req = rsp.getJSONObject("operation").getJSONObject("request");
        JSONArray layers = req.getJSONArray("layers");
        Assert.assertEquals(1, layers.size());
        JSONObject layer = layers.getJSONObject(0);
        Assert.assertEquals("cite:BasicPolygons", layer.getString("name"));
    }

    @Test
    public void testGetMapWithViewParams() throws Exception {
        JSONObject rsp = ((JSONObject) (getAsJSON(("wms?" + (String.join("&", "service=wms", "request=GetMap", "version=1.1.1", ("layers=" + (getLayerId(BASIC_POLYGONS))), "styles=", "bbox=-170,-80,170,80", "srs=EPSG:4326", "width=256", "height=256", "format=image/png", "viewparams=foo:bar;baz:bam", "simulate=true"))))));
        print(rsp);
        JSONObject req = rsp.getJSONObject("operation").getJSONObject("request");
        JSONArray vp = req.getJSONArray("view_params");
        Assert.assertEquals(1, vp.size());
        JSONObject kvp = vp.getJSONObject(0);
        Assert.assertEquals(2, kvp.size());
        Assert.assertEquals("bam", kvp.get("BAZ"));
        Assert.assertEquals("bar", kvp.get("FOO"));
    }
}


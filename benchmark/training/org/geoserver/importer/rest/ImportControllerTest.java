/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;


import H2DataStoreFactory.DBTYPE.key;
import ImportContext.State.PENDING;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.importer.Database;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.importer.SpatialFile;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class ImportControllerTest extends ImporterTestSupport {
    @Test
    public void testGetAllImports() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports"), 200)));
        Assert.assertNotNull(json.get("imports"));
        JSONArray imports = ((JSONArray) (json.get("imports")));
        Assert.assertEquals(3, imports.size());
        JSONObject imprt = imports.getJSONObject(0);
        Assert.assertEquals(0, imprt.getInt("id"));
        Assert.assertTrue(imprt.getString("href").endsWith("/imports/0"));
        imprt = imports.getJSONObject(1);
        Assert.assertEquals(1, imprt.getInt("id"));
        Assert.assertTrue(imprt.getString("href").endsWith("/imports/1"));
        imprt = imports.getJSONObject(2);
        Assert.assertEquals(2, imprt.getInt("id"));
        Assert.assertTrue(imprt.getString("href").endsWith("/imports/2"));
    }

    @Test
    public void testGetNonExistantImport() throws Exception {
        MockHttpServletResponse resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/9999"));
        Assert.assertEquals(404, resp.getStatus());
    }

    @Test
    public void testGetImport() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0"), 200)));
        Assert.assertNotNull(json.get("import"));
        JSONObject imprt = json.optJSONObject("import");
        Assert.assertEquals(0, imprt.getInt("id"));
        JSONArray tasks = imprt.getJSONArray("tasks");
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("READY", tasks.getJSONObject(0).get("state"));
        Assert.assertEquals("READY", tasks.getJSONObject(1).get("state"));
    }

    @Test
    public void testGetImportExpandChildren() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0?expand=2"))));
        JSONObject source = json.getJSONObject("import").getJSONObject("data");
        Assert.assertEquals("directory", source.getString("type"));
        Assert.assertEquals("Shapefile", source.getString("format"));
        ImportContext context = importer.getContext(0);
        Assert.assertEquals(getFile().getPath(), source.getString("location"));
        JSONArray files = source.getJSONArray("files");
        Assert.assertEquals(2, files.size());
        JSONArray tasks = json.getJSONObject("import").getJSONArray("tasks");
        Assert.assertEquals(2, tasks.size());
        JSONObject t = tasks.getJSONObject(0);
        Assert.assertEquals("READY", t.getString("state"));
        t = tasks.getJSONObject(1);
        Assert.assertEquals("READY", t.getString("state"));
    }

    @Test
    public void testGetImport2() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1?expand=3"), 200)));
        Assert.assertNotNull(json.get("import"));
        JSONObject imprt = json.optJSONObject("import");
        Assert.assertEquals(1, imprt.getInt("id"));
        JSONArray tasks = imprt.getJSONArray("tasks");
        Assert.assertEquals(1, tasks.size());
        JSONObject task = tasks.getJSONObject(0);
        Assert.assertEquals("READY", task.get("state"));
        JSONObject source = imprt.getJSONArray("tasks").getJSONObject(0).getJSONObject("data");
        Assert.assertEquals("file", source.getString("type"));
        Assert.assertEquals("GeoTIFF", source.getString("format"));
        ImportContext context = importer.getContext(1);
        Assert.assertEquals(getFile().getParentFile().getPath(), source.getString("location"));
        Assert.assertEquals("EmissiveCampania.tif", source.getString("file"));
    }

    @Test
    public void testGetImport3() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/2?expand=2"))));
        Assert.assertNotNull(json.get("import"));
        JSONObject imprt = json.optJSONObject("import");
        Assert.assertEquals(2, imprt.getInt("id"));
        JSONArray tasks = imprt.getJSONArray("tasks");
        Assert.assertEquals(1, tasks.size());
        JSONObject task = tasks.getJSONObject(0);
        Assert.assertEquals("NO_CRS", task.get("state"));
        JSONObject source = task.getJSONObject("data");
        Assert.assertEquals("file", source.getString("type"));
        Assert.assertEquals("Shapefile", source.getString("format"));
        Assert.assertEquals("archsites.shp", source.getString("file"));
    }

    @Test
    public void testGetImportDatabase() throws Exception {
        File dir = unpack("h2/cookbook.zip");
        Map params = new HashMap();
        params.put(key, "h2");
        params.put(H2DataStoreFactory.DATABASE.key, new File(dir, "cookbook").getAbsolutePath());
        importer.createContext(new Database(params));
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/3?expand=2"))));
        Assert.assertNotNull(json.get("import"));
        JSONObject source = json.getJSONObject("import").getJSONObject("data");
        Assert.assertEquals("database", source.getString("type"));
        Assert.assertEquals("H2", source.getString("format"));
        JSONArray tables = source.getJSONArray("tables");
        Assert.assertTrue(tables.contains("point"));
        Assert.assertTrue(tables.contains("line"));
        Assert.assertTrue(tables.contains("polygon"));
    }

    @Test
    public void testPost() throws Exception {
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports"), "", "application/json");
        Assert.assertEquals(201, resp.getStatus());
        Assert.assertNotNull(resp.getHeader("Location"));
        int id = lastId();
        Assert.assertTrue(resp.getHeader("Location").endsWith(("/imports/" + id)));
        JSONObject json = ((JSONObject) (json(resp)));
        JSONObject imprt = json.getJSONObject("import");
        Assert.assertEquals(PENDING.name(), imprt.get("state"));
        Assert.assertEquals(id, imprt.getInt("id"));
    }

    @Test
    public void testPutWithId() throws Exception {
        // propose a new import id
        MockHttpServletResponse resp = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/8675309"), "", "application/json");
        Assert.assertEquals(201, resp.getStatus());
        JSONObject json = ((JSONObject) (json(resp)));
        JSONObject imprt = json.getJSONObject("import");
        Assert.assertEquals(8675309, imprt.getInt("id"));
        resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/8675309"));
        Assert.assertEquals(200, resp.getStatus());
        json = ((JSONObject) (json(resp)));
        imprt = json.getJSONObject("import");
        Assert.assertEquals(8675309, imprt.getInt("id"));
        // now propose a new one that is less than the earlier
        resp = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/8675000"), "", "application/json");
        Assert.assertEquals(201, resp.getStatus());
        // it should be one more than the latest
        Assert.assertTrue(resp.getHeader("Location").endsWith(((RestBaseController.ROOT_PATH) + "/imports/8675310")));
        // and just make sure the other parts work
        resp = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/8675310"));
        Assert.assertEquals(200, resp.getStatus());
        json = ((JSONObject) (json(resp)));
        imprt = json.getJSONObject("import");
        Assert.assertEquals(8675310, imprt.getInt("id"));
        // now a normal request - make sure it continues the sequence
        resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/"), "{}", "application/json");
        Assert.assertEquals(201, resp.getStatus());
        Assert.assertNotNull(resp.getHeader("Location"));
        Assert.assertTrue(resp.getHeader("Location").endsWith("/imports/8675311"));
    }

    @Test
    public void testPutWithIdNoContentType() throws Exception {
        // propose a new import id
        MockHttpServletResponse resp = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/8675317"));
        Assert.assertEquals(201, resp.getStatus());
    }

    @Test
    public void testPostWithTarget() throws Exception {
        createH2DataStore("sf", "skunkworks");
        String json = "{" + (((((((((((("\"import\": { " + "\"targetWorkspace\": {") + "\"workspace\": {") + "\"name\": \"sf\"") + "}") + "},") + "\"targetStore\": {") + "\"dataStore\": {") + "\"name\": \"skunkworks\"") + "}") + "}") + "}") + "}");
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports"), json, "application/json");
        Assert.assertEquals(201, resp.getStatus());
        Assert.assertNotNull(resp.getHeader("Location"));
        int id = lastId();
        Assert.assertTrue(resp.getHeader("Location").endsWith(("/imports/" + id)));
        ImportContext ctx = importer.getContext(id);
        Assert.assertNotNull(ctx);
        Assert.assertNotNull(ctx.getTargetWorkspace());
        Assert.assertEquals("sf", ctx.getTargetWorkspace().getName());
        Assert.assertNotNull(ctx.getTargetStore());
        Assert.assertEquals("skunkworks", ctx.getTargetStore().getName());
        resp = postAsServletResponse((((RestBaseController.ROOT_PATH) + "/imports/") + id), "");
        Assert.assertEquals(204, resp.getStatus());
    }

    @Test
    public void testPostNoMediaType() throws Exception {
        MockHttpServletResponse resp = postAsServletResponseNoContentType(((RestBaseController.ROOT_PATH) + "/imports"), "");
        Assert.assertEquals(201, resp.getStatus());
    }

    @Test
    public void testImportSessionIdNotInt() throws Exception {
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/foo"), "");
        // assertEquals(404, resp.getStatus());
        // Spring feels that 400 is better than 404 for this! - IJT
        Assert.assertEquals(400, resp.getStatus());
    }

    @Test
    public void testContentNegotiation() throws Exception {
        MockHttpServletResponse res = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0"));
        Assert.assertEquals("application/json", res.getContentType());
        MockHttpServletRequest req = createRequest(((RestBaseController.ROOT_PATH) + "/imports/0"));
        req.setMethod("GET");
        req.addHeader("Accept", "text/html");
        res = dispatch(req);
        Assert.assertEquals(200, res.getStatus());
        System.out.println(res.getContentAsString());
        Assert.assertEquals("text/html", res.getContentType());
    }

    @Test
    public void testGetImportGeoJSON() throws Exception {
        File dir = unpack("geojson/point.json.zip");
        importer.createContext(new SpatialFile(new File(dir, "point.json")));
        int id = lastId();
        JSONObject json = ((JSONObject) (getAsJSON(((((RestBaseController.ROOT_PATH) + "/imports/") + id) + "?expand=3"))));
        Assert.assertNotNull(json);
        JSONObject imprt = json.optJSONObject("import");
        Assert.assertEquals(id, imprt.getInt("id"));
        JSONArray tasks = imprt.getJSONArray("tasks");
        Assert.assertEquals(1, tasks.size());
        JSONObject task = tasks.getJSONObject(0);
        JSONObject source = task.getJSONObject("data");
        Assert.assertEquals("file", source.getString("type"));
        Assert.assertEquals("GeoJSON", source.getString("format"));
        Assert.assertEquals("point.json", source.getString("file"));
        JSONObject layer = task.getJSONObject("layer");
        JSONArray attributes = layer.getJSONArray("attributes");
        Assert.assertNotEquals(0, attributes.size());
        Assert.assertTrue(layer.containsKey("bbox"));
    }
}


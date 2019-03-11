/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;


import ImportTask.State.READY;
import MediaType.APPLICATION_JSON;
import State.PENDING;
import UpdateMode.APPEND;
import UpdateMode.CREATE;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.data.util.IOUtils;
import org.geoserver.importer.ImportContext.State;
import org.geoserver.rest.RestBaseController;
import org.geotools.data.DataStore;
import org.geotools.jdbc.JDBCDataStore;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Ian Schneider <ischneider@opengeo.org>
 */
public class ImportTaskControllerTest extends ImporterTestSupport {
    JDBCDataStore jdbcStore;

    @Test
    public void testGetAllTasks() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks"))));
        JSONArray tasks = json.getJSONArray("tasks");
        Assert.assertEquals(2, tasks.size());
        JSONObject task = tasks.getJSONObject(0);
        Assert.assertEquals(0, task.getInt("id"));
        Assert.assertTrue(task.getString("href").endsWith("/imports/0/tasks/0"));
        task = tasks.getJSONObject(1);
        Assert.assertEquals(1, task.getInt("id"));
        Assert.assertTrue(task.getString("href").endsWith("/imports/0/tasks/1"));
    }

    @Test
    public void testGetTask() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"))));
        JSONObject task = json.getJSONObject("task");
        Assert.assertEquals(0, task.getInt("id"));
        Assert.assertTrue(task.getString("href").endsWith("/imports/0/tasks/0"));
    }

    @Test
    public void testGetTaskProgress() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/progress"), 200)));
        Assert.assertEquals("READY", json.get("state"));
        // TODO: trigger import and check progress
    }

    @Test
    public void testDeleteTask() throws Exception {
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports"), "");
        Assert.assertEquals(201, resp.getStatus());
        Assert.assertNotNull(resp.getHeader("Location"));
        String[] split = resp.getHeader("Location").split("/");
        Integer id = Integer.parseInt(split[((split.length) - 1)]);
        ImportContext context = importer.getContext(id);
        File dir = unpack("shape/archsites_epsg_prj.zip");
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        new File(dir, "extra.file").createNewFile();
        File[] files = dir.listFiles();
        Part[] parts = new Part[files.length];
        for (int i = 0; i < (files.length); i++) {
            parts[i] = new FilePart(files[i].getName(), files[i]);
        }
        MultipartRequestEntity multipart = new MultipartRequestEntity(parts, new PostMethod().getParams());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        multipart.writeRequest(bout);
        MockHttpServletRequest req = createRequest(((((RestBaseController.ROOT_PATH) + "/imports/") + id) + "/tasks"));
        req.setContentType(multipart.getContentType());
        req.addHeader("Content-Type", multipart.getContentType());
        req.setMethod("POST");
        req.setContent(bout.toByteArray());
        resp = dispatch(req);
        context = importer.getContext(context.getId());
        Assert.assertEquals(2, context.getTasks().size());
        req = createRequest(((((RestBaseController.ROOT_PATH) + "/imports/") + id) + "/tasks/1"));
        req.setMethod("DELETE");
        resp = dispatch(req);
        Assert.assertEquals(204, resp.getStatus());
        context = importer.getContext(context.getId());
        Assert.assertEquals(1, context.getTasks().size());
    }

    @Test
    public void testPostMultiPartFormData() throws Exception {
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports"), "");
        Assert.assertEquals(201, resp.getStatus());
        Assert.assertNotNull(resp.getHeader("Location"));
        String[] split = resp.getHeader("Location").split("/");
        Integer id = Integer.parseInt(split[((split.length) - 1)]);
        ImportContext context = importer.getContext(id);
        Assert.assertNull(context.getData());
        Assert.assertTrue(context.getTasks().isEmpty());
        File dir = unpack("shape/archsites_epsg_prj.zip");
        Part[] parts = new Part[]{ new FilePart("archsites.shp", new File(dir, "archsites.shp")), new FilePart("archsites.dbf", new File(dir, "archsites.dbf")), new FilePart("archsites.shx", new File(dir, "archsites.shx")), new FilePart("archsites.prj", new File(dir, "archsites.prj")) };
        MultipartRequestEntity multipart = new MultipartRequestEntity(parts, new PostMethod().getParams());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        multipart.writeRequest(bout);
        MockHttpServletRequest req = createRequest(((((RestBaseController.ROOT_PATH) + "/imports/") + id) + "/tasks"));
        req.setContentType(multipart.getContentType());
        req.addHeader("Content-Type", multipart.getContentType());
        req.setMethod("POST");
        req.setContent(bout.toByteArray());
        resp = dispatch(req);
        context = importer.getContext(context.getId());
        Assert.assertNull(context.getData());
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        Assert.assertTrue(((task.getData()) instanceof SpatialFile));
        Assert.assertEquals(READY, task.getState());
        SpatialFile data = ((SpatialFile) (task.getData()));
        Assert.assertTrue(new File(data.getFile().getParentFile(), ".locking").exists());
    }

    @Test
    public void testPostGeotiffBz2() throws Exception {
        String path = "geotiff/EmissiveCampania.tif.bz2";
        InputStream stream = ImporterTestSupport.class.getResourceAsStream(("test-data/" + path));
        uploadGeotiffAndVerify(new File(path).getName(), stream, "application/x-bzip2");
    }

    @Test
    public void testPostGeotiffBz2TargetWorkspaceJsonUTF8() throws Exception {
        String path = "geotiff/EmissiveCampania.tif.bz2";
        InputStream stream = ImporterTestSupport.class.getResourceAsStream(("test-data/" + path));
        String creationRequest = "{\n" + ((((((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"sf\"\n") + "         }\n") + "      }\n") + "   }\n") + "}");
        ImportContext context = uploadGeotiffAndVerify(new File(path).getName(), stream, "application/x-bzip2", creationRequest, "application/json;charset=UTF-8");
        final ImportTask task = context.getTasks().get(0);
        Assert.assertEquals("sf", task.getStore().getWorkspace().getName());
    }

    @Test
    public void testPostGeotiff() throws Exception {
        File tempBase = tmpDir();
        File tempDir = new File(tempBase, "testPostGeotiff");
        if (!(tempDir.mkdirs())) {
            throw new IllegalStateException("Cannot create temp dir for testing geotiff");
        }
        String tifname = "EmissiveCampania.tif";
        String bz2name = tifname + ".bz2";
        File destinationArchive = new File(tempDir, bz2name);
        InputStream inputStream = ImporterTestSupport.class.getResourceAsStream(("test-data/geotiff/" + bz2name));
        IOUtils.copy(inputStream, destinationArchive);
        VFSWorker vfs = new VFSWorker();
        vfs.extractTo(destinationArchive, tempDir);
        File tiff = new File(tempDir, tifname);
        if (!(tiff.exists())) {
            throw new IllegalStateException("Did not extract tif correctly");
        }
        FileInputStream fis = new FileInputStream(tiff);
        uploadGeotiffAndVerify(tifname, fis, "image/tiff");
    }

    @Test
    public void testGetTarget() throws Exception {
        JSONObject json = getJSONObject("task");
        JSONObject target = json.getJSONObject("target");
        Assert.assertTrue(target.has("href"));
        Assert.assertTrue(target.getString("href").endsWith(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target")));
        Assert.assertTrue(target.has("dataStore"));
        target = target.getJSONObject("dataStore");
        Assert.assertTrue(target.has("name"));
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"))));
        Assert.assertNotNull(json.get("dataStore"));
    }

    @Test
    public void testPutTarget() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"))));
        Assert.assertEquals("archsites", json.getJSONObject("dataStore").getString("name"));
        String update = "{\"dataStore\": { \"type\": \"foo\" }}";
        put(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"), update, APPLICATION_JSON.toString());
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"))));
        Assert.assertEquals("foo", json.getJSONObject("dataStore").getString("type"));
    }

    @Test
    public void testPutTargetExisting() throws Exception {
        createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "foo");
        String update = "{\"dataStore\": { \"name\": \"foo\" }}";
        put(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"), update, APPLICATION_JSON.toString());
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/target"))));
        Assert.assertEquals("foo", json.getJSONObject("dataStore").getString("name"));
        Assert.assertEquals("H2", json.getJSONObject("dataStore").getString("type"));
    }

    @Test
    public void testUpdateMode() throws Exception {
        createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "foo");
        ImportContext session = importer.getContext(0);
        Assert.assertEquals(CREATE, session.getTasks().get(0).getUpdateMode());
        // change to append mode
        String update = "{\"task\": { \"updateMode\" : \"APPEND\" }}";
        put(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"), update, APPLICATION_JSON.toString());
        session = importer.getContext(0);
        Assert.assertEquals(APPEND, session.getTasks().get(0).getUpdateMode());
        // put a dumby and verify the modified updateMode remains
        update = "{\"task\": {}}";
        put(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"), update, APPLICATION_JSON.toString());
        session = importer.getContext(0);
        Assert.assertEquals(APPEND, session.getTasks().get(0).getUpdateMode());
    }

    @Test
    public void testPutItemSRS() throws Exception {
        File dir = unpack("shape/archsites_no_crs.zip");
        importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"))));
        JSONObject task = json.getJSONObject("task");
        Assert.assertEquals("NO_CRS", task.get("state"));
        Assert.assertFalse(task.getJSONObject("layer").containsKey("srs"));
        // verify invalid SRS handling
        MockHttpServletResponse resp = setSRSRequest(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"), "26713");
        verifyInvalidCRSErrorResponse(resp);
        resp = setSRSRequest(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"), "EPSG:9838275");
        verifyInvalidCRSErrorResponse(resp);
        setSRSRequest(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"), "EPSG:26713");
        ImportContext context = importer.getContext(1);
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0?expand=2"))));
        task = json.getJSONObject("task");
        Assert.assertEquals("READY", task.get("state"));
        Assert.assertEquals("EPSG:26713", task.getJSONObject("layer").getString("srs"));
        State state = context.getState();
        Assert.assertEquals("Invalid context state", PENDING, state);
    }

    /**
     * This variant matches exactly the documentation and puts the changes directly on the layer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPutItemSRSOnLayer() throws Exception {
        File dir = unpack("shape/archsites_no_crs.zip");
        importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"))));
        JSONObject task = json.getJSONObject("task");
        Assert.assertEquals("NO_CRS", task.get("state"));
        Assert.assertFalse(task.getJSONObject("layer").containsKey("srs"));
        setSRSRequest(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0/layer"), "EPSG:26713");
        ImportContext context = importer.getContext(1);
        json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0?expand=2"))));
        task = json.getJSONObject("task");
        Assert.assertEquals("READY", task.get("state"));
        Assert.assertEquals("EPSG:26713", task.getJSONObject("layer").getString("srs"));
        State state = context.getState();
        Assert.assertEquals("Invalid context state", PENDING, state);
    }

    /**
     * Rename layer test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRenameLayerAndImportIntoH2() throws Exception {
        // create H2 store to act as a target
        DataStoreInfo h2Store = createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "testTarget");
        // create context with default name
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(0L);
        context.setTargetStore(h2Store);
        importer.changed(context);
        importer.update(context, new SpatialFile(new File(dir, "archsites.shp")));
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0"))));
        JSONObject task = json.getJSONObject("task");
        Assert.assertEquals("READY", task.get("state"));
        // now rename the layer
        String renamer = "{\n" + (((("  \"layer\": {\n" + "\t\t\t\"name\": \"test123\",\n") + "\t\t  \"nativeName\": \"test123\",\n") + "\t\t}\n") + "}");
        JSONObject response = ((JSONObject) (putAsJSON(((RestBaseController.ROOT_PATH) + "/imports/1/tasks/0/layer"), renamer, "application/json")));
        JSONObject layer = response.getJSONObject("layer");
        Assert.assertEquals("test123", layer.getString("name"));
        Assert.assertEquals("test123", layer.getString("nativeName"));
        Assert.assertEquals("archsites", layer.getString("originalName"));
        context = importer.getContext(1);
        importer.run(context);
        // check created type, layer and database table
        FeatureTypeInfo ftInfo = getCatalog().getFeatureTypeByName("test123");
        Assert.assertNotNull(ftInfo);
        Assert.assertEquals("test123", ftInfo.getNativeName());
        DataStore store = ((DataStore) (h2Store.getDataStore(null)));
        Assert.assertThat(Arrays.asList(store.getTypeNames()), CoreMatchers.hasItem("test123"));
    }

    /**
     * Ideally, many variations of error handling could be tested here. (For performance - otherwise
     * too much tear-down/setup)
     */
    @Test
    public void testErrorHandling() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"))));
        JSONObjectBuilder badDateFormatTransform = new JSONObjectBuilder();
        badDateFormatTransform.object().key("task").object().key("transformChain").object().key("type").value("VectorTransformChain").key("transforms").array().object().key("field").value("datefield").key("type").value("DateFormatTransform").key("format").value("xxx").endObject().endArray().endObject().endObject().endObject();
        MockHttpServletResponse resp = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"), badDateFormatTransform.buildObject().toString(), "application/json");
        assertErrorResponse(resp, "Invalid date parsing format");
    }

    @Test
    public void testDeleteTask2() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0"));
        Assert.assertEquals(204, response.getStatus());
        JSONObject json = ((JSONObject) (getAsJSON(((RestBaseController.ROOT_PATH) + "/imports/0/tasks"))));
        JSONArray items = json.getJSONArray("tasks");
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(1, items.getJSONObject(0).getInt("id"));
    }

    @Test
    public void testGetLayer() throws Exception {
        String path = (RestBaseController.ROOT_PATH) + "/imports/0/tasks/0";
        JSONObject json = getJSONObject("task");
        Assert.assertTrue(json.has("layer"));
        JSONObject layer = json.getJSONObject("layer");
        Assert.assertTrue(layer.has("name"));
        Assert.assertTrue(layer.has("href"));
        Assert.assertTrue(layer.getString("href").endsWith((path + "/layer")));
        json = ((JSONObject) (getAsJSON((path + "/layer"))));
        Assert.assertTrue(layer.has("name"));
        Assert.assertTrue(layer.has("href"));
        Assert.assertTrue(layer.getString("href").endsWith((path + "/layer")));
    }
}


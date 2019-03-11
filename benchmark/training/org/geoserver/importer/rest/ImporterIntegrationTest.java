/**
 * (c) 2013 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;


import DimensionPresentation.DISCRETE_INTERVAL;
import HttpStatus.CREATED;
import ImportContext.State;
import ImportContext.State.COMPLETE;
import ImportContext.State.PENDING;
import ImportTask.State.READY;
import Importer.UPLOAD_ROOT_KEY;
import MockData.BASIC_POLYGONS;
import Query.ALL;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.importer.ImportContext;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterDataTest;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.importer.SpatialFile;
import org.geoserver.platform.resource.Resources;
import org.geoserver.rest.RestBaseController;
import org.geotools.coverage.grid.io.GranuleSource;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.NameImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class ImporterIntegrationTest extends ImporterTestSupport {
    @Test
    public void testDirectWrongFile() throws Exception {
        // set a callback to check that the request spring context is passed to the job thread
        RequestContextListener listener = applicationContext.getBean(RequestContextListener.class);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication());
        final boolean[] invoked = new boolean[]{ false };
        listener.setCallBack(( request, user, resource) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertThat(request, CoreMatchers.notNullValue());
            Assert.assertThat(resource, CoreMatchers.notNullValue());
            Assert.assertThat(auth, CoreMatchers.notNullValue());
            invoked[0] = true;
        });
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        File geotiffFile = new File(dir, "EmissiveCampania.tif");
        String wrongPath = jsonSafePath(geotiffFile).replace("/EmissiveCampania", "/foobar");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        // @formatter:off
        String contextDefinition = ((((((((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"") + wrongPath) + "\"\n") + "      },") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"h2\",\n") + "        }\n") + "      }\n") + "   }\n") + "}";
        // @formatter:on
        // initialize the import
        MockHttpServletResponse servletResponse = postAsServletResponse("/rest/imports", contextDefinition, "application/json");
        JSONObject json = ((JSONObject) (json(servletResponse)));
        int importId = json.getJSONObject("import").getInt("id");
        json = ((JSONObject) (getAsJSON(("/rest/imports/" + importId))));
        // Expect an error
        Assert.assertEquals("INIT_ERROR", json.getJSONObject("import").getString("state"));
        Assert.assertEquals("input == null!", json.getJSONObject("import").getString("message"));
    }

    @Test
    public void testDirectWrongDir() throws Exception {
        // set a callback to check that the request spring context is passed to the job thread
        RequestContextListener listener = applicationContext.getBean(RequestContextListener.class);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication());
        final boolean[] invoked = new boolean[]{ false };
        listener.setCallBack(( request, user, resource) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Assert.assertThat(request, CoreMatchers.notNullValue());
            Assert.assertThat(resource, CoreMatchers.notNullValue());
            Assert.assertThat(auth, CoreMatchers.notNullValue());
            invoked[0] = true;
        });
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        File geotiffFile = new File(dir, "EmissiveCampania.tif");
        String wrongPath = jsonSafePath(geotiffFile).replace("/EmissiveCampania", "bar/EmissiveCampania");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        // @formatter:off
        String contextDefinition = ((((((((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"") + wrongPath) + "\"\n") + "      },") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"h2\",\n") + "        }\n") + "      }\n") + "   }\n") + "}";
        // @formatter:on
        // initialize the import
        MockHttpServletResponse servletResponse = postAsServletResponse("/rest/imports", contextDefinition, "application/json");
        Assert.assertEquals(500, servletResponse.getStatus());
    }

    @Test
    public void testDefaultTransformationsInit() throws Exception {
        File dir = unpack("csv/locations.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        File locations = new File(dir, "locations.csv");
        // @formatter:off
        String contextDefinition = (((((((((((((((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"") + (jsonSafePath(locations))) + "\"\n") + "      },\n") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"h2\",\n") + "        }\n") + "      },\n") + "      \"transforms\": [\n") + "        {\n") + "          \"type\": \"AttributesToPointGeometryTransform\",\n") + "          \"latField\": \"LAT\",") + "          \"lngField\": \"LON\"") + "        }\n") + "      ]") + "   }\n") + "}";
        // @formatter:on
        JSONObject json = ((JSONObject) (json(postAsServletResponse("/rest/imports", contextDefinition, "application/json"))));
        // print(json);
        int importId = json.getJSONObject("import").getInt("id");
        checkLatLonTransformedImport(importId);
    }

    @Test
    public void testDefaultTransformationsUpload() throws Exception {
        File dir = unpack("csv/locations.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        File locations = new File(dir, "locations.csv");
        // @formatter:off
        String contextDefinition = (((((((((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"h2\",\n") + "        }\n") + "      },\n") + "      \"transforms\": [\n") + "        {\n") + "          \"type\": \"AttributesToPointGeometryTransform\",\n") + "          \"latField\": \"LAT\",") + "          \"lngField\": \"LON\"") + "        }\n") + "      ]") + "   }\n") + "}";
        // @formatter:on
        JSONObject json = ((JSONObject) (json(postAsServletResponse("/rest/imports", contextDefinition, "application/json"))));
        // print(json);
        int importId = json.getJSONObject("import").getInt("id");
        // upload the data
        String body = (("--AaB03x\r\nContent-Disposition: form-data; name=filedata; filename=data.csv\r\n" + ("Content-Type: text/plain\n" + "\r\n\r\n")) + (FileUtils.readFileToString(locations))) + "\r\n\r\n--AaB03x--";
        post((("/rest/imports/" + importId) + "/tasks"), body, "multipart/form-data; boundary=AaB03x");
        checkLatLonTransformedImport(importId);
    }

    @Test
    public void testDirectExecuteAsync() throws Exception {
        testDirectExecuteInternal(true);
    }

    @Test
    public void testDirectExecuteSync() throws Exception {
        testDirectExecuteInternal(false);
    }

    @Test
    public void testDirectExecutePhasedAsync() throws Exception {
        testDirectExecutePhasedInternal(true);
    }

    @Test
    public void testDirectExecutePhasedSync() throws Exception {
        testDirectExecutePhasedInternal(false);
    }

    @Test
    public void testImportGranuleInEmptyMosaic() throws Exception {
        Catalog catalog = getCatalog();
        // prepare an empty mosaic
        File root = getTestData().getDataDirectoryRoot();
        String mosaicName = "emptyMosaic";
        File mosaicRoot = new File(root, mosaicName);
        ensureClean(mosaicRoot);
        File granulesRoot = new File(root, (mosaicName + "_granules"));
        ensureClean(granulesRoot);
        Properties props = new Properties();
        props.put("SPI", "org.geotools.data.h2.H2DataStoreFactory");
        props.put("database", "empty");
        try (FileOutputStream fos = new FileOutputStream(new File(mosaicRoot, "datastore.properties"))) {
            props.store(fos, null);
        }
        props.clear();
        props.put("CanBeEmpty", "true");
        try (FileOutputStream fos = new FileOutputStream(new File(mosaicRoot, "indexer.properties"))) {
            props.store(fos, null);
        }
        CatalogBuilder cb = new CatalogBuilder(catalog);
        WorkspaceInfo ws = catalog.getDefaultWorkspace();
        cb.setWorkspace(ws);
        CoverageStoreInfo store = cb.buildCoverageStore(mosaicName);
        store.setURL(("./" + mosaicName));
        store.setType("ImageMosaic");
        catalog.save(store);
        // put a granule in the mosaic
        unpack("geotiff/EmissiveCampania.tif.bz2", granulesRoot);
        File granule = new File(granulesRoot, "EmissiveCampania.tif");
        store = catalog.getCoverageStoreByName(mosaicName);
        // @formatter:off
        String contextDefinition = (((((((((((("{\n" + ((("   \"import\": {\n" + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"")) + (jsonSafePath(granule.getAbsoluteFile()))) + "\"\n") + "      },") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"") + (store.getName())) + "\",\n") + "        }\n") + "      }\n") + "   }\n") + "}";
        // @formatter:on
        // sync execution
        JSONObject json = ((JSONObject) (json(postAsServletResponse("/rest/imports?exec=true", contextDefinition, "application/json"))));
        // print(json);
        String state = json.getJSONObject("import").getString("state");
        Assert.assertEquals("COMPLETE", state);
        // check the import produced a granule
        StructuredGridCoverage2DReader reader = ((StructuredGridCoverage2DReader) (store.getGridCoverageReader(null, null)));
        GranuleSource granules = reader.getGranules(reader.getGridCoverageNames()[0], true);
        Assert.assertEquals(1, granules.getCount(ALL));
        // check we now also have a layer
        LayerInfo layer = catalog.getLayerByName(mosaicName);
        Assert.assertNotNull(layer);
    }

    /**
     * Attribute computation integration test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAttributeCompute() throws Exception {
        // create H2 store to act as a target
        DataStoreInfo h2Store = createH2DataStore(getCatalog().getDefaultWorkspace().getName(), "computeDB");
        // create context with default name
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(0L);
        context.setTargetStore(h2Store);
        importer.changed(context);
        importer.update(context, new SpatialFile(new File(dir, "archsites.shp")));
        // add a transformation to compute a new attribute
        String json = "{\n" + (((("  \"type\": \"AttributeComputeTransform\",\n" + "  \"field\": \"label\",\n") + "  \"fieldType\": \"java.lang.String\",\n") + "  \"cql\": \"\'Test string\'\"\n") + "}");
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/transforms"), json, "application/json");
        Assert.assertEquals(CREATED.value(), resp.getStatus());
        // run it
        context = importer.getContext(0);
        importer.run(context);
        // check created type, layer and database table
        DataStore store = ((DataStore) (h2Store.getDataStore(null)));
        SimpleFeatureSource fs = store.getFeatureSource("archsites");
        Assert.assertNotNull(fs.getSchema().getType("label"));
        SimpleFeature first = DataUtilities.first(fs.getFeatures());
        Assert.assertEquals("Test string", first.getAttribute("label"));
    }

    @Test
    public void testUploadRootExternal() throws Exception {
        File dirFromEnv = null;
        try {
            // Let's now override the external folder through the Environment variable. This takes
            // precedence on .properties
            System.setProperty(UPLOAD_ROOT_KEY, "env_uploads");
            Assert.assertNotNull(importer.getUploadRoot());
            // the target layer is not there
            Assert.assertNull(getCatalog().getLayerByName("archsites"));
            // create context with default name
            File dir = unpack("shape/archsites_epsg_prj.zip");
            ImportContext context = importer.createContext(0L);
            importer.changed(context);
            importer.update(context, new SpatialFile(new File(dir, "archsites.shp")));
            // run it
            context = importer.getContext(0);
            importer.run(context);
            // check the layer has been created
            Assert.assertNotNull(getCatalog().getLayerByName("archsites"));
            // verify the file has been placed under the uploaded root specified on Env vars
            dirFromEnv = Resources.directory(Resources.fromPath("env_uploads"));
            // ... and ensure it is the same as defined on the .properties file
            Assert.assertEquals(dirFromEnv, importer.getUploadRoot());
            // ... and that the "archsites_epsg_prj" data has been stored inside that folder
            for (String subFolder : dirFromEnv.list()) {
                File archsites = new File(subFolder, "archsites.shp");
                Assert.assertTrue(archsites.exists());
                break;
            }
        } finally {
            if ((dirFromEnv != null) && (dirFromEnv.exists())) {
                FileUtils.deleteQuietly(dirFromEnv);
            }
            if ((System.getProperty(UPLOAD_ROOT_KEY)) != null) {
                System.clearProperty(UPLOAD_ROOT_KEY);
            }
        }
    }

    @Test
    public void testRunPostScript() throws Exception {
        // check if bash is there
        Assume.assumeTrue("Could not find sh in path, skipping", ImporterDataTest.checkShellAvailable());
        // even with bash available, the test won't work on windows as it won't know
        // how to run the .sh out of the box
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        // the target layer is not there
        Assert.assertNull(getCatalog().getLayerByName("archsites"));
        // write out a simple shell script in the data dir and make it executable
        File scripts = getDataDirectory().findOrCreateDir("importer", "scripts");
        File script = new File(scripts, "test.sh");
        FileUtils.writeStringToFile(script, "touch test.properties\n");
        script.setExecutable(true, true);
        // create context with default name
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(0L);
        importer.changed(context);
        importer.update(context, new SpatialFile(new File(dir, "archsites.shp")));
        // add a transformation to run post script
        String json = "{\n" + (("  \"type\": \"PostScriptTransform\",\n" + "  \"name\": \"test.sh\"\n") + "}");
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/transforms"), json, "application/json");
        Assert.assertEquals(CREATED.value(), resp.getStatus());
        // run it
        context = importer.getContext(0);
        importer.run(context);
        // check the layer has been created
        Assert.assertNotNull(getCatalog().getLayerByName("archsites"));
        // verify the script also run
        File testFile = new File(scripts, "test.properties");
        Assert.assertTrue(testFile.exists());
    }

    @Test
    public void testRunPostScriptWithOptions() throws Exception {
        // check if bash is there
        Assume.assumeTrue("Could not find sh in path, skipping", ImporterDataTest.checkShellAvailable());
        // even with bash available, the test won't work on windows as it won't know
        // how to run the .sh out of the box
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        // the target layer is not there
        Assert.assertNull(getCatalog().getLayerByName("archsites"));
        // write out a simple shell script in the data dir and make it executable
        File scripts = getDataDirectory().findOrCreateDir("importer", "scripts");
        File script = new File(scripts, "test.sh");
        FileUtils.writeStringToFile(script, "touch $1\n");
        script.setExecutable(true, true);
        // create context with default name
        File dir = unpack("shape/archsites_epsg_prj.zip");
        ImportContext context = importer.createContext(0L);
        importer.changed(context);
        importer.update(context, new SpatialFile(new File(dir, "archsites.shp")));
        // add a transformation to run post script
        String json = "{\n" + ((("  \"type\": \"PostScriptTransform\",\n" + "  \"name\": \"test.sh\",\n") + "  \"options\": [\"test.abc\"]") + "}");
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/transforms"), json, "application/json");
        Assert.assertEquals(CREATED.value(), resp.getStatus());
        // run it
        context = importer.getContext(0);
        importer.run(context);
        // check the layer has been created
        Assert.assertNotNull(getCatalog().getLayerByName("archsites"));
        // verify the script also run
        File testFile = new File(scripts, "test.abc");
        Assert.assertTrue(testFile.exists());
    }

    @Test
    public void testRunWithTimeDimention() throws Exception {
        Catalog cat = getCatalog();
        DataStoreInfo ds = createH2DataStore(cat.getDefaultWorkspace().getName(), "ming");
        // the target layer is not there
        Assert.assertNull(getCatalog().getLayerByName("ming_time"));
        // create context with default name
        File dir = unpack("shape/ming_time.zip");
        ImportContext context = importer.createContext(0L);
        importer.changed(context);
        importer.update(context, new SpatialFile(new File(dir, "ming_time.shp")));
        context.setTargetStore(ds);
        Assert.assertEquals(1, context.getTasks().size());
        context.getTasks().get(0).getData().setCharsetEncoding("UTF-8");
        // add a transformation to run post script
        String json = "{\n" + ((("  \"type\": \"DateFormatTransform\",\n" + "  \"field\": \"Year_Date\",\n") + "  \"presentation\": \"DISCRETE_INTERVAL\"") + "}");
        MockHttpServletResponse resp = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/imports/0/tasks/0/transforms"), json, "application/json");
        Assert.assertEquals(CREATED.value(), resp.getStatus());
        // run it
        context = importer.getContext(0);
        ImportTask task = context.getTasks().get(0);
        task.setDirect(false);
        task.setStore(ds);
        importer.changed(task);
        Assert.assertEquals(READY, task.getState());
        context.updated();
        Assert.assertEquals(PENDING, context.getState());
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        // check the layer has been created
        LayerInfo layer = cat.getLayerByName("ming_time");
        Assert.assertNotNull(layer);
        ResourceInfo resource = layer.getResource();
        // verify the TIME dimension has benn defined
        MetadataMap md = resource.getMetadata();
        Assert.assertNotNull(md);
        Assert.assertTrue(md.containsKey("time"));
        DimensionInfo timeDimension = ((DimensionInfo) (md.get("time")));
        Assert.assertNotNull(timeDimension);
        Assert.assertEquals(timeDimension.getAttribute(), "Year_Date");
        Assert.assertEquals(timeDimension.getPresentation(), DISCRETE_INTERVAL);
    }

    @Test
    public void testIndirectImportTempCleanup() throws Exception {
        File dir = unpack("csv/locations.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        File locations = new File(dir, "locations.csv");
        // @formatter:off
        String contextDefinition = ((((((((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"") + (jsonSafePath(locations))) + "\"\n") + "      },\n") + "      targetStore: {\n") + "        dataStore: {\n") + "        name: \"h2\",\n") + "        }\n") + "      }\n") + "   }\n") + "}";
        // @formatter:on
        JSONObject json = ((JSONObject) (json(postAsServletResponse("/rest/imports", contextDefinition, "application/json"))));
        // print(json);
        int importId = json.getJSONObject("import").getInt("id");
        ImportContext context = importer.getContext(importId);
        Assert.assertEquals(PENDING, context.getState());
        Assert.assertTrue(new File(context.getUploadDirectory().getFile(), ".locking").exists());
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        LayerInfo layer = task.getLayer();
        ResourceInfo resource = layer.getResource();
        resource.setSRS("EPSG:4326");
        importer.changed(task);
        Assert.assertEquals(READY, task.getState());
        context.updated();
        Assert.assertEquals(PENDING, context.getState());
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        Assert.assertTrue(((context.getState()) == (State.COMPLETE)));
        Assert.assertFalse(new File(context.getUploadDirectory().getFile(), ".locking").exists());
        Assert.assertTrue(new File(context.getUploadDirectory().getFile(), ".clean-me").exists());
    }

    @Test
    public void testDirectImportTempCleanup() throws Exception {
        File dir = unpack("csv/locations.zip");
        String wsName = getCatalog().getDefaultWorkspace().getName();
        File locations = new File(dir, "locations.csv");
        // @formatter:off
        String contextDefinition = (((((((((((("{\n" + ((("   \"import\": {\n" + "      \"targetWorkspace\": {\n") + "         \"workspace\": {\n") + "            \"name\": \"")) + wsName) + "\"\n") + "         }\n") + "      },\n") + "      \"data\": {\n") + "        \"type\": \"file\",\n") + "        \"file\": \"") + (jsonSafePath(locations))) + "\"\n") + "      }\n") + "   }\n") + "}";
        // @formatter:on
        JSONObject json = ((JSONObject) (json(postAsServletResponse("/rest/imports", contextDefinition, "application/json"))));
        // print(json);
        int importId = json.getJSONObject("import").getInt("id");
        ImportContext context = importer.getContext(importId);
        Assert.assertEquals(PENDING, context.getState());
        Assert.assertTrue(new File(context.getUploadDirectory().getFile(), ".locking").exists());
        Assert.assertEquals(1, context.getTasks().size());
        ImportTask task = context.getTasks().get(0);
        LayerInfo layer = task.getLayer();
        ResourceInfo resource = layer.getResource();
        resource.setSRS("EPSG:4326");
        importer.changed(task);
        Assert.assertEquals(READY, task.getState());
        context.updated();
        Assert.assertEquals(PENDING, context.getState());
        importer.run(context);
        Assert.assertEquals(COMPLETE, context.getState());
        Assert.assertTrue(((context.getState()) == (State.COMPLETE)));
        Assert.assertTrue(new File(context.getUploadDirectory().getFile(), ".locking").exists());
    }

    // GEOS-8869 - Importer Converter can override normal layer converter
    @Test
    public void testLayerRest() throws Exception {
        Catalog catalog = getCatalog();
        getTestData().addVectorLayer(BASIC_POLYGONS, catalog);
        // Normal layer converter can handle styles, but the importer one ignores them,
        // so we can use this to determine which one is getting used.
        StyleInfo generic = catalog.getStyleByName("generic");
        StyleInfo polygon = catalog.getStyleByName("polygon");
        NameImpl basicPolygonsName = new NameImpl(BASIC_POLYGONS.getPrefix(), BASIC_POLYGONS.getLocalPart());
        LayerInfo basicPolygons = catalog.getLayerByName(basicPolygonsName);
        // initialize to a known default style
        basicPolygons.setDefaultStyle(generic);
        catalog.save(basicPolygons);
        Assert.assertEquals(generic.getId(), catalog.getLayerByName(basicPolygonsName).getDefaultStyle().getId());
        // Do a JSON PUT to change the default style
        String contextDefinition = "{\"layer\": {\n" + (((("  \"name\": \"BasicPolygons\",\n" + "  \"defaultStyle\":   {\n") + "    \"name\": \"polygon\"\n") + "  },\n") + "}}");
        MockHttpServletResponse response = putAsServletResponse(("/rest/layers/" + (basicPolygonsName.toString())), contextDefinition, "application/json");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(polygon.getName(), catalog.getLayerByName(basicPolygonsName).getDefaultStyle().getName());
        Assert.assertEquals(polygon.getId(), catalog.getLayerByName(basicPolygonsName).getDefaultStyle().getId());
    }
}


/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;


import HttpStatus.CREATED;
import HttpStatus.OK;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.importer.ImportTask;
import org.geoserver.importer.ImporterTestSupport;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class ImportTransformTest extends ImporterTestSupport {
    DataStoreInfo store;

    ImportTask importTask;

    private static String BASEPATH = RestBaseController.ROOT_PATH;

    @Test
    public void testGetTransforms() throws Exception {
        JSON j = getAsJSON(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms"));
        List<JSONObject> txs = parseTransformObjectsFromResponse(j);
        Assert.assertEquals(2, txs.size());
        Assert.assertEquals("ReprojectTransform", get("type"));
        Assert.assertEquals("IntegerFieldToDateTransform", get("type"));
    }

    @Test
    public void testGetTransform() throws Exception {
        JSON j = getAsJSON(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms/0"));
        Assert.assertTrue((j instanceof JSONObject));
        Assert.assertEquals("ReprojectTransform", get("type"));
    }

    @Test
    public void testGetTransformsExpandNone() throws Exception {
        JSON j = getAsJSON(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms?expand=none"));
        List<JSONObject> txs = parseTransformObjectsFromResponse(j);
        Assert.assertEquals(2, txs.size());
        Assert.assertTrue(txs.get(0).containsKey("href"));
        Assert.assertTrue(txs.get(1).containsKey("href"));
    }

    @Test
    public void testPostTransform() throws Exception {
        String json = "{\"type\": \"ReprojectTransform\", \"target\": \"EPSG:3005\"}";
        MockHttpServletResponse resp = postAsServletResponse(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms"), json, "application/json");
        String location = resp.getHeader("Location");
        Assert.assertEquals(CREATED.value(), resp.getStatus());
        // Make sure it was created
        Assert.assertEquals(3, importTask.getTransform().getTransforms().size());
    }

    @Test
    public void testDeleteTransform() throws Exception {
        MockHttpServletResponse resp = deleteAsServletResponse(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms/0"));
        Assert.assertEquals(OK.value(), resp.getStatus());
        // Make sure it was deleted
        Assert.assertEquals(1, importTask.getTransform().getTransforms().size());
    }

    @Test
    public void testPutTransform() throws Exception {
        String json = "{\"type\": \"ReprojectTransform\", \"target\": \"EPSG:3005\"}";
        MockHttpServletResponse resp = putAsServletResponse(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms/0"), json, "application/json");
        Assert.assertEquals(OK.value(), resp.getStatus());
        // Get it again and make sure it changed.
        JSON j = getAsJSON(((ImportTransformTest.BASEPATH) + "/imports/0/tasks/0/transforms/0"));
        Assert.assertTrue((j instanceof JSONObject));
        Assert.assertEquals("EPSG:3005", get("target"));
    }
}


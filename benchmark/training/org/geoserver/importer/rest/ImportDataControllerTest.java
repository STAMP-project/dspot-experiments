/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer.rest;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.importer.ImporterTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Created by vickdw on 3/30/17.
 */
public class ImportDataControllerTest extends ImporterTestSupport {
    @Test
    public void testGet() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((ROOT_PATH) + "/imports/0/data"), 200)));
        Assert.assertEquals("directory", json.getString("type"));
        Assert.assertEquals(2, json.getJSONArray("files").size());
    }

    @Test
    public void testGetFiles() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((ROOT_PATH) + "/imports/0/data/files"), 200)));
        Assert.assertEquals(2, json.getJSONArray("files").size());
    }

    @Test
    public void testGetFile() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(((ROOT_PATH) + "/imports/0/data/files/archsites.shp"), 200)));
        System.out.println(json);
        Assert.assertEquals("archsites.shp", json.getString("file"));
        Assert.assertEquals("archsites.prj", json.getString("prj"));
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((ROOT_PATH) + "/imports/0/data/files/archsites.shp"));
        Assert.assertEquals(200, response.getStatus());
        response = deleteAsServletResponse(((ROOT_PATH) + "/imports/0/data/files/archsites.shp"));
        Assert.assertEquals(204, response.getStatus());
        response = getAsServletResponse(((ROOT_PATH) + "/imports/0/data/files/archsites.shp"));
        Assert.assertEquals(404, response.getStatus());
        JSONArray arr = getJSONArray("files");
        Assert.assertEquals(1, arr.size());
    }
}


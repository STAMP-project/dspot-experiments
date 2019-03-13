/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import java.util.List;
import java.util.Random;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class TemplateControllerTest extends CatalogRESTTestSupport {
    @Test
    public void testGetPutGetDeleteGet() throws Exception {
        String path = (RestBaseController.ROOT_PATH) + "/templates/my_template.ftl";
        testGetPutGetDeleteGet(path, "hello world");
    }

    @Test
    public void testAllPathsSequentially() throws Exception {
        Random random = new Random();
        for (String path : getAllPaths()) {
            testGetPutGetDeleteGet(path, ("hello test " + (random.nextInt(1000))));
        }
    }

    @Test
    public void testAllPaths() throws Exception {
        String contentHeader = "hello path ";
        List<String> paths = getAllPaths();
        for (String path : paths) {
            // GET - confirm template not there
            assertNotFound(path);
        }
        for (String path : paths) {
            // PUT
            put(path, (contentHeader + path)).close();
        }
        for (String path : paths) {
            // GET
            Assert.assertEquals((contentHeader + path), getAsString(path).trim());
        }
        for (String path : paths) {
            // DELETE
            MockHttpServletResponse response = deleteAsServletResponse(path);
            Assert.assertEquals(200, response.getStatus());
        }
        for (String path : paths) {
            // GET - confirm template removed
            assertNotFound(path);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        String fooTemplate = (RestBaseController.ROOT_PATH) + "/templates/foo.ftl";
        String barTemplate = (RestBaseController.ROOT_PATH) + "/templates/bar.ftl";
        String fooContent = "hello foo - longer than bar";
        String barContent = "hello bar";
        // PUT
        put(fooTemplate, fooContent).close();
        put(barTemplate, barContent).close();
        // GET
        Assert.assertEquals(fooContent, getAsString(fooTemplate).trim());
        Assert.assertEquals(barContent, getAsString(barTemplate).trim());
        fooContent = "goodbye foo";
        // PUT
        put(fooTemplate, fooContent).close();
        // GET
        Assert.assertEquals(fooContent, getAsString(fooTemplate).trim());
        Assert.assertEquals(barContent, getAsString(barTemplate).trim());
    }
}


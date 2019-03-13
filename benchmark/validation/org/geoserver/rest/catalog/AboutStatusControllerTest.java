/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Carlo Cancellieri - GeoSolutions SAS
 */
public class AboutStatusControllerTest extends GeoServerSystemTestSupport {
    private static String BASEPATH = RestBaseController.ROOT_PATH;

    @Test
    public void testGetStatusHTML() throws Exception {
        String html = getAsString(((AboutStatusControllerTest.BASEPATH) + "/about/status"));
        Assert.assertTrue(html.contains("Available"));
        Assert.assertTrue(html.contains("Enabled"));
    }

    @Test
    public void testGetStatusXML() throws Exception {
        getAsDOM(((AboutStatusControllerTest.BASEPATH) + "/about/status.xml"));
    }

    @Test
    public void testGetStatusJSON() throws Exception {
        getAsJSON(((AboutStatusControllerTest.BASEPATH) + "/about/status.json"));
    }

    @Test
    public void testGetSingleModule() throws Exception {
        String html = getAsString(((AboutStatusControllerTest.BASEPATH) + "/about/status/gs-main"));
        Assert.assertTrue(html.contains("<b>Module</b> : gs-main"));
        Assert.assertTrue(html.contains("<b>Enabled</b> : true"));
    }

    @Test
    public void testMalformedModuleName() throws Exception {
        String html = getAsString(((AboutStatusControllerTest.BASEPATH) + "/about/status/fake1_module"));
        Assert.assertTrue(html.contains("No such module"));
    }
}


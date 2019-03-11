/**
 * (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Carlo Cancellieri - GeoSolutions SAS
 */
public class AboutControllerTest extends GeoServerSystemTestSupport {
    private static String BASEPATH = RestBaseController.ROOT_PATH;

    @Test
    public void testEmptyListHTMLTemplate() throws Exception {
        try {
            getAsDOM(((AboutControllerTest.BASEPATH) + "/about/version?manifest=NOTEXISTS.*"));
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testGetVersionsAsXML() throws Exception {
        // make the request, parsing the result as a dom
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/version.xml"));
        checkXMLModel(dom);
    }

    @Test
    public void testGetManifestsAsXML() throws Exception {
        // make the request, parsing the result as a dom
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/manifest.xml"));
        checkXMLModel(dom);
    }

    @Test
    public void testGetAsVersionsHTML() throws Exception {
        // make the request, parsing the result into a Dom object
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/version"));
        checkHTMLModel(dom);
    }

    @Test
    public void testGetAsManifestsHTML() throws Exception {
        // make the request, parsing the result into a Dom object
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/manifest?manifest=freemarker.*"));
        checkHTMLModel(dom);
    }

    @Test
    public void testGetAsVersionsJSON() throws Exception {
        // make the request, parsing the result into a json object
        JSONObject json = ((JSONObject) (getAsJSON(((AboutControllerTest.BASEPATH) + "/about/version.json"))));
        // print(json);
        checkJSONModel(json);
    }

    @Test
    public void testGetAsManifestsJSON() throws Exception {
        // make the request, parsing the result into a json object
        JSONObject json = ((JSONObject) (getAsJSON(((AboutControllerTest.BASEPATH) + "/about/manifest.json"))));
        checkJSONModel(json);
    }

    @Test
    public void testGetStatusAsHTML() throws Exception {
        // add an escape char to the environment
        System.setProperty("badString", "\u0007\b\u001b[46m");
        // make the request, parsing the result into a Dom object
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/status"));
        checkHTMLModel(dom);
        Document dom2 = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/status.html"));
        checkHTMLModel(dom2);
    }

    @Test
    public void testGetStatusAsJSON() throws Exception {
        // make the request, parsing the result into a Dom object
        JSON dom = getAsJSON(((AboutControllerTest.BASEPATH) + "/about/status.json"));
    }

    @Test
    public void testGetStatusAsXML() throws Exception {
        // make the request, parsing the result into a Dom object
        Document dom = getAsDOM(((AboutControllerTest.BASEPATH) + "/about/status.xml"));
    }
}


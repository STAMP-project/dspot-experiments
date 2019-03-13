/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import com.jayway.jsonpath.DocumentContext;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class LandingPageTest extends WFS3TestSupport {
    @Test
    public void testLandingPageNoSlash() throws Exception {
        DocumentContext json = getAsJSONPath("wfs3", 200);
        checkJSONLandingPage(json);
    }

    @Test
    public void testLandingPageSlash() throws Exception {
        DocumentContext json = getAsJSONPath("wfs3/", 200);
        checkJSONLandingPage(json);
    }

    @Test
    public void testLandingPageJSON() throws Exception {
        DocumentContext json = getAsJSONPath("wfs3?f=json", 200);
        checkJSONLandingPage(json);
    }

    @Test
    public void testLandingPageWorkspaceSpecific() throws Exception {
        DocumentContext json = getAsJSONPath("cdf/wfs3/", 200);
        checkJSONLandingPage(json);
    }

    @Test
    public void testLandingPageXML() throws Exception {
        Document dom = getAsDOM("wfs3/?f=application/xml");
        print(dom);
        // TODO: add actual tests in here
    }

    @Test
    public void testLandingPageYaml() throws Exception {
        String yaml = getAsString("wfs3/?f=application/x-yaml");
        // System.out.println(yaml);
        // TODO: add actual tests in here
    }

    @Test
    public void testLandingPageHTML() throws Exception {
        org.jsoup.nodes.Document document = getAsJSoup("wfs3?f=html");
        // check a couple of links
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/collections?f=text%2Fhtml", document.select("#htmlCollectionsLink").attr("href"));
        Assert.assertEquals("http://localhost:8080/geoserver/wfs3/api?f=text%2Fhtml", document.select("#htmlApiLink").attr("href"));
    }

    @Test
    public void testLandingPageHTMLInWorkspace() throws Exception {
        org.jsoup.nodes.Document document = getAsJSoup("sf/wfs3?f=html");
        // check a couple of links
        Assert.assertEquals("http://localhost:8080/geoserver/sf/wfs3/collections?f=text%2Fhtml", document.select("#htmlCollectionsLink").attr("href"));
        Assert.assertEquals("http://localhost:8080/geoserver/sf/wfs3/api?f=text%2Fhtml", document.select("#htmlApiLink").attr("href"));
    }
}


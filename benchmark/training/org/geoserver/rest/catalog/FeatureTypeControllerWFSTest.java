/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class FeatureTypeControllerWFSTest extends CatalogRESTTestSupport {
    private static String BASEPATH = RestBaseController.ROOT_PATH;

    @Test
    public void testGetAllByWorkspace() throws Exception {
        Document dom = getAsDOM(((FeatureTypeControllerWFSTest.BASEPATH) + "/workspaces/sf/featuretypes.xml"));
        Assert.assertEquals(catalog.getFeatureTypesByNamespace(catalog.getNamespaceByPrefix("sf")).size(), dom.getElementsByTagName("featureType").getLength());
    }

    @Test
    public void testPostAsXML() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&typename=sf:pdsa");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addPropertyDataStore(false);
        String xml = "<featureType>" + (((((((((((("<name>pdsa</name>" + "<nativeName>pdsa</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<nativeBoundingBox>") + "<minx>0.0</minx>") + "<maxx>1.0</maxx>") + "<miny>0.0</miny>") + "<maxy>1.0</maxy>") + "<crs>EPSG:4326</crs>") + "</nativeBoundingBox>") + "<store>pds</store>") + "</featureType>");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerWFSTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/datastores/pds/featuretypes/pdsa"));
        dom = getAsDOM("wfs?request=getfeature&typename=gs:pdsa");
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(2, dom.getElementsByTagName("gs:pdsa").getLength());
    }

    @Test
    public void testPostAsXMLInlineStore() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&typename=sf:pdsa");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addPropertyDataStore(false);
        String xml = "<featureType>" + (((((((((((("<name>pdsa</name>" + "<nativeName>pdsa</nativeName>") + "<srs>EPSG:4326</srs>") + "<nativeCRS>EPSG:4326</nativeCRS>") + "<nativeBoundingBox>") + "<minx>0.0</minx>") + "<maxx>1.0</maxx>") + "<miny>0.0</miny>") + "<maxy>1.0</maxy>") + "<crs>EPSG:4326</crs>") + "</nativeBoundingBox>") + "<store>pds</store>") + "</featureType>");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerWFSTest.BASEPATH) + "/workspaces/gs/featuretypes/"), xml, "text/xml");
        Assert.assertEquals(201, response.getStatus());
        assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/featuretypes/pdsa"));
        dom = getAsDOM("wfs?request=getfeature&typename=gs:pdsa");
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(2, dom.getElementsByTagName("gs:pdsa").getLength());
    }

    @Test
    public void testPostAsJSON() throws Exception {
        Document dom = getAsDOM("wfs?request=getfeature&typename=sf:pdsa");
        Assert.assertEquals("ows:ExceptionReport", dom.getDocumentElement().getNodeName());
        addPropertyDataStore(false);
        String json = "{" + (((((((((((((("'featureType':{" + "'name':'pdsa',") + "'nativeName':'pdsa',") + "'srs':'EPSG:4326',") + "'nativeBoundingBox':{") + "'minx':0.0,") + "'maxx':1.0,") + "'miny':0.0,") + "'maxy':1.0,") + "'crs':'EPSG:4326'") + "},") + "'nativeCRS':'EPSG:4326',") + "'store':'pds'") + "}") + "}");
        MockHttpServletResponse response = postAsServletResponse(((FeatureTypeControllerWFSTest.BASEPATH) + "/workspaces/gs/datastores/pds/featuretypes/"), json, "text/json");
        Assert.assertEquals(201, response.getStatus());
        assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/datastores/pds/featuretypes/pdsa"));
        dom = getAsDOM("wfs?request=getfeature&typename=gs:pdsa");
        Assert.assertEquals("wfs:FeatureCollection", dom.getDocumentElement().getNodeName());
        Assert.assertEquals(2, dom.getElementsByTagName("gs:pdsa").getLength());
    }
}


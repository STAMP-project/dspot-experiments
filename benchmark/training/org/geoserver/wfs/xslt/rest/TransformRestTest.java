/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.xslt.rest;


import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.rest.RestBaseController;
import org.geoserver.wfs.xslt.XSLTTestSupport;
import org.geoserver.wfs.xslt.config.TransformInfo;
import org.geoserver.wfs.xslt.config.TransformRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class TransformRestTest extends XSLTTestSupport {
    private XpathEngine xpath;

    private TransformRepository repository;

    @Test
    public void testListHTML() throws Exception {
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms.html"));
        // print(d);
        Assert.assertEquals("1", xpath.evaluate("count(//h:h2)", d));
        Assert.assertEquals("XSLT transformations:", xpath.evaluate("/h:html/h:body/h:h2", d));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/services/wfs/transforms/general.html"), xpath.evaluate("//h:li[h:a = \'general\']/h:a/@href", d));
    }

    @Test
    public void testListXML() throws Exception {
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms.xml"));
        // print(d);
        Assert.assertEquals("3", xpath.evaluate("count(//transform)", d));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/services/wfs/transforms/general.xml"), xpath.evaluate("//transform[name='general']/atom:link/@href", d));
    }

    @Test
    public void testGetTransformHTML() throws Exception {
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms/general.html"));
        // print(d);
        Assert.assertEquals("Source format: \"text/xml; subtype=gml/2.1.2\"", xpath.evaluate("//h:li[1]", d));
        Assert.assertEquals("Output format: \"text/html; subtype=xslt\"", xpath.evaluate("//h:li[2]", d));
        Assert.assertEquals("File extension: \"html\"", xpath.evaluate("//h:li[3]", d));
        Assert.assertEquals("XSLT transform: \"general.xslt\"", xpath.evaluate("//h:li[4]", d));
    }

    @Test
    public void testGetTransformXML() throws Exception {
        Document d = getAsDOM(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms/general.xml"));
        // print(d);
        Assert.assertEquals("text/xml; subtype=gml/2.1.2", xpath.evaluate("//sourceFormat", d));
        Assert.assertEquals("text/html; subtype=xslt", xpath.evaluate("//outputFormat", d));
        Assert.assertEquals("html", xpath.evaluate("//fileExtension", d));
        Assert.assertEquals("general.xslt", xpath.evaluate("//xslt", d));
    }

    @Test
    public void testPostXML() throws Exception {
        String xml = "<transform>\n" + (((((((("  <name>buildings</name>\n" + // 
        "  <sourceFormat>text/xml; subtype=gml/2.1.2</sourceFormat>\n") + // 
        "  <outputFormat>text/html</outputFormat>\n") + // 
        "  <fileExtension>html</fileExtension>\n") + // 
        "  <xslt>buildings.xslt</xslt>\n") + // 
        "  <featureType>\n") + // 
        "    <name>cite:Buildings</name>\n") + // 
        "  </featureType>\n") + "</transform>\n");
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms"), xml);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith((("" + (RestBaseController.ROOT_PATH)) + "/services/wfs/transforms/buildings")));
        TransformInfo info = repository.getTransformInfo("buildings");
        Assert.assertNotNull(info);
    }

    @Test
    public void testPostXSLT() throws Exception {
        String xslt = FileUtils.readFileToString(new File("src/test/resources/org/geoserver/wfs/xslt/general2.xslt"));
        // test for missing params
        MockHttpServletResponse response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms?name=general2"), xslt, "application/xslt+xml");
        Assert.assertEquals(400, response.getStatus());
        // now pass all
        response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms?name=general2&sourceFormat=gml&outputFormat=HTML&outputMimeType=text/html"), xslt, "application/xslt+xml");
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith((("" + (RestBaseController.ROOT_PATH)) + "/services/wfs/transforms/general2")));
        TransformInfo info = repository.getTransformInfo("general2");
        Assert.assertNotNull(info);
        Assert.assertEquals("gml", info.getSourceFormat());
        Assert.assertEquals("HTML", info.getOutputFormat());
        Assert.assertEquals("text/html", info.getOutputMimeType());
    }

    @Test
    public void testPutXML() throws Exception {
        // let's change the output format
        String xml = "<transform>\n" + (((("  <sourceFormat>text/xml; subtype=gml/2.1.2</sourceFormat>\n" + "  <outputFormat>text/html</outputFormat>\n") + "  <fileExtension>html</fileExtension>\n") + "  <xslt>general.xslt</xslt>\n") + "</transform>");
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms/general"), xml, "text/xml");
        Assert.assertEquals(200, response.getStatus());
        TransformInfo info = repository.getTransformInfo("general");
        Assert.assertEquals("text/html", info.getOutputFormat());
    }

    @Test
    public void testPutXSLT() throws Exception {
        String xslt = FileUtils.readFileToString(new File("src/test/resources/org/geoserver/wfs/xslt/general2.xslt"));
        MockHttpServletResponse response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms/general"), xslt, "application/xslt+xml");
        Assert.assertEquals(200, response.getStatus());
        TransformInfo info = repository.getTransformInfo("general");
        InputStream is = null;
        String actual = null;
        try {
            is = repository.getTransformSheet(info);
            actual = IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        Assert.assertEquals(xslt, actual);
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/services/wfs/transforms/general"));
        Assert.assertEquals(200, response.getStatus());
        TransformInfo info = repository.getTransformInfo("general");
        Assert.assertNull(info);
    }
}


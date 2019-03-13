/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.resources;


import Type.DIRECTORY;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.test.JSONAssert;
import org.apache.commons.lang3.SystemUtils;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resource.Type;
import org.geoserver.platform.resource.Resources;
import org.geoserver.rest.RestBaseController;
import org.geoserver.rest.util.IOUtils;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Niels Charlier
 */
public class ResourceControllerTest extends GeoServerSystemTestSupport {
    private final String STR_MY_TEST;

    private final String STR_MY_NEW_TEST;

    private final NamespaceContext NS_XML;

    private final NamespaceContext NS_HTML;

    private final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");

    private final DateFormat FORMAT_HEADER = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private Resource myRes;

    public ResourceControllerTest() {
        CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
        if (encoder.canEncode("??")) {
            STR_MY_TEST = "This is my test. ? ?";
        } else {
            STR_MY_TEST = "This is my test.";
        }
        if (encoder.canEncode("??")) {
            STR_MY_NEW_TEST = "This is my new test. ? ?";
        } else {
            STR_MY_NEW_TEST = "This is my new test.";
        }
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        FORMAT_HEADER.setTimeZone(TimeZone.getTimeZone("GMT"));
        Map<String, String> mapXML = new HashMap<>();
        mapXML.put("atom", "http://www.w3.org/2005/Atom");
        NS_XML = new SimpleNamespaceContext(mapXML);
        Map<String, String> mapHTML = new HashMap<>();
        mapHTML.put("x", "http://www.w3.org/1999/xhtml");
        NS_HTML = new SimpleNamespaceContext(mapHTML);
    }

    @Test
    public void testResource() throws Exception {
        String str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir/myres")).trim();
        Assert.assertEquals(STR_MY_TEST, str);
    }

    @Test
    public void testResourceMetadataXML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_XML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/mydir/myres?operation=mEtAdATa&format=xml"));
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("myres", "/ResourceMetadata/name", doc);
        XMLAssert.assertXpathEvaluatesTo("/mydir", "/ResourceMetadata/parent/path", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir"), "/ResourceMetadata/parent/atom:link/@href", doc);
        XMLAssert.assertXpathEvaluatesTo(FORMAT.format(myRes.lastmodified()), "/ResourceMetadata/lastModified", doc);
    }

    @Test
    public void testResourceMetadataJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/resource/mydir/myres?operation=metadata&format=json"));
        // print(json);
        String expected = ((((((((((("{\"ResourceMetadata\": {" + (((("  \"name\": \"myres\"," + "  \"parent\":   {") + "    \"path\": \"/mydir\",") + "    \"link\": {") + "       \"href\": \"http://localhost:8080/geoserver")) + (RestBaseController.ROOT_PATH)) + "/resource/mydir\",") + "       \"rel\": \"alternate\",                ") + "       \"type\": \"application/json\"") + "     }") + "   },") + "  \"lastModified\": \"") + (FORMAT.format(myRes.lastmodified()))) + "\",") + "  \"type\": \"resource\"") + "}}";
        JSONAssert.assertEquals(expected, ((JSONObject) (json)));
    }

    @Test
    public void testResourceMetadataWithResourceExtension() throws Exception {
        String str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.xml")).trim();
        Assert.assertEquals(STR_MY_TEST, str);
        str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.json")).trim();
        Assert.assertEquals(STR_MY_TEST, str);
        // format=xml should return XML regardless of extension
        XMLUnit.setXpathNamespaceContext(NS_XML);
        str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.xml?operation=mEtAdATa&format=xml"));
        Assert.assertTrue(str.startsWith("<ResourceMetadata"));
        str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.json?operation=mEtAdATa&format=xml"));
        Assert.assertTrue(str.startsWith("<ResourceMetadata"));
        // format=json should return JSON regardless of extension
        str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.xml?operation=metadata&format=json"));
        Assert.assertTrue(str.startsWith("{\"ResourceMetadata\""));
        str = getAsString(((RestBaseController.ROOT_PATH) + "/resource/mydir2/myres.json?operation=metadata&format=json"));
        Assert.assertTrue(str.startsWith("{\"ResourceMetadata\""));
    }

    @Test
    public void testResourceMetadataHTML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_HTML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/mydir/myres?operation=metadata&format=html"));
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("Name: 'myres'", "/x:html/x:body/x:ul/x:li[1]", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir"), "/x:html/x:body/x:ul/x:li[2]/x:a/@href", doc);
        XMLAssert.assertXpathEvaluatesTo("Type: resource", "/x:html/x:body/x:ul/x:li[3]", doc);
        XMLAssert.assertXpathEvaluatesTo(("Last modified: " + (new Date(myRes.lastmodified()).toString())), "/x:html/x:body/x:ul/x:li[4]", doc);
    }

    @Test
    public void testResourceHeaders() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir2/fake.png"));
        Assert.assertEquals(FORMAT_HEADER.format(getDataDirectory().get("/mydir2/fake.png").lastmodified()), response.getHeader("Last-Modified"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir2"), response.getHeader("Resource-Parent"));
        Assert.assertEquals("resource", response.getHeader("Resource-Type"));
        assertContentType("image/png", response);
    }

    @Test
    public void testResourceHead() throws Exception {
        MockHttpServletResponse response = headAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir2/fake.png"));
        Assert.assertEquals(FORMAT_HEADER.format(getDataDirectory().get("/mydir2/fake.png").lastmodified()), response.getHeader("Last-Modified"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir2"), response.getHeader("Resource-Parent"));
        Assert.assertEquals("resource", response.getHeader("Resource-Type"));
        assertContentType("image/png", response);
    }

    @Test
    public void testSpecialCharacterNames() throws Exception {
        // if the file system encoded the file with a ? we need to skip this test
        Assume.assumeTrue(((SystemUtils.IS_OS_WINDOWS) || ((getDataDirectory().get("po?zie").getType()) == (Type.UNDEFINED))));
        Assert.assertEquals(DIRECTORY, getDataDirectory().get("po?zie").getType());
        XMLUnit.setXpathNamespaceContext(NS_XML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/po%c3%abzie?format=xml"));
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/po%C3%ABzie/caf%C3%A9"), "/ResourceDirectory/children/child/atom:link/@href", doc);
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/po%c3%abzie/caf%c3%a9?format=xml"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("resource", response.getHeader("Resource-Type"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/po%C3%ABzie"), response.getHeader("Resource-Parent"));
    }

    @Test
    public void testDirectoryXML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_XML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/mydir?format=xml"));
        print(doc);
        XMLAssert.assertXpathEvaluatesTo("mydir", "/ResourceDirectory/name", doc);
        XMLAssert.assertXpathEvaluatesTo("/", "/ResourceDirectory/parent/path", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/"), "/ResourceDirectory/parent/atom:link/@href", doc);
        XMLAssert.assertXpathEvaluatesTo(FORMAT.format(myRes.parent().lastmodified()), "/ResourceDirectory/lastModified", doc);
        XMLAssert.assertXpathEvaluatesTo("myres", "/ResourceDirectory/children/child/name", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir/myres"), "/ResourceDirectory/children/child/atom:link/@href", doc);
    }

    @Test
    public void testDirectoryJSON() throws Exception {
        JSON json = getAsJSON(((RestBaseController.ROOT_PATH) + "/resource/mydir?format=json"));
        print(json);
        String expected = (((((((((((((((((((("{\"ResourceDirectory\": {" + (((("\"name\": \"mydir\"," + "\"parent\":   {") + "  \"path\": \"/\",") + "    \"link\":     {") + "      \"href\": \"http://localhost:8080/geoserver")) + (RestBaseController.ROOT_PATH)) + "/resource/\",") + "      \"rel\": \"alternate\",") + "      \"type\": \"application/json\"") + "  }") + "},") + "\"lastModified\": \"") + (FORMAT.format(myRes.parent().lastmodified()))) + "\",") + "  \"children\": {\"child\": [  {") + "    \"name\": \"myres\",") + "    \"link\":     {") + "      \"href\": \"http://localhost:8080/geoserver") + (RestBaseController.ROOT_PATH)) + "/resource/mydir/myres\",") + "      \"rel\": \"alternate\",") + "      \"type\": \"application/octet-stream\"") + "    }") + "  }]}") + "}}";
        JSONAssert.assertEquals(expected, ((JSONObject) (json)));
    }

    @Test
    public void testDirectoryHTML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_HTML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/mydir?format=html"));
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("Name: 'mydir'", "/x:html/x:body/x:ul/x:li[1]", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/"), "/x:html/x:body/x:ul/x:li[2]/x:a/@href", doc);
        XMLAssert.assertXpathEvaluatesTo(("Last modified: " + (new Date(myRes.parent().lastmodified()).toString())), "/x:html/x:body/x:ul/x:li[3]", doc);
        XMLAssert.assertXpathEvaluatesTo((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/mydir/myres"), "/x:html/x:body/x:ul/x:li[4]/x:ul/x:li/x:a/@href", doc);
    }

    @Test
    public void testDirectoryRootXML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_XML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource?format=xml"));
        XMLAssert.assertXpathEvaluatesTo("", "/ResourceDirectory/name", doc);
        XMLAssert.assertXpathEvaluatesTo("", "/ResourceDirectory/parent", doc);
    }

    @Test
    public void testDirectoryRootHTML() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_HTML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource?format=html"));
        XMLAssert.assertXpathEvaluatesTo("Name: ''", "/x:html/x:body/x:ul/x:li[1]", doc);
        XMLAssert.assertXpathEvaluatesTo("Parent: ", "/x:html/x:body/x:ul/x:li[2]", doc);
    }

    @Test
    public void testDirectoryHeaders() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir?format=xml"));
        Assert.assertEquals(FORMAT_HEADER.format(myRes.parent().lastmodified()), response.getHeader("Last-Modified"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/"), response.getHeader("Resource-Parent"));
        Assert.assertEquals("directory", response.getHeader("Resource-Type"));
        assertContentType("application/xml", response);
    }

    @Test
    public void testDirectoryHead() throws Exception {
        MockHttpServletResponse response = headAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir?format=xml"));
        Assert.assertEquals(FORMAT_HEADER.format(myRes.parent().lastmodified()), response.getHeader("Last-Modified"));
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/resource/"), response.getHeader("Resource-Parent"));
        Assert.assertEquals("directory", response.getHeader("Resource-Type"));
        assertContentType("application/xml", response);
    }

    @Test
    public void testDirectoryMimeTypes() throws Exception {
        XMLUnit.setXpathNamespaceContext(NS_XML);
        Document doc = getAsDOM(((RestBaseController.ROOT_PATH) + "/resource/mydir2?format=xml"));
        // print(doc);
        XMLAssert.assertXpathEvaluatesTo("image/png", "/ResourceDirectory/children/child[name='imagewithoutextension']/atom:link/@type", doc);
        XMLAssert.assertXpathEvaluatesTo("image/png", "/ResourceDirectory/children/child[name='fake.png']/atom:link/@type", doc);
    }

    @Test
    public void testUpload() throws Exception {
        put(((RestBaseController.ROOT_PATH) + "/resource/mydir/mynewres"), STR_MY_NEW_TEST);
        Resource newRes = getDataDirectory().get("/mydir/mynewres");
        try (InputStream is = newRes.in()) {
            Assert.assertEquals(STR_MY_NEW_TEST, IOUtils.toString(is));
        }
        newRes.delete();
    }

    @Test
    public void testCopy() throws Exception {
        put(((RestBaseController.ROOT_PATH) + "/resource/mydir/mynewres?operation=cOpY"), "/mydir/myres");
        Resource newRes = getDataDirectory().get("/mydir/mynewres");
        Assert.assertTrue(Resources.exists(myRes));
        Assert.assertTrue(Resources.exists(newRes));
        try (InputStream is = newRes.in()) {
            Assert.assertEquals(STR_MY_TEST, IOUtils.toString(is));
        }
        newRes.delete();
    }

    @Test
    public void testMove() throws Exception {
        put(((RestBaseController.ROOT_PATH) + "/resource/mydir/mynewres?operation=move"), "/mydir/myres");
        Resource newRes = getDataDirectory().get("/mydir/mynewres");
        Assert.assertFalse(Resources.exists(myRes));
        Assert.assertTrue(Resources.exists(newRes));
        try (InputStream is = newRes.in()) {
            Assert.assertEquals(STR_MY_TEST, IOUtils.toString(is));
        }
        newRes.renameTo(myRes);
    }

    @Test
    public void testMoveDirectory() throws Exception {
        put(((RestBaseController.ROOT_PATH) + "/resource/mydir/mynewdir?operation=move"), "/mydir");
        put(((RestBaseController.ROOT_PATH) + "/resource/mynewdir?operation=move"), "/mydir");
        Resource newDir = getDataDirectory().get("/mynewdir");
        Assert.assertTrue(Resources.exists(newDir));
        Assert.assertTrue(((newDir.getType()) == (Type.DIRECTORY)));
        Assert.assertFalse(Resources.exists(myRes));
        Assert.assertTrue(Resources.exists(getDataDirectory().get("/mynewdir/myres")));
        newDir.renameTo(getDataDirectory().get("/mydir"));
    }

    @Test
    public void testDelete() throws Exception {
        Resource newRes = getDataDirectory().get("/mydir/mynewres");
        Resources.copy(myRes, newRes);
        Assert.assertTrue(Resources.exists(newRes));
        deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir/mynewres"));
        Assert.assertFalse(Resources.exists(newRes));
    }

    @Test
    public void testErrorResponseCodes() throws Exception {
        MockHttpServletResponse response;
        // get resource that doesn't exist
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/doesntexist"));
        Assert.assertEquals(404, response.getStatus());
        // delete resource that doesn't exist
        response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/doesntexist"));
        Assert.assertEquals(404, response.getStatus());
        // upload to dir
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir"));
        Assert.assertEquals(405, response.getStatus());
        // copy dir
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mynewdir?operation=copy"), "/mydir", "text/plain");
        Assert.assertEquals(405, response.getStatus());
        // copy resource that doesn't exist
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mynewres?operation=copy"), "/doesntexist", "text/plain");
        Assert.assertEquals(404, response.getStatus());
        // move resource that doesn't exist
        response = putAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mynewres?operation=move"), "/doesntexist", "text/plain");
        Assert.assertEquals(404, response.getStatus());
        // post
        response = postAsServletResponse(((RestBaseController.ROOT_PATH) + "/resource/mydir"), "blabla");
        Assert.assertEquals(405, response.getStatus());
    }
}


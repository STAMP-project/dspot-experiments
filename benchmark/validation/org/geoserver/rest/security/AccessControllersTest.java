/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.security;


import CatalogMode.CHALLENGE;
import CatalogMode.HIDE;
import CatalogMode.MIXED;
import CatalogModeController.MODE_ELEMENT;
import CatalogModeController.XML_ROOT_ELEM;
import java.text.MessageFormat;
import net.sf.json.JSONObject;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static CatalogModeController.MODE_ELEMENT;
import static CatalogModeController.XML_ROOT_ELEM;


/**
 * Test for {@link DataAccessController},{@link ServiceAccessController} and {@link RestAccessController}
 *
 * @author christian
 */
@TestSetup(run = TestSetupFrequency.REPEAT)
public class AccessControllersTest extends SecurityRESTTestSupport {
    static final String BASE_URI = (RestBaseController.ROOT_PATH) + "/security/acl/";

    static final String DATA_URI = (AccessControllersTest.BASE_URI) + "layers";

    static final String DATA_URI_XML = (AccessControllersTest.DATA_URI) + ".xml";

    static final String DATA_URI_JSON = (AccessControllersTest.DATA_URI) + ".json";

    static final String SERVICE_URI = (AccessControllersTest.BASE_URI) + "services";

    static final String SERVICE_URI_XML = (AccessControllersTest.SERVICE_URI) + ".xml";

    static final String SERVICE_URI_JSON = (AccessControllersTest.SERVICE_URI) + ".json";

    static final String REST_URI = (AccessControllersTest.BASE_URI) + "rest";

    static final String REST_URI_XML = (AccessControllersTest.REST_URI) + ".xml";

    static final String REST_URI_JSON = (AccessControllersTest.REST_URI) + ".json";

    static final String CATALOG_URI = (AccessControllersTest.BASE_URI) + "catalog";

    static final String CATALOG_URI_XML = (AccessControllersTest.CATALOG_URI) + ".xml";

    static final String CATALOG_URI_JSON = (AccessControllersTest.CATALOG_URI) + ".json";

    private static final String TEST_ROLE1 = "TEST_ROLE1";

    private static final String TEST_ROLE2 = "TEST_ROLE2";

    private static final String TEST_ROLELIST = ((AccessControllersTest.TEST_ROLE1) + ",") + (AccessControllersTest.TEST_ROLE2);

    @Test
    public void testGet() throws Exception {
        String[][] layerRules = getDefaultLayerRules();
        JSONObject json = ((JSONObject) (getAsJSON(AccessControllersTest.DATA_URI_JSON)));
        // System.out.println(json.toString(1));
        checkJSONResponse(json, layerRules);
        Document dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        // print(dom);
        checkXMLResponse(dom, layerRules);
        String[][] serviceRules = getDefaultServiceRules();
        json = ((JSONObject) (getAsJSON(AccessControllersTest.SERVICE_URI_JSON)));
        checkJSONResponse(json, serviceRules);
        dom = getAsDOM(AccessControllersTest.SERVICE_URI_XML);
        checkXMLResponse(dom, serviceRules);
        String[][] restRules = getDefaultRestRules();
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, restRules);
        json = ((JSONObject) (getAsJSON(AccessControllersTest.REST_URI_JSON)));
        checkJSONResponse(json, restRules);
    }

    @Test
    public void testDelete() throws Exception {
        String[][] layerRules = getDefaultLayerRules();
        Assert.assertEquals(200, deleteAsServletResponse((((AccessControllersTest.DATA_URI) + "/") + (layerRules[0][0]))).getStatus());
        Assert.assertEquals(404, deleteAsServletResponse((((AccessControllersTest.DATA_URI) + "/") + (layerRules[0][0]))).getStatus());
        Assert.assertEquals(404, deleteAsServletResponse(((AccessControllersTest.SERVICE_URI) + "/wfs.getFeature")).getStatus());
        String[][] restRules = getDefaultRestRulesForDelete();
        Assert.assertEquals(200, deleteAsServletResponse((((AccessControllersTest.REST_URI) + "/") + (restRules[0][0]))).getStatus());
        Assert.assertEquals(404, deleteAsServletResponse((((AccessControllersTest.REST_URI) + "/") + (restRules[0][0]))).getStatus());
    }

    @Test
    public void testXMLPost() throws Exception {
        // layer rules
        String[][] rules = getDefaultLayerRules();
        String[][] toBeAdded = new String[][]{ rules[0], new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        Document dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        // add
        String[][] toBeAdded2 = new String[][]{ new String[]{ "ws.layer1.w", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };
        String[][] expected = new String[][]{ rules[0], rules[1], toBeAdded2[0], toBeAdded2[1] };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded2), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, expected);
        // service rules
        rules = getDefaultServiceRules();
        toBeAdded2 = new String[][]{ new String[]{ "ws.*", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws2.GetFeature", AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(toBeAdded2), "text/xml").getStatus());
        expected = new String[][]{ rules[0], toBeAdded2[0], toBeAdded2[1] };
        dom = getAsDOM(AccessControllersTest.SERVICE_URI_XML);
        checkXMLResponse(dom, expected);
        // check conflict
        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(toBeAdded2), "text/xml").getStatus());
        // REST rules
        rules = getDefaultRestRules();
        toBeAdded = new String[][]{ rules[0], new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, rules);
        // add
        toBeAdded2 = new String[][]{ new String[]{ "/myworkspace/**:PUT,POST", AccessControllersTest.TEST_ROLE1 }, new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };
        expected = new String[][]{ rules[0], rules[1], toBeAdded2[0], toBeAdded2[1] };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(toBeAdded2), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, expected);
    }

    @Test
    public void testJSONPost() throws Exception {
        // layer rules
        String[][] rules = getDefaultLayerRules();
        String[][] toBeAdded = new String[][]{ rules[0], new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(toBeAdded), "text/json").getStatus());
        // check if nothing changed
        JSONObject json = ((JSONObject) (getAsJSON(AccessControllersTest.DATA_URI_JSON)));
        checkJSONResponse(json, rules);
        // add
        String[][] toBeAdded2 = new String[][]{ new String[]{ "ws.layer1.w", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };
        String[][] expected = new String[][]{ rules[0], rules[1], toBeAdded2[0], toBeAdded2[1] };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(toBeAdded2), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.DATA_URI_JSON)));
        checkJSONResponse(json, expected);
        // service rules
        rules = getDefaultServiceRules();
        toBeAdded2 = new String[][]{ new String[]{ "ws.*", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws2.GetFeature", AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(toBeAdded2), "text/json").getStatus());
        expected = new String[][]{ rules[0], toBeAdded2[0], toBeAdded2[1] };
        json = ((JSONObject) (getAsJSON(AccessControllersTest.SERVICE_URI_JSON)));
        checkJSONResponse(json, expected);
        // check conflict
        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(toBeAdded2), "text/json").getStatus());
        // REST rules
        rules = getDefaultRestRules();
        toBeAdded = new String[][]{ rules[0], new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, postAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(toBeAdded), "text/json").getStatus());
        // check if nothing changed
        json = ((JSONObject) (getAsJSON(AccessControllersTest.REST_URI_JSON)));
        checkJSONResponse(json, rules);
        // add
        toBeAdded2 = new String[][]{ new String[]{ "/myworkspace/**:PUT,POST", AccessControllersTest.TEST_ROLE1 }, new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };
        expected = new String[][]{ rules[0], rules[1], toBeAdded2[0], toBeAdded2[1] };
        Assert.assertEquals(200, postAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(toBeAdded2), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.REST_URI_JSON)));
        checkJSONResponse(json, expected);
    }

    @Test
    public void testJSONPut() throws Exception {
        // layer rules
        String[][] rules = getDefaultLayerRules();
        String[][] toBeModified = new String[][]{ rules[0], new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(toBeModified), "text/json").getStatus());
        // check if nothing changed
        JSONObject json = ((JSONObject) (getAsJSON(AccessControllersTest.DATA_URI_JSON)));
        checkJSONResponse(json, rules);
        // modify
        String[][] toBeModified2 = new String[][]{ new String[]{ rules[0][0], AccessControllersTest.TEST_ROLE1 }, new String[]{ rules[1][0], AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(toBeModified2), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.DATA_URI_JSON)));
        checkJSONResponse(json, toBeModified2);
        // service rules
        rules = getDefaultServiceRules();
        toBeModified2 = new String[][]{ new String[]{ "ws.*", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws2.GetFeature", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(toBeModified2), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.SERVICE_URI_JSON)));
        checkJSONResponse(json, rules);
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(new String[][]{  }), "text/json").getStatus());
        // REST rules
        rules = getDefaultRestRules();
        toBeModified = new String[][]{ rules[0], new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(toBeModified), "text/json").getStatus());
        // check if nothing changed
        json = ((JSONObject) (getAsJSON(AccessControllersTest.REST_URI_JSON)));
        checkJSONResponse(json, rules);
        // modify
        toBeModified2 = new String[][]{ new String[]{ rules[0][0], AccessControllersTest.TEST_ROLE1 }, new String[]{ rules[1][0], AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(toBeModified2), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.REST_URI_JSON)));
        checkJSONResponse(json, toBeModified2);
    }

    @Test
    public void testXMLPut() throws Exception {
        // layer rules
        String[][] rules = getDefaultLayerRules();
        String[][] toBeModified = new String[][]{ rules[0], new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeModified), "text/xml").getStatus());
        // check if nothing changed
        Document dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        // modify
        String[][] toBeModified2 = new String[][]{ new String[]{ rules[0][0], AccessControllersTest.TEST_ROLE1 }, new String[]{ rules[1][0], AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeModified2), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, toBeModified2);
        // service rules
        rules = getDefaultServiceRules();
        toBeModified2 = new String[][]{ new String[]{ "ws.*", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws2.GetFeature", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(toBeModified2), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.SERVICE_URI_XML);
        checkXMLResponse(dom, rules);
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(new String[][]{  }), "text/xml").getStatus());
        // REST rules
        rules = getDefaultRestRules();
        toBeModified = new String[][]{ rules[0], new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(409, putAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(toBeModified), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, rules);
        // modify
        toBeModified2 = new String[][]{ new String[]{ rules[0][0], AccessControllersTest.TEST_ROLE1 }, new String[]{ rules[1][0], AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(toBeModified2), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, toBeModified2);
    }

    @Test
    public void testCatalogMode() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(AccessControllersTest.CATALOG_URI_JSON)));
        String mode = ((String) (json.get(MODE_ELEMENT)));
        Assert.assertEquals(HIDE.toString(), mode);
        Document dom = getAsDOM(AccessControllersTest.CATALOG_URI_XML);
        print(dom);
        Assert.assertEquals(XML_ROOT_ELEM, dom.getDocumentElement().getNodeName());
        NodeList nl = dom.getElementsByTagName(MODE_ELEMENT);
        Assert.assertEquals(1, nl.getLength());
        mode = nl.item(0).getTextContent();
        Assert.assertEquals(HIDE.toString(), mode);
        String jsonTemplate = ("\'{\'\"" + (MODE_ELEMENT)) + "\":\"{0}\"\'}\'";
        String xmlTemplate = (("<" + (XML_ROOT_ELEM)) + ">") + "\n";
        xmlTemplate += (" <" + (MODE_ELEMENT)) + ">{0}";
        xmlTemplate += (("</" + (MODE_ELEMENT)) + ">") + "\n";
        xmlTemplate += (("</" + (XML_ROOT_ELEM)) + ">") + "\n";
        Assert.assertEquals(404, putAsServletResponse(AccessControllersTest.CATALOG_URI_JSON, "{\"modexxxxx\": \"HIDE\"}", "text/json").getStatus());
        Assert.assertEquals(422, putAsServletResponse(AccessControllersTest.CATALOG_URI_JSON, MessageFormat.format(jsonTemplate, "ABC"), "text/json").getStatus());
        Assert.assertEquals(422, putAsServletResponse(AccessControllersTest.CATALOG_URI_XML, MessageFormat.format(xmlTemplate, "ABC"), "text/xml").getStatus());
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.CATALOG_URI_JSON, MessageFormat.format(jsonTemplate, MIXED.toString()), "text/json").getStatus());
        json = ((JSONObject) (getAsJSON(AccessControllersTest.CATALOG_URI_JSON)));
        mode = ((String) (json.get(MODE_ELEMENT)));
        Assert.assertEquals(MIXED.toString(), mode);
        Assert.assertEquals(200, putAsServletResponse(AccessControllersTest.CATALOG_URI_XML, MessageFormat.format(xmlTemplate, CHALLENGE.toString()), "text/xml").getStatus());
        dom = getAsDOM(AccessControllersTest.CATALOG_URI_XML);
        nl = dom.getElementsByTagName(MODE_ELEMENT);
        mode = nl.item(0).getTextContent();
        Assert.assertEquals(CHALLENGE.toString(), mode);
    }

    @Test
    public void testCatalogModeXXE() throws Exception {
        String resource = getClass().getResource("secret.txt").toExternalForm();
        String xml = ("<!DOCTYPE " + (XML_ROOT_ELEM)) + " [";
        xml += ("<!ELEMENT " + (XML_ROOT_ELEM)) + " ANY>";
        xml += ("<!ENTITY xxe SYSTEM \"" + resource) + "\">]>";
        xml += (("<" + (XML_ROOT_ELEM)) + ">") + "\n";
        xml += (" <" + (MODE_ELEMENT)) + ">&xxe;";
        xml += (("</" + (MODE_ELEMENT)) + ">") + "\n";
        xml += (("</" + (XML_ROOT_ELEM)) + ">") + "\n";
        MockHttpServletResponse resp = putAsServletResponse(AccessControllersTest.CATALOG_URI_XML, xml, "text/xml");
        Assert.assertEquals(400, resp.getStatus());
        Assert.assertThat(resp.getContentAsString(), CoreMatchers.not(CoreMatchers.containsString("HELLO WORLD")));
    }

    @Test
    public void testInvalidRules() throws Exception {
        // layer rules
        String[][] rules = getDefaultLayerRules();
        String[][] toBeAdded = new String[][]{ new String[]{ "ws.layer1.r.c", AccessControllersTest.TEST_ROLELIST } };
        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        Document dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        toBeAdded = new String[][]{ new String[]{ "ws.layer1.x", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        toBeAdded = new String[][]{ new String[]{ "*.layer1.r", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        toBeAdded = new String[][]{ new String[]{ "ws.layer1.a", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.DATA_URI_XML);
        checkXMLResponse(dom, rules);
        // services
        rules = getDefaultServiceRules();
        toBeAdded = new String[][]{ new String[]{ "ws.getMap.c", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.SERVICE_URI_XML);
        checkXMLResponse(dom, rules);
        toBeAdded = new String[][]{ new String[]{ "*.getMap", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.SERVICE_URI_XML);
        checkXMLResponse(dom, rules);
        rules = getDefaultRestRules();
        toBeAdded = new String[][]{ rules[0], new String[]{ "/myworkspace/**!!!GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(422, postAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(toBeAdded), "text/xml").getStatus());
        // check if nothing changed
        dom = getAsDOM(AccessControllersTest.REST_URI_XML);
        checkXMLResponse(dom, rules);
    }

    @Test
    public void testNotAuthorized() throws Exception {
        logout();
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.DATA_URI_XML).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.DATA_URI_JSON).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.SERVICE_URI_XML).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.SERVICE_URI_JSON).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.REST_URI_XML).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.REST_URI_JSON).getStatus());
        // layer rules
        String[][] dataRules = new String[][]{ new String[]{ "ws.layer1.w", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws.layer1.r", AccessControllersTest.TEST_ROLELIST } };
        String[][] serviceRules = new String[][]{ new String[]{ "ws.*", AccessControllersTest.TEST_ROLE1 }, new String[]{ "ws2.GetFeature", AccessControllersTest.TEST_ROLELIST } };
        String[][] restRules = new String[][]{ new String[]{ "/myworkspace/**:GET", AccessControllersTest.TEST_ROLELIST } };// conflict

        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(dataRules), "text/xml").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(dataRules), "text/json").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(serviceRules), "text/xml").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(serviceRules), "text/json").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(restRules), "text/xml").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(restRules), "text/json").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.DATA_URI_XML, createXMLBody(dataRules), "text/xml").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.DATA_URI_JSON, createJSONBody(dataRules), "text/json").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.SERVICE_URI_XML, createXMLBody(serviceRules), "text/xml").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.SERVICE_URI_JSON, createJSONBody(serviceRules), "text/json").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.REST_URI_XML, createXMLBody(restRules), "text/xml").getStatus());
        Assert.assertEquals(403, postAsServletResponse(AccessControllersTest.REST_URI_JSON, createJSONBody(restRules), "text/json").getStatus());
        Assert.assertEquals(403, deleteAsServletResponse(((AccessControllersTest.DATA_URI) + "/fakerule")).getStatus());
        Assert.assertEquals(403, deleteAsServletResponse(((AccessControllersTest.SERVICE_URI) + "/fakerule")).getStatus());
        Assert.assertEquals(403, deleteAsServletResponse(((AccessControllersTest.REST_URI) + "/fakerule")).getStatus());
        String jsonTemplate = ("{\"" + (MODE_ELEMENT)) + "\":\"MIXED\"}";
        String xmlTemplate = (("<" + (XML_ROOT_ELEM)) + ">") + "\n";
        xmlTemplate += (" <" + (MODE_ELEMENT)) + ">MIXED";
        xmlTemplate += (("</" + (MODE_ELEMENT)) + ">") + "\n";
        xmlTemplate += (("</" + (XML_ROOT_ELEM)) + ">") + "\n";
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.CATALOG_URI_XML).getStatus());
        Assert.assertEquals(403, getAsServletResponse(AccessControllersTest.CATALOG_URI_JSON).getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.CATALOG_URI_XML, xmlTemplate, "text/xml").getStatus());
        Assert.assertEquals(403, putAsServletResponse(AccessControllersTest.CATALOG_URI_JSON, jsonTemplate, "text/json").getStatus());
    }
}


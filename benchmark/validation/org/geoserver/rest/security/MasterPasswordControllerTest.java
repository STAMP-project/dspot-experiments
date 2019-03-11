/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.security;


import MasterPasswordController.MP_CURRENT_KEY;
import MasterPasswordController.XML_ROOT_ELEM;
import java.io.IOException;
import java.text.MessageFormat;
import net.sf.json.JSONObject;
import org.geoserver.rest.RestBaseController;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import static MasterPasswordController.MP_CURRENT_KEY;
import static MasterPasswordController.MP_NEW_KEY;
import static MasterPasswordController.XML_ROOT_ELEM;


/**
 * Test for {@link MasterPasswordController}
 *
 * @author christian
 */
public class MasterPasswordControllerTest extends SecurityRESTTestSupport {
    static final String MP_URI_JSON = (RestBaseController.ROOT_PATH) + "/security/masterpw.json";

    static final String MP_URI_XML = (RestBaseController.ROOT_PATH) + "/security/masterpw.xml";

    String xmlTemplate = (((((((((((((("<" + (XML_ROOT_ELEM)) + ">") + "<") + (MP_CURRENT_KEY)) + ">{0}</") + (MP_CURRENT_KEY)) + ">") + "<") + (MP_NEW_KEY)) + ">{1}</") + (MP_NEW_KEY)) + ">") + "</") + (XML_ROOT_ELEM)) + ">";

    String jsonTemplate = (((("{\"" + (MP_CURRENT_KEY)) + "\":\"%s\",") + "\"") + (MP_NEW_KEY)) + "\":\"%s\"}";

    @Test
    public void testGetAsXML() throws Exception {
        Document dom = getAsDOM(MasterPasswordControllerTest.MP_URI_XML, 200);
        Assert.assertEquals(XML_ROOT_ELEM, dom.getDocumentElement().getNodeName());
        Assert.assertEquals("geoserver", SecurityRESTTestSupport.xp.evaluate(((("/" + (XML_ROOT_ELEM)) + "/") + (MP_CURRENT_KEY)), dom));
    }

    @Test
    public void testGetAsXMLNotAuthorized() throws Exception {
        logout();
        Assert.assertEquals(403, getAsServletResponse(MasterPasswordControllerTest.MP_URI_XML).getStatus());
    }

    @Test
    public void testGetAsJSON() throws Exception {
        JSONObject json = ((JSONObject) (getAsJSON(MasterPasswordControllerTest.MP_URI_JSON)));
        String password = ((String) (json.get(MP_CURRENT_KEY)));
        Assert.assertEquals("geoserver", password);
    }

    @Test
    public void testUnallowedMethod() throws Exception {
        boolean failed = false;
        try {
            getSecurityManager().getMasterPasswordForREST();
        } catch (IOException ex) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testPutUnauthorized() throws Exception {
        logout();
        String body = MessageFormat.format(xmlTemplate, "geoserver", "abc");
        Assert.assertEquals(405, putAsServletResponse(MasterPasswordControllerTest.MP_URI_XML, body, "text/xml").getStatus());
    }

    @Test
    public void testPutInvalidNewPassword() throws Exception {
        String body = MessageFormat.format(xmlTemplate, "geoserver", "abc");
        Assert.assertEquals(422, putAsServletResponse(MasterPasswordControllerTest.MP_URI_XML, body, "text/xml").getStatus());
    }

    @Test
    public void testPutInvalidCurrentPassword() throws Exception {
        String body = MessageFormat.format(xmlTemplate, "geoserverXY", "geoserver1");
        Assert.assertEquals(422, putAsServletResponse(MasterPasswordControllerTest.MP_URI_XML, body, "text/xml").getStatus());
    }

    @Test
    public void testPutAsXML() throws Exception {
        String body = MessageFormat.format(xmlTemplate, "geoserver", "geoserver1");
        Assert.assertEquals(200, putAsServletResponse(MasterPasswordControllerTest.MP_URI_XML, body, "text/xml").getStatus());
        Assert.assertTrue(getSecurityManager().checkMasterPassword("geoserver1"));
        body = MessageFormat.format(xmlTemplate, "geoserver1", "geoserver");
        Assert.assertEquals(200, putAsServletResponse(MasterPasswordControllerTest.MP_URI_XML, body, "text/xml").getStatus());
        Assert.assertTrue(getSecurityManager().checkMasterPassword("geoserver"));
    }

    @Test
    public void testPutAsJSON() throws Exception {
        String body = String.format(jsonTemplate, "geoserver", "geoserver1");
        Assert.assertEquals(200, putAsServletResponse(MasterPasswordControllerTest.MP_URI_JSON, body, "text/json").getStatus());
        Assert.assertTrue(getSecurityManager().checkMasterPassword("geoserver1"));
        body = String.format(jsonTemplate, "geoserver1", "geoserver");
        Assert.assertEquals(200, putAsServletResponse(MasterPasswordControllerTest.MP_URI_JSON, body, "text/json").getStatus());
        Assert.assertTrue(getSecurityManager().checkMasterPassword("geoserver"));
    }
}


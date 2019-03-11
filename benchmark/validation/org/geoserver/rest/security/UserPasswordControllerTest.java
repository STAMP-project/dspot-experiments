/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.security;


import HttpStatus.BAD_REQUEST;
import HttpStatus.METHOD_NOT_ALLOWED;
import XMLUserGroupService.DEFAULT_NAME;
import java.text.MessageFormat;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.rest.RestBaseController;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static UserPasswordController.UP_NEW_PW;
import static UserPasswordController.XML_ROOT_ELEM;


/**
 * Test for {@link UserPasswordController}
 *
 * @author Emanuele Tajariol <etj at geo-solutions.it>
 */
public class UserPasswordControllerTest extends GeoServerSystemTestSupport {
    static final String UP_URI = (RestBaseController.ROOT_PATH) + "/security/self/password";

    static final String USERNAME = "restuser";

    static final String USERPW = "restpassword";

    protected static XpathEngine xp;

    String xmlTemplate = ((((((((("<" + (XML_ROOT_ELEM)) + ">") + "<") + (UP_NEW_PW)) + ">{0}</") + (UP_NEW_PW)) + ">") + "</") + (XML_ROOT_ELEM)) + ">";

    String xmlBadTemplate = ((((("<" + (XML_ROOT_ELEM)) + ">") + "<not_the_right_element>{0}</not_the_right_element>") + "</") + (XML_ROOT_ELEM)) + ">";

    @Test
    public void testGetAsAuthorized() throws Exception {
        login();
        Assert.assertEquals(METHOD_NOT_ALLOWED, HttpStatus.valueOf(getAsServletResponse(UserPasswordControllerTest.UP_URI).getStatus()));
    }

    @Test
    public void testGetAsNotAuthorized() throws Exception {
        logout();
        Assert.assertEquals(METHOD_NOT_ALLOWED, HttpStatus.valueOf(getAsServletResponse(UserPasswordControllerTest.UP_URI).getStatus()));
    }

    @Test
    public void testPutUnauthorized() throws Exception {
        logout();
        String body = MessageFormat.format(xmlTemplate, "new01");
        Assert.assertEquals(405, putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
    }

    @Test
    public void testPutInvalidNewPassword() throws Exception {
        login();
        String body = MessageFormat.format(xmlTemplate, "   ");
        Assert.assertEquals(BAD_REQUEST.value(), putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
    }

    @Test
    public void testPutInvalidElement() throws Exception {
        login();
        String body = MessageFormat.format(xmlBadTemplate, "newpw42");
        Assert.assertEquals(BAD_REQUEST.value(), putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
    }

    @Test
    public void testPutAsXML() throws Exception {
        login();
        String body = MessageFormat.format(xmlTemplate, "pw01");
        Assert.assertEquals(200, putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
    }

    @Test
    public void checkUpdatedPassword() throws Exception {
        GeoServerUserGroupService service = getSecurityManager().loadUserGroupService(DEFAULT_NAME);
        GeoServerUser user;
        login();
        // store proper starting encoding
        user = service.getUserByUsername(UserPasswordControllerTest.USERNAME);
        String originalPw = user.getPassword();
        String body = MessageFormat.format(xmlTemplate, "pw01");
        Assert.assertEquals(200, putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
        // check pw has been updated
        service.load();
        user = service.getUserByUsername(UserPasswordControllerTest.USERNAME);
        String pw1 = user.getPassword();
        Assert.assertNotEquals(originalPw, pw1);
        body = MessageFormat.format(xmlTemplate, "pw02");
        Assert.assertEquals(200, putAsServletResponse(UserPasswordControllerTest.UP_URI, body, "text/xml").getStatus());
        // check pw has been updated
        service.load();
        user = service.getUserByUsername(UserPasswordControllerTest.USERNAME);
        String pw2 = user.getPassword();
        Assert.assertNotEquals(originalPw, pw2);
        Assert.assertNotEquals(pw1, pw2);
    }
}


package org.javaee7.jaspictest.customprincipal;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that we can login from a public page (a page for which no security constraints have been set)
 * and that for this type of page the custom principal correctly arrives in a Servlet.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class CustomPrincipalPublicTest extends ArquillianBase {
    @Test
    public void testPublicPageLoggedin() throws IOException, SAXException {
        // JASPIC has to be able to authenticate a user when accessing a public (non-protected) resource.
        String response = getFromServerPath("public/servlet?doLogin=true");
        // Has to be logged-in with the right principal
        Assert.assertTrue("Username is not the expected one 'test'", response.contains("web username: test"));
        Assert.assertTrue("Username is correct, but the expected role 'architect' is not present.", response.contains("web user has role \"architect\": true"));
        Assert.assertTrue("Username and roles are correct, but principal type is not the expected custom type.", response.contains("isCustomPrincipal: true"));
    }
}


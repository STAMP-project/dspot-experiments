package org.javaee7.jaspic.basicauthentication;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that we can login from a public page (a page for which no security constraints have been set).
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class BasicAuthenticationPublicTest extends ArquillianBase {
    @Test
    public void testPublicPageNotLoggedin() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        // Not logged-in
        Assert.assertTrue(("Not authenticated, but a username other than null was encountered. " + "This is not correct."), response.contains("web username: null"));
        Assert.assertTrue(("Not authenticated, but the user seems to have the role \"architect\". " + "This is not correct."), response.contains("web user has role \"architect\": false"));
    }

    @Test
    public void testPublicPageLoggedin() throws IOException, SAXException {
        // JASPIC has to be able to authenticate a user when accessing a public (non-protected) resource.
        String response = getFromServerPath("public/servlet?doLogin=true");
        // Now has to be logged-in
        Assert.assertTrue(("User should have been authenticated and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue(("User should have been authenticated and given role \"architect\", " + " but does not appear to have this role"), response.contains("web user has role \"architect\": true"));
    }
}


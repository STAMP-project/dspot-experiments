package org.javaee7.jaspic.ejbpropagation;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that the established authenticated identity propagates correctly from the web layer to a "protected" EJB (an EJB
 * with declarative role checking).
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class ProtectedEJBPropagationTest extends ArquillianBase {
    @Test
    public void protectedServletCallingProtectedEJB() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet-protected-ejb?doLogin=true");
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        Assert.assertTrue(("User should have been authenticated in the web layer and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue("Web has user principal set, but EJB not.", response.contains("EJB username: test"));
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see that the
        // user has the role "architect".
        Assert.assertTrue(response.contains("web user has role \"architect\": true"));
        Assert.assertTrue("Web user principal has role \"architect\", but one in EJB doesn\'t.", response.contains("EJB user has role \"architect\": true"));
    }

    /**
     * A small variation on the testProtectedServletWithLoginCallingEJB that tests if for authentication that happened for
     * public resources the security context also propagates to EJB.
     */
    @Test
    public void publicServletCallingProtectedEJB() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet-protected-ejb?doLogin=true");
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        Assert.assertTrue(("User should have been authenticated in the web layer and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue("Web has user principal set, but EJB not.", response.contains("EJB username: test"));
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see that the
        // user has the role "architect".
        Assert.assertTrue(response.contains("web user has role \"architect\": true"));
        Assert.assertTrue("Web user principal has role \"architect\", but one in EJB doesn\'t.", response.contains("EJB user has role \"architect\": true"));
    }
}


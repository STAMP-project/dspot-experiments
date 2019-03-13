package org.javaee7.jaspic.registersession;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * Variant of the RegisterSessionCustomPrincipalTest, where it's tested
 * if the authenticated identity restored by the runtime correctly propagates
 * to EJB.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class RegisterSessionCustomPrincipalEJBPropagationTest extends ArquillianBase {
    @Test
    public void testRemembersSession() throws IOException, SAXException {
        // -------------------- Request 1 ---------------------------
        // Accessing protected page without login
        String response = getFromServerPath("protected/servlet");
        // Not logged-in thus should not be accessible.
        Assert.assertFalse(response.contains("This is a protected servlet"));
        // -------------------- Request 2 ---------------------------
        // We access the protected page again and now login
        response = getFromServerPath("protected/servlet?doLogin=true&customPrincipal=true");
        // Now has to be logged-in so page is accessible
        Assert.assertTrue(("Could not access protected page, but should be able to. " + "Did the container remember the previously set 'unauthenticated identity'?"), response.contains("This is a protected servlet"));
        // Check principal has right name and right type and roles are available
        checkAuthenticatedIdentity(response);
        // -------------------- Request 3 ---------------------------
        // JASPIC is normally stateless, but for this test the SAM uses the register session feature so now
        // we should be logged-in when doing a call without explicitly logging in again.
        response = getFromServerPath("protected/servlet?continueSession=true");
        // Logged-in thus should be accessible.
        Assert.assertTrue(("Could not access protected page, but should be able to. " + "Did the container not remember the authenticated identity via 'javax.servlet.http.registerSession'?"), response.contains("This is a protected servlet"));
        // Both the user name and roles/groups have to be restored
        // *** NOTE ***: The JASPIC 1.1 spec is NOT clear about remembering roles, but spec lead Ron Monzillo clarified that
        // this should indeed be the case. The next JASPIC revision of the spec will have to mention this explicitly.
        // Intuitively it should make sense though that the authenticated identity is fully restored and not partially,
        // but again the spec should make this clear to avoid ambiguity.
        checkAuthenticatedIdentity(response);
        // -------------------- Request 4 ---------------------------
        // The session should also be remembered and propagated to a public EJB
        response = getFromServerPath("public/servlet-public-ejb?continueSession=true");
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        Assert.assertTrue(("User should have been authenticated in the web layer and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue("Web has user principal set, but EJB not.", response.contains("EJB username: test"));
        // -------------------- Request 5 ---------------------------
        // The session should also be remembered and propagated to a protected EJB
        response = getFromServerPath("public/servlet-protected-ejb?continueSession=true");
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


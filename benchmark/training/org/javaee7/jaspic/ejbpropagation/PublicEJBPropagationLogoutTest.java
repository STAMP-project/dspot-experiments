package org.javaee7.jaspic.ejbpropagation;


import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that the established authenticated identity propagates correctly
 * from the web layer to a "public" EJB (an EJB without declarative role
 * checking) and that after logging out but still within the same request this
 * identity is cleared.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class PublicEJBPropagationLogoutTest extends ArquillianBase {
    @Test
    public void publicServletCallingPublicEJBThenLogout() {
        String response = getFromServerPath("public/servlet-public-ejb-logout?doLogin=true");
        System.out.println(response);
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the
        // same user name.
        Assert.assertTrue(("User should have been authenticated in the web layer and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue("Web has user principal set, but EJB not.", response.contains("EJB username: test"));
        // After logging out, both the web and EJB should no longer see the user
        // name
        Assert.assertFalse("Web module did not clear authenticated identity after logout", response.contains("web username after logout: test"));
        Assert.assertFalse("EJB did not clear authenticated identity after logout", response.contains("EJB username after logout: test"));
    }
}


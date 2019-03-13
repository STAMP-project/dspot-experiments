package org.javaee7.jaspic.ejbpropagation;


import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that the established authenticated identity propagates correctly from the web layer to a "public" EJB (an EJB
 * without declarative role checking).
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class PublicEJBPropagationTest extends ArquillianBase {
    @Test
    public void protectedServletCallingPublicEJB() {
        String response = getFromServerPath("protected/servlet-public-ejb?doLogin=true");
        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        Assert.assertTrue(("User should have been authenticated in the web layer and given name \"test\", " + " but does not appear to have this name"), response.contains("web username: test"));
        Assert.assertTrue("Web has user principal set, but EJB not.", response.contains("EJB username: test"));
    }
}


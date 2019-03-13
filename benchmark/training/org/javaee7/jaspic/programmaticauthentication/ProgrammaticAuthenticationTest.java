package org.javaee7.jaspic.programmaticauthentication;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that a call from a Servlet to HttpServletRequest#authenticate can result
 * in a successful authentication.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class ProgrammaticAuthenticationTest extends ArquillianBase {
    @Test
    public void testAuthenticate() throws IOException, SAXException {
        assertAuthenticated(getFromServerPath("public/authenticate"));
    }

    @Test
    public void testAuthenticateFailFirstOnce() throws IOException, SAXException {
        // Before authenticating successfully, call request.authenticate which
        // is known to fail (do nothing)
        assertAuthenticated(getFromServerPath("public/authenticate?failFirst=1"));
    }

    @Test
    public void testAuthenticateFailFirstTwice() throws IOException, SAXException {
        // Before authenticating successfully, call request.authenticate twice which
        // are both known to fail (do nothing)
        assertAuthenticated(getFromServerPath("public/authenticate?failFirst=2"));
    }
}


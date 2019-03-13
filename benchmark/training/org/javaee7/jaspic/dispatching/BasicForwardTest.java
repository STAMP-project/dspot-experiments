package org.javaee7.jaspic.dispatching;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * The basic forward test tests that a SAM is able to forward to a simple Servlet.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class BasicForwardTest extends ArquillianBase {
    @Test
    public void testBasicForwardViaPublicResource() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        Assert.assertTrue("Response did not contain output from public Servlet that SAM forwarded to.", response.contains("response from forwardedServlet"));
    }

    @Test
    public void testBasicForwardViaProtectedResource() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        Assert.assertTrue("Response did not contain output from protected Servlet that SAM forwarded to.", response.contains("response from forwardedServlet"));
    }
}


package org.javaee7.jaspictest.dispatching;


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
public class CDIForwardTest extends ArquillianBase {
    /**
     * Tests that the forwarded resource can utilize a CDI bean
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardViaPublicResource() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        Assert.assertTrue("Response did not contain output from public Servlet with CDI that SAM forwarded to.", response.contains("response from forwardedServlet - Called from CDI"));
    }

    /**
     * Tests that the forwarded resource can utilize a CDI bean
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardViaProtectedResource() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        Assert.assertTrue("Response did not contain output from protected Servlet with CDI that SAM forwarded to.", response.contains("response from forwardedServlet - Called from CDI"));
    }

    /**
     * Tests that the forwarded resource has the correct servlet path
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardWithRequestPublic() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        Assert.assertTrue("Servletpath reported by servlet request after forward from SAM not as expected.", response.contains("servletPath via Servlet - /forwardedServlet"));
    }

    /**
     * Tests that the forwarded resource has the correct servlet path
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardWithRequestProtected() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        Assert.assertTrue("Servletpath reported by servlet request after forward from SAM not as expected.", response.contains("servletPath via Servlet - /forwardedServlet"));
    }

    /**
     * Tests that the forwarded resource can utilize an injected HttpServletRequest and that
     * the value is correct.
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardWithRequestInjectPublic() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        Assert.assertTrue("Servletpath reported by servlet request after forward from SAM not as expected.", response.contains("servletPath via Servlet - /forwardedServlet"));
        Assert.assertTrue(("Response did not contain output from forwarded Servlet using CDI injected request. " + "Request appears not to be usable."), response.contains("servletPath via CDI"));
        Assert.assertTrue("Servletpath reported by injected request after forward from SAM not as expected.", response.contains("servletPath via CDI - /forwardedServlet"));
    }

    /**
     * Tests that the forwarded resource can utilize an injected HttpServletRequest and that
     * the value is correct.
     *
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     */
    @Test
    public void testCDIForwardWithRequestInjectProtected() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        Assert.assertTrue("Servletpath reported by servlet request after forward from SAM not as expected.", response.contains("servletPath via Servlet - /forwardedServlet"));
        Assert.assertTrue(("Response did not contain output from forwarded Servlet using CDI injected request. " + "Request appears not to be usable."), response.contains("servletPath via CDI"));
        Assert.assertTrue("Servletpath reported by injected request after forward from SAM not as expected.", response.contains("servletPath via CDI - /forwardedServlet"));
    }
}


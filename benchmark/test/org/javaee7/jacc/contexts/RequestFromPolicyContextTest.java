package org.javaee7.jacc.contexts;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests demonstrates how code can obtain a reference to the {@link HttpServletRequest} from the JACC
 * context.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class RequestFromPolicyContextTest extends ArquillianBase {
    /**
     * Tests that we are able to obtain a reference to the {@link HttpServletRequest} from a Servlet.
     */
    @Test
    public void testCanObtainRequestInServlet() throws IOException, SAXException {
        String response = getFromServerPath("requestServlet");
        Assert.assertTrue(response.contains("Obtained request from context."));
    }

    /**
     * Tests that we are able to obtain a reference to the {@link HttpServletRequest} from an EJB.
     */
    @Test
    public void testCanObtainRequestInEJB() throws IOException, SAXException {
        String response = getFromServerPath("requestServletEJB");
        Assert.assertTrue(response.contains("Obtained request from context."));
    }

    /**
     * Tests that the {@link HttpServletRequest} reference that we obtained from JACC in a Servlet actually
     * works by getting a request attribute and request parameter from it.
     */
    @Test
    public void testDataInServlet() throws IOException, SAXException {
        String response = getFromServerPath("requestServlet?jacc_test=true");
        Assert.assertTrue("Request scope attribute not present in request obtained from context in Servlet, but should have been", response.contains("Attribute present in request from context."));
        Assert.assertTrue("Request parameter not present in request obtained from context in Servlet, but should have been", response.contains("Request parameter present in request from context."));
    }

    /**
     * Tests that the {@link HttpServletRequest} reference that we obtained from JACC in an EJB actually
     * works by getting a request attribute and request parameter from it.
     */
    @Test
    public void testDataInEJB() throws IOException, SAXException {
        String response = getFromServerPath("requestServlet?jacc_test=true");
        Assert.assertTrue("Request scope attribute not present in request obtained from context in EJB, but should have been", response.contains("Attribute present in request from context."));
        Assert.assertTrue("Request parameter not present in request obtained from context in EJB, but should have been", response.contains("Request parameter present in request from context."));
    }
}


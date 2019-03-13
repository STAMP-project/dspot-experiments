package org.javaee7.jaspic.wrapping;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that the wrapped request and response a SAM puts into the MessageInfo structure reaches the Servlet that's
 * invoked as well as all filters executed before that.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class WrappingTest extends ArquillianBase {
    @Test
    public void testProgrammaticFilterRequestWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        Assert.assertTrue("Request wrapped by SAM did not arrive in programmatic Filter.", response.contains("programmatic filter request isWrapped: true"));
    }

    @Test
    public void testProgrammaticFilterResponseWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        Assert.assertTrue("Response wrapped by SAM did not arrive in programmatic Filter.", response.contains("programmatic filter response isWrapped: true"));
    }

    @Test
    public void testDeclaredFilterRequestWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        Assert.assertTrue("Request wrapped by SAM did not arrive in declared Filter.", response.contains("declared filter request isWrapped: true"));
    }

    @Test
    public void testDeclaredFilterResponseWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        Assert.assertTrue("Response wrapped by SAM did not arrive in declared Filter.", response.contains("declared filter response isWrapped: true"));
    }

    @Test
    public void testRequestWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        Assert.assertTrue("Request wrapped by SAM did not arrive in Servlet.", response.contains("servlet request isWrapped: true"));
    }

    @Test
    public void testResponseWrapping() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        Assert.assertTrue("Response wrapped by SAM did not arrive in Servlet.", response.contains("servlet response isWrapped: true"));
    }
}


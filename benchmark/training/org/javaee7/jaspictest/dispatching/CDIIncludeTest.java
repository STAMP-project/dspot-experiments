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
public class CDIIncludeTest extends ArquillianBase {
    @Test
    public void testCDIIncludeViaPublicResource() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet?dispatch=include");
        Assert.assertTrue("Response did not contain output from public Servlet with CDI that SAM included to.", response.contains("response from includedServlet - Called from CDI"));
        Assert.assertTrue("Response did not contain output from target Servlet after included one.", response.contains("Resource invoked"));
        Assert.assertTrue("Output from included Servlet with CDI and target Servlet in wrong order.", ((response.indexOf("response from includedServlet - Called from CDI")) < (response.indexOf("Resource invoked"))));
    }
}


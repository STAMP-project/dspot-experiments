package org.javaee7.jaspictest.dispatching;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * The JSF with CDI forward test tests that a SAM is able to forward to a plain JSF view.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class JSFForwardTest extends ArquillianBase {
    @Test
    public void testJSFForwardViaPublicResource() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet?tech=jsf");
        Assert.assertTrue("Response did not contain output from JSF view that SAM forwarded to.", response.contains("response from JSF forward"));
    }

    @Test
    public void testJSFForwardViaProtectedResource() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet?tech=jsf");
        Assert.assertTrue("Response did not contain output from JSF view that SAM forwarded to.", response.contains("response from JSF forward"));
    }
}


package org.javaee7.jaspictest.dispatching;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * The JSF with CDI forward test tests that a SAM is able to forward to a JSF view
 * that uses a CDI backing bean.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class JSFCDIForwardTest extends ArquillianBase {
    @Test
    public void testJSFwithCDIForwardViaPublicResource() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet?tech=jsfcdi");
        Assert.assertTrue("Response did not contain output from JSF view with CDI that SAM forwarded to.", response.contains("response from JSF forward - Called from CDI"));
    }

    @Test
    public void testJSFwithCDIForwardViaProtectedResource() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet?tech=jsfcdi");
        Assert.assertTrue("Response did not contain output from JSF view with CDI that SAM forwarded to.", response.contains("response from JSF forward - Called from CDI"));
    }
}


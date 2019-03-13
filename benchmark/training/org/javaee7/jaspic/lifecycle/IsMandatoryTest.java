package org.javaee7.jaspic.lifecycle;


import java.io.IOException;
import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that the "javax.security.auth.message.MessagePolicy.isMandatory" key
 * in the message info map is "true" for a protected resource, and not "true" for
 * a public resource.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class IsMandatoryTest extends ArquillianBase {
    @Test
    public void testPublicIsNonMandatory() throws IOException, SAXException {
        String response = getFromServerPath("public/servlet");
        Assert.assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Public resource invoked"));
        Assert.assertTrue("isMandatory should be false for public resource, but was not.", response.contains("isMandatory: false"));
    }

    @Test
    public void testProtectedIsMandatory() throws IOException, SAXException {
        String response = getFromServerPath("protected/servlet");
        Assert.assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Resource invoked"));
        Assert.assertTrue("isMandatory should be true for protected resource, but was not.", response.contains("isMandatory: true"));
    }
}


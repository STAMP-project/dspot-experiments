package org.javaee7.jaspictest.invoke;


import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that a SAM is able to obtain and call a CDI bean when the request is to a protected resource
 * (a resource for which security constraints have been set).
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class InvokeCDIBeanProtectedTest extends ArquillianBase {
    @Test
    public void protectedInvokeCDIFromValidateRequest() {
        String response = getFromServerPath("protected/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for validateRequest for protected resource. (note: this is not required by the spec)", response.contains("validateRequest: Called from CDI"));
    }

    @Test
    public void protectedInvokeCDIFromCleanSubject() {
        String response = getFromServerPath("protected/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for cleanSubject for protected resource. (note: this is not required by the spec)", response.contains("cleanSubject: Called from CDI"));
    }

    @Test
    public void protectedInvokeCDIFromSecureResponse() {
        String response = getFromServerPath("protected/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for secureResponse for protected resource. (note: this is not required by the spec)", response.contains("secureResponse: Called from CDI"));
    }
}


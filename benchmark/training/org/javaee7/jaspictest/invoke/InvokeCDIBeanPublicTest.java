package org.javaee7.jaspictest.invoke;


import org.javaee7.jaspic.common.ArquillianBase;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that a SAM is able to obtain and call a CDI bean when the request is to a public resource
 * (a resource for which no security constraints have been set).
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class InvokeCDIBeanPublicTest extends ArquillianBase {
    @Test
    public void publicInvokeCDIFromValidateRequest() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for validateRequest for public resource. (note: this is not required by the spec)", response.contains("validateRequest: Called from CDI"));
    }

    @Test
    public void publicInvokeCDIFromCleanSubject() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for cleanSubject for public resource. (note: this is not required by the spec)", response.contains("cleanSubject: Called from CDI"));
    }

    @Test
    public void publicInvokeCDIFromSecureResponse() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean for secureResponse for public resource. (note: this is not required by the spec)", response.contains("secureResponse: Called from CDI"));
    }

    @Test
    public void publicInvokeCDIUseInjectedRequestFromValidateRequest() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean using an inject request for validateRequest for public resource. (note: this is not required by the spec)", response.contains("validateRequest: Called from CDI via injected request"));
    }

    @Test
    public void publicInvokeCDIUseInjectedRequestFromCleanSubject() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean using an inject request for cleanSubject for public resource. (note: this is not required by the spec)", response.contains("cleanSubject: Called from CDI via injected request"));
    }

    @Test
    public void publicInvokeCDIUseInjectedRequestFromSecureResponse() {
        String response = getFromServerPath("public/servlet?tech=cdi");
        Assert.assertTrue("Response did not contain output from CDI bean using an inject request for secureResponse for public resource. (note: this is not required by the spec)", response.contains("secureResponse: Called from CDI via injected request"));
    }
}


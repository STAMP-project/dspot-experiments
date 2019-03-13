package org.javaee7.jaxrs.security.declarative;


import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import java.io.IOException;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    @ArquillianResource
    private URL base;

    private static final String WEBAPP_SRC = "src/main/webapp";

    private WebClient webClient;

    private DefaultCredentialsProvider correctCreds = new DefaultCredentialsProvider();

    private DefaultCredentialsProvider incorrectCreds = new DefaultCredentialsProvider();

    @Test
    public void testGetWithCorrectCredentials() throws IOException, SAXException {
        webClient.setCredentialsProvider(correctCreds);
        TextPage page = webClient.getPage(((base) + "webresources/myresource"));
        Assert.assertTrue(page.getContent().contains("get"));
    }

    @Test
    public void testGetSubResourceWithCorrectCredentials() throws IOException, SAXException {
        webClient.setCredentialsProvider(correctCreds);
        TextPage page = webClient.getPage(((base) + "webresources/myresource/1"));
        Assert.assertTrue(page.getContent().contains("get1"));
    }

    @Test
    public void testGetWithIncorrectCredentials() throws IOException, SAXException {
        webClient.setCredentialsProvider(incorrectCreds);
        try {
            webClient.getPage(((base) + "webresources/myresource"));
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertEquals(401, e.getStatusCode());
            return;
        }
        Assert.fail("GET can be called with incorrect credentials");
    }

    @Test
    public void testPost() throws IOException, SAXException {
        webClient.setCredentialsProvider(correctCreds);
        try {
            WebRequest postRequest = new WebRequest(toUrlUnsafe(((base) + "webresources/myresource")), POST);
            postRequest.setRequestBody("name=myname");
            webClient.getPage(postRequest);
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertEquals(403, e.getStatusCode());
            return;
        }
        // All methods are excluded except for GET
        Assert.fail("POST is not authorized and can still be called");
    }

    @Test
    public void testPut() throws IOException, SAXException {
        webClient.setCredentialsProvider(correctCreds);
        try {
            WebRequest postRequest = new WebRequest(toUrlUnsafe(((base) + "webresources/myresource")), PUT);
            postRequest.setRequestBody("name=myname");
            webClient.getPage(postRequest);
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertEquals(403, e.getStatusCode());
            return;
        }
        // All methods are excluded except for GET
        Assert.fail("PUT is not authorized and can still be called");
    }
}


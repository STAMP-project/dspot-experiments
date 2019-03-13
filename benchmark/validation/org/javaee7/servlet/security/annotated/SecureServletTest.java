package org.javaee7.servlet.security.annotated;


import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class SecureServletTest {
    @ArquillianResource
    private URL base;

    private DefaultCredentialsProvider correctCreds = new DefaultCredentialsProvider();

    private DefaultCredentialsProvider incorrectCreds = new DefaultCredentialsProvider();

    private WebClient webClient;

    @Test
    public void testGetWithCorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        HtmlPage page = webClient.getPage(((base) + "/SecureServlet"));
        Assert.assertEquals("Servlet Security Annotated - Basic Auth with File-base Realm", page.getTitleText());
    }

    @Test
    public void testGetWithIncorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(incorrectCreds);
        try {
            webClient.getPage(((base) + "/SecureServlet"));
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(401, e.getStatusCode());
            return;
        }
        Assert.fail("/SecureServlet could be accessed without proper security credentials");
    }

    @Test
    public void testPostWithCorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        WebRequest request = new WebRequest(new URL(((base) + "/SecureServlet")), POST);
        HtmlPage page = webClient.getPage(request);
        Assert.assertEquals("Servlet Security Annotated - Basic Auth with File-base Realm", page.getTitleText());
    }

    @Test
    public void testPostWithIncorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(incorrectCreds);
        WebRequest request = new WebRequest(new URL(((base) + "/SecureServlet")), POST);
        try {
            webClient.getPage(request);
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(401, e.getStatusCode());
            return;
        }
        Assert.fail("/SecureServlet could be accessed without proper security credentials");
    }
}


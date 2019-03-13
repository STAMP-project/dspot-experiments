package org.javaee7.servlet.security.basicauth.omission;


import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
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
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    private WebClient webClient;

    private DefaultCredentialsProvider correctCreds = new DefaultCredentialsProvider();

    private DefaultCredentialsProvider incorrectCreds = new DefaultCredentialsProvider();

    @Test
    public void testGetWithCorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        TextPage page = webClient.getPage(((base) + "/SecureServlet"));
        Assert.assertEquals("my GET", page.getContent());
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
    public void testPostWithNoCredentials() throws Exception {
        WebRequest request = new WebRequest(new URL(((base) + "SecureServlet")), POST);
        TextPage page = webClient.getPage(request);
        Assert.assertEquals("my POST", page.getContent());
    }

    @Test
    public void testPostWithCorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        WebRequest request = new WebRequest(new URL(((base) + "SecureServlet")), POST);
        TextPage page = webClient.getPage(request);
        Assert.assertEquals("my POST", page.getContent());
    }

    @Test
    public void testPostWithIncorrectCredentials() throws Exception {
        webClient.setCredentialsProvider(incorrectCreds);
        WebRequest request = new WebRequest(new URL(((base) + "SecureServlet")), POST);
        TextPage page = webClient.getPage(request);
        Assert.assertEquals("my POST", page.getContent());
    }
}


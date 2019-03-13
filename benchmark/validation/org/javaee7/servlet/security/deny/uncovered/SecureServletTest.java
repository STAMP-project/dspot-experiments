package org.javaee7.servlet.security.deny.uncovered;


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
    @ArquillianResource
    private URL base;

    DefaultCredentialsProvider correctCreds = new DefaultCredentialsProvider();

    DefaultCredentialsProvider incorrectCreds = new DefaultCredentialsProvider();

    WebClient webClient;

    @Test
    public void testGetMethod() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        TextPage page = webClient.getPage(((base) + "/SecureServlet"));
        Assert.assertEquals("my GET", page.getContent());
    }

    @Test
    public void testPostMethod() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        WebRequest request = new WebRequest(new URL(((base) + "SecureServlet")), POST);
        try {
            TextPage p = webClient.getPage(request);
            System.out.println(p.getContent());
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(403, e.getStatusCode());
            return;
        }
        Assert.fail("POST method could be called even with deny-uncovered-http-methods");
    }

    @Test
    public void testPutMethod() throws Exception {
        webClient.setCredentialsProvider(correctCreds);
        WebRequest request = new WebRequest(new URL(((base) + "SecureServlet")), PUT);
        System.out.println("\n\n**** After request");
        try {
            TextPage p = webClient.getPage(request);
            System.out.println(p.getContent());
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(403, e.getStatusCode());
            return;
        }
        Assert.fail("PUT method could be called even with deny-unocvered-http-methods");
    }
}


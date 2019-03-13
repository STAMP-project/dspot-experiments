package org.javaee7.servlet.programmatic.login;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
public class LoginServletTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    @Test
    public void testUnauthenticatedRequest() throws IOException, SAXException {
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(((base) + "/LoginServlet"));
        String responseText = page.asText();
        System.out.println((("testUnauthenticatedRequest:\n" + responseText) + "\n"));
        Assert.assertTrue(responseText.contains("isUserInRole?false"));
        Assert.assertTrue(responseText.contains("getRemoteUser?null"));
        Assert.assertTrue(responseText.contains("getUserPrincipal?null"));
        Assert.assertTrue(responseText.contains("getAuthType?null"));
    }

    @Test
    public void testAuthenticatedRequest() throws IOException, SAXException {
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(((base) + "/LoginServlet?user=u1&password=p1"));
        String responseText = page.asText();
        System.out.println((("testAuthenticatedRequest:\n" + responseText) + "\n"));
        Assert.assertTrue(responseText.contains("isUserInRole?true"));
        Assert.assertTrue(responseText.contains("getRemoteUser?u1"));
        Assert.assertTrue(responseText.contains("getUserPrincipal?u1"));
        Assert.assertTrue(responseText.contains("getAuthType?BASIC"));
    }
}


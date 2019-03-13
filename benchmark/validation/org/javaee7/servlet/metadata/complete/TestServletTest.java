package org.javaee7.servlet.metadata.complete;


import com.gargoylesoftware.htmlunit.HttpMethod;
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
public class TestServletTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    WebClient webClient;

    @Test
    public void testGet() throws IOException, SAXException {
        TextPage page = webClient.getPage(((base) + "TestServlet"));
        Assert.assertEquals("my GET", page.getContent());
    }

    @Test
    public void testPost() throws IOException, SAXException {
        WebRequest request = new WebRequest(new URL(((base) + "TestServlet")), HttpMethod.POST);
        TextPage page = webClient.getPage(request);
        Assert.assertEquals("my POST", page.getContent());
    }
}


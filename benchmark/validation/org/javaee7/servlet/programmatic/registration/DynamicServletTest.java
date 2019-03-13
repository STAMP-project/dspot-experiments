package org.javaee7.servlet.programmatic.registration;


import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
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
 * @author OrelGenya
 */
@RunWith(Arquillian.class)
public class DynamicServletTest {
    @ArquillianResource
    private URL base;

    WebClient webClient;

    @Test
    public void testChildServlet() throws IOException, SAXException {
        TextPage page = webClient.getPage(((base) + "dynamic"));
        Assert.assertEquals("dynamic GET", page.getContent());
    }
}


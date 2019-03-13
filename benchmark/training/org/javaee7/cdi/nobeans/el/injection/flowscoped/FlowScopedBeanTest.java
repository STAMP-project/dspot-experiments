package org.javaee7.cdi.nobeans.el.injection.flowscoped;


import com.gargoylesoftware.htmlunit.WebClient;
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
public class FlowScopedBeanTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    @Test
    public void checkRenderedPage() throws Exception {
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(((base) + "/faces/myflow/index.xhtml"));
        Assert.assertNotNull(page);
        assert page.asText().contains("Hello there!");
    }
}


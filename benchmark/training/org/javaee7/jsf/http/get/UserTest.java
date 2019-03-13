package org.javaee7.jsf.http.get;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
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
public class UserTest {
    @ArquillianResource
    private URL base;

    WebClient webClient;

    private static final String WEBAPP_SRC = "src/main/webapp";

    HtmlPage page;

    @Test
    public void testLink() throws IOException {
        HtmlAnchor anchor = ((HtmlAnchor) (page.getElementById("link1")));
        Assert.assertTrue(anchor.getHrefAttribute().contains("faces/login.xhtml"));
        Assert.assertEquals("Login1", anchor.asText());
        HtmlPage output = anchor.click();
        Assert.assertEquals("HTTP GET (Login)", output.getTitleText());
    }

    @Test
    public void testLinkWithParam() throws IOException {
        HtmlAnchor anchor = ((HtmlAnchor) (page.getElementById("link2")));
        Assert.assertTrue(anchor.getHrefAttribute().contains("faces/login.xhtml"));
        Assert.assertTrue(anchor.getHrefAttribute().contains("?name=Jack"));
        Assert.assertEquals("Login2", anchor.asText());
        HtmlPage output = anchor.click();
        Assert.assertEquals("HTTP GET (Login)", output.getTitleText());
    }

    @Test
    public void testLinkWithPreProcessParams() {
        HtmlAnchor anchor = ((HtmlAnchor) (page.getElementById("link3")));
        Assert.assertEquals("Login3", anchor.asText());
        Assert.assertTrue(anchor.getHrefAttribute().contains("faces/index2.xhtml"));
        Assert.assertTrue(anchor.getHrefAttribute().contains("?name=Jack"));
    }

    @Test
    public void testButton() throws IOException {
        HtmlButtonInput button = ((HtmlButtonInput) (page.getElementById("button1")));
        Assert.assertEquals("Login4", button.asText());
        HtmlPage output = button.click();
        Assert.assertEquals("HTTP GET (Login)", output.getTitleText());
    }
}


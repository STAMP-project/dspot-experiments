package jenkins.security;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import static FrameOptionsPageDecorator.enabled;


public class FrameOptionsPageDecoratorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void defaultHeaderPresent() throws IOException, SAXException {
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo("");
        Assert.assertEquals("Expected different X-Frame-Options value", FrameOptionsPageDecoratorTest.getFrameOptionsFromResponse(page.getWebResponse()), "sameorigin");
    }

    @Test
    public void testDisabledFrameOptions() throws IOException, SAXException {
        enabled = false;
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo("");
        Assert.assertNull("Expected X-Frame-Options unset", FrameOptionsPageDecoratorTest.getFrameOptionsFromResponse(page.getWebResponse()));
    }
}


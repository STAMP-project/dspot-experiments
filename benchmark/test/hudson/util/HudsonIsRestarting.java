package hudson.util;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class HudsonIsRestarting {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-55062")
    public void withPrefix() throws Exception {
        j.jenkins.servletContext.setAttribute("app", new HudsonIsRestarting());
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);// this is a failure page already

        wc.getOptions().setJavaScriptEnabled(false);
        Page p = wc.goTo("", "text/html");
        Assert.assertTrue(p.isHtmlPage());
        String body = p.getWebResponse().getContentAsString();
        Assert.assertThat(body, CoreMatchers.containsString("data-resurl=\""));
        Assert.assertThat(body, CoreMatchers.containsString("data-rooturl=\""));
        Assert.assertThat(body, CoreMatchers.containsString("resURL=\""));
    }
}


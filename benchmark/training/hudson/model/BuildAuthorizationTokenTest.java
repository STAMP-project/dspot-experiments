package hudson.model;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class BuildAuthorizationTokenTest {
    @Rule
    public JenkinsRule jr = new JenkinsRule();

    private static final String token = "whatever";

    @Test
    public void triggerJobWithTokenShouldSucceedWithPost() throws Exception {
        FreeStyleProject project = createFreestyleProjectWithToken();
        JenkinsRule.WebClient wc = jr.createWebClient();
        HtmlPage page = wc.getPage(wc.addCrumb(new com.gargoylesoftware.htmlunit.WebRequest(new URL(jr.getURL(), (((project.getUrl()) + "build?delay=0&token=") + (BuildAuthorizationTokenTest.token))), HttpMethod.POST)));
        jr.waitUntilNoActivity();
        Assert.assertThat("the project should have been built", project.getBuilds(), hasSize(1));
    }

    @Test
    public void triggerJobWithTokenShouldSucceedWithGet() throws Exception {
        FreeStyleProject project = createFreestyleProjectWithToken();
        JenkinsRule.WebClient wc = jr.createWebClient();
        HtmlPage page = wc.getPage(new com.gargoylesoftware.htmlunit.WebRequest(new URL(jr.getURL(), (((project.getUrl()) + "build?delay=0&token=") + (BuildAuthorizationTokenTest.token))), HttpMethod.GET));
        jr.waitUntilNoActivity();
        Assert.assertThat("the project should have been built", project.getBuilds(), hasSize(1));
    }

    @Test
    public void triggerJobsWithoutTokenShouldFail() throws Exception {
        FreeStyleProject project = jr.createFreeStyleProject();
        JenkinsRule.WebClient wc = jr.createWebClient();
        try {
            HtmlPage page = wc.getPage(wc.addCrumb(new com.gargoylesoftware.htmlunit.WebRequest(new URL(jr.getURL(), ((project.getUrl()) + "build?delay=0")), HttpMethod.POST)));
            Assert.fail("should not reach here as anonymous does not have Item.BUILD and token is not set");
        } catch (FailingHttpStatusCodeException fex) {
            Assert.assertThat("Should fail with access denined", fex.getStatusCode(), CoreMatchers.is(403));
        }
    }
}


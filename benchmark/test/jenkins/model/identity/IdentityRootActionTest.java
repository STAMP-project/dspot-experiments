package jenkins.model.identity;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.ExtensionList;
import hudson.model.UnprotectedRootAction;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class IdentityRootActionTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void ui() throws Exception {
        HtmlPage p = r.createWebClient().goTo("instance-identity");
        Assert.assertThat(p.getElementById("fingerprint").getTextContent(), Matchers.containsString(ExtensionList.lookup(UnprotectedRootAction.class).get(IdentityRootAction.class).getFingerprint()));
    }
}


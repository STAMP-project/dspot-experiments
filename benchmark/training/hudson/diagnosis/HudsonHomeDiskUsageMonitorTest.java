package hudson.diagnosis;


import Jenkins.ADMINISTER;
import Jenkins.READ;
import JenkinsRule.DummySecurityRealm;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import hudson.model.User;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class HudsonHomeDiskUsageMonitorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void flow() throws Exception {
        // manually activate this
        HudsonHomeDiskUsageMonitor mon = HudsonHomeDiskUsageMonitor.get();
        mon.activated = true;
        // clicking yes should take us to somewhere
        j.submit(getForm(mon), "yes");
        Assert.assertTrue(mon.isEnabled());
        // now dismiss
        // submit(getForm(mon),"no"); TODO: figure out why this test is fragile
        mon.doAct("no");
        Assert.assertFalse(mon.isEnabled());
        // and make sure it's gone
        try {
            Assert.fail(((getForm(mon)) + " shouldn't be there"));
        } catch (ElementNotFoundException e) {
            // as expected
        }
    }

    @Issue("SECURITY-371")
    @Test
    public void noAccessForNonAdmin() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        JenkinsRule.WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        // TODO: Use MockAuthorizationStrategy in later versions
        JenkinsRule.DummySecurityRealm realm = j.createDummySecurityRealm();
        realm.addGroups("administrator", "admins");
        realm.addGroups("bob", "users");
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(ADMINISTER, "admins");
        auth.add(READ, "users");
        j.jenkins.setAuthorizationStrategy(auth);
        User bob = User.getById("bob", true);
        User administrator = User.getById("administrator", true);
        WebRequest request = new WebRequest(new URL(((wc.getContextPath()) + "administrativeMonitor/hudsonHomeIsFull/act")), HttpMethod.POST);
        NameValuePair param = new NameValuePair("no", "true");
        request.setRequestParameters(Collections.singletonList(param));
        HudsonHomeDiskUsageMonitor mon = HudsonHomeDiskUsageMonitor.get();
        wc.withBasicApiToken(bob);
        Page p = wc.getPage(request);
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, p.getWebResponse().getStatusCode());
        Assert.assertTrue(mon.isEnabled());
        WebRequest requestReadOnly = new WebRequest(new URL(((wc.getContextPath()) + "administrativeMonitor/hudsonHomeIsFull")), HttpMethod.GET);
        p = wc.getPage(requestReadOnly);
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, p.getWebResponse().getStatusCode());
        wc.withBasicApiToken(administrator);
        p = wc.getPage(request);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, p.getWebResponse().getStatusCode());
        Assert.assertFalse(mon.isEnabled());
    }
}


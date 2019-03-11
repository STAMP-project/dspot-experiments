package jenkins.bugs;


import hudson.security.HudsonPrivateSecurityRealm;
import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class Jenkins41511Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void configRoundTrip() throws Exception {
        Jenkins.getInstance().setSecurityRealm(new HudsonPrivateSecurityRealm(true, false, null));
        j.submit(j.createWebClient().goTo("configureSecurity").getFormByName("config"));
    }
}


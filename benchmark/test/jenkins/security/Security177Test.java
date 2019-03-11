package jenkins.security;


import java.net.URL;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Issue("SECURITY-177")
public class Security177Test {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void nosniff() throws Exception {
        WebClient wc = jenkins.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        URL u = jenkins.getURL();
        verifyNoSniff(wc.getPage(new URL(u, "adjuncts/507db12b/nosuch/adjunct.js")));
        verifyNoSniff(wc.getPage(new URL(u, "no-such-page")));
        verifyNoSniff(wc.getPage(new URL(u, "images/title.svg")));
        verifyNoSniff(wc.getPage(u));
    }
}


package jenkins.model;


import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class JenkinsLocationConfigurationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Makes sure the use of "localhost" in the Hudson URL reports a warning.
     */
    @Test
    public void localhostWarning() throws Exception {
        HtmlPage p = j.createWebClient().goTo("configure");
        HtmlInput url = p.getFormByName("config").getInputByName("_.url");
        url.setValueAttribute("http://localhost:1234/");
        MatcherAssert.assertThat(p.getDocumentElement().getTextContent(), Matchers.containsString("instead of localhost"));
    }
}


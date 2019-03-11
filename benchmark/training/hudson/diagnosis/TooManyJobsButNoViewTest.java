package hudson.diagnosis;


import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.ListView;
import java.net.URL;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static TooManyJobsButNoView.THRESHOLD;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class TooManyJobsButNoViewTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    private TooManyJobsButNoView mon;

    /**
     * Shouldn't be active at the beginning
     */
    @Test
    public void initialState() throws Exception {
        verifyNoForm();
    }

    /**
     * Once we have enough jobs, it should kick in
     */
    @Test
    public void activated() throws Exception {
        for (int i = 0; i <= (THRESHOLD); i++)
            r.createFreeStyleProject();

        HtmlPage p = r.createWebClient().goTo("manage");
        HtmlForm f = p.getFormByName(mon.id);
        Assert.assertNotNull(f);
        // this should take us to the new view page
        URL url = r.submit(f, "yes").getUrl();
        Assert.assertTrue(url.toExternalForm(), url.toExternalForm().endsWith("/newView"));
        // since we didn't create a view, if we go back, we should see the warning again
        p = r.createWebClient().goTo("manage");
        Assert.assertNotNull(p.getFormByName(mon.id));
        // once we create a view, the message should disappear
        r.jenkins.addView(new ListView("test"));
        verifyNoForm();
    }
}


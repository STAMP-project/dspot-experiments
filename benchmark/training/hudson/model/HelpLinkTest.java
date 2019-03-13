package hudson.model;


import JenkinsRule.WebClient;
import WebResponseListener.StatusListener;
import com.gargoylesoftware.htmlunit.WebResponseListener;
import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Click all the help links and make sure they resolve to some text, not 404.
 *
 * @author Kohsuke Kawaguchi
 */
/* Excluding test to be able to ship 2.0 beta 1
Jenkins confirms that this test is now taking 45mins to complete.

The problem appears to be the following. When a help link is clicked, the execution hangs at the following point:

"Executing negative(hudson.model.HelpLinkTest)@1" prio=5 tid=0x1 nid=NA waiting
java.lang.Thread.State: WAITING
at java.lang.Object.wait(Object.java:-1)
at com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManagerImpl.waitForJobs(JavaScriptJobManagerImpl.java:200)
at com.gargoylesoftware.htmlunit.WebClient.waitForBackgroundJavaScript(WebClient.java:1843)
at com.gargoylesoftware.htmlunit.WebClientUtil.waitForJSExec(WebClientUtil.java:57)
at com.gargoylesoftware.htmlunit.WebClientUtil.waitForJSExec(WebClientUtil.java:46)
at com.gargoylesoftware.htmlunit.html.HtmlElementUtil.click(HtmlElementUtil.java:61)
at hudson.model.HelpLinkTest.clickAllHelpLinks(HelpLinkTest.java:70)
at hudson.model.HelpLinkTest.clickAllHelpLinks(HelpLinkTest.java:61)
at hudson.model.HelpLinkTest.negative(HelpLinkTest.java:106)

In debugger, I can see that JavaScriptJobManagerImpl.waitForJobs is looping through yet each time getJobCount()>0
because there's always some window.setTimeout activities that appear to be scheduled. Common ones are:

window.setTimeout(  function () {
thisConfig.trackSectionVisibility();
}, 500)

window.setTimeout(  function () {
return __method.apply(__method, args);
}, 10)

window.setTimeout(  function () {
oSelf._printBuffer();
}, 100)

getJobCount() never appears to return 0, so the method blocks until the time out of 10 secs is reached.
Multiply that by 50 or so help links to click and now you see why each test takes 10mins.

Maybe this is related to the scrollspy changes?
 */
@Ignore
public class HelpLinkTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void systemConfig() throws Exception {
        clickAllHelpLinks(j.createWebClient().goTo("configure"));
    }

    @Test
    public void freestyleConfig() throws Exception {
        clickAllHelpLinks(j.createFreeStyleProject());
    }

    @Test
    public void mavenConfig() throws Exception {
        clickAllHelpLinks(j.jenkins.createProject(MavenModuleSet.class, "mms"));
    }

    @Test
    public void matrixConfig() throws Exception {
        clickAllHelpLinks(j.jenkins.createProject(MatrixProject.class, "mp"));
    }

    public static class HelpNotFoundBuilder extends Publisher {
        public static final class DescriptorImpl extends BuildStepDescriptor {
            public boolean isApplicable(Class jobType) {
                return true;
            }

            @Override
            public String getHelpFile() {
                return "no-such-file/exists";
            }
        }

        public BuildStepMonitor getRequiredMonitorService() {
            return BuildStepMonitor.BUILD;
        }
    }

    /**
     * Make sure that this test is meaningful.
     * Intentionally put 404 and verify that it's detected.
     */
    @Test
    public void negative() throws Exception {
        HelpLinkTest.HelpNotFoundBuilder.DescriptorImpl d = new HelpLinkTest.HelpNotFoundBuilder.DescriptorImpl();
        Publisher.all().add(d);
        try {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getPublishersList().add(new HelpLinkTest.HelpNotFoundBuilder());
            JenkinsRule.WebClient webclient = j.createWebClient();
            WebResponseListener.StatusListener statusListener = new WebResponseListener.StatusListener(404);
            webclient.addWebResponseListener(statusListener);
            clickAllHelpLinks(webclient, p);
            statusListener.assertHasResponses();
            String contentAsString = statusListener.getResponses().get(0).getContentAsString();
            Assert.assertTrue(contentAsString.contains(d.getHelpFile()));
        } finally {
            Publisher.all().remove(d);
        }
    }
}


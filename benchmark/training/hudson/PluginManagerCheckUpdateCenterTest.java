package hudson;


import hudson.model.DownloadService;
import hudson.model.RootAction;
import hudson.model.UpdateSiteTest;
import hudson.util.HttpResponses;
import hudson.util.Retrier;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import static PluginManager.CHECK_UPDATE_ATTEMPTS;


public class PluginManagerCheckUpdateCenterTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule();

    /**
     * Check if the page contains the right message after checking an update site with an url that returns a 502 error code.
     *
     * @throws Exception
     * 		If there are errors managing the web elements.
     */
    @Test
    public void updateSiteReturn502Test() throws Exception {
        checkUpdateSite(((Jenkins.get().getRootUrl()) + "updateSite502/getJson"), "IOException: Server returned HTTP response code: 502 for URL", false);
    }

    /**
     * Check if the page contains the right message after checking an update site with an url that returns a wrong json.
     *
     * @throws Exception
     * 		If there are errors managing the web elements.
     */
    @Test
    public void updateSiteWrongJsonTest() throws Exception {
        checkUpdateSite(((Jenkins.get().getRootUrl()) + "updateSiteWrongJson/getJson"), "JSONException: Unquotted string 'wrongjson'", false);
    }

    /**
     * Check if the page contains the right message after checking an update site that returns a well defined json.
     *
     * @throws Exception
     * 		If there are errors managing the web elements.
     */
    @Test
    public void updateSiteRightJsonTest() throws Exception {
        // Save the actual value to leave it so, when the test finish, just in case it is needed for other tests
        boolean oldValueSignatureCheck = DownloadService.signatureCheck;
        try {
            // Avoid CertPathValidatorException: Algorithm constraints check failed on signature algorithm: MD5withRSA
            DownloadService.signatureCheck = false;
            // Have to end in update-center.json or it fails. See UpdateSite#getMetadataUrlForDownloadable
            checkUpdateSite(((Jenkins.get().getRootUrl()) + "updateSiteRightJson/update-center.json"), "", true);
        } finally {
            DownloadService.signatureCheck = oldValueSignatureCheck;
        }
    }

    /**
     * Checks if the message to activate the warnings is written in the log when the log level is higher than WARNING
     * and the attempts higher than 1.
     *
     * @throws Exception
     * 		See {@link #updateSiteWrongJsonTest()}
     */
    @Test
    public void changeLogLevelInLog() throws Exception {
        Logger pmLogger = Logger.getLogger(PluginManager.class.getName());
        Logger rLogger = Logger.getLogger(Retrier.class.getName());
        // save current level (to avoid interfering other tests)
        Level pmLevel = pmLogger.getLevel();
        Level rLevel = rLogger.getLevel();
        try {
            // set level to record
            pmLogger.setLevel(Level.SEVERE);
            rLogger.setLevel(Level.SEVERE);
            // check with more than 1 attempt and level > WARNING
            CHECK_UPDATE_ATTEMPTS = 2;
            updateSiteWrongJsonTest();
            // the messages has been recorded in the log
            Assert.assertThat(logging, LoggerRule.recorded(CoreMatchers.is(Messages.PluginManager_UpdateSiteChangeLogLevel(Retrier.class.getName()))));
        } finally {
            // restore level
            pmLogger.setLevel(pmLevel);
            rLogger.setLevel(rLevel);
        }
    }

    @TestExtension("updateSiteReturn502Test")
    public static final class FailingWith502UpdateCenterAction implements RootAction {
        @Override
        public String getIconFileName() {
            return "gear2.png";
        }

        @Override
        public String getDisplayName() {
            return "Update Site returning 502";
        }

        @Override
        public String getUrlName() {
            return "updateSite502";
        }

        public HttpResponse doGetJson(StaplerRequest request) {
            return HttpResponses.error(502, "Gateway error");
        }
    }

    @TestExtension({ "updateSiteWrongJsonTest", "changeLogLevelInLog" })
    public static final class FailingWithWrongJsonUpdateCenterAction implements RootAction {
        @Override
        public String getIconFileName() {
            return "gear2.png";
        }

        @Override
        public String getDisplayName() {
            return "Update Site returning wrong json";
        }

        @Override
        public String getUrlName() {
            return "updateSiteWrongJson";
        }

        public void doGetJson(StaplerRequest request, StaplerResponse response) throws IOException {
            response.setContentType("text/json");
            response.setStatus(200);
            response.getWriter().append("{wrongjson}");
        }
    }

    @TestExtension("updateSiteRightJsonTest")
    public static final class ReturnRightJsonUpdateCenterAction implements RootAction {
        @Override
        public String getIconFileName() {
            return "gear2.png";
        }

        @Override
        public String getDisplayName() {
            return "Update Site returning right json";
        }

        @Override
        public String getUrlName() {
            return "updateSiteRightJson";
        }

        // The url has to end in update-center.json. See: UpdateSite#getMetadataUrlForDownloadable
        public void doDynamic(StaplerRequest staplerRequest, StaplerResponse staplerResponse) throws IOException, ServletException {
            staplerResponse.setContentType("text/json");
            staplerResponse.setStatus(200);
            staplerResponse.serveFile(staplerRequest, UpdateSiteTest.class.getResource("update-center.json"));
        }
    }
}


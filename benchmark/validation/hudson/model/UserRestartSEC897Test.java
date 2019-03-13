package hudson.model;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import hudson.FilePath;
import java.net.URL;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;


// TODO after the security fix, it could be merged inside UserRestartTest
public class UserRestartSEC897Test {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    public void legacyConfigMoveCannotEscapeUserFolder() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                rr.j.jenkins.setSecurityRealm(rr.j.createDummySecurityRealm());
                Assert.assertThat(rr.j.jenkins.isUseSecurity(), IsEqual.equalTo(true));
                // in order to create the folder "users"
                User.getById("admin", true).save();
                {
                    // attempt with ".."
                    JenkinsRule.WebClient wc = rr.j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("..", "any-password"));
                    wc.getPage(request);
                }
                {
                    // attempt with "../users/.."
                    JenkinsRule.WebClient wc = rr.j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("../users/..", "any-password"));
                    wc.getPage(request);
                }
                // security is still active
                Assert.assertThat(rr.j.jenkins.isUseSecurity(), IsEqual.equalTo(true));
                // but, the config file was moved
                FilePath rootPath = rr.j.jenkins.getRootPath();
                Assert.assertThat(rootPath.child("config.xml").exists(), IsEqual.equalTo(true));
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Assert.assertThat(rr.j.jenkins.isUseSecurity(), IsEqual.equalTo(true));
                FilePath rootPath = rr.j.jenkins.getRootPath();
                Assert.assertThat(rootPath.child("config.xml").exists(), IsEqual.equalTo(true));
            }
        });
    }
}


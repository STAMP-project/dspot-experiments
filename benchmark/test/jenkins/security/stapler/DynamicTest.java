package jenkins.security.stapler;


import JenkinsRule.WebClient;
import hudson.model.UnprotectedRootAction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.StaplerRequest;


@Issue("SECURITY-400")
public class DynamicTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testRequestsDispatchedToEligibleDynamic() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        Stream.of("whatever", "displayName", "iconFileName", "urlName", "response1", "response2").forEach(( url) -> {
            try {
                Assert.assertThat(wc.goTo(("root/" + url)).getWebResponse().getContentAsString(), CoreMatchers.containsString(url));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @TestExtension
    public static class Root implements UnprotectedRootAction {
        @CheckForNull
        @Override
        public String getIconFileName() {
            return null;
        }

        @CheckForNull
        @Override
        public String getDisplayName() {
            return null;
        }

        @StaplerNotDispatchable
        public HttpResponse getResponse1() {
            return null;
        }

        @StaplerNotDispatchable
        public HttpResponse doResponse2() {
            return null;
        }

        public void doDynamic(StaplerRequest req) {
            throw HttpResponses.errorWithoutStack(200, req.getRestOfPath());
        }

        @CheckForNull
        @Override
        public String getUrlName() {
            return "root";
        }
    }
}


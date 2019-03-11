package hudson.model;


import AuthorizationStrategy.UNSECURED;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.security.ACL;
import hudson.security.AuthorizationStrategy;
import hudson.security.SecurityRealm;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;


public class DescriptorVisibilityFilterTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logger = new LoggerRule();

    @Test
    @Issue("JENKINS-40545")
    public void jenkins40545() throws Exception {
        logger.record("hudson.ExpressionFactory2$JexlExpression", Level.WARNING);
        logger.record("hudson.model.DescriptorVisibilityFilter", Level.WARNING);
        logger.capture(10);
        HtmlPage page = j.createWebClient().goTo("jenkins40545");
        Assert.assertThat(logger.getRecords(), Matchers.not(Matchers.emptyIterable()));
        for (LogRecord record : logger.getRecords()) {
            String message = record.getMessage();
            Assert.assertThat(message, Matchers.allOf(Matchers.containsString("Descriptor list is null for context 'class hudson.model.DescriptorVisibilityFilterTest$Jenkins40545'"), Matchers.containsString("DescriptorVisibilityFilterTest/Jenkins40545/index.jelly"), Matchers.not(Matchers.endsWith("NullPointerException"))));
        }
        Assert.assertThat(page.getWebResponse().getContentAsString(), Matchers.containsString("descriptors found: ."));// No output written from expression

    }

    @Test
    @Issue("JENKINS-49044")
    public void securityRealmAndAuthStrategyHidden() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(UNSECURED);
        HtmlPage page = j.createWebClient().goTo("configureSecurity");
        String response = page.getWebResponse().getContentAsString();
        Assert.assertThat(response, Matchers.not(Matchers.containsString("TestSecurityRealm")));
        Assert.assertThat(response, Matchers.not(Matchers.containsString("TestAuthStrategy")));
    }

    public static final class TestSecurityRealm extends SecurityRealm {
        @Override
        public SecurityComponents createSecurityComponents() {
            return null;
        }

        @TestExtension
        public static final class DescriptorImpl extends Descriptor<SecurityRealm> {
            @Nonnull
            @Override
            public String getDisplayName() {
                return "TestSecurityRealm";
            }
        }

        @TestExtension
        public static final class HideDescriptor extends DescriptorVisibilityFilter {
            @Override
            public boolean filter(@CheckForNull
            Object context, @Nonnull
            Descriptor descriptor) {
                return !(descriptor instanceof DescriptorVisibilityFilterTest.TestSecurityRealm.DescriptorImpl);
            }
        }
    }

    public static final class TestAuthStrategy extends AuthorizationStrategy {
        @Nonnull
        @Override
        public ACL getRootACL() {
            return null;
        }

        @Nonnull
        @Override
        public Collection<String> getGroups() {
            return null;
        }

        @TestExtension
        public static final class DescriptorImpl extends Descriptor<AuthorizationStrategy> {
            @Nonnull
            @Override
            public String getDisplayName() {
                return "TestAuthStrategy";
            }
        }

        @TestExtension
        public static final class HideDescriptor extends DescriptorVisibilityFilter {
            @Override
            public boolean filter(@CheckForNull
            Object context, @Nonnull
            Descriptor descriptor) {
                return !(descriptor instanceof DescriptorVisibilityFilterTest.TestAuthStrategy.DescriptorImpl);
            }
        }
    }

    @TestExtension("jenkins40545")
    public static final class Jenkins40545 implements UnprotectedRootAction {
        @Override
        public String getIconFileName() {
            return "notepad.png";
        }

        @Override
        public String getDisplayName() {
            return "jenkins40545";
        }

        @Override
        public String getUrlName() {
            return "jenkins40545";
        }
    }
}


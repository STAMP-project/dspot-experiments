package jenkins.scm;


import hudson.model.AbstractBuild.AbstractBuildExecution;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class SCMCheckoutStrategyTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void configRoundtrip1() throws Exception {
        Assert.assertEquals(1, SCMCheckoutStrategyDescriptor.all().size());
        FreeStyleProject p = j.createFreeStyleProject();
        Assert.assertFalse(pageHasUI(p));// no configuration UI because there's only one option

    }

    /**
     * This should show the UI.
     */
    @Test
    public void configRoundtrip2() throws Exception {
        Assert.assertEquals(2, SCMCheckoutStrategyDescriptor.all().size());
        FreeStyleProject p = j.createFreeStyleProject();
        System.out.println(SCMCheckoutStrategyDescriptor.all());
        SCMCheckoutStrategyTest.TestSCMCheckoutStrategy before = new SCMCheckoutStrategyTest.TestSCMCheckoutStrategy();
        p.setScmCheckoutStrategy(before);
        j.configRoundtrip(((Item) (p)));
        SCMCheckoutStrategy after = p.getScmCheckoutStrategy();
        Assert.assertNotSame(before, after);
        Assert.assertSame(before.getClass(), after.getClass());
        Assert.assertTrue(pageHasUI(p));
    }

    @SuppressWarnings("rawtypes")
    public static class TestSCMCheckoutStrategy extends SCMCheckoutStrategy {
        @DataBoundConstructor
        public TestSCMCheckoutStrategy() {
        }

        @Override
        public void checkout(AbstractBuildExecution execution) throws IOException, InterruptedException {
            execution.getListener().getLogger().println("Hello!");
            super.checkout(execution);
        }

        @TestExtension("configRoundtrip2")
        public static class DescriptorImpl extends SCMCheckoutStrategyDescriptor {
            @Override
            public boolean isApplicable(AbstractProject project) {
                return true;
            }
        }
    }
}


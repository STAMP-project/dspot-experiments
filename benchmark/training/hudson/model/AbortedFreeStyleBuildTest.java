package hudson.model;


import Result.ABORTED;
import Result.FAILURE;
import hudson.Launcher;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.TestBuildWrapper;
import org.jvnet.hudson.test.TestBuilder;


public class AbortedFreeStyleBuildTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-8054")
    public void buildWrapperSeesAbortedStatus() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        TestBuildWrapper wrapper = new TestBuildWrapper();
        project.getBuildWrappersList().add(wrapper);
        project.getBuildersList().add(new AbortedFreeStyleBuildTest.AbortingBuilder());
        Run build = project.scheduleBuild2(0).get();
        Assert.assertEquals(ABORTED, build.getResult());
        Assert.assertEquals(ABORTED, wrapper.buildResultInTearDown);
    }

    @Test
    @Issue("JENKINS-9203")
    public void interruptAsFailure() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        TestBuildWrapper wrapper = new TestBuildWrapper();
        project.getBuildWrappersList().add(wrapper);
        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                Executor.currentExecutor().interrupt(FAILURE);
                throw new InterruptedException();
            }
        });
        Run build = project.scheduleBuild2(0).get();
        Assert.assertEquals(FAILURE, build.getResult());
        Assert.assertEquals(FAILURE, wrapper.buildResultInTearDown);
    }

    private static class AbortingBuilder extends TestBuilder {
        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            throw new InterruptedException();
        }
    }
}


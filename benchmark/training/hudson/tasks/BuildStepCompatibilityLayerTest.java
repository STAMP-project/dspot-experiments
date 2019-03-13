package hudson.tasks;


import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.mockito.Mockito;


public class BuildStepCompatibilityLayerTest {
    @Issue("JENKINS-18734")
    @Test(expected = AbstractMethodError.class)
    public void testPerformExpectAbstractMethodError() throws IOException, InterruptedException {
        FreeStyleBuild mock = Mockito.mock(FreeStyleBuild.class, Mockito.CALLS_REAL_METHODS);
        BuildStepCompatibilityLayer bscl = new BuildStepCompatibilityLayer() {};
        bscl.perform(mock, null, null);
    }

    @Issue("JENKINS-18734")
    @Test
    public void testPerform() throws IOException, InterruptedException {
        FreeStyleBuild mock = Mockito.mock(FreeStyleBuild.class, Mockito.CALLS_REAL_METHODS);
        BuildStepCompatibilityLayer bscl = new BuildStepCompatibilityLayer() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                return true;
            }
        };
        Assert.assertTrue(bscl.perform(mock, null, null));
    }
}


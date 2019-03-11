package jenkins.util;


import hudson.slaves.DumbSlave;
import java.io.IOException;
import jenkins.security.MasterToSlaveCallable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class JenkinsJVMRealTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void isJenkinsJVM() throws Throwable {
        Assert.assertThat(new JenkinsJVMRealTest.IsJenkinsJVM().call(), Matchers.is(true));
        DumbSlave slave = JenkinsJVMRealTest.j.createOnlineSlave();
        Assert.assertThat(slave.getChannel().call(new JenkinsJVMRealTest.IsJenkinsJVM()), Matchers.is(false));
    }

    public static class IsJenkinsJVM extends MasterToSlaveCallable<Boolean, IOException> {
        @Override
        public Boolean call() throws IOException {
            return JenkinsJVM.isJenkinsJVM();
        }
    }
}


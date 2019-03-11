package hudson.model;


import Jenkins.READ;
import JenkinsRule.WebClient;
import Queue.Executable;
import Result.FAILURE;
import hudson.Launcher;
import hudson.remoting.VirtualChannel;
import hudson.slaves.DumbSlave;
import hudson.tasks.Builder;
import hudson.util.OneShotEvent;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import jenkins.model.InterruptedBuildAction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.TestExtension;


public class ExecutorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-4756")
    public void whenAnExecutorDiesHardANewExecutorTakesItsPlace() throws Exception {
        j.jenkins.setNumExecutors(1);
        Computer c = j.jenkins.toComputer();
        Executor e = getExecutorByNumber(c, 0);
        j.jenkins.getQueue().schedule(new QueueTest.TestTask(new AtomicInteger()) {
            @Override
            public Executable createExecutable() throws IOException {
                throw new IllegalStateException("oops");
            }
        }, 0);
        while (e.isActive()) {
            Thread.sleep(10);
        } 
        waitUntilExecutorSizeIs(c, 1);
        Assert.assertNotNull(getExecutorByNumber(c, 0));
    }

    /**
     * Makes sure that the cause of interruption is properly recorded.
     */
    @Test
    public void abortCause() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        Future<FreeStyleBuild> r = ExecutorTest.startBlockingBuild(p);
        User johnny = User.get("Johnny");
        // test the merge semantics
        p.getLastBuild().getExecutor().interrupt(FAILURE, new jenkins.model.CauseOfInterruption.UserInterruption(johnny), new jenkins.model.CauseOfInterruption.UserInterruption(johnny));
        FreeStyleBuild b = r.get();
        // make sure this information is recorded
        Assert.assertEquals(b.getResult(), FAILURE);
        InterruptedBuildAction iba = b.getAction(InterruptedBuildAction.class);
        Assert.assertEquals(1, iba.getCauses().size());
        Assert.assertEquals(getUser(), johnny);
        // make sure it shows up in the log
        Assert.assertTrue(b.getLog().contains(johnny.getId()));
    }

    @Test
    public void disconnectCause() throws Exception {
        DumbSlave slave = j.createOnlineSlave();
        FreeStyleProject p = j.createFreeStyleProject();
        p.setAssignedNode(slave);
        Future<FreeStyleBuild> r = ExecutorTest.startBlockingBuild(p);
        User johnny = User.get("Johnny");
        p.getLastBuild().getBuiltOn().toComputer().disconnect(new hudson.slaves.OfflineCause.UserCause(johnny, "Taking offline to break your build"));
        FreeStyleBuild b = r.get();
        String log = b.getLog();
        Assert.assertEquals(b.getResult(), FAILURE);
        Assert.assertThat(log, containsString("Finished: FAILURE"));
        Assert.assertThat(log, containsString("Build step 'BlockingBuilder' marked build as failure"));
        Assert.assertThat(log, containsString("Agent went offline during the build"));
        Assert.assertThat(log, containsString("Disconnected by Johnny : Taking offline to break your buil"));
    }

    @Issue("SECURITY-611")
    @Test
    public void apiPermissions() throws Exception {
        DumbSlave slave = new DumbSlave("slave", j.jenkins.getRootDir().getAbsolutePath(), j.createComputerLauncher(null));
        slave.setNumExecutors(2);
        j.jenkins.addNode(slave);
        FreeStyleProject publicProject = j.createFreeStyleProject("public-project");
        publicProject.setAssignedNode(slave);
        ExecutorTest.startBlockingBuild(publicProject);
        FreeStyleProject secretProject = j.createFreeStyleProject("secret-project");
        secretProject.setAssignedNode(slave);
        ExecutorTest.startBlockingBuild(secretProject);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(READ).everywhere().toEveryone().grant(Item.READ).onItems(publicProject).toEveryone().grant(Item.READ).onItems(secretProject).to("has-security-clearance"));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.withBasicCredentials("has-security-clearance");
        String api = wc.goTo(((slave.toComputer().getUrl()) + "api/json?pretty&depth=1"), null).getWebResponse().getContentAsString();
        System.out.println(api);
        Assert.assertThat(api, allOf(containsString("public-project"), containsString("secret-project")));
        wc = j.createWebClient();
        wc.withBasicCredentials("regular-joe");
        api = wc.goTo(((slave.toComputer().getUrl()) + "api/json?pretty&depth=1"), null).getWebResponse().getContentAsString();
        System.out.println(api);
        Assert.assertThat(api, allOf(containsString("public-project"), not(containsString("secret-project"))));
    }

    private static final class BlockingBuilder extends Builder {
        private final OneShotEvent e;

        private BlockingBuilder(OneShotEvent e) {
            this.e = e;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            VirtualChannel channel = launcher.getChannel();
            Node node = build.getBuiltOn();
            e.signal();// we are safe to be interrupted

            for (; ;) {
                // Keep using the channel
                channel.call(node.getClockDifferenceCallable());
                Thread.sleep(100);
            }
        }

        @TestExtension
        public static class DescriptorImpl extends Descriptor<Builder> {}
    }
}


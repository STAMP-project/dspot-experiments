package hudson.model;


import Queue.Executable;
import Queue.Item;
import Result.ABORTED;
import hudson.model.queue.QueueTaskFuture;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SleepBuilder;


public class RunMapTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    // TODO https://github.com/jenkinsci/jenkins/pull/2438: @Rule public LoggerRule logs = new LoggerRule();
    /**
     * Makes sure that reloading the project while a build is in progress won't clobber that in-progress build.
     */
    @Issue("JENKNS-12318")
    @Test
    public void reloadWhileBuildIsInProgress() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        // want some completed build records
        FreeStyleBuild b1 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        // now create a build that hangs until we signal the OneShotEvent
        p.getBuildersList().add(new SleepBuilder(9999999));
        FreeStyleBuild b2 = p.scheduleBuild2(0).waitForStart();
        Assert.assertEquals(2, b2.number);
        // now reload
        p.updateByXml(((Source) (new StreamSource(p.getConfigFile().getFile()))));
        // we should still see the same object for #2 because that's in progress
        Assert.assertSame(p.getBuildByNumber(b2.number), b2);
        // build #1 should be reloaded
        Assert.assertNotSame(b1, p.getBuildByNumber(1));
        // and reference gets fixed up
        b1 = p.getBuildByNumber(1);
        Assert.assertSame(b1.getNextBuild(), b2);
        Assert.assertSame(b2.getPreviousBuild(), b1);
    }

    @Issue("JENKINS-27530")
    @Test
    public void reloadWhileBuildIsInQueue() throws Exception {
        // logs.record(Queue.class, Level.FINE);
        FreeStyleProject p = r.createFreeStyleProject("p");
        p.getBuildersList().add(new SleepBuilder(9999999));
        r.jenkins.setNumExecutors(1);
        Assert.assertEquals(1, p.scheduleBuild2(0).waitForStart().number);
        p.scheduleBuild2(0);
        // Note that the bug does not reproduce simply from p.doReload(), since in that case Job identity remains intact:
        r.jenkins.reload();
        p = r.jenkins.getItemByFullName("p", FreeStyleProject.class);
        FreeStyleBuild b1 = p.getLastBuild();
        Assert.assertEquals(1, b1.getNumber());
        /* Currently fails since Run.project is final. But anyway that is not the problem:
        assertEquals(p, b1.getParent());
         */
        Queue[] items = Queue.getInstance().getItems();
        Assert.assertEquals(1, items.length);
        Assert.assertEquals(p, items[0].task);// the real issue: assignBuildNumber was being called on the wrong Job

        QueueTaskFuture<Queue.Executable> b2f = items[0].getFuture();
        b1.getExecutor().interrupt();
        r.assertBuildStatus(ABORTED, r.waitForCompletion(b1));
        FreeStyleBuild b2 = ((FreeStyleBuild) (b2f.waitForStart()));
        Assert.assertEquals(2, b2.getNumber());
        Assert.assertEquals(p, b2.getParent());
        b2.getExecutor().interrupt();
        r.assertBuildStatus(ABORTED, r.waitForCompletion(b2));
        FreeStyleBuild b3 = p.scheduleBuild2(0).waitForStart();
        Assert.assertEquals(3, b3.getNumber());
        Assert.assertEquals(p, b3.getParent());
        b3.getExecutor().interrupt();
        r.assertBuildStatus(ABORTED, r.waitForCompletion(b3));
    }

    /**
     * Testing if the lazy loading can gracefully tolerate a RuntimeException during unmarshalling.
     */
    @Issue("JENKINS-15533")
    @Test
    public void runtimeExceptionInUnmarshalling() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FreeStyleBuild b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        b.addAction(new RunMapTest.BombAction());
        b.save();
        p._getRuns().purgeCache();
        b = p.getBuildByNumber(b.number);
        // Original test assumed that b == null, but after JENKINS-21024 this is no longer true,
        // so this may not really be testing anything interesting:
        Assert.assertNotNull(b);
        Assert.assertNull(b.getAction(RunMapTest.BombAction.class));
        Assert.assertTrue(RunMapTest.bombed);
    }

    public static class BombAction extends InvisibleAction {
        private Object readResolve() {
            RunMapTest.bombed = true;
            throw new NullPointerException();
        }
    }

    private static boolean bombed;

    @Issue("JENKINS-25788")
    @Test
    public void remove() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FreeStyleBuild b1 = r.buildAndAssertSuccess(p);
        FreeStyleBuild b2 = r.buildAndAssertSuccess(p);
        RunMap<FreeStyleBuild> runs = p._getRuns();
        Assert.assertEquals(2, runs.size());
        Assert.assertTrue(runs.remove(b1));
        Assert.assertEquals(1, runs.size());
        Assert.assertFalse(runs.remove(b1));
        Assert.assertEquals(1, runs.size());
        Assert.assertTrue(runs.remove(b2));
        Assert.assertEquals(0, runs.size());
        Assert.assertFalse(runs.remove(b2));
        Assert.assertEquals(0, runs.size());
    }
}


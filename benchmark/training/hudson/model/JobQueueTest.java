package hudson.model;


import hudson.model.Queue.BlockedItem;
import hudson.model.Queue.WaitingItem;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SleepBuilder;


public class JobQueueTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private static volatile boolean fireCompletedFlag = false;

    private static volatile boolean fireFinalizeFlag = false;

    @Test
    public void buildPendingWhenBuildRunning() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        project.getBuildersList().add(new SleepBuilder(2000));
        // Kick the first Build
        project.scheduleBuild2(1);
        // Schedule another build
        project.scheduleBuild2(1);
        // The project should be in Queue when Run is in BUILDING stage
        Assert.assertTrue(project.isInQueue());
        // Cancel the project from queue
        j.jenkins.getQueue().cancel(project.getQueueItem());
        // Verify the project is removed from Queue
        Assert.assertTrue(j.jenkins.getQueue().isEmpty());
    }

    @Test
    public void buildPendingWhenBuildInPostProduction() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        project.getBuildersList().add(new SleepBuilder(1000));
        // Kick the first Build
        project.scheduleBuild2(1);
        int count = 0;
        // Now, Wait for run to be in POST_PRODUCTION stage
        while ((!(JobQueueTest.fireCompletedFlag)) && (count < 100)) {
            Thread.sleep(100);
            count++;
        } 
        if (JobQueueTest.fireCompletedFlag) {
            // Schedule the build for the project and this build should be in Queue since the state is POST_PRODUCTION
            project.scheduleBuild2(0);
            Assert.assertTrue(project.isInQueue());// That means it's pending or it's waiting or blocked

            j.jenkins.getQueue().maintain();
            while ((j.jenkins.getQueue().getItem(project)) instanceof WaitingItem) {
                System.out.println(j.jenkins.getQueue().getItem(project));
                j.jenkins.getQueue().maintain();
                Thread.sleep(10);
            } 
            Assert.assertTrue(((j.jenkins.getQueue().getItem(project)) instanceof BlockedItem));// check is it is blocked

        } else {
            Assert.fail("The maximum attempts for checking if the job is in POST_PRODUCTION State have reached");
        }
        count = 0;
        while ((!(JobQueueTest.fireFinalizeFlag)) && (count < 100)) {
            Thread.sleep(100);
            count++;
        } 
        if (JobQueueTest.fireFinalizeFlag) {
            // Verify the build is removed from Queue since now it is in Completed state
            // it should be scheduled for run
            j.jenkins.getQueue().maintain();
            Assert.assertFalse(((j.jenkins.getQueue().getItem(project)) instanceof BlockedItem));
        } else {
            Assert.fail("The maximum attempts for checking if the job is in COMPLETED State have reached");
        }
        Thread.sleep(1000);// Sleep till job completes.

    }
}


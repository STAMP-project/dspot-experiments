package hudson.model.queue;


import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.model.Queue;
import hudson.model.Queue.Item;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;
import java.io.StringWriter;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;


public class QueueTaskDispatcherTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule().record(Queue.class, Level.ALL);

    @Test
    public void canRunBlockageIsDisplayed() throws Exception {
        FreeStyleProject project = r.createFreeStyleProject();
        r.jenkins.getQueue().schedule(project, 0);
        r.getInstance().getQueue().maintain();
        Item item = r.jenkins.getQueue().getItem(project);
        Assert.assertTrue("Not blocked", item.isBlocked());
        Assert.assertEquals("Expected CauseOfBlockage to be returned", "blocked by canRun", item.getWhy());
    }

    @TestExtension("canRunBlockageIsDisplayed")
    public static class MyQueueTaskDispatcher extends QueueTaskDispatcher {
        @Override
        public CauseOfBlockage canRun(Item item) {
            return new CauseOfBlockage() {
                @Override
                public String getShortDescription() {
                    return "blocked by canRun";
                }
            };
        }
    }

    @Issue("JENKINS-38514")
    @Test
    public void canTakeBlockageIsDisplayed() throws Exception {
        FreeStyleProject project = r.createFreeStyleProject();
        r.jenkins.getQueue().schedule(project, 0);
        r.getInstance().getQueue().maintain();
        Queue.Item item = r.jenkins.getQueue().getItem(project);
        Assert.assertNotNull(item);
        CauseOfBlockage cob = item.getCauseOfBlockage();
        Assert.assertNotNull(cob);
        Assert.assertThat(cob.getShortDescription(), Matchers.containsString("blocked by canTake"));
        StringWriter w = new StringWriter();
        TaskListener l = new StreamTaskListener(w);
        cob.print(l);
        l.getLogger().flush();
        Assert.assertThat(w.toString(), Matchers.containsString("blocked by canTake"));
    }

    @TestExtension("canTakeBlockageIsDisplayed")
    public static class AnotherQueueTaskDispatcher extends QueueTaskDispatcher {
        @Override
        public CauseOfBlockage canTake(Node node, Queue.BuildableItem item) {
            return new CauseOfBlockage() {
                @Override
                public String getShortDescription() {
                    return "blocked by canTake";
                }
            };
        }
    }
}


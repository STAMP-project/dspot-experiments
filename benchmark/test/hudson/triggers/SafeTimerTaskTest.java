package hudson.triggers;


import SafeTimerTask.LOGS_ROOT_PATH_PROPERTY;
import hudson.model.AsyncPeriodicWork;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;


public class SafeTimerTaskTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public LoggerRule loggerRule = new LoggerRule();

    @Issue("JENKINS-50291")
    @Test
    public void changeLogsRoot() throws Exception {
        Assert.assertNull(System.getProperty(LOGS_ROOT_PATH_PROPERTY));
        File temporaryFolder = folder.newFolder();
        // Check historical default value
        final File logsRoot = new File(j.jenkins.getRootDir(), "logs/tasks");
        // Give some time for the logs to arrive
        Thread.sleep((3 * (SafeTimerTaskTest.LogSpammer.RECURRENCE_PERIOD)));
        Assert.assertTrue(logsRoot.exists());
        Assert.assertTrue(logsRoot.isDirectory());
        System.setProperty(LOGS_ROOT_PATH_PROPERTY, temporaryFolder.toString());
        Assert.assertEquals(temporaryFolder.toString(), SafeTimerTask.getLogsRoot().toString());
    }

    @TestExtension
    public static class LogSpammer extends AsyncPeriodicWork {
        public static final long RECURRENCE_PERIOD = 50L;

        public LogSpammer() {
            super("wut");
        }

        @Override
        protected void execute(TaskListener listener) throws IOException, InterruptedException {
            listener.getLogger().println("blah");
        }

        @Override
        public long getRecurrencePeriod() {
            return SafeTimerTaskTest.LogSpammer.RECURRENCE_PERIOD;
        }
    }
}


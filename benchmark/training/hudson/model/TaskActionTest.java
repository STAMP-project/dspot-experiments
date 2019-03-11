package hudson.model;


import hudson.console.AnnotatedLargeText;
import hudson.security.ACL;
import hudson.security.Permission;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jerome Lacoste
 */
public class TaskActionTest {
    private static class MyTaskThread extends TaskThread {
        MyTaskThread(TaskAction taskAction) {
            super(taskAction, ListenerAndText.forMemory(taskAction));
        }

        protected void perform(TaskListener listener) throws Exception {
            listener.hyperlink("/localpath", "a link");
        }
    }

    private static class MyTaskAction extends TaskAction {
        void start() {
            workerThread = new TaskActionTest.MyTaskThread(this);
            workerThread.start();
        }

        public String getIconFileName() {
            return "Iconfilename";
        }

        public String getDisplayName() {
            return "My Task Thread";
        }

        public String getUrlName() {
            return "xyz";
        }

        protected Permission getPermission() {
            return Permission.READ;
        }

        protected ACL getACL() {
            return ACL.lambda(( a, p) -> true);
        }
    }

    @Test
    public void annotatedText() throws Exception {
        TaskActionTest.MyTaskAction action = new TaskActionTest.MyTaskAction();
        action.start();
        AnnotatedLargeText annotatedText = obtainLog();
        while (!(annotatedText.isComplete())) {
            Thread.sleep(10);
        } 
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        annotatedText.writeLogTo(0, os);
        Assert.assertTrue(os.toString("UTF-8").startsWith("a linkCompleted"));
    }
}


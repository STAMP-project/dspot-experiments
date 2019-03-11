package hudson.model;


import Queue.Item;
import hudson.slaves.NodeProvisionerRule;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


// TODO merge into QueueTest after security patch
public class QueueSEC891Test {
    @Rule
    public JenkinsRule r = new NodeProvisionerRule((-1), 0, 10);

    @Test
    public void doCancelItem_PermissionIsChecked() throws Exception {
        checkCancelOperationUsingUrl(( item) -> "queue/cancelItem?id=" + (item.getId()));
    }

    @Test
    public void doCancelQueue_PermissionIsChecked() throws Exception {
        checkCancelOperationUsingUrl(( item) -> ("queue/item/" + (item.getId())) + "/cancelQueue");
    }
}


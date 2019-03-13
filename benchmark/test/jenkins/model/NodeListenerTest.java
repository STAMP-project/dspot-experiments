package jenkins.model;


import hudson.cli.CreateNodeCommand;
import hudson.cli.DeleteNodeCommand;
import hudson.cli.GetNodeCommand;
import hudson.cli.UpdateNodeCommand;
import hudson.model.Node;
import hudson.slaves.DumbSlave;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NodeListenerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private NodeListener mock;

    @Test
    public void crud() throws Exception {
        DumbSlave slave = j.createSlave();
        String xml = cli(new GetNodeCommand()).invokeWithArgs(slave.getNodeName()).stdout();
        cli(new UpdateNodeCommand()).withStdin(new StringInputStream(xml)).invokeWithArgs(slave.getNodeName());
        cli(new DeleteNodeCommand()).invokeWithArgs(slave.getNodeName());
        cli(new CreateNodeCommand()).withStdin(new StringInputStream(xml)).invokeWithArgs("replica");
        j.jenkins.getComputer("replica").doDoDelete();
        Mockito.verify(mock, Mockito.times(2)).onCreated(ArgumentMatchers.any(Node.class));
        Mockito.verify(mock, Mockito.times(1)).onUpdated(ArgumentMatchers.any(Node.class), ArgumentMatchers.any(Node.class));
        Mockito.verify(mock, Mockito.times(2)).onDeleted(ArgumentMatchers.any(Node.class));
        Mockito.verifyNoMoreInteractions(mock);
    }
}


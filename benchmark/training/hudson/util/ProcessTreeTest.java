package hudson.util;


import hudson.ChannelRule;
import hudson.remoting.VirtualChannel;
import hudson.util.ProcessTree.OSProcess;
import hudson.util.ProcessTree.ProcessCallable;
import java.io.IOException;
import java.io.Serializable;
import jenkins.security.MasterToSlaveCallable;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

import static ProcessTree.DEFAULT;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ProcessTreeTest {
    @Rule
    public ChannelRule channels = new ChannelRule();

    static class Tag implements Serializable {
        ProcessTree tree;

        OSProcess p;

        int id;

        private static final long serialVersionUID = 1L;
    }

    @Test
    public void remoting() throws Exception {
        Assume.assumeFalse("on some platforms where we fail to list any processes", ((ProcessTree.get()) == (DEFAULT)));
        ProcessTreeTest.Tag t = channels.french.call(new ProcessTreeTest.MyCallable());
        // make sure the serialization preserved the reference graph
        Assert.assertSame(t.p.getTree(), t.tree);
        // verify that some remote call works
        t.p.getEnvironmentVariables();
        // it should point to the same object
        Assert.assertEquals(t.id, t.p.getPid());
        t.p.act(new ProcessTreeTest.ProcessCallableImpl());
    }

    private static class MyCallable extends MasterToSlaveCallable<ProcessTreeTest.Tag, IOException> implements Serializable {
        public ProcessTreeTest.Tag call() throws IOException {
            ProcessTreeTest.Tag t = new ProcessTreeTest.Tag();
            t.tree = ProcessTree.get();
            t.p = t.tree.iterator().next();
            t.id = t.p.getPid();
            return t;
        }

        private static final long serialVersionUID = 1L;
    }

    private static class ProcessCallableImpl implements ProcessCallable<Void> {
        public Void invoke(OSProcess process, VirtualChannel channel) throws IOException {
            Assert.assertNotNull(process);
            Assert.assertNotNull(channel);
            return null;
        }
    }
}


package hudson.node_monitors;


import ClockMonitor.DESCRIPTOR;
import hudson.slaves.DumbSlave;
import hudson.slaves.SlaveComputer;
import hudson.util.ClockDifference;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Richard Mortimer
 */
public class ClockMonitorDescriptorTest {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    /**
     * Makes sure that it returns sensible values.
     */
    @Test
    public void testClockMonitor() throws Exception {
        DumbSlave s = jenkins.createOnlineSlave();
        SlaveComputer c = s.getComputer();
        if (c.isOffline())
            Assert.fail(("Slave failed to go online: " + (c.getLog())));

        ClockDifference cd = DESCRIPTOR.monitor(c);
        long diff = cd.diff;
        Assert.assertTrue((diff < (TimeUnit.SECONDS.toMillis(5))));
        Assert.assertTrue((diff > (TimeUnit.SECONDS.toMillis((-5)))));
        Assert.assertTrue(((cd.abs()) >= 0));
        Assert.assertTrue(((cd.abs()) < (TimeUnit.SECONDS.toMillis(5))));
        Assert.assertFalse(cd.isDangerous());
        Assert.assertTrue("html output too short", ((cd.toHtml().length()) > 0));
    }
}


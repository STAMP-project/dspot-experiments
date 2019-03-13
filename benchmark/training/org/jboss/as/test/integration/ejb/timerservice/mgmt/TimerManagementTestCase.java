package org.jboss.as.test.integration.ejb.timerservice.mgmt;


import java.io.Serializable;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test non persistent interval timer.
 *
 * @unknown baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TimerManagementTestCase extends AbstractTimerManagementTestCase {
    @Test
    @InSequence(1)
    public void testResourceExistence() throws Exception {
        super.testResourceExistence();
        this.bean.waitOnTimeout();
        Assert.assertEquals("Timer ticks should register some ++!", 1, this.bean.getTimerTicks());
    }

    @Test
    @InSequence(2)
    public void testSuspendAndActivate() throws Exception {
        this.bean.createTimer();
        this.suspendTimer();
        final long ticksCount = this.bean.getTimerTicks();
        this.waitOverTimer();
        Assert.assertEquals("Timer ticks should not change after suspension!", ticksCount, this.bean.getTimerTicks());
        this.activateTimer();
        this.bean.waitOnTimeout();
        Assert.assertEquals("Timer ticks should register some ++!", (ticksCount + 1), this.bean.getTimerTicks());
        try {
            getTimerDetails();
        } catch (OperationFailedException ofe) {
            final ModelNode failureDescription = ofe.getFailureDescription();
            Assert.assertThat("Wrong failure description", failureDescription.toString(), CoreMatchers.containsString("WFLYCTL0216"));
        }
    }

    @Test
    @InSequence(3)
    public void testCancel() throws Exception {
        this.bean.createTimer();
        this.cancelTimer();
        try {
            getTimerDetails();
        } catch (OperationFailedException ofe) {
            final ModelNode failureDescription = ofe.getFailureDescription();
            Assert.assertThat("Wrong failure description", failureDescription.toString(), CoreMatchers.containsString("WFLYCTL0216"));
        }
    }

    private Serializable info = "PersistentIntervalTimerCLITestCase";
}


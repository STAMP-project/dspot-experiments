package org.jboss.as.test.integration.ejb.timerservice.mgmt;


import java.io.Serializable;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test non persistent interval timer.
 *
 * @unknown baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
@Ignore("Intermittently fails")
public class PersistentCalendarTimerManagementTestCase extends AbstractTimerManagementTestCase {
    @Test
    @InSequence(1)
    public void testResourceExistence() throws Exception {
        super.testResourceExistence();
    }

    @Test
    @InSequence(2)
    public void testSuspendAndActivate() throws Exception {
        super.testSuspendAndActivate();
    }

    @Test
    @InSequence(3)
    public void testCancel() throws Exception {
        super.testCancel();
    }

    @Test
    @InSequence(4)
    public void testTrigger() throws Exception {
        super.testTrigger();
    }

    @Test
    @InSequence(5)
    public void testSuspendAndTrigger() throws Exception {
        super.testSuspendAndTrigger();
    }

    private Serializable info = "PersistentCalendarTimerCLITestCase";
}


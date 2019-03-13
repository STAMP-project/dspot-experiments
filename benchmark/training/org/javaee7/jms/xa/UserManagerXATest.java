package org.javaee7.jms.xa;


import java.util.concurrent.TimeUnit;
import org.javaee7.jms.xa.utils.AbstractUserManagerTest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class UserManagerXATest extends AbstractUserManagerTest {
    @Test
    public void emailAlreadyRegisteredXA() throws Exception {
        try {
            // This email is already in the DB so we should get an exception trying to register it.
            userManager.register("jack@itcrowd.pl");
        } catch (Exception e) {
            AbstractUserManagerTest.logger.info(("Got expected exception " + e));
        }
        // Wait for at most 30 seconds for the JMS method to NOT be called, since we're testing for something
        // to NOT happen we can never be 100% sure, but 30 seconds should cover almost all cases.
        DeliveryStats.countDownLatch.await(30, TimeUnit.SECONDS);
        Assert.assertEquals("countDownLatch was decreased meaning JMS method was called, but should not have been.", 1, DeliveryStats.countDownLatch.getCount());
        Assert.assertEquals("Message should not be delivered due to transaction rollback", 0L, deliveryStats.getDeliveredMessagesCount());
    }

    @Test
    public void happyPathXA() throws Exception {
        userManager.register("bernard@itcrowd.pl");
        // Wait for at most 90 seconds for the JMS method to be called
        DeliveryStats.countDownLatch.await(90, TimeUnit.SECONDS);
        Assert.assertEquals("Timeout expired and countDownLatch did not reach 0 (so JMS method not called)", 0, DeliveryStats.countDownLatch.getCount());
        Assert.assertEquals(1L, deliveryStats.getDeliveredMessagesCount());
    }
}


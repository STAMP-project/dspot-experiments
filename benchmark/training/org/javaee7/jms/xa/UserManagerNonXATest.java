package org.javaee7.jms.xa;


import java.util.concurrent.TimeUnit;
import org.javaee7.jms.xa.utils.AbstractUserManagerTest;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class UserManagerNonXATest extends AbstractUserManagerTest {
    @Test
    public void emailAlreadyRegisteredNonXA() throws Exception {
        System.out.println("Entering emailAlreadyRegisteredNonXA");
        try {
            // This email is already in DB so we should get an exception trying to register it.
            userManager.register("jack@itcrowd.pl");
        } catch (Exception e) {
            AbstractUserManagerTest.logger.info(("Got expected exception " + e));
        }
        DeliveryStats.countDownLatch.await(90, TimeUnit.SECONDS);
        Assert.assertEquals("Timeout expired and countDownLatch did not reach 0", 0, DeliveryStats.countDownLatch.getCount());
        Assert.assertEquals("Message should be delivered despite transaction rollback", 1L, deliveryStats.getDeliveredMessagesCount());
    }

    @Test
    public void happyPathNonXA() throws Exception {
        System.out.println("Entering happyPathNonXA");
        userManager.register("bernard@itcrowd.pl");
        DeliveryStats.countDownLatch.await(90, TimeUnit.SECONDS);
        Assert.assertEquals("Timeout expired and countDownLatch did not reach 0", 0, DeliveryStats.countDownLatch.getCount());
        Assert.assertEquals(1L, deliveryStats.getDeliveredMessagesCount());
    }
}


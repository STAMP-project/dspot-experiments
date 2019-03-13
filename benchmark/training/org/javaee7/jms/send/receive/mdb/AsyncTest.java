package org.javaee7.jms.send.receive.mdb;


import javax.ejb.EJB;
import org.javaee7.jms.send.receive.simple.MessageSenderAsync;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Patrik Dudits
 */
@RunWith(Arquillian.class)
public class AsyncTest {
    @EJB
    MessageSenderAsync asyncSender;

    private final int messageReceiveTimeoutInMillis = 10000;

    @Test
    public void testAsync() throws InterruptedException {
        asyncSender.sendMessage("Fire!");
        ReceptionSynchronizer.waitFor(MessageReceiverAsync.class, "onMessage", messageReceiveTimeoutInMillis);
        // unless we timed out, the test passes
    }
}


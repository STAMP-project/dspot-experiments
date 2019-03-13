package org.javaee7.jms.send.receive;


import java.util.concurrent.TimeoutException;
import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import org.javaee7.jms.send.receive.classic.ClassicMessageReceiver;
import org.javaee7.jms.send.receive.classic.ClassicMessageSender;
import org.javaee7.jms.send.receive.simple.MessageReceiverSync;
import org.javaee7.jms.send.receive.simple.MessageSenderSync;
import org.javaee7.jms.send.receive.simple.appmanaged.MessageReceiverAppManaged;
import org.javaee7.jms.send.receive.simple.appmanaged.MessageSenderAppManaged;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Patrik Dudits
 */
@RunWith(Arquillian.class)
public class SyncTest {
    @EJB
    ClassicMessageSender classicSender;

    @EJB
    ClassicMessageReceiver classicReceiver;

    @EJB
    MessageSenderSync simpleSender;

    @EJB
    MessageReceiverSync simpleReceiver;

    @EJB
    MessageSenderAppManaged appManagedSender;

    @EJB
    MessageReceiverAppManaged appManagedReceiver;

    private final int messageReceiveTimeoutInMillis = 10000;

    @Test
    public void testClassicApi() throws TimeoutException, JMSException {
        String message = "The test message over JMS 1.1 API";
        classicSender.sendMessage(message);
        Assert.assertEquals(message, classicReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testContainerManagedJmsContext() throws TimeoutException, JMSRuntimeException {
        String message = "Test message over container-managed JMSContext";
        simpleSender.sendMessage(message);
        Assert.assertEquals(message, simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testAppManagedJmsContext() throws TimeoutException, JMSRuntimeException {
        String message = "The test message over app-managed JMSContext";
        appManagedSender.sendMessage(message);
        Assert.assertEquals(message, appManagedReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }

    @Test
    public void testMultipleSendAndReceive() throws TimeoutException, JMSRuntimeException {
        simpleSender.sendMessage("1");
        simpleSender.sendMessage("2");
        Assert.assertEquals("1", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        Assert.assertEquals("2", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        simpleSender.sendMessage("3");
        simpleSender.sendMessage("4");
        simpleSender.sendMessage("5");
        Assert.assertEquals("3", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        Assert.assertEquals("4", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
        Assert.assertEquals("5", simpleReceiver.receiveMessage(messageReceiveTimeoutInMillis));
    }
}


package com.baeldung.mbassador;


import net.engio.mbassy.bus.MBassador;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorHierarchyUnitTest {
    private MBassador dispatcher = new MBassador<Message>();

    private Message message;

    private AckMessage ackMessage;

    private RejectMessage rejectMessage;

    @Test
    public void whenMessageDispatched_thenMessageHandled() {
        dispatcher.post(new Message()).now();
        Assert.assertNotNull(message);
        Assert.assertNull(ackMessage);
        Assert.assertNull(rejectMessage);
    }

    @Test
    public void whenRejectDispatched_thenMessageAndRejectHandled() {
        dispatcher.post(new RejectMessage()).now();
        Assert.assertNotNull(message);
        Assert.assertNotNull(rejectMessage);
        Assert.assertNull(ackMessage);
    }

    @Test
    public void whenAckDispatched_thenMessageAndAckHandled() {
        dispatcher.post(new AckMessage()).now();
        Assert.assertNotNull(message);
        Assert.assertNotNull(ackMessage);
        Assert.assertNull(rejectMessage);
    }
}


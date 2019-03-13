package com.baeldung.smack;


import StanzaTypeFilter.MESSAGE;
import java.util.concurrent.CountDownLatch;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmackIntegrationTest {
    private static AbstractXMPPConnection connection;

    private Logger logger = LoggerFactory.getLogger(SmackIntegrationTest.class);

    @Test
    public void whenSendMessageWithChat_thenReceiveMessage() throws InterruptedException, XmppStringprepException {
        CountDownLatch latch = new CountDownLatch(1);
        ChatManager chatManager = ChatManager.getInstanceFor(SmackIntegrationTest.connection);
        final String[] expected = new String[]{ null };
        new StanzaThread().run();
        chatManager.addIncomingListener(( entityBareJid, message, chat) -> {
            logger.info(("Message arrived: " + (message.getBody())));
            expected[0] = message.getBody();
            latch.countDown();
        });
        latch.await();
        Assert.assertEquals("Hello!", expected[0]);
    }

    @Test
    public void whenSendMessage_thenReceiveMessageWithFilter() throws InterruptedException, XmppStringprepException {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] expected = new String[]{ null };
        new StanzaThread().run();
        SmackIntegrationTest.connection.addAsyncStanzaListener(( stanza) -> {
            if (stanza instanceof Message) {
                Message message = ((Message) (stanza));
                expected[0] = message.getBody();
                latch.countDown();
            }
        }, MESSAGE);
        latch.await();
        Assert.assertEquals("Hello!", expected[0]);
    }
}


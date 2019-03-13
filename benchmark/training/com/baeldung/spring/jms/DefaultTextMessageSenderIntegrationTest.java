package com.baeldung.spring.jms;


import org.junit.Test;


public class DefaultTextMessageSenderIntegrationTest {
    private static SampleJmsMessageSender messageProducer;

    private static SampleListener messageListener;

    @Test
    public void testSimpleSend() {
        DefaultTextMessageSenderIntegrationTest.messageProducer.simpleSend();
    }

    @Test
    public void testSendTextMessage() {
        DefaultTextMessageSenderIntegrationTest.messageProducer.sendTextMessage(null);
    }
}


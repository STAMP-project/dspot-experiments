package org.springframework.batch.integration;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class SmokeTests {
    @Autowired
    private MessageChannel smokein;

    @Autowired
    private PollableChannel smokeout;

    @Test
    public void testDummyWithSimpleAssert() throws Exception {
        Assert.assertTrue(true);
    }

    @Test
    public void testVanillaSendAndReceive() throws Exception {
        smokein.send(new org.springframework.messaging.support.GenericMessage("foo"));
        @SuppressWarnings("unchecked")
        Message<String> message = ((Message<String>) (smokeout.receive(100)));
        String result = (message == null) ? null : message.getPayload();
        Assert.assertEquals("foo: 1", result);
        Assert.assertEquals(1, SmokeTests.AnnotatedEndpoint.count);
    }

    @MessageEndpoint
    static class AnnotatedEndpoint {
        // This has to be static because Spring Integration registers the handler
        // more than once (every time a test instance is created), but only one of
        // them will get the message.
        private static volatile int count = 0;

        @ServiceActivator(inputChannel = "smokein", outputChannel = "smokeout")
        public String process(String message) {
            (SmokeTests.AnnotatedEndpoint.count)++;
            String result = (message + ": ") + (SmokeTests.AnnotatedEndpoint.count);
            return result;
        }
    }
}


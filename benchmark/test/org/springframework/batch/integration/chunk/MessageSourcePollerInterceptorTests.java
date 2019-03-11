package org.springframework.batch.integration.chunk;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;


public class MessageSourcePollerInterceptorTests {
    @Test(expected = IllegalStateException.class)
    public void testMandatoryPropertiesUnset() throws Exception {
        MessageSourcePollerInterceptor interceptor = new MessageSourcePollerInterceptor();
        interceptor.afterPropertiesSet();
    }

    @Test
    public void testMandatoryPropertiesSetViaConstructor() throws Exception {
        MessageSourcePollerInterceptor interceptor = new MessageSourcePollerInterceptor(new MessageSourcePollerInterceptorTests.TestMessageSource("foo"));
        interceptor.afterPropertiesSet();
    }

    @Test
    public void testMandatoryPropertiesSet() throws Exception {
        MessageSourcePollerInterceptor interceptor = new MessageSourcePollerInterceptor();
        interceptor.setMessageSource(new MessageSourcePollerInterceptorTests.TestMessageSource("foo"));
        interceptor.afterPropertiesSet();
    }

    @Test
    public void testPreReceive() throws Exception {
        MessageSourcePollerInterceptor interceptor = new MessageSourcePollerInterceptor(new MessageSourcePollerInterceptorTests.TestMessageSource("foo"));
        QueueChannel channel = new QueueChannel();
        Assert.assertTrue(interceptor.preReceive(channel));
        Assert.assertEquals("foo", channel.receive(10L).getPayload());
    }

    private static class TestMessageSource implements MessageSource<String> {
        private final String payload;

        public TestMessageSource(String payload) {
            super();
            this.payload = payload;
        }

        public Message<String> receive() {
            return new org.springframework.messaging.support.GenericMessage(payload);
        }
    }
}


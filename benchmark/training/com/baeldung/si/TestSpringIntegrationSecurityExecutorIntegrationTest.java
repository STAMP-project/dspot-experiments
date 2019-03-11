package com.baeldung.si;


import com.baeldung.si.security.MessageConsumer;
import com.baeldung.si.security.SecurityConfig;
import com.baeldung.si.security.SecurityPubSubChannel;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityPubSubChannel.class, MessageConsumer.class, SecurityConfig.class })
public class TestSpringIntegrationSecurityExecutorIntegrationTest {
    @Autowired
    SubscribableChannel startPSChannel;

    @Autowired
    MessageConsumer messageConsumer;

    @Autowired
    ThreadPoolTaskExecutor executor;

    final String DIRECT_CHANNEL_MESSAGE = "Direct channel message";

    @Test
    @WithMockUser(username = "user", roles = { "VIEWER" })
    public void givenRoleUser_whenSendMessageToPSChannel_thenNoMessageArrived() throws IllegalStateException, InterruptedException {
        startPSChannel.send(new org.springframework.messaging.support.GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));
        executor.getThreadPoolExecutor().awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertEquals(1, messageConsumer.getMessagePSContent().size());
        Assert.assertTrue(messageConsumer.getMessagePSContent().values().contains("user"));
    }

    @Test
    @WithMockUser(username = "user", roles = { "LOGGER", "VIEWER" })
    public void givenRoleUserAndLogger_whenSendMessageToPSChannel_then2GetMessages() throws IllegalStateException, InterruptedException {
        startPSChannel.send(new org.springframework.messaging.support.GenericMessage<String>(DIRECT_CHANNEL_MESSAGE));
        executor.getThreadPoolExecutor().awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertEquals(2, messageConsumer.getMessagePSContent().size());
        Assert.assertTrue(messageConsumer.getMessagePSContent().values().contains("user"));
        Assert.assertTrue(messageConsumer.getMessagePSContent().values().contains("ROLE_LOGGER,ROLE_VIEWER"));
    }
}


package com.baeldung.spring.data.redis;


import RedisMessageSubscriber.messageList;
import com.baeldung.spring.data.redis.config.RedisConfig;
import com.baeldung.spring.data.redis.queue.RedisMessagePublisher;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RedisConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RedisMessageListenerIntegrationTest {
    private static RedisServer redisServer;

    @Autowired
    private RedisMessagePublisher redisMessagePublisher;

    @Test
    public void testOnMessage() throws Exception {
        String message = "Message " + (UUID.randomUUID());
        redisMessagePublisher.publish(message);
        Thread.sleep(100);
        Assert.assertTrue(messageList.get(0).contains(message));
    }
}


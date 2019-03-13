package com.taobao.meta.test;


import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_??????????topic
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class SpecialCharTopicTest extends BaseMetaTest {
    private final String topic = "!@#$%";

    @Test
    public void sendConsume() throws Exception {
        createProducer();
        producer.publish(this.topic);
        // ????????????????
        createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            sendMessage(count, "hello", this.topic);
            Assert.fail();
            // ??????????????????????
            subscribe(this.topic, (1024 * 1024), count);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof RuntimeException));
            Assert.assertTrue(((e.getMessage().indexOf("The server do not accept topic !@#$%")) != (-1)));
        } finally {
            producer.shutdown();
            consumer.shutdown();
        }
    }
}


package com.taobao.meta.test;


import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_topic???
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class BlankTopicTest extends BaseMetaTest {
    private final String topic = " ";

    @Test(expected = IllegalArgumentException.class)
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            this.sendMessage(count, "hello", this.topic);
            Assert.fail();
            // ??????????????????????
            this.subscribe(this.topic, (1024 * 1024), count);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Blank topic"));
            e.printStackTrace();
            throw e;
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


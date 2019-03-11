package com.taobao.meta.test;


import com.taobao.metamorphosis.exception.MetaClientException;
import org.junit.Assert;
import org.junit.Test;


public class RandomTopicTest extends BaseMetaTest {
    private final String topic = "gongyangyu";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            this.sendMessage(count, "hello", this.topic);
            // ??????????????????????
            this.subscribe(this.topic, (1024 * 1024), count);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof MetaClientException));
            Assert.assertTrue(((e.getMessage().indexOf("There is no aviable partition for topic")) != (-1)));
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????BigCount
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class BigCountTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        createProducer();
        producer.publish(this.topic);
        // ????????????????
        createConsumer("group1");
        try {
            // ???????
            final int count = 10000;
            sendMessage(count, "hello", this.topic);
            // ??????????????????????
            subscribe(this.topic, (1024 * 1024), count);
        } finally {
            producer.shutdown();
            consumer.shutdown();
        }
    }
}


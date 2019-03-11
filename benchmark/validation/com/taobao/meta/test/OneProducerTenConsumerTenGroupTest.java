package com.taobao.meta.test;


import org.junit.Test;


public class OneProducerTenConsumerTenGroupTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        createProducer();
        // ???????topic
        producer.publish(this.topic);
        // ????????????????
        try {
            // ???????
            final int count = 50;
            sendMessage(count, "hello", this.topic);
            // ??????????????????????
            subscribe_nConsumer(topic, (1024 * 1024), count, 10, 1);
        } finally {
            producer.shutdown();
            for (int i = 0; i < 10; i++) {
                consumerList.get(i).shutdown();
            }
        }
    }
}


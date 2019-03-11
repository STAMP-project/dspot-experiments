package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????_OneProducerTenConsumerTenGroupTwoBroker
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class OneProducerTenConsumerTenGroupTwoBrokerTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.startServer("server2");
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


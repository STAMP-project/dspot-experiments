package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????_TenProducerOneConsumerTest
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class TenProducerOneConsumerTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        create_nProducer(10);
        // ???????topic
        // ????????????????
        createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            sendMessage_nProducer(count, "hello", this.topic, 10);
            // ??????????????????????
            subscribe(this.topic, (1024 * 1024), 50);
        } finally {
            for (int i = 0; i < 10; i++) {
                producerList.get(i).shutdown();
            }
            consumer.shutdown();
        }
    }
}


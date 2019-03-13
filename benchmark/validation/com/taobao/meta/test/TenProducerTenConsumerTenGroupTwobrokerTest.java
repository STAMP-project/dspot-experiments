package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????_TenProducerTenConsumerTenGroupTwobroker
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class TenProducerTenConsumerTenGroupTwobrokerTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.startServer("server2");
        create_nProducer(10);
        try {
            // ???????
            final int count = 5;
            sendMessage_nProducer(count, "hello", this.topic, 10);
            // ??????????????????????
            subscribe_nConsumer(this.topic, (1024 * 1024), count, 10, 10);
        } finally {
            for (int i = 0; i < 10; i++) {
                producerList.get(i).shutdown();
                consumerList.get(i).shutdown();
            }
        }
    }
}


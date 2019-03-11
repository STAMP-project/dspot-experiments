package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????_OneProducerOneConsumer
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class SendMessageCallbackTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            // ????????????topic
            this.sendMessage(count, "hello", this.topic);
            // ??????????????????????
            this.subscribe(this.topic, (1024 * 1024), count);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


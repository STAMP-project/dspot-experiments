package com.taobao.meta.test;


import org.junit.Test;


/**
 * meta???????_OneProducerOneConsumer
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class SetDefaultTopicTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.setDefaultTopic(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            // ????????????topic
            this.sendMessage(count, "hello", "SetDefaultTopicTest");
            // ??????????????????????
            this.subscribe("SetDefaultTopicTest", (1024 * 1024), count);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


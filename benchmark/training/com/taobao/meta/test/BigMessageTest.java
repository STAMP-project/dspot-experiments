package com.taobao.meta.test;


import org.junit.Ignore;
import org.junit.Test;


/**
 * ???????????????
 *
 * @author ???
 * @since 2011-8-17 ????5:41:41
 */
@Ignore
public class BigMessageTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????2M?????
            final int count = 50;
            this.sendMessage(count, Utils.getData(((2 * 1024) * 1024)), this.topic);
            // ??????????????????????
            this.subscribe(this.topic, ((5 * 1024) * 1024), count);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


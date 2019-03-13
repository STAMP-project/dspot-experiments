package com.taobao.meta.test.ha;


import com.taobao.meta.test.BaseMetaTest;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-7-12 ????11:08:25
 */
public class OneMasterOneProducerOneConsumerTest extends HABaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        super.startServer("server1");
        super.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        super.createConsumer("group1");
        // ???????
        final int count = 5;
        super.sendMessage(count, "hello", this.topic);
        // ??????????????????????
        super.subscribe(this.topic, (1024 * 1024), count);
    }
}


package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????????????????????????
 *
 * @author ???
 * @since 2011-11-14 ????5:03:52
 */
public class ComsumeFromMaxOffsetTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        producer.publish(this.topic);
        try {
            // ?????????????????
            int count = 5;
            this.sendMessage(count, "hello", this.topic);
            Thread.sleep(1000);// ???????????

            ConsumerConfig consumerConfig = new ConsumerConfig("group1");
            consumerConfig.setConsumeFromMaxOffset();// ??????????????5??

            this.consumer = this.sessionFactory.createConsumer(consumerConfig);
            this.subscribe(this.topic, (1024 * 1024), 0);
            count = 6;
            this.sendMessage(count, "haha", this.topic);// ??????????

            this.subscribeRepeatable(this.topic, (1024 * 1024), count);
            // ?????????????????????????haha
            Assert.assertEquals(count, this.queue.size());
            for (Message msg : this.queue) {
                Assert.assertTrue(new String(msg.getData()).contains("haha"));
            }
        } finally {
            producer.shutdown();
            consumer.shutdown();
        }
    }
}


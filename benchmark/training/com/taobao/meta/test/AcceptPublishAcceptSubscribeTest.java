package com.taobao.meta.test;


import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.exception.MetaClientException;
import junit.framework.Assert;
import org.junit.Test;


/**
 * meta???????_OneProducerOneConsumer
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class AcceptPublishAcceptSubscribeTest extends BaseMetaTest {
    private final String topic1 = "meta-test";

    private final String topic2 = "meta-test2";

    private MetaClientConfig metaClientConfig;

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic1);
        this.producer.publish(this.topic2);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 5;
            this.sendMessage(count, "hello", this.topic1);
            // ??????????????????????
            this.subscribe(this.topic1, (1024 * 1024), count);
            // Send topic2 message failed;
            try {
                this.sendMessage(count, "hello", this.topic2);
                Assert.fail();
            } catch (MetaClientException e) {
                Assert.assertEquals("There is no aviable partition for topic meta-test2,maybe you don't publish it at first?", e.getMessage());
            }
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


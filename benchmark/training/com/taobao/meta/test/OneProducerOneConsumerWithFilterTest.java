package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import com.taobao.metamorphosis.exception.MetaClientException;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_OneProducerOneConsumer
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class OneProducerOneConsumerWithFilterTest extends BaseMetaTest {
    private final String topic = "filter-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 1000;
            this.sendMessage(count, "hello", this.topic);
            // ??????????
            try {
                this.consumer.subscribe(this.topic, (1024 * 1024), new MessageListener() {
                    public void recieveMessages(final Message messages) {
                        OneProducerOneConsumerWithFilterTest.this.queue.add(messages);
                    }

                    public Executor getExecutor() {
                        return null;
                    }
                }).completeSubscribe();
            } catch (final MetaClientException e) {
                throw e;
            }
            while ((this.queue.size()) < (count / 2)) {
                Thread.sleep(1000);
                System.out.println((((("??????????" + (count / 2)) + "???????????") + (this.queue.size())) + "??"));
            } 
            // ??????????????????????
            Assert.assertEquals((count / 2), this.queue.size());
            int i = 0;
            if (count != 0) {
                for (final Message msg : this.messages) {
                    if (((++i) % 2) == 0) {
                        Assert.assertTrue(this.queue.contains(msg));
                    }
                }
            }
            this.log.info(("received message count:" + (this.queue.size())));
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


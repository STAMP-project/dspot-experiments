package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MetaMessageSessionFactory;
import com.taobao.metamorphosis.client.consumer.ConcurrentLRUHashMap;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.SimpleFetchManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_OneProducerOneConsumer
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class OneProducerTenConsumerOneGroupTest extends BaseMetaTest {
    private final String topic = "test";

    @Test
    public void sendConsume() throws Exception {
        SimpleFetchManager.setMessageIdCache(new ConcurrentLRUHashMap());
        this.createProducer();
        this.producer.publish(this.topic);
        List<MetaMessageSessionFactory> sessionFactories = new ArrayList<MetaMessageSessionFactory>();
        final CountDownLatch latch = new CountDownLatch(6);
        try {
            // ???????
            final int count = 100;
            this.sendMessage(count, "hello", this.topic);
            for (int i = 0; i < 6; i++) {
                MetaMessageSessionFactory createdSessionFactory = new MetaMessageSessionFactory(this.metaClientConfig);
                MessageConsumer createdConsumer = createdSessionFactory.createConsumer(new ConsumerConfig("group"));
                this.subscribe(latch, count, createdConsumer);
                sessionFactories.add(createdSessionFactory);
                latch.countDown();
            }
            while ((this.queue.size()) < count) {
                Thread.sleep(1000);
                System.out.println((((("??????????" + count) + "???????????") + (this.queue.size())) + "??"));
            } 
            // ??????????????????????
            Assert.assertEquals(count, this.queue.size());
            if (count != 0) {
                for (final Message msg : this.messages) {
                    Assert.assertTrue(this.queue.contains(msg));
                }
            }
            this.log.info(("received message count:" + (this.queue.size())));
        } finally {
            this.producer.shutdown();
            for (MetaMessageSessionFactory factory : sessionFactories) {
                factory.shutdown();
            }
        }
    }
}


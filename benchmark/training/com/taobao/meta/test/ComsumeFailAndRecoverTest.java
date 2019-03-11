package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????,?????????????????recover
 *
 * @author ???
 * @since 2011-11-14 ????6:45:35
 */
public class ComsumeFailAndRecoverTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        final ConsumerConfig consumerConfig = new ConsumerConfig("group1");
        consumerConfig.setMaxFetchRetries(5);
        this.consumer = this.sessionFactory.createConsumer(consumerConfig);
        final AtomicInteger i = new AtomicInteger(0);
        try {
            int count = 2;
            this.sendMessage(count, "hello", this.topic);
            this.consumer.subscribe(this.topic, (1024 * 1024), new MessageListener() {
                public void recieveMessages(final Message messages) {
                    ComsumeFailAndRecoverTest.this.queue.add(messages);
                    // ?????????????,retry 5+1??????????????????recover,recover????????
                    // ?????????????8??
                    // ???????????,???????????9?????
                    if ((Arrays.equals(messages.getData(), "hello0".getBytes())) && ((i.get()) <= ((consumerConfig.getMaxFetchRetries()) + 1))) {
                        i.incrementAndGet();
                        throw new RuntimeException("don't worry,just for test");
                    }
                }

                public Executor getExecutor() {
                    return null;
                }
            }).completeSubscribe();
            while ((this.queue.size()) < ((count + 2) + (consumerConfig.getMaxFetchRetries()))) {
                Thread.sleep(1000);
                System.out.println((((("??????????" + ((count + 2) + (consumerConfig.getMaxFetchRetries()))) + "???????????") + (this.queue.size())) + "??"));
            } 
            int j = 0;
            for (Message msg : this.queue) {
                if (Arrays.equals(msg.getData(), "hello0".getBytes())) {
                    ++j;
                } else {
                    System.out.println(new String(msg.getData()));
                    Assert.assertTrue(Arrays.equals(msg.getData(), "hello1".getBytes()));
                }
            }
            // ????????hello1,????????hello0
            Assert.assertEquals(j, ((this.queue.size()) - 1));
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}


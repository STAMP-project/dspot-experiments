package com.taobao.meta.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.TopicBrowser;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class TopicBrowserTest extends BaseMetaTest {
    private final String topic = "test";

    @Test
    public void sendConsume() throws Exception {
        this.createProducer();
        this.producer.publish(this.topic);
        // ????????????????
        this.createConsumer("group1");
        try {
            // ???????
            final int count = 100;
            this.sendMessage(count, "hello", this.topic);
            // ??????????????????????
            this.subscribe(this.topic, (1024 * 1024), count);
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
        TopicBrowser topicBrowser = this.sessionFactory.createTopicBrowser(this.topic);
        try {
            Iterator<Message> it = topicBrowser.iterator();
            int n = 0;
            while (it.hasNext()) {
                Assert.assertNotNull(it.next());
                n++;
            } 
            Assert.assertEquals(100, n);
        } finally {
            topicBrowser.shutdown();
        }
    }
}


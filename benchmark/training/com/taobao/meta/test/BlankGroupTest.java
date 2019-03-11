package com.taobao.meta.test;


import com.taobao.metamorphosis.exception.InvalidConsumerConfigException;
import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_group???
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class BlankGroupTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        createProducer();
        // ???????topic
        producer.publish(this.topic);
        try {
            // ??????????????
            createConsumer2();
            Assert.fail();
            // ???????
            final int count = 5;
            sendMessage(count, "hello", this.topic);
            // ??????????????????????
            subscribe(this.topic, (1024 * 1024), count);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof InvalidConsumerConfigException));
            System.out.println("fuck");
            Assert.assertTrue(((e.getMessage().indexOf("Blank group")) != (-1)));
        } finally {
            producer.shutdown();
            if (null != (consumer)) {
                consumer.shutdown();
            }
        }
    }
}


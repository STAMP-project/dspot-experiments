package com.taobao.meta.test;


import com.taobao.metamorphosis.exception.InvalidConsumerConfigException;
import org.junit.Assert;
import org.junit.Test;


/**
 * meta???????_??????????GroupName
 *
 * @author gongyangyu(gongyangyu@taobao.com)
 */
public class SpecialCharGroupNameTest extends BaseMetaTest {
    private final String topic = "meta-test";

    @Test
    public void sendConsume() throws Exception {
        createProducer();
        producer.publish(this.topic);
        try {
            // ????????????????
            createConsumer("~!@#$%");
            // ???????
            final int count = 5;
            sendMessage(count, "hello", this.topic);
            // ??????????????????????
            subscribe(this.topic, (1024 * 1024), count);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof InvalidConsumerConfigException));
            Assert.assertTrue(((e.getMessage().indexOf("Group name has invalid character")) != (-1)));
            // e.printStackTrace();
        } finally {
            producer.shutdown();
            if ((consumer) != null)
                consumer.shutdown();

        }
    }
}


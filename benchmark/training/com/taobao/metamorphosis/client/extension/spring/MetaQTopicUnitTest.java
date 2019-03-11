package com.taobao.metamorphosis.client.extension.spring;


import com.taobao.metamorphosis.client.consumer.ConsumerConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MetaQTopicUnitTest {
    @Test
    public void testAsMapKey() {
        MetaqTopic metaQTopic1 = new MetaqTopic("test", 1024, new ConsumerConfig("test-group"));
        MetaqTopic metaQTopic2 = new MetaqTopic("test", 1024, new ConsumerConfig("test-group"));
        Map<MetaqTopic, Boolean> map = new HashMap<MetaqTopic, Boolean>();
        Assert.assertEquals(metaQTopic1, metaQTopic2);
        map.put(metaQTopic1, true);
        Assert.assertEquals("test", metaQTopic1.getTopic());
        Assert.assertEquals("test-group", metaQTopic1.getConsumerConfig().getGroup());
        Assert.assertEquals(1024, metaQTopic1.getMaxBufferSize());
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.get(metaQTopic1));
        Assert.assertTrue(map.get(metaQTopic2));
        metaQTopic2.setAlwaysConsumeFromMaxOffset(true);
        Assert.assertNull(map.get(metaQTopic2));
        Assert.assertTrue(map.get(metaQTopic1));
    }
}


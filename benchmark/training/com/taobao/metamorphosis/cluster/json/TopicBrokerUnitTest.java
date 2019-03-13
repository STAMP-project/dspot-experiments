package com.taobao.metamorphosis.cluster.json;


import com.taobao.metamorphosis.utils.JSONUtils;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TopicBrokerUnitTest {
    @Test
    public void testToJsonParse() throws Exception {
        TopicBroker tb = new TopicBroker(10, "0-m");
        String json = tb.toJson();
        Assert.assertEquals(JSONUtils.deserializeObject("{\"numParts\":10,\"broker\":\"0-m\"}", Map.class), JSONUtils.deserializeObject(json, Map.class));
        System.out.println(json);
        TopicBroker parsed = TopicBroker.parse(json);
        Assert.assertNotSame(tb, parsed);
        Assert.assertEquals(tb, parsed);
    }
}


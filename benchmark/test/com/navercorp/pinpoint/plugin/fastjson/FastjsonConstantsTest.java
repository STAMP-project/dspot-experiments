package com.navercorp.pinpoint.plugin.fastjson;


import FastjsonConstants.ANNOTATION_KEY_JSON_LENGTH;
import FastjsonConstants.CONFIG;
import FastjsonConstants.SCOPE;
import FastjsonConstants.SERVICE_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class FastjsonConstantsTest {
    @Test
    public void test() {
        Assert.assertEquals(SCOPE, "FASTJSON_SCOPE");
        Assert.assertEquals(CONFIG, "profiler.json.fastjson");
        Assert.assertEquals(SERVICE_TYPE.getCode(), 5013);
        Assert.assertEquals(SERVICE_TYPE.getName(), "FASTJSON");
        Assert.assertEquals(ANNOTATION_KEY_JSON_LENGTH.getCode(), 9003);
        Assert.assertEquals(ANNOTATION_KEY_JSON_LENGTH.getName(), "fastjson.json.length");
    }
}


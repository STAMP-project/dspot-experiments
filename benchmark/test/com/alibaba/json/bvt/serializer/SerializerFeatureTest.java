package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializerFeatureTest extends TestCase {
    public void test_0() throws Exception {
        int feature = 0;
        feature = SerializerFeature.config(feature, BrowserCompatible, true);
        Assert.assertEquals(true, SerializerFeature.isEnabled(feature, BrowserCompatible));
        feature = SerializerFeature.config(feature, BrowserCompatible, false);
        Assert.assertEquals(false, SerializerFeature.isEnabled(feature, BrowserCompatible));
    }

    public void test_1() throws Exception {
        int feature = 0;
        feature = SerializerFeature.config(feature, BrowserSecure, true);
        Assert.assertEquals(true, SerializerFeature.isEnabled(feature, BrowserSecure));
        feature = SerializerFeature.config(feature, BrowserSecure, false);
        Assert.assertEquals(false, SerializerFeature.isEnabled(feature, BrowserSecure));
    }

    public void test_assert_cnt() throws Exception {
        int len = SerializerFeature.values().length;
        TestCase.assertTrue((len <= 32));
    }
}


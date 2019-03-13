package com.alibaba.json.test;


import SerializerFeature.DisableCircularReferenceDetect;
import SerializerFeature.SortField;
import SerializerFeature.WriteSlashAsSpecial;
import SerializerFeature.WriteTabAsSpecial;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("deprecation")
public class TestWriteSlashAsSpecial extends TestCase {
    public void test_writeSlashAsSpecial() throws Exception {
        int features = JSON.DEFAULT_GENERATE_FEATURE;
        features = SerializerFeature.config(features, WriteSlashAsSpecial, true);
        features = SerializerFeature.config(features, WriteTabAsSpecial, true);
        features = SerializerFeature.config(features, DisableCircularReferenceDetect, true);
        features = SerializerFeature.config(features, SortField, false);
        Assert.assertEquals("\"\\/\"", JSON.toJSONString("/", features));
    }
}


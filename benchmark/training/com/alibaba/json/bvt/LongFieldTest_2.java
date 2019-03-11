package com.alibaba.json.bvt;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldTest_2 extends TestCase {
    public void test_min() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2.V0 v1 = JSON.parseObject(text, LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_min_reader() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2.V0 v1 = new JSONReader(new StringReader(text)).readObject(LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MAX_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2.V0 v1 = JSON.parseObject(text, LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max_reader() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MAX_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2.V0 v1 = new JSONReader(new StringReader(text)).readObject(LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_min_array() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2.V0 v1 = JSON.parseObject(text, LongFieldTest_2.V0.class, SupportArrayToBean);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_min_array_reader() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2.V0 v1 = readObject(LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max_array() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MAX_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2.V0 v1 = JSON.parseObject(text, LongFieldTest_2.V0.class, SupportArrayToBean);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max_array_reader() throws Exception {
        LongFieldTest_2.V0 v = new LongFieldTest_2.V0();
        v.setValue(Long.MAX_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2.V0 v1 = readObject(LongFieldTest_2.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public static class V0 {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}


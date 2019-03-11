package com.alibaba.json.bvt;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldTest_2_private extends TestCase {
    public void test_min() throws Exception {
        LongFieldTest_2_private.V0 v = new LongFieldTest_2_private.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2_private.V0 v1 = JSON.parseObject(text, LongFieldTest_2_private.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max() throws Exception {
        LongFieldTest_2_private.V0 v = new LongFieldTest_2_private.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        LongFieldTest_2_private.V0 v1 = JSON.parseObject(text, LongFieldTest_2_private.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_min_array() throws Exception {
        LongFieldTest_2_private.V0 v = new LongFieldTest_2_private.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2_private.V0 v1 = JSON.parseObject(text, LongFieldTest_2_private.V0.class, SupportArrayToBean);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max_array() throws Exception {
        LongFieldTest_2_private.V0 v = new LongFieldTest_2_private.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        LongFieldTest_2_private.V0 v1 = JSON.parseObject(text, LongFieldTest_2_private.V0.class, SupportArrayToBean);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    private static class V0 {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}


package com.alibaba.json.bvt;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldTest_2_stream extends TestCase {
    public void test_min() throws Exception {
        LongFieldTest_2_stream.V0 v = new LongFieldTest_2_stream.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        JSONReader reader = new JSONReader(new StringReader(text));
        LongFieldTest_2_stream.V0 v1 = reader.readObject(LongFieldTest_2_stream.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
        reader.close();
    }

    public void test_max() throws Exception {
        LongFieldTest_2_stream.V0 v = new LongFieldTest_2_stream.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v);
        JSONReader reader = new JSONReader(new StringReader(text));
        LongFieldTest_2_stream.V0 v1 = reader.readObject(LongFieldTest_2_stream.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_min_array() throws Exception {
        LongFieldTest_2_stream.V0 v = new LongFieldTest_2_stream.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
        LongFieldTest_2_stream.V0 v1 = reader.readObject(LongFieldTest_2_stream.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_max_array() throws Exception {
        LongFieldTest_2_stream.V0 v = new LongFieldTest_2_stream.V0();
        v.setValue(Long.MIN_VALUE);
        String text = JSON.toJSONString(v, BeanToArray);
        JSONReader reader = new JSONReader(new StringReader(text), Feature.SupportArrayToBean);
        LongFieldTest_2_stream.V0 v1 = reader.readObject(LongFieldTest_2_stream.V0.class);
        Assert.assertEquals(v.getValue(), v1.getValue());
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


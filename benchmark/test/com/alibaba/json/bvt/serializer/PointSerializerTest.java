package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.awt.Point;
import junit.framework.TestCase;
import org.junit.Assert;


public class PointSerializerTest extends TestCase {
    public void test_null() throws Exception {
        PointSerializerTest.VO vo = new PointSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Point value;

        public Point getValue() {
            return value;
        }

        public void setValue(Point value) {
            this.value = value;
        }
    }
}


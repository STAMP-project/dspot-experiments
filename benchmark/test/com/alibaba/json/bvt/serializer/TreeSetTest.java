package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collection;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class TreeSetTest extends TestCase {
    public void test_null() throws Exception {
        TreeSetTest.VO vo = new TreeSetTest.VO();
        vo.setValue(new TreeSet());
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.serializer.TreeSetTest$VO\",\"value\":TreeSet[]}", JSON.toJSONString(vo, WriteClassName));
    }

    public static class VO {
        private Collection value;

        public Collection getValue() {
            return value;
        }

        public void setValue(Collection value) {
            this.value = value;
        }
    }
}


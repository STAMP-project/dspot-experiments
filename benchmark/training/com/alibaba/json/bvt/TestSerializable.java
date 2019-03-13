package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.ArrayList;
import junit.framework.TestCase;


public class TestSerializable extends TestCase {
    public void test_codec() throws Exception {
        TestSerializable.VO vo = new TestSerializable.VO();
        vo.setValue(new ArrayList());
        JSON.toJSONString(vo);
    }

    public static class VO {
        private long id;

        private Serializable value;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Serializable getValue() {
            return value;
        }

        public void setValue(Serializable value) {
            this.value = value;
        }
    }
}


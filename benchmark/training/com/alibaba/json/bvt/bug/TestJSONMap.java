package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestJSONMap extends TestCase {
    public void test_0() throws Exception {
        TestJSONMap.Record record = new TestJSONMap.Record();
        Map map = new HashMap();
        record.setRecord(map);
        String s = JSON.toJSONString(record, WriteClassName);
        System.out.println(s);
        record = ((TestJSONMap.Record) (JSON.parse(s)));// ??????

        System.out.println(record.getRecord().size());
    }

    public static class Record {
        private Map<Integer, Integer> record;

        public Map<Integer, Integer> getRecord() {
            return record;
        }

        public void setRecord(Map<Integer, Integer> record) {
            this.record = record;
        }
    }
}


package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;


public class Bug_for_dubbo2 extends TestCase {
    public void test_emptyHashMap() throws Exception {
        Bug_for_dubbo2.VO vo = new Bug_for_dubbo2.VO();
        vo.setValue(new HashMap());
        String text = JSON.toJSONString(vo, WriteClassName);
        JSON.parse(text);
    }

    public static class VO {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}


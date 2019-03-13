package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_252 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_252.VO vo = new Bug_for_issue_252.VO();
        String text = JSON.toJSONString(vo, WriteMapNullValue);
        Assert.assertEquals("{\"type\":null}", text);
    }

    public static class VO {
        private Class<?> type;

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }
    }
}


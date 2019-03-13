package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_Issue_534 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_Issue_534.Value value = new Bug_for_Issue_534.Value();
        value.aLong = 2459838341588L;
        String text = JSON.toJSONString(value);
        Assert.assertEquals("{\"aLong\":2459838341588}", text);
    }

    public void test_for_issue_1() throws Exception {
        Long value = 2459838341588L;
        String text = JSON.toJSONString(value);
        Assert.assertEquals("2459838341588", text);
    }

    class Value {
        private Long aLong;

        public Long getaLong() {
            return aLong;
        }

        public void setaLong(Long aLong) {
            this.aLong = aLong;
        }
    }
}


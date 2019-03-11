package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_316 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_316.Model model = new Bug_for_issue_316.Model();
        model.value = new Timestamp(1460563200000L);
        Assert.assertEquals("{\"value\":1460563200000}", JSON.toJSONString(model));
    }

    public static class Model {
        public Timestamp value;
    }
}


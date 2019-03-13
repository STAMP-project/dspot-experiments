package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


public class Issue2244 extends TestCase {
    public void test_for_issue() throws Exception {
        String str = "\"2019-01-14T06:32:09.029Z\"";
        JSON.parseObject(str, Date.class);
    }
}


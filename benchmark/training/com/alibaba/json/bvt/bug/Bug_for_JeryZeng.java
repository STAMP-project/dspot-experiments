package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_JeryZeng extends TestCase {
    public void test_0() throws Exception {
        System.out.println(JSON.parseObject("{123:123,124:true,\"value\":{123:\"abc\"}}"));
    }
}


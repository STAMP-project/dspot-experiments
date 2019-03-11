package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvtVO.AccessHttpConfigModel;
import junit.framework.TestCase;


public class Bug_for_yaoming_1 extends TestCase {
    public void test_0() throws Exception {
        JSON.parseObject("{}", AccessHttpConfigModel.class);
    }
}


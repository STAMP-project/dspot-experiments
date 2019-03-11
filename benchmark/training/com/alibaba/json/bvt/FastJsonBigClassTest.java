package com.alibaba.json.bvt;


import SerializerFeature.IgnoreNonFieldGetter;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvtVO.BigClass;
import junit.framework.TestCase;


public class FastJsonBigClassTest extends TestCase {
    public void test_big_class() {
        BigClass bigObj = new BigClass();
        String json = JSON.toJSONString(bigObj, IgnoreNonFieldGetter);
        // assertThat(json, not(containsString("skipme")));
    }
}


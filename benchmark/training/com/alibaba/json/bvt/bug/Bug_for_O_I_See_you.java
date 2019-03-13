package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_O_I_See_you extends TestCase {
    public void test_bug() throws Exception {
        Object[] arra = new Object[]{ "aa", "bb" };
        Object[] arr = new Object[]{ "sssss", arra };
        String s = JSON.toJSONString(arr);
        Object[] ar = JSON.parseObject(s, Object[].class);
        System.out.println();
    }
}


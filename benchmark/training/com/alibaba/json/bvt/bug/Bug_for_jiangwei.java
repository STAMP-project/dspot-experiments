package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_jiangwei extends TestCase {
    public void test_0() throws Exception {
        String text = "[\'42-0\',\'\u8d85\u7d1a\u806f\u968a\\x28\u4e2d\\x29\',\'\u8f9b\u7576\u65af\',\'1.418\',10,\'11/18/2012 02:15\',1,0,1,0,\'\',0,0,0,0]";
        JSON.parse(text);
    }
}


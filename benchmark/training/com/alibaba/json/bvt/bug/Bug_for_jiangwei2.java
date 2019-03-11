package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import junit.framework.TestCase;


public class Bug_for_jiangwei2 extends TestCase {
    public void test_for_jiangwei() throws Exception {
        // String str = "?[2,'??????','????(??)','??SK?? ??',['?/?',3],'??? : 138.5 @ 0-0','','?','0.66','',1,25,200,1,0,0,'True','False',0,'','','',0,0,19819905,1,'h',145528,0]";
        // JSONArray array = JSON.parseArray(str);
        String str = "[]";
        str = "?[]";
        JSONArray array = JSON.parseArray(str);
    }
}


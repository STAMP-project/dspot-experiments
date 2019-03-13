package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/04/2017.
 */
public class Issue1140 extends TestCase {
    public void test_for_issue() throws Exception {
        String s = "\ud83c\uddeb\ud83c\uddf7";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JSON.writeJSONString(out, s);
    }
}


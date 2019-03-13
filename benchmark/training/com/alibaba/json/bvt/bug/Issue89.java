package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue89 extends TestCase {
    public void test_for_issue() throws Exception {
        Exception error = null;
        try {
            JSON.parse("{\"a\":\u0437\u300d\u2220)_,\"}");
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}


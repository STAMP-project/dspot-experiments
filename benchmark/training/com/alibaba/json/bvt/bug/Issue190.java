package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue190 extends TestCase {
    public void test_for_issue() throws Exception {
        Assert.assertEquals(Issue190.WebSoscketCommand.A, JSON.parseObject("\"A\"", Issue190.WebSoscketCommand.class));
    }

    public static enum WebSoscketCommand {

        A,
        B,
        C;}
}


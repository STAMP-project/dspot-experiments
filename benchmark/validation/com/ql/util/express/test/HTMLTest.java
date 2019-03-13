package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class HTMLTest {
    @Test
    public void testABC() throws Exception {
        // String express ="\"<div style=\\\"font-family:??;font-size:12px;line-height:25px;\\\">?????\"";
        ExpressRunner runner = new ExpressRunner(false, true);
        String express = "\"\u7ecf\\\"\u8d39\u6536\\\"\u5165\\\"aaa-\" + 100";
        Object r = runner.execute(express, null, null, false, true);
        System.out.println(r);
        System.out.println("\u7ecf\"\u8d39\u6536\"\u5165\"aaa-100");
        Assert.assertTrue("????????", r.equals("\u7ecf\"\u8d39\u6536\"\u5165\"aaa-100"));
    }
}


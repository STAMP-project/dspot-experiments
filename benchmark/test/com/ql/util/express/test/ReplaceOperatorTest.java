package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceOperatorTest {
    @Test
    public void testReplaceOperatorTest() throws Exception {
        String express = " 3 + 4";
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        Assert.assertTrue("?????", r.toString().equalsIgnoreCase("7"));
        runner.replaceOperator("+", new ReplaceOperatorAddReduce("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new ReplaceOperatorAddReduce2("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new ReplaceOperatorAddReduce("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        Assert.assertTrue("????????", r.toString().equalsIgnoreCase("(3*4)"));
    }
}


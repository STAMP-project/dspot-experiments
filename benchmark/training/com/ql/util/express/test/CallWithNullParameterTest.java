package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class CallWithNullParameterTest {
    @Test
    public void testABC() throws Exception {
        String express = "new com.ql.util.express.test.BeanExample().testLongObject(null);";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, false, true);
        System.out.println(r);
        Assert.assertTrue("????????", r.toString().equalsIgnoreCase("toString-LongObject:null"));
    }
}


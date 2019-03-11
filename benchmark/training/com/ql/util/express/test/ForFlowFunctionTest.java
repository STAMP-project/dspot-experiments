package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class ForFlowFunctionTest {
    @Test
    public void testABC() throws Exception {
        String express = "for(i=0;i<1;i=i+1){" + ("??(70)" + "}??(70); return 10");
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("??", System.out, "println", new String[]{ "int" }, null);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("for???????????????", r.toString().equals("10"));
    }
}


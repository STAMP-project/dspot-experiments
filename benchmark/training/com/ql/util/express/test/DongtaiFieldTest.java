package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetRunner;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;


public class DongtaiFieldTest {
    private static final Log log = LogFactory.getLog(DongtaiFieldTest.class);

    @Test
    public void testField() throws Exception {
        String express = "String \u7528\u6237 = \"\u5f20\u4e09\";" + (("??.??  = 100;" + "\u7528\u6237 = \"\u674e\u56db\";") + "??.??  = 200;");
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Map<String, Object> fee = new HashMap<String, Object>();
        context.put("??", fee);
        InstructionSet set = runner.parseInstructionSet(express);
        InstructionSetRunner.executeOuter(runner, set, null, context, null, true, false, null, true);
        runner.execute(express, context, null, false, true);
        System.out.println(context.get("??"));
        Assert.assertTrue("??????", fee.get("??").toString().equals("100"));
        Assert.assertTrue("??????", fee.get("??").toString().equals("200"));
    }

    @Test
    public void testLoadFromFile() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.loadExpress("TestFunctionParamerType");
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("auctionUtil", new BeanExample());
        context.put("log", DongtaiFieldTest.log);
        Object r = runner.executeByExpressName("TestFunctionParamerType", context, null, false, false, null);
        System.out.println(r);
        System.out.println(context);
    }
}


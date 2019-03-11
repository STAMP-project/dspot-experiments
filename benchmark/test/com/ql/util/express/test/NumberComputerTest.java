package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


public class NumberComputerTest {
    @Test
    public void testBigDecimalComputer() throws Exception {
        System.out.println((1.0 - 0.42));// 0.5800000000000001

        String expressString = "1.0-0.42";
        ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(expressString, context, null, false, false);
        System.out.println(r);// 0.58

        Assert.assertTrue("??????", r.toString().equals("0.58"));
    }

    @Test
    public void testBigDecimalTransfer() throws Exception {
        String expressString = "System.out.println(new java.math.BigDecimal(0.02))";
        ExpressRunner runner = new ExpressRunner(false, false);
        BeanExample bean = new BeanExample();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("bean", bean);
        Object r = runner.execute(expressString, context, null, false, false);
        System.out.println(r);
    }

    @Test
    public void testBigDecimal() throws Exception {
        String expressString = "bean.intValue = 10;" + (("bean.longValue = 10000;" + "bean.doubleValue = bean.intValue + 100.01;") + "return bean.doubleValue + 10;");
        ExpressRunner runner = new ExpressRunner(false, false);
        BeanExample bean = new BeanExample();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("bean", bean);
        Object r = runner.execute(expressString, context, null, false, false);
        Assert.assertTrue("??????", r.getClass().equals(Double.class));
        runner = new ExpressRunner(true, true);
        bean = new BeanExample();
        context = new DefaultContext<String, Object>();
        context.put("bean", bean);
        r = runner.execute(expressString, context, null, false, false);
        Assert.assertTrue("??????", r.getClass().equals(BigDecimal.class));
    }

    @Test
    public void testMod() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        Assert.assertTrue("Mod????", runner.execute("20 mod 5", null, null, true, true).toString().equalsIgnoreCase("0"));
        Assert.assertTrue("Mod????", runner.execute("20 mod 3", null, null, true, true).toString().equalsIgnoreCase("2"));
        Assert.assertTrue("Mod????", runner.execute("20 mod 1", null, null, true, true).toString().equalsIgnoreCase("0"));
    }
}


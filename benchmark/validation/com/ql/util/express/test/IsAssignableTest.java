package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import java.util.AbstractList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class IsAssignableTest {
    @Test
    public void testABC() throws Exception {
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(boolean.class, Boolean.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(char.class, Character.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(long.class, int.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(Long.class, int.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(Long.class, Integer.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(List.class, AbstractList.class)) == true));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(List.class, AbstractList.class)) == (ExpressUtil.isAssignableOld(List.class, AbstractList.class))));
        Assert.assertTrue("??????????", ((ExpressUtil.isAssignable(long.class, int.class)) == (ExpressUtil.isAssignableOld(long.class, int.class))));
        int index = ExpressUtil.findMostSpecificSignature(new Class[]{ Integer.class }, new Class[][]{ new Class[]{ Integer.class }, new Class[]{ int.class } });
        System.out.println(index);
        String express = "bean.testInt(p)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("bean", new BeanExample());
        context.put("p", 100);
        Object r = runner.execute(express, context, null, false, true);
        System.out.println(r);
        Assert.assertTrue("?????????", r.toString().equalsIgnoreCase("toString-int:100"));
        context = new DefaultContext<String, Object>();
        express = "bean.testLong(p)";
        context.put("bean", new BeanExample());
        context.put("p", 100L);
        r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("?????????", r.toString().equalsIgnoreCase("toString-long:100"));
    }
}


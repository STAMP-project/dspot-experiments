package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class DefineTest {
    @Test
    public void testDefExpressInner() throws Exception {
        String express = "int qh =  1 ";
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ExpressRunner runner = new ExpressRunner(false, true);
        context.put("qh", 100);
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("??????????", r.toString().equalsIgnoreCase("1"));
        Assert.assertTrue("??????????", context.get("qh").toString().equalsIgnoreCase("100"));
    }

    @Test
    public void testDefUserContext() throws Exception {
        String express = "qh = 1 + 1";
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ExpressRunner runner = new ExpressRunner();
        context.put("qh", 100);
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("??????????", r.toString().equalsIgnoreCase("2"));
        Assert.assertTrue("??????????", context.get("qh").toString().equalsIgnoreCase("2"));
    }

    @Test
    public void testAlias() throws Exception {
        String express = " ???? qh example.child; " + (((("{???? qh example.child.a;" + " qh =qh + \"-ssss\";") + "};") + " qh.a = qh.a +\"-qh\";") + " return example.child.a");
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("????", "alias", null);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("example", new BeanExample());
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("?", "then", null);
        runner.addOperatorWithAlias("??", "else", null);
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("???? ??", r.toString().equalsIgnoreCase("qh-ssss-qh"));
        Assert.assertTrue("???? ??", ((BeanExample) (context.get("example"))).child.a.toString().equalsIgnoreCase("qh-ssss-qh"));
    }

    @Test
    public void testMacro() throws Exception {
        String express = "???  ??   {bean.unionName(name)}; ??; return  ??";
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("???", "macro", null);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
        context.put("name", "xuannn");
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("??? ??", r.toString().equalsIgnoreCase("qhlhl2010@gmail.com-xuannn"));
        System.out.println(r);
    }

    @Test
    public void test_?????() throws Exception {
        String express = "????  ??(int a){" + ((((((" if(a == 1)then{ " + "   return 1;") + "  }else{ ") + "     return ??(a - 1) *  a;") + "  } ") + "}; ") + "??(10);");
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("????", "function", null);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, true, false);
        Assert.assertTrue("????? ??", r.toString().equals("3628800"));
    }

    @Test
    public void testProperty() throws Exception {
        // String express =" cache isVIP(\"qh\") ;  cache isVIP(\"xuannan\") cache isVIP(\"qh\") ;";
        String express = " example.child.a = \"ssssssss\";" + (" map.name =\"ffff\";" + "return map.name;");
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("example", new BeanExample("??"));
        context.put("map", new HashMap<String, Object>());
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(), "isVIP", new String[]{ "String" }, "$1??VIP??");
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("??????", r.toString().equalsIgnoreCase("ffff"));
        Assert.assertTrue("??????", ((BeanExample) (context.get("example"))).child.a.toString().equalsIgnoreCase("ssssssss"));
    }

    @Test
    public void test_??() throws Exception {
        long s = System.currentTimeMillis();
        String express = "qh = 0; ??(int i = 1;  i<=10;i = i + 1){ if(i > 5) then{ ??;}; " + ((((((("??(int j=0;j<10;j= j+1){  " + "    if(j > 5)then{") + "       ??;") + "    }; ") + "    qh = qh + j;") + // "   ??(i +\":\" + j+ \":\" +qh);"+
        " };  ") + "};") + "return qh;");
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("??", "for", null);
        runner.addOperatorWithAlias("??", "continue", null);
        runner.addOperatorWithAlias("??", "break", null);
        runner.addFunctionOfServiceMethod("??", System.out, "println", new String[]{ Object.class.getName() }, null);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
        context.put("name", "xuannn");
        int count = 1;
        s = System.currentTimeMillis();
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(("?????" + ((System.currentTimeMillis()) - s)));
        for (int i = 0; i < count; i++) {
            r = runner.execute(express, context, null, false, false);
            Assert.assertTrue("??????", r.toString().equals("75"));
        }
        System.out.println(("?????" + ((System.currentTimeMillis()) - s)));
        System.out.println(context);
    }
}


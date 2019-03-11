package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExpressTest {
    @Test
    public void testDemo() throws Exception {
        String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, null, null, false, false);
        Assert.assertTrue("?????", r.toString().equalsIgnoreCase("117"));
        System.out.println(((("??????" + express) + " = ") + r));
    }

    @Test
    public void tes10000?() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
        int num = 100000;
        runner.execute(express, null, null, true, false);
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            runner.execute(express, null, null, true, false);
        }
        System.out.println(((((("??" + num) + "\u6b21\"") + express) + "\" \u8017\u65f6\uff1a") + ((System.currentTimeMillis()) - start)));
    }

    @Test
    public void testExpress() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("?", "then", null);
        runner.addOperatorWithAlias("??", "else", null);
        runner.addOperator("love", new LoveOperator("love"));
        runner.addOperatorWithAlias("??", "in", "??$1???????");
        runner.addOperatorWithAlias("myand", "and", "??$1???????");
        runner.addFunction("??", new GroupOperator("??"));
        runner.addFunction("group", new GroupOperator("group"));
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(), "isVIP", new String[]{ "String" }, "$1??VIP??");
        runner.addFunctionOfClassMethod("????", Math.class.getName(), "abs", new String[]{ "double" }, null);
        runner.addFunctionOfClassMethod("????TWO", Math.class.getName(), "abs", new Class[]{ double.class }, null);
        runner.addFunctionOfClassMethod("?????", BeanExample.class.getName(), "upper", new String[]{ "String" }, null);
        runner.addFunctionOfClassMethod("testLong", BeanExample.class.getName(), "testLong", new String[]{ "long" }, null);
        String[][] expressTest = new String[][]{ new String[]{ "isVIP(\"qh\") ; isVIP(\"xuannan\"); return isVIP(\"qh\") ;", "false" }, new String[]{ "??  ????  ?  'a' love 'b'  ??   'b' love 'd' ", "b{a}b" }, new String[]{ "when  ????  then  'a' love 'b'  ??   'b' love 'd' ", "b{a}b" }, new String[]{ "int defVar = 100; defVar = defVar + 100;", "200" }, new String[]{ "int a=0; if false then a = 5 else  a=10+1 ; return a ", "11" }, new String[]{ " 3+ (1==2?4:3) +8", "14" }, new String[]{ " ??  (true) ? {2+2;} ?? {20 + 20;} ", "4" }, new String[]{ "\'AAAAAAA\' +\'-\' + \"\" +\'\' + \"B\"", "AAAAAAA-B" }, new String[]{ "System.out.println(\"ss\")", "null" }, new String[]{ "unionName = new com.ql.util.express.test.BeanExample(\"\u5f20\u4e09\").unionName(\"\u674e\u56db\")", "??-??" }, new String[]{ "group(2,3,4)", "9" }, new String[]{ "????(-5.0)", "5.0" }, new String[]{ "????TWO(-10.0)", "10.0" }, new String[]{ "max(2,3,4,10)", "10" }, new String[]{ "max(2,-1)", "2" }, new String[]{ "max(3,2) + \u8f6c\u6362\u4e3a\u5927\u5199(\"abc\")", "3ABC" }, new String[]{ "c = 1000 + 2000", "3000" }, new String[]{ "b = ??(1,2,3)+??(4,5,6)", "21" }, new String[]{ "???? and ???? ", "true" }, new String[]{ "new String(\"12345\").length()", "5" }, new String[]{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" }, new String[]{ "10 * (10 + 1) + 2 * (3 + 5) * 2", "142" }, new String[]{ "( 2  \u5c5e\u4e8e (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false" }, new String[]{ " 1!=1 and isVIP(\"qhlhl2010@gmail.com\")", "false" }, new String[]{ " 1==1 or isVIP(\"qhlhl2010@gmail.com\") ", "true" }, new String[]{ "abc == 1", "true" }, new String[]{ "2+2 in 2+2", "true" }, new String[]{ "true or null", "true" }, new String[]{ "null or true", "true" }, new String[]{ "null or null", "false" }, new String[]{ "true and null", "false" }, new String[]{ "null and true", "false" }, new String[]{ "null and null", "false" }, new String[]{ "'a' nor null", "a" }, new String[]{ "'a' nor 'b'", "a" }, new String[]{ " null nor null", "null" }, new String[]{ " null nor 'b'", "b" }, new String[]{ "testLong(abc)", "toString-long:1" }, new String[]{ "bean.testLongObject(abc)", "toString-LongObject:1" }, new String[]{ "sum=0;n=7.3;for(i=0;i<n;i=i+1){sum=sum+i;};sum;", "28" }, new String[]{ "0 in (16,50008090,9,8,7,50011397,50013864,28,1625,50006842,50020808,50020857,50008164,50020611,50008163,50023804,50020332,27)", "false" } };
        IExpressContext<String, Object> expressContext = new ExpressContextExample(null);
        expressContext.put("b", new Integer(200));
        expressContext.put("c", new Integer(300));
        expressContext.put("d", new Integer(400));
        expressContext.put("bean", new BeanExample());
        expressContext.put("abc", 1L);
        expressContext.put("defVar", 1000);
        for (int point = 0; point < (expressTest.length); point++) {
            String expressStr = expressTest[point][0];
            List<String> errorList = new ArrayList<String>();
            Object result = runner.execute(expressStr, expressContext, null, false, true);
            if (((expressTest[point][1].equalsIgnoreCase("null")) && (result != null)) || ((result != null) && ((expressTest[point][1].equalsIgnoreCase(result.toString())) == false))) {
                throw new Exception(((((("????,???????????:" + expressStr) + " = ") + result) + "???????") + (expressTest[point][1])));
            }
            System.out.println(((((("Example " + point) + " : ") + expressStr) + " =  ") + result));
            if ((errorList.size()) > 0) {
                System.out.println(("\t\t\u7cfb\u7edf\u8f93\u51fa\u7684\u9519\u8bef\u63d0\u793a\u4fe1\u606f:" + errorList));
            }
        }
        System.out.println(expressContext);
    }
}


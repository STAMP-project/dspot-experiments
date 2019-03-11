package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Assert;
import org.junit.Test;


public class GetExpressFunctionNamesTest {
    @Test
    public void testFunctionDefine() throws Exception {
        String express = "function fun1(){return null;} function fun2(int a,int b,int c){return a*b+c;} a =fun1();b=fun2(1,2,3)+fun3(1,2);";
        ExpressRunner runner = new ExpressRunner(false, true);
        String[] names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println(("function : " + s));
        }
        Assert.assertTrue("????????", ((names.length) == 1));
        Assert.assertTrue("????????", names[0].equalsIgnoreCase("fun3"));
        // ??:
        // ??????function?????????fun3, fun3?????????operator,?????????fun3????
        // (1)???function
        runner = new ExpressRunner(true, true);
        runner.addFunctionOfServiceMethod("fun3", this, "fun3", new Class[]{ Object.class, Object.class }, null);
        names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println(("function : " + s));
        }
        Assert.assertTrue("????????", ((names.length) == 0));
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(("result : " + r));
        // (2)????function
        runner = new ExpressRunner(true, true);
        runner.addFunction("fun3", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                String s = "";
                for (Object obj : list) {
                    s = s + obj;
                }
                return s;
            }
        });
        names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println(("function : " + s));
        }
        Assert.assertTrue("????????", ((names.length) == 0));
        context = new com.ql.util.express.DefaultContext<String, Object>();
        r = runner.execute(express, context, null, false, false);
        System.out.println(("result : " + r));
    }
}


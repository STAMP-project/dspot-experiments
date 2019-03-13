package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;


/**
 *
 *
 * @author tianqiao
 */
public class MethodInvokeTest {
    @Test
    public void testNullParameter() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfClassMethod("getSearchResult", MethodInvokeTest.class.getName(), "getSearchResult", new Class[]{ PersonalShopInfo.class }, null);
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        Object r = runner.execute("getSearchResult(null)", expressContext, null, false, false);
        System.out.print(("r=" + r));
    }

    @Test
    public void testNullParameter2() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfClassMethod("getOnlinePersonalShopInfo", MethodInvokeTest.class.getName(), "getOnlinePersonalShopInfo", new Class[]{ long.class }, null);
        runner.addFunctionOfClassMethod("getSearchResult", MethodInvokeTest.class.getName(), "getSearchResult", new Class[]{ PersonalShopInfo.class }, null);
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        Object r = runner.execute("getSearchResult(getOnlinePersonalShopInfo(123L))", expressContext, null, false, false);
        System.out.print(("r=" + r));
    }

    @Test
    public void testNullParameter3() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfClassMethod("getOnlinePersonalShopInfo", MethodInvokeTest.class.getName(), "getOnlinePersonalShopInfo", new Class[]{ long.class }, null);
        String express = "info = getOnlinePersonalShopInfo(127L);";
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        Object r = runner.execute(express, expressContext, null, false, false);
        System.out.print(("r=" + r));
    }

    @Test
    public void testNullParameter4() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfClassMethod("getOnlinePersonalShopInfo", MethodInvokeTest.class.getName(), "getOnlinePersonalShopInfo", new Class[]{ long.class }, null);
        String express = "info = getOnlinePersonalShopInfo(127L);";
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        Object r = runner.execute(express, expressContext, null, false, false);
        System.out.print(("r=" + r));
    }

    @Test
    public void testNullParameter5() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfClassMethod("testVoidMethod", MethodInvokeTest.class.getName(), "testVoidMethod", new Class[]{ Long.class }, null);
        String express = "info = testVoidMethod(1L);";
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        try {
            Object r = runner.execute(express, expressContext, null, false, false);
            System.out.println(("r=" + r));
        } catch (Exception e) {
            System.out.println("return void checked success!");
            return;
        }
        throw new Exception("return void exception not checked!");
    }
}


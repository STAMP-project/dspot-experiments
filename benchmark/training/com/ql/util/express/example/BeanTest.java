package com.ql.util.express.example;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;


public class BeanTest {
    @Test
    public void test1() throws Exception {
        String exp = "import com.ql.util.express.example.CustBean;" + (("CustBean cust = new CustBean(1);" + "cust.setName(\"\u5c0f\u5f3a\");") + "return cust.getName();");
        ExpressRunner runner = new ExpressRunner();
        // ????????????r
        String r = ((String) (runner.execute(exp, null, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", "??".equals(r));
    }

    @Test
    public void test2() throws Exception {
        String exp = "cust.setName(\"\u5c0f\u5f3a\");" + // "cust.name = \"??\";" +
        "return cust.getName();";
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();
        // ????????????r
        String r = ((String) (runner.execute(exp, expressContext, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", "??".equals(r));
    }

    @Test
    public void test3() throws Exception {
        String exp = "\u9996\u5b57\u6bcd\u5927\u5199(\"abcd\")";
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfClassMethod("?????", CustBean.class.getName(), "firstToUpper", new String[]{ "String" }, null);
        // ????????????r
        String r = ((String) (runner.execute(exp, null, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", "Abcd".equals(r));
    }

    /**
     * ????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAlias() throws Exception {
        String exp = "cust.setName(\"\u5c0f\u5f3a\");" + ("???? custName cust.name;" + "return custName;");
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();
        // 
        runner.addOperatorWithAlias("????", "alias", null);
        // ????????????r
        String r = ((String) (runner.execute(exp, expressContext, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", "??".equals(r));
    }

    /**
     * ???
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMacro() throws Exception {
        String exp = "cust.setName(\"\u5c0f\u5f3a\");" + ("??? custName {cust.name};" + "return custName;");
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();
        // 
        runner.addOperatorWithAlias("???", "macro", null);
        // ????????????r
        String r = ((String) (runner.execute(exp, expressContext, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", "??".equals(r));
    }
}


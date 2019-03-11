package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;


public class AClassDefine {
    @Test
    public void testNewVClass() throws Exception {
        String express = "" + ((((((("int a = 100;" + "class ABC(){") + " int a = 200;") + " println a;") + "}") + "ABC  abc = new ABC();") + "println a + abc.a;") + "return a + abc.a;");
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.loadMutilExpress("ClassTest", express);
        Object r = runner.executeByExpressName("ClassTest", context, null, false, false, null);
        Assert.assertTrue("VClass??????", r.toString().equalsIgnoreCase("300"));
    }

    @Test
    public void testABC() throws Exception {
        String expressDefine = "class ABC(com.ql.util.express.test.BeanExample bean,String name){" + ((((((((("??= name;" + "??? = new InnerClass();") + "???:bean.intValue;") + "???:bean.doubleValue;") + "???:{bean.hashCode();};") + "function add(int a,int b){return a + b + ??? + ???.??;};") + "class InnerClass(){") + "int ?? =200;") + "};") + "};");
        String express = "ABC example = new ABC(new com.ql.util.express.test.BeanExample(),'xuannan');" + ((((((((("ABC example2 = new ABC(new com.ql.util.express.test.BeanExample(),'xuanyu');" + " example.??? =100;") + " example2.??? =200;") + " example.???= 99.99;") + " example2.???= 11.11;") + " example.??? = example.??? + example.???;") + " result = example.add(10,20) +'--'+ example2.add(10,20) +'--'+  example.?? +'--'+ example2.?? +'--'+ example.??? +'--' + example2.??? ;") + " println result;") + " return result ;") + "");
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.loadMutilExpress("", expressDefine);
        runner.loadMutilExpress("ClassTest", express);
        Object r = runner.executeByExpressName("ClassTest", context, null, false, false, null);
        Assert.assertTrue("VClass??????", r.toString().equalsIgnoreCase("330--430--xuannan--xuanyu--199.99--11.11"));
    }
}


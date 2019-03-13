package com.ql.util.express.test;


import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;


/**
 * tianqiao
 * 2016-09-12
 */
public class TestMethodParams {
    @Test
    public void testMethodDynamicDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        // (1)????????????????
        Object r = runner.execute("a = new com.ql.util.express.test.TestMethodParams();a.getTemplate([11,'22',33L,true])", expressContext, null, false, false);
        System.out.println(r);
        // (2)???????Object[]?????????
        Object r2 = runner.execute("a = new com.ql.util.express.test.TestMethodParams();a.getTemplate(11,'22',33L,true)", expressContext, null, false, false);
        System.out.println(r2);
    }

    @Test
    public void testDynamicDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        runner.addFunctionOfServiceMethod("getTemplate", this, "getTemplate", new Class[]{ Object[].class }, null);
        runner.addFunctionOfClassMethod("getTemplateStatic", TestMethodParams.class.getName(), "getTemplateStatic", new Class[]{ Object.class, String[].class }, null);
        // (1)????????????????
        Object r = runner.execute("getTemplate([11,'22',33L,true])", expressContext, null, false, false);
        System.out.println(r);
        // (2)?java??,??????????,??????????,?????????
        DynamicParamsUtil.supportDynamicParams = true;
        r = runner.execute("getTemplate(11,'22',33L,true)", expressContext, null, false, false);
        System.out.println(r);
        r = runner.execute("getTemplateStatic('22','33','44')", expressContext, null, false, false);
        System.out.println(r);
    }

    @Test
    public void testMethodArrayParamType() throws Exception {
        Class<?> longClasss = Long.class;
        Class<?> numberClasss = Number.class;
        Class<?> stringClass = String.class;
        Class<?> objectClass = Object.class;
        assert numberClasss.isAssignableFrom(longClasss);
        assert objectClass.isAssignableFrom(numberClasss);
        assert objectClass.isAssignableFrom(stringClass);
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        runner.addFunctionOfServiceMethod("integerArrayInvoke", this, "integerArrayInvoke", new Class[]{ Integer[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvokeWithHead", this, "objectArrayInvokeWithHead", new Class[]{ String.class, Integer[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke", this, "objectArrayInvoke", new Class[]{ Object[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke_Integer", this, "objectArrayInvoke", new Class[]{ Integer[].class }, null);
        testInvoke("integerArrayInvoke([1,2,3,4])", "10", runner, expressContext);
        // null??
        testInvoke("integerArrayInvoke(null)", "0", runner, expressContext);
        testInvoke("integerArrayInvoke([null,1,2,3,null,4])", "10", runner, expressContext);
        testInvoke("integerArrayInvoke([null,null])", "0", runner, expressContext);
        testInvoke("objectArrayInvoke([null,null])", "", runner, expressContext);
        // ????
        testInvoke("objectArrayInvoke([1,2,3,null,4])", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke_Integer([1,2,3,null,4])", "10", runner, expressContext);
        // ????????Object
        testInvoke("objectArrayInvoke(['1',2,3,null,4])", "1,2,3,4,", runner, expressContext);
        // ??head
        testInvoke("objectArrayInvokeWithHead('hello:',[1,2,3,null,4])", "hello:10", runner, expressContext);
    }

    @Test
    public void testDynamicParams() throws Exception {
        DynamicParamsUtil.supportDynamicParams = true;
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        runner.addFunctionOfServiceMethod("integerArrayInvoke", this, "integerArrayInvoke", new Class[]{ Integer[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke", this, "objectArrayInvoke", new Class[]{ Object[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke_Integer", this, "objectArrayInvoke", new Class[]{ Integer[].class }, null);
        runner.addFunctionOfServiceMethod("objectArrayInvokeWithHead", this, "objectArrayInvokeWithHead", new Class[]{ String.class, Integer[].class }, null);
        testInvoke("integerArrayInvoke()", "0", runner, expressContext);
        testInvoke("integerArrayInvoke(null)", "0", runner, expressContext);
        testInvoke("integerArrayInvoke(1)", "1", runner, expressContext);
        testInvoke("integerArrayInvoke(1,2,3,4)", "10", runner, expressContext);
        // null??
        testInvoke("integerArrayInvoke(null,1,2,3,null,4)", "10", runner, expressContext);
        testInvoke("integerArrayInvoke(null,null)", "0", runner, expressContext);
        testInvoke("objectArrayInvoke(null,null)", "", runner, expressContext);
        // ????
        testInvoke("objectArrayInvoke()", "", runner, expressContext);
        testInvoke("objectArrayInvoke(null)", "", runner, expressContext);
        testInvoke("objectArrayInvoke(null,1,2,3,4)", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke(1,2,3,null,4)", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke_Integer(1,2,3,null,4)", "10", runner, expressContext);
        // ????????Object
        testInvoke("objectArrayInvoke('1',2,3,null,4)", "1,2,3,4,", runner, expressContext);
        // ??head
        testInvoke("objectArrayInvokeWithHead('hello:',1,2,3,null,4)", "hello:10", runner, expressContext);
    }
}


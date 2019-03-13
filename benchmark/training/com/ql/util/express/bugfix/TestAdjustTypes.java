package com.ql.util.express.bugfix;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;


/**
 * ????????case
 * Created by tianqiao on 17/6/20.
 */
public class TestAdjustTypes {
    public static TestAdjustTypes instance = new TestAdjustTypes();

    @Test
    public void testDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("test", TestAdjustTypes.instance, "test", new Class[]{ Integer.class }, null);
        runner.addFunctionOfServiceMethod("testString", TestAdjustTypes.instance, "test", new Class[]{ String.class }, null);
        String exp = "test(1) +test(1) + testString('aaaa')";
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }

    @Test
    public void testDemo2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("testAdjustTypes", TestAdjustTypes.instance);
        String exp = "testAdjustTypes.test(1) + testAdjustTypes.test(1) + testAdjustTypes.test('aaaa')";
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }

    @Test
    public void testDemo3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("testAdjustTypes", TestAdjustTypes.instance);
        String exp = "testAdjustTypes.test(1) + testAdjustTypes.test(1) + testAdjustTypes.test('aaaa')+ testAdjustTypes.test('aaaa','bbbbb')";
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}


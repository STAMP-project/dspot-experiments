package com.ql.util.express.bugfix;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;


/**
 * Created by tianqiao on 17/6/29.
 */
public class LuofanTest {
    public class Response {}

    @Test
    public void testDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfClassMethod("lenOfAds", LuofanTest.class.getName(), "lenOfAds", new String[]{ LuofanTest.Response.class.getName() }, null);
        String exp = "lenOfAds(resp)";
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("resp", new LuofanTest.Response());
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}


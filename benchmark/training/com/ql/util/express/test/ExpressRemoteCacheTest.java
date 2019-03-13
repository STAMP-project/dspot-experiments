package com.ql.util.express.test;


import com.ql.util.express.ExpressRemoteCacheRunner;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;


public class ExpressRemoteCacheTest {
    @Test
    public void testcache() {
        ExpressRunner runner = new ExpressRunner();
        ExpressRemoteCacheRunner cacheRunner = new com.ql.util.express.LocalExpressCacheRunner(runner);
        cacheRunner.loadCache("????", "a+b");
        cacheRunner.loadCache("????", "a-b");
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("a", 1);
        context.put("b", 2);
        if ((cacheRunner.getCache("????")) != null) {
            Object result = cacheRunner.execute("????", context, null, false, true, null);
            Assert.assertTrue("????", result.toString().equalsIgnoreCase("3"));
            System.out.println(result);
        }
        if ((cacheRunner.getCache("????")) != null) {
            Object result = cacheRunner.execute("????", context, null, false, true, null);
            Assert.assertTrue("????", result.toString().equalsIgnoreCase("-1"));
            System.out.println(result);
        }
        if ((cacheRunner.getCache("????")) != null) {
            Object result = cacheRunner.execute("????", context, null, false, true, null);
            Assert.assertTrue("????", result.toString().equalsIgnoreCase("2"));
            System.out.println(result);
        } else {
            System.out.println("?????????.");
        }
    }
}


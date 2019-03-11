package com.ql.util.express.test;


import com.ql.util.express.ExpressRemoteCacheRunner;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import java.util.Date;
import org.junit.Test;


/**
 * ??ExpressRunner?????????
 *
 * @author tianqiao
 */
public class ExpressCacheTest {
    ExpressRunner runner = new ExpressRunner();

    @Test
    public void testScriptCache() throws Exception {
        runner.addMacro("??????", "(??+??+??)/3.0");
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("??", 88);
        context.put("??", 99);
        context.put("??", 95);
        long times = 10000;
        long start = new Date().getTime();
        while ((times--) > 0) {
            calulateTask(false, context);
        } 
        long end = new Date().getTime();
        echo((("???????" + (end - start)) + " ms"));
        times = 10000;
        start = new Date().getTime();
        while ((times--) > 0) {
            calulateTask(true, context);
        } 
        end = new Date().getTime();
        echo((("??????" + (end - start)) + " ms"));
    }

    @Test
    public void testLocalCacheMutualImpact() throws Exception {
        // ????????????????????
        runner.addMacro("??????", "(??+??+??)/3.0");
        runner.addMacro("????", "??????>90");
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("??", 88);
        context.put("??", 99);
        context.put("??", 95);
        echo(runner.execute("????", context, null, false, false));
    }

    @Test
    public void testRemoteCache() {
        // ???????
        ExpressRunner runner = new ExpressRunner();
        ExpressRemoteCacheRunner cacheRunner = new com.ql.util.express.LocalExpressCacheRunner(runner);
        cacheRunner.loadCache("??????", "(??+??+??)/3.0");
        cacheRunner.loadCache("????", "??????>90");
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        context.put("??", 88);
        context.put("??", 99);
        context.put("??", 95);
        // ExpressRemoteCacheRunner??????????????????????????????????
        echo(cacheRunner.execute("??????", context, null, false, false, null));
        try {
            echo(cacheRunner.execute("??????>90", context, null, false, false, null));
        } catch (Exception e) {
            echo("ExpressRemoteCacheRunner????????????");
        }
        try {
            echo(cacheRunner.execute("????", context, null, false, false, null));
        } catch (Exception e) {
            echo("ExpressRemoteCacheRunner???????????");
        }
    }
}


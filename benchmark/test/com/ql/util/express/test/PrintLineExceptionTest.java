package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tianqiao on 17/4/1.
 */
public class PrintLineExceptionTest {
    @Test
    public void testLoadFromFile() throws Exception {
        String script = PrintLineExceptionTest.getResourceAsStream("lineTest.ql");
        ExpressRunner runner = new ExpressRunner(false, false);
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        try {
            Object obj = runner.execute(script, context, null, true, false);
            System.out.println(obj);
        } catch (Exception e) {
            // e.printStackTrace();
            Assert.assertTrue(e.toString().contains("at line 7"));
        }
    }
}


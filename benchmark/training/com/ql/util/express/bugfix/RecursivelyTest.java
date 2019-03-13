package com.ql.util.express.bugfix;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Test;


/**
 * Created by tianqiao on 17/3/2.
 */
public class RecursivelyTest {
    static ExpressRunner runner = new ExpressRunner();

    static ExpressRunner runnerInner = new ExpressRunner();

    static {
        Operator exeOperator = new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                System.out.println("executeInner:r_exeAll");
                IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
                RecursivelyTest.runnerInner.execute("1+2", context, null, false, true);
                System.out.println(list[0]);
                return null;
            }
        };
        RecursivelyTest.runner.addFunction("r_exeAll", exeOperator);
        RecursivelyTest.runnerInner.addFunction("r_exeAll", exeOperator);
    }

    @Test
    public void testAllByFunction() throws Exception {
        String exp = "r_exeAll(1,2,3)";
        IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
        RecursivelyTest.runner.execute(exp, context, null, false, true);
    }
}


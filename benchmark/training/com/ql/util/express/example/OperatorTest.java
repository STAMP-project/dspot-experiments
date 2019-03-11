package com.ql.util.express.example;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.AddNOperator;
import com.ql.util.express.example.operator.AddTwiceOperator;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?????????????????
 */
public class OperatorTest {
    /**
     * ????????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddTwice() throws Exception {
        // ????????? 1+(22+22)+(2+2)
        String exp = " 1 addT 22 addT 2";
        ExpressRunner runner = new ExpressRunner();
        // ?????addT?????AddTwiceOperator
        runner.addOperator("addT", new AddTwiceOperator());
        // ????????????r
        int r = ((Integer) (runner.execute(exp, null, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", (r == 49));
    }

    /**
     * ?????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddNByOperator() throws Exception {
        // ?????????4+1+2+3
        String exp = "4 addN (1,2,3)";
        ExpressRunner runner = new ExpressRunner();
        // ?????addN?????AddNOperator??????in??
        runner.addOperator("addN", "in", new AddNOperator());
        // ????????????r
        int r = ((Integer) (runner.execute(exp, null, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", (r == 10));
    }

    /**
     * ??????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddNByFunction() throws Exception {
        // ?????????1+2+3+4
        String exp = "addN(1,2,3,4)";
        ExpressRunner runner = new ExpressRunner();
        // ????addN?????AddNOperator
        runner.addFunction("addN", new AddNOperator());
        // ????????????r
        int r = ((Integer) (runner.execute(exp, null, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", (r == 10));
    }

    /**
     * ????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddTwiceWithParams() throws Exception {
        // ????????? i+(j+j)+(n+n)
        String exp = " i addT j addT n";
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("i", Integer.valueOf(1));
        expressContext.put("j", Integer.valueOf(22));
        expressContext.put("n", Integer.valueOf(2));
        ExpressRunner runner = new ExpressRunner();
        // ?????addT?????AddTwiceOperator
        runner.addOperator("addT", new AddTwiceOperator());
        // ????????????r
        int r = ((Integer) (runner.execute(exp, expressContext, null, false, false)));
        System.out.println(r);
        Assert.assertTrue("???????", (r == 49));
    }
}


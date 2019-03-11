package com.ql.util.express.test;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;


public class IfTest {
    @Test
    public void testIf() throws Exception {
        String[][] expresses = new String[][]{ new String[]{ "if 1==1 then return 100   else return 10;", "100" }, new String[]{ "if 1==2 then return 100   else return 10;", "10" }, new String[]{ "if 1==1 then return 100;  else return 10;", "100" }, new String[]{ "if 1==2 then return 100;  else return 10;", "10" }, new String[]{ "if 1==1 then {return 100}  else {return 10;}", "100" }, new String[]{ "if 1==2 then {return 100}  else {return 10;}", "10" }, new String[]{ "if 1==1 then return 100 ; return 10000;", "100" }, new String[]{ "if 1==2 then return 100; return 10000;", "10000" }, new String[]{ "if (1==1)  return 100   else return 10;", "100" }, new String[]{ "if (1==2)  return 100   else return 10;", "10" }, new String[]{ "if (1==1)  return 100;  else return 10;", "100" }, new String[]{ "if (1==2)  return 100;  else return 10;", "10" }, new String[]{ "if (1==1)  {return 100}  else {return 10;}", "100" }, new String[]{ "if (1==2)  {return 100}  else {return 10;}", "10" }, new String[]{ "if (1==1) return 100 ; return 10000;", "100" }, new String[]{ "if (1==2) return 100; return 10000;", "10000" } };
        for (int i = 0; i < (expresses.length); i++) {
            IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
            ExpressRunner runner = new ExpressRunner(false, true);
            runner.addOperatorWithAlias("?", "+", null);
            runner.addOperator("love", "+", new LoveOperator("love"));
            Object result = runner.execute(expresses[i][0], expressContext, null, false, true);
            System.out.println(("?????" + result));
            System.out.println(("?????" + expressContext));
            Assert.assertTrue(((((("???????:" + (expresses[i][0])) + " ????") + (expresses[i][1])) + " ?????") + result), expresses[i][1].equals((result == null ? "null" : result.toString())));
        }
    }
}


package com.ql.util.express.example;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.ApproveOperator;
import org.junit.Test;


/**
 * ??????????????
 * ???????????????????????
 */
public class WorkflowTest {
    /**
     * ??????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApprove1() throws Exception {
        // ?????
        String exp = "?? (????(??,??)){" + (((((((((((((((((((("   ??  (??  ?? 5000){ " + "     ??  (????(??,??)){") + "        ??  (????(??,??)){") + "           ????(??)") + "        }??  {") + "            ????(???)") + "        }") + "     }?? {") + "        ????(???)") + "     }") + "   }??  {") + "      ??  (????(??,??)){") + "        ????(??)") + "      }?? {") + "         ????(???)") + "      }") + "   }") + "}?? {") + "   ????(???)") + "}") + "\u6253\u5370(\"\u5b8c\u6210\")");
        ExpressRunner runner = new ExpressRunner();
        // ???????
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("??", "else", null);
        runner.addOperatorWithAlias("??", ">", null);
        // 
        runner.addFunctionOfServiceMethod("??", new WorkflowTest(), "println", new String[]{ "String" }, null);
        // ????
        runner.addFunction("????", new ApproveOperator(1));
        runner.addFunction("????", new ApproveOperator(2));
        runner.addFunction("????", new ApproveOperator(3));
        // ???????
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("???", "??");
        expressContext.put("??", new Integer(4000));
        // ?????
        runner.execute(exp, expressContext, null, false, false);
    }

    /**
     * ?????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApprove2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        // ???????
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("??", "else", null);
        runner.addOperatorWithAlias("??", ">", null);
        // 
        runner.addFunctionOfServiceMethod("??", new WorkflowTest(), "println", new String[]{ "String" }, null);
        // ????
        runner.addFunction("????", new ApproveOperator(1));
        runner.addFunction("????", new ApproveOperator(2));
        runner.addFunction("????", new ApproveOperator(3));
        // ????
        runner.loadExpress("example/approve1");
        // ????????????????
        // ???????
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("???", "??");
        expressContext.put("??", new Integer(5000));
        runner.executeByExpressName("example/approve1", expressContext, null, false, false, null);
    }

    /**
     * ????????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApprove3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        // ???????
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("??", "else", null);
        runner.addOperatorWithAlias("??", ">", null);
        // 
        runner.addFunctionOfServiceMethod("??", new WorkflowTest(), "println", new String[]{ "String" }, null);
        // ????
        runner.loadExpress("example/approve");
        // ???????
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("???", "??");
        expressContext.put("??", new Integer(6000));
        runner.executeByExpressName("example/approve", expressContext, null, false, false, null);
    }

    /**
     * ???????????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApprove4() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        // ???????
        runner.addOperatorWithAlias("??", "if", null);
        runner.addOperatorWithAlias("??", "else", null);
        runner.addOperatorWithAlias("??", ">", null);
        // 
        runner.addFunctionOfServiceMethod("??", new WorkflowTest(), "println", new String[]{ "String" }, null);
        // ????
        runner.loadExpress("example/approve1");
        runner.loadExpress("example/approve2");
        // ???????
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("??", "???");
        expressContext.put("???", "??");
        expressContext.put("??", new Integer(7000));
        runner.executeByExpressName("example/approve1", expressContext, null, false, false, null);
    }
}


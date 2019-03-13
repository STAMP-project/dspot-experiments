package com.ql.util.express.test.logic;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


/**
 * ???????
 *
 * @author tianqiao
 */
public class ShortCircuitLogicTest {
    private ExpressRunner runner = new ExpressRunner();

    /**
     * ???????,????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testShortCircuit() throws Exception {
        runner.setShortCircuit(true);
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("????", 100);
        expressContext.put("??????", 11);
        expressContext.put("VIP", false);
        List<String> errorInfo = new ArrayList<String>();
        initial();
        String expression = "2 ?? 1 and (???? ?? 90 or ?????? ?? 12)";
        boolean result = calculateLogicTest(expression, expressContext, errorInfo);
        if (result) {
            System.out.println("result is success!");
        } else {
            System.out.println("result is fail!");
            for (String error : errorInfo) {
                System.out.println(error);
            }
        }
    }

    /**
     * ???????,????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoShortCircuit() throws Exception {
        runner.setShortCircuit(false);
        IExpressContext<String, Object> expressContext = new com.ql.util.express.DefaultContext<String, Object>();
        expressContext.put("????", 100);
        expressContext.put("??????", 11);
        expressContext.put("VIP", false);
        List<String> errorInfo = new ArrayList<String>();
        initial();
        String expression = "2 ?? 1 and (???? ?? 90 or ?????? ?? 12)";
        boolean result = calculateLogicTest(expression, expressContext, errorInfo);
        if (result) {
            System.out.println("result is success!");
        } else {
            System.out.println("result is fail!");
            for (String error : errorInfo) {
                System.out.println(error);
            }
        }
    }
}


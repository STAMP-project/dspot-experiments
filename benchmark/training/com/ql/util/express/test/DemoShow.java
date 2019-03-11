package com.ql.util.express.test;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class DemoShow {
    /**
     * ????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testArithmetic() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.execute("(1+2)*3", null, null, false, true);
    }

    /**
     * for??
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testForLoop() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.execute("sum=0;for(i=0;i<10;i=i+1){sum=sum+i;}", context, null, true, true);
    }

    /**
     * for????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testForLoop2() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.execute("sum=0;for(i=0;i<10;i=i+1){for(j=0;j<10;j++){sum=sum+i+j;}}", context, null, false, true);
    }

    /**
     * ?????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHanoiMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfClassMethod("?????", DemoShow.class.getName(), "hanoi", new Class[]{ int.class, char.class, char.class, char.class }, null);
        runner.execute("?????(3, '1', '2', '3')", null, null, false, false);
    }

    /**
     * ?????2
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHanoiMethod2() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfServiceMethod("?????", new DemoShow(), "hanoi", new Class[]{ int.class, char.class, char.class, char.class }, null);
        runner.execute("?????(3, '1', '2', '3')", null, null, false, false);
    }

    /**
     * ?????3
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHanoiMethod3() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("?????", new DemoShow(), "hanoi", new Class[]{ int.class, char.class, char.class, char.class }, null);
        runner.addMacro("???????", "?????(3, '1', '2', '3')");
        runner.execute("???????", null, null, false, false);
    }

    /**
     * ??????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOperator() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.addOperator("join", new DemoShow.JoinOperator());
        Object r = runner.execute("1 join 2 join 3", context, null, false, false);
        System.out.println(r);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
    public class JoinOperator extends Operator {
        public Object executeInner(Object[] list) throws Exception {
            Object opdata1 = list[0];
            Object opdata2 = list[1];
            if (opdata1 instanceof List) {
                ((List) (opdata1)).add(opdata2);
                return opdata1;
            } else {
                List result = new ArrayList();
                result.add(opdata1);
                result.add(opdata2);
                return result;
            }
        }
    }

    /**
     * ?????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReplaceOperator() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute("1 + 2 + 3", context, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new DemoShow.JoinOperator());
        r = runner.execute("1 + 2 + 3", context, null, false, false);
        System.out.println(r);
    }

    /**
     * ?????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testShortLogicAndErrorInfo() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("A?????90??", true);
        context.put("??????", 11);
        context.put("????", 11);
        context.put("?????", false);
        context.put("????DSR", 4.0);
        String expression = "A?????90?? ==false and (??????<48 or ????<12) and ????? ==false and ????DSR>4.6";
        expression = initial(runner, expression);
        List<String> errorInfo = new ArrayList<String>();
        boolean result = ((Boolean) (runner.execute(expression, context, errorInfo, true, false)));
        if (result) {
            System.out.println("????????");
        } else {
            System.out.println("?????????");
            for (String error : errorInfo) {
                System.out.println(error);
            }
        }
    }

    /**
     * ?????? & ???
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVirtualClass() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.loadMutilExpress("????", "class People(){sex;height;money;skin};");
        runner.loadMutilExpress("????", "a = new People();a.sex='male';a.height=185;a.money=10000000;");
        runner.loadMutilExpress("??", "if(a.sex=='male' && a.height>180 && a.money>5000000) return '????????'");
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute("????;????;??", context, null, false, false);
        System.out.println(r);
    }
}


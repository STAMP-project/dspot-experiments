package com.ql.util.express.bugfix;


import com.ql.util.express.instruction.op.OperatorBase;
import org.junit.Test;


/**
 *
 *
 * @unknown Created by tianqiao on 17/7/5.
 */
public class ContextMessagePutTest {
    class OperatorContextPut extends OperatorBase {
        public OperatorContextPut(String aName) {
            this.name = aName;
        }

        @Override
        public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
            String key = list.get(0).toString();
            Object value = list.get(1);
            parent.put(key, value);
            return null;
        }
    }

    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        OperatorBase op = new ContextMessagePutTest.OperatorContextPut("contextPut");
        runner.addFunction("contextPut", op);
        String exp = "contextPut('success','false');contextPut('error','????');contextPut('warning','????')";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("success", "true");
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
        System.out.println(context);
    }
}


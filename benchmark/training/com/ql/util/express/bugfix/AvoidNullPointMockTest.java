package com.ql.util.express.bugfix;


import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

import static com.ql.util.express.DefaultContext.put;


public class AvoidNullPointMockTest {
    public class DemoObject {
        private String code;

        private AvoidNullPointMockTest.DemoObject parent;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public AvoidNullPointMockTest.DemoObject getParent() {
            return parent;
        }

        public void setParent(AvoidNullPointMockTest.DemoObject parent) {
            this.parent = parent;
        }
    }

    @Test
    public void testNullPoint() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        String[] explist = new String[]{ "x in(1,2,3)", "demo.code", "demo.parent.code", "demo.parent.getCode()", "demo.getParent().getCode()", "demo.getParent().getCode() in (1,2,3)" };
        for (String exp : explist) {
            IExpressContext<String, Object> context = new com.ql.util.express.DefaultContext<String, Object>();
            System.out.println(exp);
            put("demo", new AvoidNullPointMockTest.DemoObject());
            Object result = runner.execute(exp, context, null, true, false);
            System.out.println(result);
        }
    }
}


package com.ql.util.express.rule;


import com.ql.util.express.ExpressRunner;
import org.junit.Test;


/**
 * Created by tianqiao on 16/12/21.
 */
public class RuleConditionTest {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        parseRule("max(metric1,30) < 20 and min(metric2,40) > 30", runner);
    }
}


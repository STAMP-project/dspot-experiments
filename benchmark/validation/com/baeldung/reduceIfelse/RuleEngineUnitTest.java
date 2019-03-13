package com.baeldung.reduceIfelse;


import com.baeldung.reducingIfElse.Expression;
import com.baeldung.reducingIfElse.Operator;
import com.baeldung.reducingIfElse.Result;
import com.baeldung.reducingIfElse.RuleEngine;
import org.junit.Assert;
import org.junit.Test;


public class RuleEngineUnitTest {
    @Test
    public void whenNumbersGivenToRuleEngine_thenReturnCorrectResult() {
        Expression expression = new Expression(5, 5, Operator.ADD);
        RuleEngine engine = new RuleEngine();
        Result result = engine.process(expression);
        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.getValue());
    }
}


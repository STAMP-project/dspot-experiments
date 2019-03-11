package com.jayway.jsonpath.internal.filter;


import Predicate.PredicateContext;
import RelationalOperator.REGEX;
import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Predicate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RegexpEvaluatorTest extends BaseTest {
    private String regexp;

    private ValueNode.ValueNode valueNode;

    private boolean expectedResult;

    public RegexpEvaluatorTest(String regexp, ValueNode.ValueNode valueNode, boolean expectedResult) {
        this.regexp = regexp;
        this.valueNode = valueNode;
        this.expectedResult = expectedResult;
    }

    @Test
    public void should_evaluate_regular_expression() {
        // given
        Evaluator evaluator = EvaluatorFactory.createEvaluator(REGEX);
        ValueNode.ValueNode patternNode = createPatternNode(regexp);
        Predicate.PredicateContext ctx = createPredicateContext();
        // when
        boolean result = evaluator.evaluate(patternNode, valueNode, ctx);
        // then
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo(expectedResult)));
    }
}


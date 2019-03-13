/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.context.expression;


import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class CachedExpressionEvaluatorTests {
    private final CachedExpressionEvaluatorTests.TestExpressionEvaluator expressionEvaluator = new CachedExpressionEvaluatorTests.TestExpressionEvaluator();

    @Test
    public void parseNewExpression() {
        Method method = ReflectionUtils.findMethod(getClass(), "toString");
        Expression expression = expressionEvaluator.getTestExpression("true", method, getClass());
        hasParsedExpression("true");
        Assert.assertEquals(true, expression.getValue());
        Assert.assertEquals("Expression should be in cache", 1, expressionEvaluator.testCache.size());
    }

    @Test
    public void cacheExpression() {
        Method method = ReflectionUtils.findMethod(getClass(), "toString");
        expressionEvaluator.getTestExpression("true", method, getClass());
        expressionEvaluator.getTestExpression("true", method, getClass());
        expressionEvaluator.getTestExpression("true", method, getClass());
        hasParsedExpression("true");
        Assert.assertEquals("Only one expression should be in cache", 1, expressionEvaluator.testCache.size());
    }

    @Test
    public void cacheExpressionBasedOnConcreteType() {
        Method method = ReflectionUtils.findMethod(getClass(), "toString");
        expressionEvaluator.getTestExpression("true", method, getClass());
        expressionEvaluator.getTestExpression("true", method, Object.class);
        Assert.assertEquals("Cached expression should be based on type", 2, expressionEvaluator.testCache.size());
    }

    private static class TestExpressionEvaluator extends CachedExpressionEvaluator {
        private final Map<ExpressionKey, Expression> testCache = new ConcurrentHashMap<>();

        public TestExpressionEvaluator() {
            super(CachedExpressionEvaluatorTests.TestExpressionEvaluator.mockSpelExpressionParser());
        }

        public Expression getTestExpression(String expression, Method method, Class<?> type) {
            return getExpression(this.testCache, new AnnotatedElementKey(method, type), expression);
        }

        private static SpelExpressionParser mockSpelExpressionParser() {
            SpelExpressionParser parser = new SpelExpressionParser();
            return Mockito.spy(parser);
        }
    }
}


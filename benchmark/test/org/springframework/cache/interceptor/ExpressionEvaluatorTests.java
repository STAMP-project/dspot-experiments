/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.cache.interceptor;


import CacheOperationExpressionEvaluator.NO_RESULT;
import CacheOperationExpressionEvaluator.RESULT_UNAVAILABLE;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Costin Leau
 * @author Phillip Webb
 * @author Sam Brannen
 * @author Stephane Nicoll
 */
public class ExpressionEvaluatorTests {
    private final CacheOperationExpressionEvaluator eval = new CacheOperationExpressionEvaluator();

    private final AnnotationCacheOperationSource source = new AnnotationCacheOperationSource();

    @Test
    public void testMultipleCachingSource() {
        Collection<CacheOperation> ops = getOps("multipleCaching");
        Assert.assertEquals(2, ops.size());
        Iterator<CacheOperation> it = ops.iterator();
        CacheOperation next = it.next();
        Assert.assertTrue((next instanceof CacheableOperation));
        Assert.assertTrue(next.getCacheNames().contains("test"));
        Assert.assertEquals("#a", next.getKey());
        next = it.next();
        Assert.assertTrue((next instanceof CacheableOperation));
        Assert.assertTrue(next.getCacheNames().contains("test"));
        Assert.assertEquals("#b", next.getKey());
    }

    @Test
    public void testMultipleCachingEval() {
        ExpressionEvaluatorTests.AnnotatedClass target = new ExpressionEvaluatorTests.AnnotatedClass();
        Method method = ReflectionUtils.findMethod(ExpressionEvaluatorTests.AnnotatedClass.class, "multipleCaching", Object.class, Object.class);
        Object[] args = new Object[]{ new Object(), new Object() };
        Collection<ConcurrentMapCache> caches = Collections.singleton(new ConcurrentMapCache("test"));
        EvaluationContext evalCtx = this.eval.createEvaluationContext(caches, method, args, target, target.getClass(), method, NO_RESULT, null);
        Collection<CacheOperation> ops = getOps("multipleCaching");
        Iterator<CacheOperation> it = ops.iterator();
        AnnotatedElementKey key = new AnnotatedElementKey(method, ExpressionEvaluatorTests.AnnotatedClass.class);
        Object keyA = this.eval.key(it.next().getKey(), key, evalCtx);
        Object keyB = this.eval.key(it.next().getKey(), key, evalCtx);
        Assert.assertEquals(args[0], keyA);
        Assert.assertEquals(args[1], keyB);
    }

    @Test
    public void withReturnValue() {
        EvaluationContext context = createEvaluationContext("theResult");
        Object value = new SpelExpressionParser().parseExpression("#result").getValue(context);
        Assert.assertThat(value, equalTo("theResult"));
    }

    @Test
    public void withNullReturn() {
        EvaluationContext context = createEvaluationContext(null);
        Object value = new SpelExpressionParser().parseExpression("#result").getValue(context);
        Assert.assertThat(value, nullValue());
    }

    @Test
    public void withoutReturnValue() {
        EvaluationContext context = createEvaluationContext(NO_RESULT);
        Object value = new SpelExpressionParser().parseExpression("#result").getValue(context);
        Assert.assertThat(value, nullValue());
    }

    @Test
    public void unavailableReturnValue() {
        EvaluationContext context = createEvaluationContext(RESULT_UNAVAILABLE);
        try {
            new SpelExpressionParser().parseExpression("#result").getValue(context);
            Assert.fail("Should have failed to parse expression, result not available");
        } catch (VariableNotAvailableException e) {
            Assert.assertEquals("wrong variable name", "result", e.getName());
        }
    }

    @Test
    public void resolveBeanReference() {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        BeanDefinition beanDefinition = new RootBeanDefinition(String.class);
        applicationContext.registerBeanDefinition("myBean", beanDefinition);
        applicationContext.refresh();
        EvaluationContext context = createEvaluationContext(NO_RESULT, applicationContext);
        Object value = new SpelExpressionParser().parseExpression("@myBean.class.getName()").getValue(context);
        Assert.assertThat(value, is(String.class.getName()));
    }

    private static class AnnotatedClass {
        @Caching(cacheable = { @Cacheable(value = "test", key = "#a"), @Cacheable(value = "test", key = "#b") })
        public void multipleCaching(Object a, Object b) {
        }
    }
}


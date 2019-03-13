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
package org.springframework.aop.aspectj.annotation;


import Pointcut.TRUE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcutTests;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.tests.sample.beans.TestBean;
import test.aop.PerTargetAspect;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AspectJPointcutAdvisorTests {
    private final AspectJAdvisorFactory af = new ReflectiveAspectJAdvisorFactory();

    @Test
    public void testSingleton() throws NoSuchMethodException, SecurityException {
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(AspectJExpressionPointcutTests.MATCH_ALL_METHODS);
        InstantiationModelAwarePointcutAdvisorImpl ajpa = new InstantiationModelAwarePointcutAdvisorImpl(ajexp, TestBean.class.getMethod("getAge"), af, new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.ExceptionAspect(null), "someBean"), 1, "someBean");
        Assert.assertSame(TRUE, ajpa.getAspectMetadata().getPerClausePointcut());
        Assert.assertFalse(ajpa.isPerInstance());
    }

    @Test
    public void testPerTarget() throws NoSuchMethodException, SecurityException {
        AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
        ajexp.setExpression(AspectJExpressionPointcutTests.MATCH_ALL_METHODS);
        InstantiationModelAwarePointcutAdvisorImpl ajpa = new InstantiationModelAwarePointcutAdvisorImpl(ajexp, TestBean.class.getMethod("getAge"), af, new SingletonMetadataAwareAspectInstanceFactory(new PerTargetAspect(), "someBean"), 1, "someBean");
        Assert.assertNotSame(TRUE, ajpa.getAspectMetadata().getPerClausePointcut());
        Assert.assertTrue(((ajpa.getAspectMetadata().getPerClausePointcut()) instanceof AspectJExpressionPointcut));
        Assert.assertTrue(ajpa.isPerInstance());
        Assert.assertTrue(ajpa.getAspectMetadata().getPerClausePointcut().getClassFilter().matches(TestBean.class));
        Assert.assertFalse(ajpa.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getAge"), TestBean.class));
        Assert.assertTrue(ajpa.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), TestBean.class));
    }

    @Test(expected = AopConfigException.class)
    public void testPerCflowTarget() {
        testIllegalInstantiationModel(AbstractAspectJAdvisorFactoryTests.PerCflowAspect.class);
    }

    @Test(expected = AopConfigException.class)
    public void testPerCflowBelowTarget() {
        testIllegalInstantiationModel(AbstractAspectJAdvisorFactoryTests.PerCflowBelowAspect.class);
    }
}


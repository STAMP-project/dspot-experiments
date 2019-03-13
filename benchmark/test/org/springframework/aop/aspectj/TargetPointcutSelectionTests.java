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
package org.springframework.aop.aspectj;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for target selection matching (see SPR-3783).
 * <p>Thanks to Tomasz Blachowicz for the bug report!
 *
 * @author Ramnivas Laddad
 * @author Chris Beams
 */
public class TargetPointcutSelectionTests {
    public TargetPointcutSelectionTests.TestInterface testImpl1;

    public TargetPointcutSelectionTests.TestInterface testImpl2;

    public TargetPointcutSelectionTests.TestAspect testAspectForTestImpl1;

    public TargetPointcutSelectionTests.TestAspect testAspectForAbstractTestImpl;

    public TargetPointcutSelectionTests.TestInterceptor testInterceptor;

    @Test
    public void targetSelectionForMatchedType() {
        testImpl1.interfaceMethod();
        Assert.assertEquals("Should have been advised by POJO advice for impl", 1, testAspectForTestImpl1.count);
        Assert.assertEquals("Should have been advised by POJO advice for base type", 1, testAspectForAbstractTestImpl.count);
        Assert.assertEquals("Should have been advised by advisor", 1, testInterceptor.count);
    }

    @Test
    public void targetNonSelectionForMismatchedType() {
        testImpl2.interfaceMethod();
        Assert.assertEquals("Shouldn't have been advised by POJO advice for impl", 0, testAspectForTestImpl1.count);
        Assert.assertEquals("Should have been advised by POJO advice for base type", 1, testAspectForAbstractTestImpl.count);
        Assert.assertEquals("Shouldn't have been advised by advisor", 0, testInterceptor.count);
    }

    public static interface TestInterface {
        public void interfaceMethod();
    }

    // Reproducing bug requires that the class specified in target() pointcut doesn't
    // include the advised method's implementation (instead a base class should include it)
    public abstract static class AbstractTestImpl implements TargetPointcutSelectionTests.TestInterface {
        @Override
        public void interfaceMethod() {
        }
    }

    public static class TestImpl1 extends TargetPointcutSelectionTests.AbstractTestImpl {}

    public static class TestImpl2 extends TargetPointcutSelectionTests.AbstractTestImpl {}

    public static class TestAspect {
        public int count;

        public void increment() {
            (count)++;
        }
    }

    public static class TestInterceptor extends TargetPointcutSelectionTests.TestAspect implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation mi) throws Throwable {
            increment();
            return mi.proceed();
        }
    }
}


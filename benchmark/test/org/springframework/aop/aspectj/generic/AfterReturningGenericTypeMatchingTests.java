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
package org.springframework.aop.aspectj.generic;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests ensuring that after-returning advice for generic parameters bound to
 * the advice and the return type follow AspectJ semantics.
 *
 * <p>See SPR-3628 for more details.
 *
 * @author Ramnivas Laddad
 * @author Chris Beams
 */
public class AfterReturningGenericTypeMatchingTests {
    private GenericReturnTypeVariationClass testBean;

    private CounterAspect counterAspect;

    @Test
    public void testReturnTypeExactMatching() {
        testBean.getStrings();
        Assert.assertEquals(1, counterAspect.getStringsInvocationsCount);
        Assert.assertEquals(0, counterAspect.getIntegersInvocationsCount);
        counterAspect.reset();
        testBean.getIntegers();
        Assert.assertEquals(0, counterAspect.getStringsInvocationsCount);
        Assert.assertEquals(1, counterAspect.getIntegersInvocationsCount);
    }

    @Test
    public void testReturnTypeRawMatching() {
        testBean.getStrings();
        Assert.assertEquals(1, counterAspect.getRawsInvocationsCount);
        counterAspect.reset();
        testBean.getIntegers();
        Assert.assertEquals(1, counterAspect.getRawsInvocationsCount);
    }

    @Test
    public void testReturnTypeUpperBoundMatching() {
        testBean.getIntegers();
        Assert.assertEquals(1, counterAspect.getNumbersInvocationsCount);
    }

    @Test
    public void testReturnTypeLowerBoundMatching() {
        testBean.getTestBeans();
        Assert.assertEquals(1, counterAspect.getTestBeanInvocationsCount);
        counterAspect.reset();
        testBean.getEmployees();
        Assert.assertEquals(0, counterAspect.getTestBeanInvocationsCount);
    }
}


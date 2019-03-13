/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.aop.aspectj.autoproxy.benchmark;


import org.junit.Test;


/**
 * Integration tests for AspectJ auto proxying. Includes mixing with Spring AOP
 * Advisors to demonstrate that existing autoproxying contract is honoured.
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class BenchmarkTests {
    private static final Class<?> CLASS = BenchmarkTests.class;

    private static final String ASPECTJ_CONTEXT = (BenchmarkTests.CLASS.getSimpleName()) + "-aspectj.xml";

    private static final String SPRING_AOP_CONTEXT = (BenchmarkTests.CLASS.getSimpleName()) + "-springAop.xml";

    @Test
    public void testRepeatedAroundAdviceInvocationsWithAspectJ() {
        testRepeatedAroundAdviceInvocations(BenchmarkTests.ASPECTJ_CONTEXT, getCount(), "AspectJ");
    }

    @Test
    public void testRepeatedAroundAdviceInvocationsWithSpringAop() {
        testRepeatedAroundAdviceInvocations(BenchmarkTests.SPRING_AOP_CONTEXT, getCount(), "Spring AOP");
    }

    @Test
    public void testRepeatedBeforeAdviceInvocationsWithAspectJ() {
        testBeforeAdviceWithoutJoinPoint(BenchmarkTests.ASPECTJ_CONTEXT, getCount(), "AspectJ");
    }

    @Test
    public void testRepeatedBeforeAdviceInvocationsWithSpringAop() {
        testBeforeAdviceWithoutJoinPoint(BenchmarkTests.SPRING_AOP_CONTEXT, getCount(), "Spring AOP");
    }

    @Test
    public void testRepeatedAfterReturningAdviceInvocationsWithAspectJ() {
        testAfterReturningAdviceWithoutJoinPoint(BenchmarkTests.ASPECTJ_CONTEXT, getCount(), "AspectJ");
    }

    @Test
    public void testRepeatedAfterReturningAdviceInvocationsWithSpringAop() {
        testAfterReturningAdviceWithoutJoinPoint(BenchmarkTests.SPRING_AOP_CONTEXT, getCount(), "Spring AOP");
    }

    @Test
    public void testRepeatedMixWithAspectJ() {
        testMix(BenchmarkTests.ASPECTJ_CONTEXT, getCount(), "AspectJ");
    }

    @Test
    public void testRepeatedMixWithSpringAop() {
        testMix(BenchmarkTests.SPRING_AOP_CONTEXT, getCount(), "Spring AOP");
    }
}


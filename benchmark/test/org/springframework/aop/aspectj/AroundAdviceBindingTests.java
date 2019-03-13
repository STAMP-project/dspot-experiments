/**
 * Copyright 2002-2017 the original author or authors.
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


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests for various parameter binding scenarios with before advice.
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class AroundAdviceBindingTests {
    private AroundAdviceBindingTestAspect.AroundAdviceBindingCollaborator mockCollaborator;

    private ITestBean testBeanProxy;

    private TestBean testBeanTarget;

    protected ApplicationContext ctx;

    @Test
    public void testOneIntArg() {
        testBeanProxy.setAge(5);
        Mockito.verify(mockCollaborator).oneIntArg(5);
    }

    @Test
    public void testOneObjectArgBoundToTarget() {
        testBeanProxy.getAge();
        Mockito.verify(mockCollaborator).oneObjectArg(this.testBeanTarget);
    }

    @Test
    public void testOneIntAndOneObjectArgs() {
        testBeanProxy.setAge(5);
        Mockito.verify(mockCollaborator).oneIntAndOneObject(5, this.testBeanProxy);
    }

    @Test
    public void testJustJoinPoint() {
        testBeanProxy.getAge();
        Mockito.verify(mockCollaborator).justJoinPoint("getAge");
    }
}


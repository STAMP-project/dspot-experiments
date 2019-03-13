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


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests for various parameter binding scenarios with before advice.
 *
 * @author Adrian Colyer
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class AfterReturningAdviceBindingTests {
    private AfterReturningAdviceBindingTestAspect afterAdviceAspect;

    private ITestBean testBeanProxy;

    private TestBean testBeanTarget;

    private AfterReturningAdviceBindingTestAspect.AfterReturningAdviceBindingCollaborator mockCollaborator;

    @Test
    public void testOneIntArg() {
        testBeanProxy.setAge(5);
        Mockito.verify(mockCollaborator).oneIntArg(5);
    }

    @Test
    public void testOneObjectArg() {
        testBeanProxy.getAge();
        Mockito.verify(mockCollaborator).oneObjectArg(this.testBeanProxy);
    }

    @Test
    public void testOneIntAndOneObjectArgs() {
        testBeanProxy.setAge(5);
        Mockito.verify(mockCollaborator).oneIntAndOneObject(5, this.testBeanProxy);
    }

    @Test
    public void testNeedsJoinPoint() {
        testBeanProxy.getAge();
        Mockito.verify(mockCollaborator).needsJoinPoint("getAge");
    }

    @Test
    public void testNeedsJoinPointStaticPart() {
        testBeanProxy.getAge();
        Mockito.verify(mockCollaborator).needsJoinPointStaticPart("getAge");
    }

    @Test
    public void testReturningString() {
        testBeanProxy.setName("adrian");
        testBeanProxy.getName();
        Mockito.verify(mockCollaborator).oneString("adrian");
    }

    @Test
    public void testReturningObject() {
        testBeanProxy.returnsThis();
        Mockito.verify(mockCollaborator).oneObjectArg(this.testBeanTarget);
    }

    @Test
    public void testReturningBean() {
        testBeanProxy.returnsThis();
        Mockito.verify(mockCollaborator).oneTestBeanArg(this.testBeanTarget);
    }

    @Test
    public void testReturningBeanArray() {
        this.testBeanTarget.setSpouse(new TestBean());
        ITestBean[] spouses = this.testBeanTarget.getSpouses();
        testBeanProxy.getSpouses();
        Mockito.verify(mockCollaborator).testBeanArrayArg(spouses);
    }

    @Test
    public void testNoInvokeWhenReturningParameterTypeDoesNotMatch() {
        testBeanProxy.setSpouse(this.testBeanProxy);
        testBeanProxy.getSpouse();
        Mockito.verifyZeroInteractions(mockCollaborator);
    }

    @Test
    public void testReturningByType() {
        testBeanProxy.returnsThis();
        Mockito.verify(mockCollaborator).objectMatchNoArgs();
    }

    @Test
    public void testReturningPrimitive() {
        testBeanProxy.setAge(20);
        testBeanProxy.haveBirthday();
        Mockito.verify(mockCollaborator).oneInt(20);
    }
}


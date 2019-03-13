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


/**
 * Tests for various parameter binding scenarios with before advice.
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class AfterThrowingAdviceBindingTests {
    private ITestBean testBean;

    private AfterThrowingAdviceBindingTestAspect afterThrowingAdviceAspect;

    private AfterThrowingAdviceBindingTestAspect.AfterThrowingAdviceBindingCollaborator mockCollaborator;

    @Test(expected = Throwable.class)
    public void testSimpleAfterThrowing() throws Throwable {
        this.testBean.exceptional(new Throwable());
        Mockito.verify(mockCollaborator).noArgs();
    }

    @Test(expected = Throwable.class)
    public void testAfterThrowingWithBinding() throws Throwable {
        Throwable t = new Throwable();
        this.testBean.exceptional(t);
        Mockito.verify(mockCollaborator).oneThrowable(t);
    }

    @Test(expected = Throwable.class)
    public void testAfterThrowingWithNamedTypeRestriction() throws Throwable {
        Throwable t = new Throwable();
        this.testBean.exceptional(t);
        Mockito.verify(mockCollaborator).noArgs();
        Mockito.verify(mockCollaborator).oneThrowable(t);
        Mockito.verify(mockCollaborator).noArgsOnThrowableMatch();
    }

    @Test(expected = Throwable.class)
    public void testAfterThrowingWithRuntimeExceptionBinding() throws Throwable {
        RuntimeException ex = new RuntimeException();
        this.testBean.exceptional(ex);
        Mockito.verify(mockCollaborator).oneRuntimeException(ex);
    }

    @Test(expected = Throwable.class)
    public void testAfterThrowingWithTypeSpecified() throws Throwable {
        this.testBean.exceptional(new Throwable());
        Mockito.verify(mockCollaborator).noArgsOnThrowableMatch();
    }

    @Test(expected = Throwable.class)
    public void testAfterThrowingWithRuntimeTypeSpecified() throws Throwable {
        this.testBean.exceptional(new RuntimeException());
        Mockito.verify(mockCollaborator).noArgsOnRuntimeExceptionMatch();
    }
}


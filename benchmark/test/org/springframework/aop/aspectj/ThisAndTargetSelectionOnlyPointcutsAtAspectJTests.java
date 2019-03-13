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


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ramnivas Laddad
 * @author Chris Beams
 */
public class ThisAndTargetSelectionOnlyPointcutsAtAspectJTests {
    private ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface testBean;

    private ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface testAnnotatedClassBean;

    private ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface testAnnotatedMethodBean;

    private ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.Counter counter;

    @Test
    public void thisAsClassDoesNotMatch() {
        testBean.doIt();
        Assert.assertEquals(0, counter.thisAsClassCounter);
    }

    @Test
    public void thisAsInterfaceMatch() {
        testBean.doIt();
        Assert.assertEquals(1, counter.thisAsInterfaceCounter);
    }

    @Test
    public void targetAsClassDoesMatch() {
        testBean.doIt();
        Assert.assertEquals(1, counter.targetAsClassCounter);
    }

    @Test
    public void targetAsInterfaceMatch() {
        testBean.doIt();
        Assert.assertEquals(1, counter.targetAsInterfaceCounter);
    }

    @Test
    public void thisAsClassAndTargetAsClassCounterNotMatch() {
        testBean.doIt();
        Assert.assertEquals(0, counter.thisAsClassAndTargetAsClassCounter);
    }

    @Test
    public void thisAsInterfaceAndTargetAsInterfaceCounterMatch() {
        testBean.doIt();
        Assert.assertEquals(1, counter.thisAsInterfaceAndTargetAsInterfaceCounter);
    }

    @Test
    public void thisAsInterfaceAndTargetAsClassCounterMatch() {
        testBean.doIt();
        Assert.assertEquals(1, counter.thisAsInterfaceAndTargetAsInterfaceCounter);
    }

    @Test
    public void atTargetClassAnnotationMatch() {
        testAnnotatedClassBean.doIt();
        Assert.assertEquals(1, counter.atTargetClassAnnotationCounter);
    }

    @Test
    public void atAnnotationMethodAnnotationMatch() {
        testAnnotatedMethodBean.doIt();
        Assert.assertEquals(1, counter.atAnnotationMethodAnnotationCounter);
    }

    public static interface TestInterface {
        public void doIt();
    }

    public static class TestImpl implements ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface {
        @Override
        public void doIt() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TestAnnotation {}

    @ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestAnnotation
    public static class AnnotatedClassTestImpl implements ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface {
        @Override
        public void doIt() {
        }
    }

    public static class AnnotatedMethodTestImpl implements ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface {
        @Override
        @ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestAnnotation
        public void doIt() {
        }
    }

    @Aspect
    public static class Counter {
        int thisAsClassCounter;

        int thisAsInterfaceCounter;

        int targetAsClassCounter;

        int targetAsInterfaceCounter;

        int thisAsClassAndTargetAsClassCounter;

        int thisAsInterfaceAndTargetAsInterfaceCounter;

        int thisAsInterfaceAndTargetAsClassCounter;

        int atTargetClassAnnotationCounter;

        int atAnnotationMethodAnnotationCounter;

        public void reset() {
            thisAsClassCounter = 0;
            thisAsInterfaceCounter = 0;
            targetAsClassCounter = 0;
            targetAsInterfaceCounter = 0;
            thisAsClassAndTargetAsClassCounter = 0;
            thisAsInterfaceAndTargetAsInterfaceCounter = 0;
            thisAsInterfaceAndTargetAsClassCounter = 0;
            atTargetClassAnnotationCounter = 0;
            atAnnotationMethodAnnotationCounter = 0;
        }

        @Before("this(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestImpl)")
        public void incrementThisAsClassCounter() {
            (thisAsClassCounter)++;
        }

        @Before("this(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface)")
        public void incrementThisAsInterfaceCounter() {
            (thisAsInterfaceCounter)++;
        }

        @Before("target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestImpl)")
        public void incrementTargetAsClassCounter() {
            (targetAsClassCounter)++;
        }

        @Before("target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface)")
        public void incrementTargetAsInterfaceCounter() {
            (targetAsInterfaceCounter)++;
        }

        @Before("this(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestImpl) " + "&& target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestImpl)")
        public void incrementThisAsClassAndTargetAsClassCounter() {
            (thisAsClassAndTargetAsClassCounter)++;
        }

        @Before("this(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface) " + "&& target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface)")
        public void incrementThisAsInterfaceAndTargetAsInterfaceCounter() {
            (thisAsInterfaceAndTargetAsInterfaceCounter)++;
        }

        @Before("this(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestInterface) " + "&& target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestImpl)")
        public void incrementThisAsInterfaceAndTargetAsClassCounter() {
            (thisAsInterfaceAndTargetAsClassCounter)++;
        }

        @Before("@target(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestAnnotation)")
        public void incrementAtTargetClassAnnotationCounter() {
            (atTargetClassAnnotationCounter)++;
        }

        @Before("@annotation(org.springframework.aop.aspectj.ThisAndTargetSelectionOnlyPointcutsAtAspectJTests.TestAnnotation)")
        public void incrementAtAnnotationMethodAnnotationCounter() {
            (atAnnotationMethodAnnotationCounter)++;
        }
    }
}


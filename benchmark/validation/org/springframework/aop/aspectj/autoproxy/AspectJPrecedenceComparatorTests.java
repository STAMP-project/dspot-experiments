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
package org.springframework.aop.aspectj.autoproxy;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;


/**
 *
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class AspectJPrecedenceComparatorTests {
    private static final int HIGH_PRECEDENCE_ADVISOR_ORDER = 100;

    private static final int LOW_PRECEDENCE_ADVISOR_ORDER = 200;

    private static final int EARLY_ADVICE_DECLARATION_ORDER = 5;

    private static final int LATE_ADVICE_DECLARATION_ORDER = 10;

    private AspectJPrecedenceComparator comparator;

    private Method anyOldMethod;

    private AspectJExpressionPointcut anyOldPointcut;

    @Test
    public void testSameAspectNoAfterAdvice() {
        Advisor advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        Assert.assertEquals("advisor1 sorted before advisor2", (-1), this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Assert.assertEquals("advisor2 sorted before advisor1", 1, this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testSameAspectAfterAdvice() {
        Advisor advisor1 = createAspectJAfterAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        Assert.assertEquals("advisor2 sorted before advisor1", 1, this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJAfterReturningAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAfterThrowingAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Assert.assertEquals("advisor1 sorted before advisor2", (-1), this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testSameAspectOneOfEach() {
        Advisor advisor1 = createAspectJAfterAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        Assert.assertEquals("advisor1 and advisor2 not comparable", 1, this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testSameAdvisorPrecedenceDifferentAspectNoAfterAdvice() {
        Advisor advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("nothing to say about order here", 0, this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("nothing to say about order here", 0, this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testSameAdvisorPrecedenceDifferentAspectAfterAdvice() {
        Advisor advisor1 = createAspectJAfterAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("nothing to say about order here", 0, this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJAfterReturningAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAfterThrowingAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("nothing to say about order here", 0, this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testHigherAdvisorPrecedenceNoAfterAdvice() {
        Advisor advisor1 = createSpringAOPBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER);
        Advisor advisor2 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted before advisor2", (-1), this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted before advisor2", (-1), this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testHigherAdvisorPrecedenceAfterAdvice() {
        Advisor advisor1 = createAspectJAfterAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted before advisor2", (-1), this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJAfterReturningAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAfterThrowingAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor2 sorted after advisor1", (-1), this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testLowerAdvisorPrecedenceNoAfterAdvice() {
        Advisor advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted after advisor2", 1, this.comparator.compare(advisor1, advisor2));
        advisor1 = createAspectJBeforeAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someAspect");
        advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted after advisor2", 1, this.comparator.compare(advisor1, advisor2));
    }

    @Test
    public void testLowerAdvisorPrecedenceAfterAdvice() {
        Advisor advisor1 = createAspectJAfterAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someAspect");
        Advisor advisor2 = createAspectJAroundAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.LATE_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted after advisor2", 1, this.comparator.compare(advisor1, advisor2));
        advisor1 = createSpringAOPAfterAdvice(AspectJPrecedenceComparatorTests.LOW_PRECEDENCE_ADVISOR_ORDER);
        advisor2 = createAspectJAfterThrowingAdvice(AspectJPrecedenceComparatorTests.HIGH_PRECEDENCE_ADVISOR_ORDER, AspectJPrecedenceComparatorTests.EARLY_ADVICE_DECLARATION_ORDER, "someOtherAspect");
        Assert.assertEquals("advisor1 sorted after advisor2", 1, this.comparator.compare(advisor1, advisor2));
    }
}


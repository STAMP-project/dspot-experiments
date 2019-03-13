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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.ITestBean;


/**
 *
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class AspectAndAdvicePrecedenceTests {
    private PrecedenceTestAspect highPrecedenceAspect;

    private PrecedenceTestAspect lowPrecedenceAspect;

    private SimpleSpringBeforeAdvice highPrecedenceSpringAdvice;

    private SimpleSpringBeforeAdvice lowPrecedenceSpringAdvice;

    private ITestBean testBean;

    @Test
    public void testAdviceOrder() {
        PrecedenceTestAspect.Collaborator collaborator = new AspectAndAdvicePrecedenceTests.PrecedenceVerifyingCollaborator();
        this.highPrecedenceAspect.setCollaborator(collaborator);
        this.lowPrecedenceAspect.setCollaborator(collaborator);
        this.highPrecedenceSpringAdvice.setCollaborator(collaborator);
        this.lowPrecedenceSpringAdvice.setCollaborator(collaborator);
        this.testBean.getAge();
    }

    private static class PrecedenceVerifyingCollaborator implements PrecedenceTestAspect.Collaborator {
        private static final String[] EXPECTED = new String[]{ // this order confirmed by running the same aspects (minus the Spring AOP advisors)
        // through AspectJ...
        "beforeAdviceOne(highPrecedenceAspect)", // 1
        "beforeAdviceTwo(highPrecedenceAspect)", // 2
        "aroundAdviceOne(highPrecedenceAspect)"// 3,  before proceed
        , "aroundAdviceTwo(highPrecedenceAspect)"// 4,  before proceed
        , "beforeAdviceOne(highPrecedenceSpringAdvice)"// 5
        , "beforeAdviceOne(lowPrecedenceSpringAdvice)"// 6
        , "beforeAdviceOne(lowPrecedenceAspect)"// 7
        , "beforeAdviceTwo(lowPrecedenceAspect)"// 8
        , "aroundAdviceOne(lowPrecedenceAspect)"// 9,  before proceed
        , "aroundAdviceTwo(lowPrecedenceAspect)"// 10, before proceed
        , "aroundAdviceTwo(lowPrecedenceAspect)"// 11, after proceed
        , "aroundAdviceOne(lowPrecedenceAspect)"// 12, after proceed
        , "afterAdviceOne(lowPrecedenceAspect)"// 13
        , "afterAdviceTwo(lowPrecedenceAspect)"// 14
        , "aroundAdviceTwo(highPrecedenceAspect)"// 15, after proceed
        , "aroundAdviceOne(highPrecedenceAspect)", // 16, after proceed
        "afterAdviceOne(highPrecedenceAspect)", // 17
        "afterAdviceTwo(highPrecedenceAspect)"// 18
         };

        private int adviceInvocationNumber = 0;

        private void checkAdvice(String whatJustHappened) {
            // System.out.println("[" + adviceInvocationNumber + "] " + whatJustHappened + " ==> " + EXPECTED[adviceInvocationNumber]);
            if ((adviceInvocationNumber) > ((AspectAndAdvicePrecedenceTests.PrecedenceVerifyingCollaborator.EXPECTED.length) - 1)) {
                Assert.fail(((("Too many advice invocations, expecting " + (AspectAndAdvicePrecedenceTests.PrecedenceVerifyingCollaborator.EXPECTED.length)) + " but had ") + (adviceInvocationNumber)));
            }
            String expecting = AspectAndAdvicePrecedenceTests.PrecedenceVerifyingCollaborator.EXPECTED[((adviceInvocationNumber)++)];
            if (!(whatJustHappened.equals(expecting))) {
                Assert.fail((((((("Expecting '" + expecting) + "' on advice invocation ") + (adviceInvocationNumber)) + " but got '") + whatJustHappened) + "'"));
            }
        }

        @Override
        public void beforeAdviceOne(String beanName) {
            checkAdvice((("beforeAdviceOne(" + beanName) + ")"));
        }

        @Override
        public void beforeAdviceTwo(String beanName) {
            checkAdvice((("beforeAdviceTwo(" + beanName) + ")"));
        }

        @Override
        public void aroundAdviceOne(String beanName) {
            checkAdvice((("aroundAdviceOne(" + beanName) + ")"));
        }

        @Override
        public void aroundAdviceTwo(String beanName) {
            checkAdvice((("aroundAdviceTwo(" + beanName) + ")"));
        }

        @Override
        public void afterAdviceOne(String beanName) {
            checkAdvice((("afterAdviceOne(" + beanName) + ")"));
        }

        @Override
        public void afterAdviceTwo(String beanName) {
            checkAdvice((("afterAdviceTwo(" + beanName) + ")"));
        }
    }
}


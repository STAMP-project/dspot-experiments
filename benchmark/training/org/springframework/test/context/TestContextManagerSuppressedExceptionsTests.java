/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.context;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit 4 based unit tests for {@link TestContextManager}, which verify proper
 * support for <em>suppressed exceptions</em> thrown by {@link TestExecutionListener
 * TestExecutionListeners}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see Throwable#getSuppressed()
 */
public class TestContextManagerSuppressedExceptionsTests {
    @Test
    public void afterTestExecution() throws Exception {
        test("afterTestExecution", TestContextManagerSuppressedExceptionsTests.FailingAfterTestExecutionTestCase.class, ( tcm, c, m) -> tcm.afterTestExecution(this, m, null));
    }

    @Test
    public void afterTestMethod() throws Exception {
        test("afterTestMethod", TestContextManagerSuppressedExceptionsTests.FailingAfterTestMethodTestCase.class, ( tcm, c, m) -> tcm.afterTestMethod(this, m, null));
    }

    @Test
    public void afterTestClass() throws Exception {
        test("afterTestClass", TestContextManagerSuppressedExceptionsTests.FailingAfterTestClassTestCase.class, ( tcm, c, m) -> tcm.afterTestClass());
    }

    // -------------------------------------------------------------------
    @FunctionalInterface
    private interface Callback {
        void invoke(TestContextManager tcm, Class<?> clazz, Method method) throws Exception;
    }

    private static class FailingAfterTestClassListener1 implements TestExecutionListener {
        @Override
        public void afterTestClass(TestContext testContext) {
            Assert.fail("afterTestClass-1");
        }
    }

    private static class FailingAfterTestClassListener2 implements TestExecutionListener {
        @Override
        public void afterTestClass(TestContext testContext) {
            Assert.fail("afterTestClass-2");
        }
    }

    private static class FailingAfterTestMethodListener1 implements TestExecutionListener {
        @Override
        public void afterTestMethod(TestContext testContext) {
            Assert.fail("afterTestMethod-1");
        }
    }

    private static class FailingAfterTestMethodListener2 implements TestExecutionListener {
        @Override
        public void afterTestMethod(TestContext testContext) {
            Assert.fail("afterTestMethod-2");
        }
    }

    private static class FailingAfterTestExecutionListener1 implements TestExecutionListener {
        @Override
        public void afterTestExecution(TestContext testContext) {
            Assert.fail("afterTestExecution-1");
        }
    }

    private static class FailingAfterTestExecutionListener2 implements TestExecutionListener {
        @Override
        public void afterTestExecution(TestContext testContext) {
            Assert.fail("afterTestExecution-2");
        }
    }

    @TestExecutionListeners({ TestContextManagerSuppressedExceptionsTests.FailingAfterTestExecutionListener1.class, TestContextManagerSuppressedExceptionsTests.FailingAfterTestExecutionListener2.class })
    private static class FailingAfterTestExecutionTestCase {}

    @TestExecutionListeners({ TestContextManagerSuppressedExceptionsTests.FailingAfterTestMethodListener1.class, TestContextManagerSuppressedExceptionsTests.FailingAfterTestMethodListener2.class })
    private static class FailingAfterTestMethodTestCase {}

    @TestExecutionListeners({ TestContextManagerSuppressedExceptionsTests.FailingAfterTestClassListener1.class, TestContextManagerSuppressedExceptionsTests.FailingAfterTestClassListener2.class })
    private static class FailingAfterTestClassTestCase {}
}


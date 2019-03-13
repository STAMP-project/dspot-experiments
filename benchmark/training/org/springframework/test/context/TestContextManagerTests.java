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
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit 4 based unit test for {@link TestContextManager}, which verifies proper
 * <em>execution order</em> of registered {@link TestExecutionListener
 * TestExecutionListeners}.
 *
 * @author Sam Brannen
 * @since 2.5
 */
public class TestContextManagerTests {
    private static final List<String> executionOrder = new ArrayList<>();

    private final TestContextManager testContextManager = new TestContextManager(TestContextManagerTests.ExampleTestCase.class);

    private final Method testMethod;

    {
        try {
            this.testMethod = TestContextManagerTests.ExampleTestCase.class.getDeclaredMethod("exampleTestMethod");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void listenerExecutionOrder() throws Exception {
        // @formatter:off
        Assert.assertEquals("Registered TestExecutionListeners", 3, this.testContextManager.getTestExecutionListeners().size());
        this.testContextManager.beforeTestMethod(this, this.testMethod);
        TestContextManagerTests.assertExecutionOrder("beforeTestMethod", "beforeTestMethod-1", "beforeTestMethod-2", "beforeTestMethod-3");
        this.testContextManager.beforeTestExecution(this, this.testMethod);
        TestContextManagerTests.assertExecutionOrder("beforeTestExecution", "beforeTestMethod-1", "beforeTestMethod-2", "beforeTestMethod-3", "beforeTestExecution-1", "beforeTestExecution-2", "beforeTestExecution-3");
        this.testContextManager.afterTestExecution(this, this.testMethod, null);
        TestContextManagerTests.assertExecutionOrder("afterTestExecution", "beforeTestMethod-1", "beforeTestMethod-2", "beforeTestMethod-3", "beforeTestExecution-1", "beforeTestExecution-2", "beforeTestExecution-3", "afterTestExecution-3", "afterTestExecution-2", "afterTestExecution-1");
        this.testContextManager.afterTestMethod(this, this.testMethod, null);
        TestContextManagerTests.assertExecutionOrder("afterTestMethod", "beforeTestMethod-1", "beforeTestMethod-2", "beforeTestMethod-3", "beforeTestExecution-1", "beforeTestExecution-2", "beforeTestExecution-3", "afterTestExecution-3", "afterTestExecution-2", "afterTestExecution-1", "afterTestMethod-3", "afterTestMethod-2", "afterTestMethod-1");
        // @formatter:on
    }

    @TestExecutionListeners({ TestContextManagerTests.FirstTel.class, TestContextManagerTests.SecondTel.class, TestContextManagerTests.ThirdTel.class })
    private static class ExampleTestCase {
        @SuppressWarnings("unused")
        public void exampleTestMethod() {
        }
    }

    private static class NamedTestExecutionListener implements TestExecutionListener {
        private final String name;

        public NamedTestExecutionListener(String name) {
            this.name = name;
        }

        @Override
        public void beforeTestMethod(TestContext testContext) {
            TestContextManagerTests.executionOrder.add(("beforeTestMethod-" + (this.name)));
        }

        @Override
        public void beforeTestExecution(TestContext testContext) {
            TestContextManagerTests.executionOrder.add(("beforeTestExecution-" + (this.name)));
        }

        @Override
        public void afterTestExecution(TestContext testContext) {
            TestContextManagerTests.executionOrder.add(("afterTestExecution-" + (this.name)));
        }

        @Override
        public void afterTestMethod(TestContext testContext) {
            TestContextManagerTests.executionOrder.add(("afterTestMethod-" + (this.name)));
        }
    }

    private static class FirstTel extends TestContextManagerTests.NamedTestExecutionListener {
        public FirstTel() {
            super("1");
        }
    }

    private static class SecondTel extends TestContextManagerTests.NamedTestExecutionListener {
        public SecondTel() {
            super("2");
        }
    }

    private static class ThirdTel extends TestContextManagerTests.NamedTestExecutionListener {
        public ThirdTel() {
            super("3");
        }
    }
}


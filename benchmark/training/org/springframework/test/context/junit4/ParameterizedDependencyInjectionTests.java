/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.context.junit4;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;


/**
 * Simple JUnit 4 based integration test which demonstrates how to use JUnit's
 * {@link Parameterized} Runner in conjunction with
 * {@link ContextConfiguration @ContextConfiguration}, the
 * {@link DependencyInjectionTestExecutionListener}, and a
 * {@link TestContextManager} to provide dependency injection to a
 * <em>parameterized test instance</em>.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see org.springframework.test.context.junit4.rules.ParameterizedSpringRuleTests
 */
@RunWith(Parameterized.class)
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class ParameterizedDependencyInjectionTests {
    private static final AtomicInteger invocationCount = new AtomicInteger();

    private static final TestContextManager testContextManager = new TestContextManager(ParameterizedDependencyInjectionTests.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Pet pet;

    @Parameterized.Parameter(0)
    public String employeeBeanName;

    @Parameterized.Parameter(1)
    public String employeeName;

    @Test
    public final void verifyPetAndEmployee() {
        ParameterizedDependencyInjectionTests.invocationCount.incrementAndGet();
        // Verifying dependency injection:
        Assert.assertNotNull("The pet field should have been autowired.", this.pet);
        // Verifying 'parameterized' support:
        Employee employee = this.applicationContext.getBean(this.employeeBeanName, Employee.class);
        Assert.assertEquals((("Name of the employee configured as bean [" + (this.employeeBeanName)) + "]."), this.employeeName, employee.getName());
    }
}


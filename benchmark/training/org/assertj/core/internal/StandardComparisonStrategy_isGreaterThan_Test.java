/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal;


import java.awt.Rectangle;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Employee;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link StandardComparisonStrategy#isGreaterThan(Object, Object)}.
 *
 * @author Joel Costigliola
 */
public class StandardComparisonStrategy_isGreaterThan_Test extends AbstractTest_StandardComparisonStrategy {
    @Test
    public void verify_that_isGreaterThan_delegates_to_compare_method() {
        Employee boss = Mockito.mock(Employee.class);
        Employee young = new Employee(10000, 25);
        AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.isGreaterThan(boss, young);
        Mockito.verify(boss).compareTo(young);
    }

    @Test
    public void should_pass() {
        Employee boss = new Employee(10000, 35);
        Employee young = new Employee(10000, 25);
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.isGreaterThan(boss, young)).isTrue();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.isGreaterThan(young, boss)).isFalse();
        Assertions.assertThat(AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.isGreaterThan(boss, boss)).isFalse();
    }

    @Test
    public void should_fail_if_first_parameter_is_not_comparable() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> AbstractTest_StandardComparisonStrategy.standardComparisonStrategy.isGreaterThan(new Rectangle(), "foo"));
    }
}


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
package org.assertj.core.api.atomic.referencearray;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.assertj.core.test.Employee;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_filteredOn_condition_Test extends AtomicReferenceArrayAssert_filtered_baseTest {
    protected Condition<Employee> oldEmployees;

    @Test
    public void should_filter_object_array_under_test_on_condition() {
        Assertions.assertThat(employees).filteredOn(oldEmployees).containsOnly(yoda, obiwan);
    }

    @Test
    public void should_filter_object_array_under_test_on_combined_condition() {
        Assertions.assertThat(employees).filteredOn(Assertions.not(oldEmployees)).contains(luke, noname);
    }

    @Test
    public void should_fail_if_given_condition_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(employees).filteredOn(null)).withMessage("The filter condition should not be null");
    }
}


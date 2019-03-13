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
package org.assertj.core.api.assumptions;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.AssumptionViolatedException;
import org.junit.jupiter.api.Test;


public class Assumptions_assumeThat_Test {
    @Test
    public void should_ignore_test_when_one_of_the_assumption_fails() {
        Assumptions.assumeThat("foo").isNotEmpty();
        Assertions.assertThatExceptionOfType(AssumptionViolatedException.class).isThrownBy(() -> assumeThat("bar").isEmpty());
    }

    @Test
    public void should_run_test_when_all_assumptions_are_met() {
        Assertions.assertThatCode(() -> {
            assumeThat("foo").isNotNull().isNotEmpty().isEqualTo("foo");
            assumeThat("bar").contains("ar").isNotBlank();
            assumeThat(asList("John", "Doe", "Jane", "Doe")).as("test description").withFailMessage("error message").withRepresentation(UNICODE_REPRESENTATION).usingElementComparator(CaseInsensitiveStringComparator.instance).filteredOn(( string) -> (string.length()) == 4).containsExactly("JOHN", "JANE");
        }).doesNotThrowAnyException();
    }
}


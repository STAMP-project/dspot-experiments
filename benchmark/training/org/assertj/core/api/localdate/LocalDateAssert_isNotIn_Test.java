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
package org.assertj.core.api.localdate;


import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link LocalDate} are already defined in assertj-core)
 */
public class LocalDateAssert_isNotIn_Test extends LocalDateAssertBaseTest {
    @Test
    public void test_isNotIn_assertion() {
        // WHEN
        Assertions.assertThat(LocalDateAssertBaseTest.REFERENCE).isNotIn(LocalDateAssertBaseTest.REFERENCE.plusDays(1).toString(), LocalDateAssertBaseTest.REFERENCE.plusDays(2).toString());
        // THEN
        Assertions.assertThatThrownBy(() -> assertThat(LocalDateAssertBaseTest.REFERENCE).isNotIn(LocalDateAssertBaseTest.REFERENCE.toString(), LocalDateAssertBaseTest.REFERENCE.plusDays(1).toString())).isInstanceOf(AssertionError.class);
    }

    @Test
    public void test_isNotIn_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            assertThat(LocalDate.of(2000, 1, 5)).isNotIn(LocalDate.of(2000, 1, 5).toString(), LocalDate.of(2012, 1, 1).toString());
        }).withMessage(String.format("%nExpecting:%n <2000-01-05>%nnot to be in:%n <[2000-01-05, 2012-01-01]>%n"));
    }

    @Test
    public void should_fail_if_dates_as_string_array_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDate.now()).isNotIn(((String[]) (null)))).withMessage("The given LocalDate array should not be null");
    }

    @Test
    public void should_fail_if_dates_as_string_array_parameter_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDate.now()).isNotIn(new String[0])).withMessage("The given LocalDate array should not be empty");
    }
}


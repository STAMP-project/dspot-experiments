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
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class LocalDateAssert_isAfter_Test extends LocalDateAssertBaseTest {
    @Test
    public void test_isAfter_assertion() {
        // WHEN
        Assertions.assertThat(LocalDateAssertBaseTest.AFTER).isAfter(LocalDateAssertBaseTest.REFERENCE);
        Assertions.assertThat(LocalDateAssertBaseTest.AFTER).isAfter(LocalDateAssertBaseTest.REFERENCE.toString());
        // THEN
        LocalDateAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(LocalDateAssertBaseTest.REFERENCE, LocalDateAssertBaseTest.REFERENCE);
        LocalDateAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(LocalDateAssertBaseTest.BEFORE, LocalDateAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isAfter_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(parse("2000-01-01")).isAfter(parse("2000-01-01"))).withMessage(String.format(("%n" + ((("Expecting:%n" + "  <2000-01-01>%n") + "to be strictly after:%n") + "  <2000-01-01>"))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            LocalDate actual = null;
            assertThat(actual).isAfter(LocalDate.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_date_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDate.now()).isAfter(((LocalDate) (null)))).withMessage("The LocalDate to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_date_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalDate.now()).isAfter(((String) (null)))).withMessage("The String representing the LocalDate to compare actual with should not be null");
    }
}


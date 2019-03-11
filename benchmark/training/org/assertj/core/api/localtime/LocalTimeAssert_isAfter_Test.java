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
package org.assertj.core.api.localtime;


import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class LocalTimeAssert_isAfter_Test extends LocalTimeAssertBaseTest {
    @Test
    public void test_isAfter_assertion() {
        // WHEN
        Assertions.assertThat(LocalTimeAssertBaseTest.AFTER).isAfter(LocalTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(LocalTimeAssertBaseTest.AFTER).isAfter(LocalTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        LocalTimeAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(LocalTimeAssertBaseTest.REFERENCE, LocalTimeAssertBaseTest.REFERENCE);
        LocalTimeAssert_isAfter_Test.verify_that_isAfter_assertion_fails_and_throws_AssertionError(LocalTimeAssertBaseTest.BEFORE, LocalTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isAfter_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(parse("03:00:05.123")).isAfter(parse("03:00:05.123456789"))).withMessage(String.format(("%n" + ((("Expecting:%n" + "  <03:00:05.123>%n") + "to be strictly after:%n") + "  <03:00:05.123456789>"))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            LocalTime actual = null;
            assertThat(actual).isAfter(LocalTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_timeTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalTime.now()).isAfter(((LocalTime) (null)))).withMessage("The LocalTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_timeTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalTime.now()).isAfter(((String) (null)))).withMessage("The String representing the LocalTime to compare actual with should not be null");
    }
}


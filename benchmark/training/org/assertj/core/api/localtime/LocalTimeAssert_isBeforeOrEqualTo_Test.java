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


/**
 *
 *
 * @author Pawe? Stawicki
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class LocalTimeAssert_isBeforeOrEqualTo_Test extends LocalTimeAssertBaseTest {
    @Test
    public void test_isBeforeOrEqual_assertion() {
        // WHEN
        Assertions.assertThat(LocalTimeAssertBaseTest.BEFORE).isBeforeOrEqualTo(LocalTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(LocalTimeAssertBaseTest.REFERENCE).isBeforeOrEqualTo(LocalTimeAssertBaseTest.REFERENCE);
        // THEN
        LocalTimeAssert_isBeforeOrEqualTo_Test.verify_that_isBeforeOrEqual_assertion_fails_and_throws_AssertionError(LocalTimeAssertBaseTest.AFTER, LocalTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isBeforeOrEqual_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(LocalTime.of(3, 0, 5)).isBeforeOrEqualTo(LocalTime.of(3, 0, 4))).withMessage(String.format(("%n" + ((("Expecting:%n" + "  <03:00:05>%n") + "to be before or equals to:%n") + "  <03:00:04>"))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            LocalTime actual = null;
            assertThat(actual).isBeforeOrEqualTo(LocalTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_timeTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalTime.now()).isBeforeOrEqualTo(((LocalTime) (null)))).withMessage("The LocalTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_timeTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(LocalTime.now()).isBeforeOrEqualTo(((String) (null)))).withMessage("The String representing the LocalTime to compare actual with should not be null");
    }
}


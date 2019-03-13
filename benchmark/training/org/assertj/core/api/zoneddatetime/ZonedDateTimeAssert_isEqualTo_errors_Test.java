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
package org.assertj.core.api.zoneddatetime;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link ZonedDateTime} are already defined in assertj-core)
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class ZonedDateTimeAssert_isEqualTo_errors_Test extends ZonedDateTimeAssertBaseTest {
    @Test
    public void test_isEqualTo_assertion() {
        // WHEN
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.REFERENCE).isEqualTo(ZonedDateTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        ZonedDateTimeAssert_isEqualTo_errors_Test.verify_that_isEqualTo_assertion_fails_and_throws_AssertionError(ZonedDateTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isEqualTo_assertion_error_message() {
        // @format:off
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ZonedDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC)).isEqualTo(ZonedDateTime.of(2012, 1, 1, 3, 3, 3, 0, ZoneOffset.UTC).toString())).withMessage(String.format("%nExpecting:%n <2000-01-05T03:00:05Z>%nto be equal to:%n <2012-01-01T03:03:03Z>%nbut was not."));
        // @format:on
    }

    @Test
    public void should_fail_if_actual_dateTime_is_null_and_expected_dateTime_is_not() {
        // @format:off
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((ZonedDateTime) (null))).isEqualTo(ZonedDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC))).withMessage(String.format("%nExpecting:%n <null>%nto be equal to:%n <2000-01-05T03:00:05Z>%nbut was not."));
        // @format:on
    }

    @Test
    public void should_fail_if_actual_dateTime_is_null_and_expected_dateTime_as_string_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((ZonedDateTime) (null))).isEqualTo("2000-01-01T01:00:00+01:00")).withMessage(String.format("%nExpecting:%n <null>%nto be equal to:%n <2000-01-01T01:00+01:00>%nbut was not."));
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isEqualTo(((String) (null)))).withMessage("The String representing the ZonedDateTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_expected_ZoneDateTime_is_null_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ZonedDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC)).isEqualTo(((ZonedDateTime) (null)))).withMessage(String.format("%nExpecting:%n <2000-01-05T03:00:05Z>%nto be equal to:%n <null>%nbut was not."));
    }
}


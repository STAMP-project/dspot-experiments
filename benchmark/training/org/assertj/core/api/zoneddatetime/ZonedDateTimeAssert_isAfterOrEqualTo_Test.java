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


import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
public class ZonedDateTimeAssert_isAfterOrEqualTo_Test extends ZonedDateTimeAssertBaseTest {
    @Test
    public void test_isAfterOrEqual_assertion() {
        // WHEN
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.AFTER).isAfterOrEqualTo(ZonedDateTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.AFTER).isAfterOrEqualTo(ZonedDateTimeAssertBaseTest.REFERENCE.toString());
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.REFERENCE).isAfterOrEqualTo(ZonedDateTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.REFERENCE).isAfterOrEqualTo(ZonedDateTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        ZonedDateTimeAssert_isAfterOrEqualTo_Test.verify_that_isAfterOrEqual_assertion_fails_and_throws_AssertionError(ZonedDateTimeAssertBaseTest.BEFORE, ZonedDateTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void isAfterOrEqualTo_should_compare_datetimes_in_actual_timezone() {
        ZonedDateTime utcDateTime = ZonedDateTime.of(2013, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        ZoneId cestTimeZone = ZoneId.of("Europe/Berlin");
        ZonedDateTime cestDateTime1 = ZonedDateTime.of(2013, 6, 10, 2, 0, 0, 0, cestTimeZone);
        ZonedDateTime cestDateTime2 = ZonedDateTime.of(2013, 6, 10, 1, 0, 0, 0, cestTimeZone);
        // utcDateTime = cestDateTime1
        Assertions.assertThat(utcDateTime).as("in UTC time zone").isAfterOrEqualTo(cestDateTime1);
        // utcDateTime > cestDateTime2
        Assertions.assertThat(utcDateTime).as("in UTC time zone").isAfterOrEqualTo(cestDateTime2);
    }

    @Test
    public void test_isAfterOrEqual_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ZonedDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC)).isAfterOrEqualTo(ZonedDateTime.of(2012, 1, 1, 3, 3, 3, 0, ZoneOffset.UTC))).withMessage(String.format("%nExpecting:%n  <2000-01-05T03:00:05Z>%nto be after or equals to:%n  <2012-01-01T03:03:03Z>"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            ZonedDateTime actual = null;
            assertThat(actual).isAfterOrEqualTo(ZonedDateTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_dateTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isAfterOrEqualTo(((ZonedDateTime) (null)))).withMessage("The ZonedDateTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isAfterOrEqualTo(((String) (null)))).withMessage("The String representing the ZonedDateTime to compare actual with should not be null");
    }
}


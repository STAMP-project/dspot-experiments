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
public class ZonedDateTimeAssert_isBefore_Test extends ZonedDateTimeAssertBaseTest {
    @Test
    public void test_isBefore_assertion() {
        // WHEN
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.BEFORE).isBefore(ZonedDateTimeAssertBaseTest.REFERENCE);
        Assertions.assertThat(ZonedDateTimeAssertBaseTest.BEFORE).isBefore(ZonedDateTimeAssertBaseTest.REFERENCE.toString());
        // THEN
        ZonedDateTimeAssert_isBefore_Test.verify_that_isBefore_assertion_fails_and_throws_AssertionError(ZonedDateTimeAssertBaseTest.REFERENCE, ZonedDateTimeAssertBaseTest.REFERENCE);
        ZonedDateTimeAssert_isBefore_Test.verify_that_isBefore_assertion_fails_and_throws_AssertionError(ZonedDateTimeAssertBaseTest.AFTER, ZonedDateTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void isBefore_should_compare_datetimes_in_actual_timezone() {
        ZonedDateTime utcDateTime = ZonedDateTime.of(2013, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        ZoneId cestTimeZone = ZoneId.of("Europe/Berlin");
        ZonedDateTime cestDateTime2 = ZonedDateTime.of(2013, 6, 10, 3, 0, 0, 0, cestTimeZone);
        // utcDateTime < cestDateTime2
        Assertions.assertThat(utcDateTime).as("in UTC time zone").isBefore(cestDateTime2);
        // utcDateTime = cestDateTime1
        try {
            ZonedDateTime cestDateTime1 = ZonedDateTime.of(2013, 6, 10, 2, 0, 0, 0, cestTimeZone);
            Assertions.assertThat(utcDateTime).as("in UTC time zone").isBefore(cestDateTime1);
        } catch (AssertionError e) {
            return;
        }
        Assertions.fail("Should have thrown AssertionError");
    }

    @Test
    public void test_isBefore_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ZonedDateTime.of(2000, 1, 5, 3, 0, 0, 0, ZoneOffset.UTC)).isBefore(ZonedDateTime.of(1998, 1, 1, 3, 3, 0, 0, ZoneOffset.UTC))).withMessage(String.format("%nExpecting:%n  <2000-01-05T03:00Z>%nto be strictly before:%n  <1998-01-01T03:03Z>"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            ZonedDateTime actual = null;
            assertThat(actual).isBefore(ZonedDateTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_dateTime_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isBefore(((ZonedDateTime) (null)))).withMessage("The ZonedDateTime to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(ZonedDateTime.now()).isBefore(((String) (null)))).withMessage("The String representing the ZonedDateTime to compare actual with should not be null");
    }
}


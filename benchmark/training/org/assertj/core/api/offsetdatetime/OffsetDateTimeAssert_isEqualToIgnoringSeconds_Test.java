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
package org.assertj.core.api.offsetdatetime;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.AbstractOffsetDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class OffsetDateTimeAssert_isEqualToIgnoringSeconds_Test extends BaseTest {
    private final OffsetDateTime refOffsetDateTime = OffsetDateTime.of(2000, 1, 1, 23, 51, 0, 0, ZoneOffset.UTC);

    @Test
    public void should_pass_if_actual_is_equal_to_other_ignoring_second_fields() {
        Assertions.assertThat(refOffsetDateTime).isEqualToIgnoringSeconds(refOffsetDateTime.plusSeconds(1));
    }

    @Test
    public void should_fail_if_actual_is_not_equal_to_given_offsetdatetime_with_second_ignored() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(refOffsetDateTime).isEqualToIgnoringSeconds(refOffsetDateTime.plusMinutes(1))).withMessage(String.format(("%n" + (((("Expecting:%n" + "  <2000-01-01T23:51Z>%n") + "to have same year, month, day, hour and minute as:%n") + "  <2000-01-01T23:52Z>%n") + "but had not."))));
    }

    @Test
    public void should_fail_as_seconds_fields_are_different_even_if_time_difference_is_less_than_a_second() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(refOffsetDateTime).isEqualToIgnoringSeconds(refOffsetDateTime.minusNanos(1))).withMessage(String.format(("%n" + (((("Expecting:%n" + "  <2000-01-01T23:51Z>%n") + "to have same year, month, day, hour and minute as:%n") + "  <2000-01-01T23:50:59.999999999Z>%n") + "but had not."))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            OffsetDateTime actual = null;
            assertThat(actual).isEqualToIgnoringSeconds(OffsetDateTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_offsetdatetime_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(refOffsetDateTime).isEqualToIgnoringSeconds(null)).withMessage(AbstractOffsetDateTimeAssert.NULL_OFFSET_DATE_TIME_PARAMETER_MESSAGE);
    }
}


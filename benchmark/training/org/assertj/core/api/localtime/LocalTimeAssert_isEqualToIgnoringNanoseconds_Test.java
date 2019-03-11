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
import org.assertj.core.api.AbstractLocalTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BaseTest;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class LocalTimeAssert_isEqualToIgnoringNanoseconds_Test extends BaseTest {
    private final LocalTime refLocalTime = LocalTime.of(0, 0, 1, 0);

    @Test
    public void should_pass_if_actual_is_equal_to_other_ignoring_nanosecond_fields() {
        Assertions.assertThat(refLocalTime).isEqualToIgnoringNanos(refLocalTime.withNano(55));
        Assertions.assertThat(refLocalTime).isEqualToIgnoringNanos(refLocalTime.plusNanos(1));
    }

    @Test
    public void should_fail_if_actual_is_not_equal_to_given_localtimetime_with_nanoseconds_ignored() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(refLocalTime).isEqualToIgnoringNanos(refLocalTime.plusSeconds(1))).withMessage(String.format(("%nExpecting:%n  " + ((("<00:00:01>%n" + "to have same hour, minute and second as:%n") + "  <00:00:02>%n") + "but had not."))));
    }

    @Test
    public void should_fail_as_seconds_fields_are_different_even_if_time_difference_is_less_than_a_second() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(refLocalTime).isEqualToIgnoringNanos(refLocalTime.minusNanos(1))).withMessage(String.format(("%nExpecting:%n" + ((("  <00:00:01>%n" + "to have same hour, minute and second as:%n") + "  <00:00:00.999999999>%n") + "but had not."))));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            LocalTime actual = null;
            assertThat(actual).isEqualToIgnoringNanos(LocalTime.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_given_localtimetime_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(refLocalTime).isEqualToIgnoringNanos(null)).withMessage(AbstractLocalTimeAssert.NULL_LOCAL_TIME_PARAMETER_MESSAGE);
    }
}


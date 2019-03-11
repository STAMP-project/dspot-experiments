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
package org.assertj.core.api.date;


import java.sql.Timestamp;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.DateAssertBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.api.DateAssert#hasSameTimeAs(java.util.Date)} </code>.
 *
 * @author Alexander Bischof
 */
public class DateAssert_hasSameTimeAsOtherDate_Test extends DateAssertBaseTest {
    @Test
    public void should_verify_that_actual_has_time_equals_to_expected() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        Assertions.assertThat(date).hasSameTimeAs(timestamp);
        Assertions.assertThat(timestamp).hasSameTimeAs(date);
    }

    @Test
    public void should_fail_when_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Date) (null))).hasSameTimeAs(new Date())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_exception_when_date_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(new Date()).hasSameTimeAs(((Date) (null)))).withMessage(ErrorMessages.dateToCompareActualWithIsNull());
    }
}


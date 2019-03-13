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
package org.assertj.core.api.instant;


import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class InstantAssert_IsBeforeOrEqualTo_Test extends InstantAssertBaseTest {
    @Test
    public void test_isBeforeOrEqual_assertion() {
        // WHEN
        Assertions.assertThat(InstantAssertBaseTest.BEFORE).isBeforeOrEqualTo(InstantAssertBaseTest.REFERENCE);
        Assertions.assertThat(InstantAssertBaseTest.REFERENCE).isBeforeOrEqualTo(InstantAssertBaseTest.REFERENCE);
        // THEN
        InstantAssert_IsBeforeOrEqualTo_Test.verify_that_isBeforeOrEqual_assertion_fails_and_throws_AssertionError(InstantAssertBaseTest.AFTER, InstantAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isBeforeOrEqual_assertion_error_message() {
        Instant instantReference = Instant.parse("2007-12-03T10:15:30.00Z");
        Instant instantAfter = Instant.parse("2007-12-03T10:15:35.00Z");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(instantAfter).isBeforeOrEqualTo(instantReference)).withMessage(String.format("%nExpecting:%n  <2007-12-03T10:15:35Z>%nto be before or equals to:%n  <2007-12-03T10:15:30Z>"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Instant actual = null;
            assertThat(actual).isBeforeOrEqualTo(Instant.now());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_date_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Instant.now()).isBeforeOrEqualTo(((Instant) (null)))).withMessage("The Instant to compare actual with should not be null");
    }

    @Test
    public void should_fail_if_date_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Instant.now()).isBeforeOrEqualTo(((String) (null)))).withMessage("The String representing the Instant to compare actual with should not be null");
    }
}


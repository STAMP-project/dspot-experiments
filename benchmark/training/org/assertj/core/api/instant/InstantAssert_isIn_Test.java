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
import org.junit.jupiter.api.Test;


public class InstantAssert_isIn_Test extends InstantAssertBaseTest {
    @Test
    public void test_isIn_assertion() {
        // WHEN
        Assertions.assertThat(InstantAssertBaseTest.REFERENCE).isIn(InstantAssertBaseTest.REFERENCE.toString(), InstantAssertBaseTest.REFERENCE.plusSeconds(1).toString());
        // THEN
        Assertions.assertThatThrownBy(() -> assertThat(InstantAssertBaseTest.REFERENCE).isIn(InstantAssertBaseTest.REFERENCE.plusSeconds(1).toString(), InstantAssertBaseTest.REFERENCE.plusSeconds(2).toString())).isInstanceOf(AssertionError.class);
    }

    @Test
    public void test_isIn_assertion_error_message() {
        Instant instantReference = Instant.parse("2007-12-03T10:15:30.00Z");
        Instant instantAfter = Instant.parse("2007-12-03T10:15:35.00Z");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(instantReference).isIn(instantAfter.toString())).withMessage(String.format("%nExpecting:%n <2007-12-03T10:15:30Z>%nto be in:%n <[2007-12-03T10:15:35Z]>%n"));
    }

    @Test
    public void should_fail_if_dates_as_string_array_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Instant.now()).isIn(((String[]) (null)))).withMessage("The given Instant array should not be null");
    }

    @Test
    public void should_fail_if_dates_as_string_array_parameter_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Instant.now()).isIn(new String[0])).withMessage("The given Instant array should not be empty");
    }
}


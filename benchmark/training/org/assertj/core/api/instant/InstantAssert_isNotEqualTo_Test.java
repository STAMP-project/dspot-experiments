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


public class InstantAssert_isNotEqualTo_Test extends InstantAssertBaseTest {
    @Test
    public void test_isNotEqualTo_assertion() {
        // WHEN
        Assertions.assertThat(InstantAssertBaseTest.REFERENCE).isNotEqualTo(InstantAssertBaseTest.REFERENCE.plusSeconds(1).toString());
        // THEN
        Assertions.assertThatThrownBy(() -> assertThat(InstantAssertBaseTest.REFERENCE).isNotEqualTo(InstantAssertBaseTest.REFERENCE.toString())).isInstanceOf(AssertionError.class);
    }

    @Test
    public void test_isNotEqualTo_assertion_error_message() {
        Instant instantReference = Instant.parse("2007-12-03T10:15:30.00Z");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(instantReference).isNotEqualTo(instantReference.toString())).withMessage(String.format("%nExpecting:%n <2007-12-03T10:15:30Z>%nnot to be equal to:%n <2007-12-03T10:15:30Z>%n"));
    }

    @Test
    public void should_fail_if_date_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(Instant.now()).isNotEqualTo(((String) (null)))).withMessage("The String representing the Instant to compare actual with should not be null");
    }
}


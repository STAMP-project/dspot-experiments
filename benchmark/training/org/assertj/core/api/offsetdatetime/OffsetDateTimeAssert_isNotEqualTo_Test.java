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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link java.time.OffsetDateTime} are already defined in assertj-core)
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class OffsetDateTimeAssert_isNotEqualTo_Test extends OffsetDateTimeAssertBaseTest {
    @Test
    public void test_isNotEqualTo_assertion() {
        // WHEN
        Assertions.assertThat(OffsetDateTimeAssertBaseTest.REFERENCE).isNotEqualTo(OffsetDateTimeAssertBaseTest.REFERENCE.plusDays(1));
        Assertions.assertThat(OffsetDateTimeAssertBaseTest.REFERENCE).isNotEqualTo(OffsetDateTimeAssertBaseTest.REFERENCE.plusDays(1).toString());
        // THEN
        Assertions.assertThatThrownBy(() -> assertThat(OffsetDateTimeAssertBaseTest.REFERENCE).isNotEqualTo(OffsetDateTimeAssertBaseTest.REFERENCE.toString())).isInstanceOf(AssertionError.class);
    }

    @Test
    public void test_isNotEqualTo_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            String offsetDateTimeAsString = OffsetDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC).toString();
            assertThat(OffsetDateTime.of(2000, 1, 5, 3, 0, 5, 0, ZoneOffset.UTC)).isNotEqualTo(offsetDateTimeAsString);
        }).withMessage(String.format(("%nExpecting:%n" + ((" <2000-01-05T03:00:05Z>%n" + "not to be equal to:%n") + " <2000-01-05T03:00:05Z>%n"))));
    }

    @Test
    public void should_fail_if_dateTime_as_string_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(OffsetDateTime.now()).isNotEqualTo(((String) (null)))).withMessage("The String representing the OffsetDateTime to compare actual with should not be null");
    }
}


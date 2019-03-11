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
package org.assertj.core.api.offsettime;


import java.time.OffsetTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Only test String based assertion (tests with {@link java.time.OffsetTime} are already defined in assertj-core)
 *
 * @author Joel Costigliola
 * @author Marcin Zaj?czkowski
 */
public class OffsetTimeAssert_isIn_Test extends OffsetTimeAssertBaseTest {
    @Test
    public void test_isIn_assertion() {
        // WHEN
        Assertions.assertThat(OffsetTimeAssertBaseTest.REFERENCE).isIn(OffsetTimeAssertBaseTest.REFERENCE, OffsetTimeAssertBaseTest.REFERENCE.plusHours(1));
        Assertions.assertThat(OffsetTimeAssertBaseTest.REFERENCE).isIn(OffsetTimeAssertBaseTest.REFERENCE.toString(), OffsetTimeAssertBaseTest.REFERENCE.plusHours(1).toString());
        // THEN
        OffsetTimeAssert_isIn_Test.verify_that_isIn_assertion_fails_and_throws_AssertionError(OffsetTimeAssertBaseTest.REFERENCE);
    }

    @Test
    public void test_isIn_assertion_error_message() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(OffsetTime.of(3, 0, 5, 0, ZoneOffset.UTC)).isIn("03:03:03Z")).withMessage(String.format(("%n" + ((("Expecting:%n" + " <03:00:05Z>%n") + "to be in:%n") + " <[03:03:03Z]>%n"))));
    }

    @Test
    public void should_fail_if_offsetTimes_as_string_array_parameter_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(OffsetTime.now()).isIn(((String[]) (null)))).withMessage("The given OffsetTime array should not be null");
    }

    @Test
    public void should_fail_if_offsetTimes_as_string_array_parameter_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(OffsetTime.now()).isIn(new String[0])).withMessage("The given OffsetTime array should not be empty");
    }
}


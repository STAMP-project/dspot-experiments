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
package org.assertj.core.internal.maps;


import java.util.regex.Pattern;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainKeys;
import org.assertj.core.error.ShouldContainPattern;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Maps#assertHasEntrySatisfying(AssertionInfo, Map, Object, Consumer)}</code>.
 *
 * @author Valeriy Vyrva
 */
public class Maps_assertHasEntrySatisfyingConsumer_Test extends MapsBaseTest {
    private static final Pattern IS_DIGITS = Pattern.compile("^\\d+$");

    @Test
    public void should_pass_if_actual_contains_null_key_with_value_matching_condition() {
        maps.assertHasEntrySatisfying(TestData.someInfo(), actual, null, ( s) -> assertThat(s).isNull());
    }

    @Test
    public void should_pass_if_actual_contains_key_with_value_matching_condition() {
        maps.assertHasEntrySatisfying(TestData.someInfo(), actual, "name", ( s) -> assertThat(s).startsWith("Yo"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasEntrySatisfying(someInfo(), ((Map<Integer, ?>) (null)), 8, ( o) -> assertThat(o).isNotNull())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contains_key() {
        AssertionInfo info = TestData.someInfo();
        String key = "id";
        try {
            maps.assertHasEntrySatisfying(info, actual, key, ( s) -> assertThat(s).containsPattern(IS_DIGITS));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainKeys.shouldContainKeys(actual, Sets.newLinkedHashSet(key)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_key_with_value_not_matching_condition() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasEntrySatisfying(someInfo(), actual, "name", ( s) -> assertThat(s).containsPattern(IS_DIGITS))).withMessage(ShouldContainPattern.shouldContainPattern("Yoda", Maps_assertHasEntrySatisfyingConsumer_Test.IS_DIGITS.pattern()).create());
    }

    @Test
    public void should_fail_if_actual_contains_null_key_with_value_does_not_matching_condition() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasEntrySatisfying(someInfo(), actual, null, ( s) -> assertThat(s).isNotNull())).withMessage(FailureMessages.actualIsNull());
    }
}


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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Maps#assertDoesNotContain(AssertionInfo, Map, MapEntry[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Maps_assertDoesNotContain_Test extends MapsBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        maps.assertDoesNotContain(TestData.someInfo(), actual, Arrays.array(MapEntry.entry("job", "Jedi")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> maps.assertDoesNotContain(someInfo(), actual, new MapEntry[0])).withMessage(ErrorMessages.entriesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.entriesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertDoesNotContain(someInfo(), null, array(entry("job", "Jedi")))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        AssertionInfo info = TestData.someInfo();
        MapEntry<String, String>[] expected = Arrays.array(MapEntry.entry("name", "Yoda"), MapEntry.entry("job", "Jedi"));
        try {
            maps.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet(MapEntry.entry("name", "Yoda"))));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


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


import java.util.Collections;
import java.util.Map;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.error.ShouldContainOnly;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Maps;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Maps#assertContainsOnly(org.assertj.core.api.AssertionInfo, java.util.Map, org.assertj.core.data.MapEntry...)}</code>
 * .
 *
 * @author Jean-Christophe Gay
 */
public class Maps_assertContainsOnly_Test extends MapsBaseTest {
    @SuppressWarnings("unchecked")
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertContainsOnly(someInfo(), null, entry("name", "Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_fail_if_given_entries_array_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertContainsOnly(someInfo(), actual, ((MapEntry[]) (null)))).withMessage(ErrorMessages.entriesToLookForIsNull());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_fail_if_given_entries_array_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> maps.assertContainsOnly(someInfo(), actual, emptyEntries())).withMessage(ErrorMessages.entriesToLookForIsEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_pass_if_actual_and_entries_are_empty() {
        maps.assertContainsOnly(TestData.someInfo(), Collections.emptyMap(), MapsBaseTest.emptyEntries());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_pass_if_actual_contains_only_expected_entries() {
        maps.assertContainsOnly(TestData.someInfo(), actual, MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
    }

    @Test
    public void should_fail_if_actual_contains_unexpected_entry() {
        AssertionInfo info = TestData.someInfo();
        MapEntry<String, String>[] expected = Arrays.array(MapEntry.entry("name", "Yoda"));
        try {
            maps.assertContainsOnly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expected, Collections.emptySet(), Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("color", "green"))));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }

    @Test
    public void should_fail_if_actual_does_not_contains_every_expected_entries() {
        AssertionInfo info = TestData.someInfo();
        MapEntry<String, String>[] expected = Arrays.array(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        Map<String, String> underTest = Maps.mapOf(MapEntry.entry("name", "Yoda"));
        try {
            maps.assertContainsOnly(info, underTest, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(underTest, expected, Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("color", "green")), Collections.emptySet()));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }

    @Test
    public void should_fail_if_actual_does_not_contains_every_expected_entries_and_contains_unexpected_one() {
        AssertionInfo info = TestData.someInfo();
        MapEntry<String, String>[] expected = Arrays.array(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        Map<String, String> underTest = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("job", "Jedi"));
        try {
            maps.assertContainsOnly(info, underTest, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(underTest, expected, Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("color", "green")), Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("job", "Jedi"))));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }

    @Test
    public void should_fail_if_actual_contains_entry_key_with_different_value() {
        AssertionInfo info = TestData.someInfo();
        MapEntry<String, String>[] expectedEntries = Arrays.array(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "yellow"));
        try {
            maps.assertContainsOnly(info, actual, expectedEntries);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expectedEntries, Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("color", "yellow")), Maps_assertContainsOnly_Test.newHashSet(MapEntry.entry("color", "green"))));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }
}


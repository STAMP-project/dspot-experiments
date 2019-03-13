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
import org.assertj.core.error.ShouldContainOnlyKeys;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Maps;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Maps#assertContainsOnlyKeys(org.assertj.core.api.AssertionInfo, java.util.Map, java.lang.Object...)}</code>
 * .
 *
 * @author Christopher Arnott
 */
public class Maps_assertContainsOnlyKeys_Test extends MapsBaseTest {
    private static final String ARRAY_OF_KEYS = "array of keys";

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertContainsOnlyKeys(someInfo(), null, "name")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_given_keys_array_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertContainsOnlyKeys(someInfo(), actual, ((String[]) (null)))).withMessage(ErrorMessages.keysToLookForIsNull(Maps_assertContainsOnlyKeys_Test.ARRAY_OF_KEYS));
    }

    @Test
    public void should_fail_if_given_keys_array_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> maps.assertContainsOnlyKeys(someInfo(), actual, emptyKeys())).withMessage(ErrorMessages.keysToLookForIsEmpty(Maps_assertContainsOnlyKeys_Test.ARRAY_OF_KEYS));
    }

    @Test
    public void should_pass_if_actual_and_given_keys_are_empty() {
        maps.assertContainsOnlyKeys(TestData.someInfo(), Collections.emptyMap(), ((Object[]) (MapsBaseTest.emptyKeys())));
    }

    @Test
    public void should_pass_if_actual_contains_only_expected_keys() {
        maps.assertContainsOnlyKeys(TestData.someInfo(), actual, "color", "name");
        maps.assertContainsOnlyKeys(TestData.someInfo(), actual, "name", "color");
    }

    @Test
    public void should_fail_if_actual_contains_an_unexpected_key() {
        AssertionInfo info = TestData.someInfo();
        String[] expectedKeys = new String[]{ "name" };
        try {
            maps.assertContainsOnlyKeys(info, actual, expectedKeys);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyKeys.shouldContainOnlyKeys(actual, expectedKeys, Collections.emptySet(), Maps_assertContainsOnlyKeys_Test.newHashSet("color")));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }

    @Test
    public void should_fail_if_actual_does_not_contains_all_expected_keys() {
        AssertionInfo info = TestData.someInfo();
        String[] expectedKeys = new String[]{ "name", "color" };
        Map<String, String> underTest = Maps.mapOf(MapEntry.entry("name", "Yoda"));
        try {
            maps.assertContainsOnlyKeys(info, underTest, expectedKeys);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyKeys.shouldContainOnlyKeys(underTest, expectedKeys, Maps_assertContainsOnlyKeys_Test.newHashSet("color"), Collections.emptySet()));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }

    @Test
    public void should_fail_if_actual_does_not_contains_all_expected_keys_and_contains_unexpected_one() {
        AssertionInfo info = TestData.someInfo();
        String[] expectedKeys = new String[]{ "name", "color" };
        Map<String, String> underTest = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("job", "Jedi"));
        try {
            maps.assertContainsOnlyKeys(info, underTest, expectedKeys);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyKeys.shouldContainOnlyKeys(underTest, expectedKeys, Maps_assertContainsOnlyKeys_Test.newHashSet("color"), Maps_assertContainsOnlyKeys_Test.newHashSet("job")));
            return;
        }
        Assertions.shouldHaveThrown(AssertionError.class);
    }
}


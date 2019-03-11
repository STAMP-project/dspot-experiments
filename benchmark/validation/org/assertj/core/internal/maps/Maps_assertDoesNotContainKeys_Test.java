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
import org.assertj.core.error.ShouldNotContainKeys;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.Maps#assertDoesNotContainKeys(org.assertj.core.api.AssertionInfo, java.util.Map, Object...)}</code>.
 *
 * @author dorzey
 */
public class Maps_assertDoesNotContainKeys_Test extends MapsBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_keys() {
        maps.assertDoesNotContainKeys(TestData.someInfo(), actual, "age");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertDoesNotContainKeys(someInfo(), null, "name", "color")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_key_is_null() {
        maps.assertDoesNotContainKeys(TestData.someInfo(), actual, ((String) (null)));
    }

    @Test
    public void should_fail_if_actual_contains_key() {
        AssertionInfo info = TestData.someInfo();
        String key = "name";
        try {
            maps.assertDoesNotContainKeys(info, actual, key);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainKeys.shouldNotContainKeys(actual, Sets.newLinkedHashSet(key)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_keys() {
        AssertionInfo info = TestData.someInfo();
        String key1 = "name";
        String key2 = "color";
        try {
            maps.assertDoesNotContainKeys(info, actual, key1, key2);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainKeys.shouldNotContainKeys(actual, Sets.newLinkedHashSet(key1, key2)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


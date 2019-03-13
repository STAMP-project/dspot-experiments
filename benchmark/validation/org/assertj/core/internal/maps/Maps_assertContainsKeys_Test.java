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
import org.assertj.core.error.ShouldContainKeys;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Maps#assertContainsKeys(AssertionInfo, Map, Object...)}</code>.
 *
 * @author William Delanoue
 */
public class Maps_assertContainsKeys_Test extends MapsBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_key() {
        maps.assertContainsKeys(TestData.someInfo(), actual, "name");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertContainsKeys(someInfo(), null, "name")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_success_if_key_is_null() {
        maps.assertContainsKeys(TestData.someInfo(), actual, ((String) (null)));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_key() {
        AssertionInfo info = TestData.someInfo();
        String key = "power";
        try {
            maps.assertContainsKeys(info, actual, key);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainKeys.shouldContainKeys(actual, Sets.newLinkedHashSet(key)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_contain_keys() {
        AssertionInfo info = TestData.someInfo();
        String key1 = "power";
        String key2 = "rangers";
        try {
            maps.assertContainsKeys(info, actual, key1, key2);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainKeys.shouldContainKeys(actual, Sets.newLinkedHashSet(key1, key2)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


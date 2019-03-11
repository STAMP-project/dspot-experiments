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


import java.util.Map;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.error.ShouldHaveSize;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.Maps;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Maps#assertHasSize(AssertionInfo, Map, int)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Maps_assertHasSize_Test extends MapsBaseTest {
    @Test
    public void should_pass_if_size_of_actual_is_equal_to_expected_size() {
        Map<?, ?> actual = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("job", "Yedi Master"));
        maps.assertHasSize(TestData.someInfo(), actual, 2);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasSize(someInfo(), null, 8)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_equal_to_expected_size() {
        AssertionInfo info = TestData.someInfo();
        Map<?, ?> actual = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("job", "Yedi Master"));
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasSize(info, actual, 8)).withMessage(ShouldHaveSize.shouldHaveSize(actual, actual.size(), 8).create());
    }
}


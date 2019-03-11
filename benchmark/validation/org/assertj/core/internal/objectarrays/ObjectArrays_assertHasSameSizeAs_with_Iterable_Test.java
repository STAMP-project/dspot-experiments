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
package org.assertj.core.internal.objectarrays;


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectArrays#assertHasSameSizeAs(AssertionInfo, Object[], Iterable)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class ObjectArrays_assertHasSameSizeAs_with_Iterable_Test extends ObjectArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSameSizeAs(someInfo(), null, newArrayList("Solo", "Leia"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            String[] actual = array("Solo", "Leia");
            Iterable<?> other = null;
            arrays.assertHasSameSizeAs(someInfo(), actual, other);
        }).withMessage("The Iterable to compare actual size with should not be null");
    }

    @Test
    public void should_fail_if_actual_size_is_not_equal_to_other_size() {
        AssertionInfo info = TestData.someInfo();
        String[] actual = Arrays.array("Yoda");
        List<String> other = Lists.newArrayList("Solo", "Leia");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSameSizeAs(info, actual, other)).withMessage(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, actual.length, other.size()).create(null, info.representation()));
    }

    @Test
    public void should_pass_if_actual_has_same_size_as_other() {
        arrays.assertHasSameSizeAs(TestData.someInfo(), Arrays.array("Solo", "Leia"), Lists.newArrayList("Solo", "Leia"));
    }
}


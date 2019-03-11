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


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldOnlyHaveElementsOfTypes;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ObjectArrays_assertHasOnlyElementsOfTypes_Test extends ObjectArraysBaseTest {
    private static final Object[] ARRAY = new Object[]{ 6, 7.0, 8L };

    @Test
    public void should_pass_if_actual_has_only_elements_of_the_expected_types() {
        arrays.assertHasOnlyElementsOfTypes(TestData.someInfo(), ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY, Number.class);
        arrays.assertHasOnlyElementsOfTypes(TestData.someInfo(), ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY, Number.class, Long.class, Integer.class);
    }

    @Test
    public void should_pass_if_actual_has_only_elements_of_the_expected_types_even_if_some_types_dont_match_any_elements() {
        arrays.assertHasOnlyElementsOfTypes(TestData.someInfo(), ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY, Number.class, Long.class, Integer.class, String.class);
    }

    @Test
    public void should_pass_if_actual_and_given_types_are_empty() {
        Class<?>[] types = new Class<?>[0];
        arrays.assertHasOnlyElementsOfTypes(TestData.someInfo(), Arrays.array(), types);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // GIVEN
        Object[] array = null;
        // GIVEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> arrays.assertHasOnlyElementsOfTypes(someInfo(), array, .class));
        // THEN
        Assertions.assertThat(error).hasMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_types_are_empty_but_actual_is_not() {
        // GIVEN
        Class<?>[] types = new Class<?>[0];
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> arrays.assertHasOnlyElementsOfTypes(someInfo(), ARRAY, types));
        // THEN
        Assertions.assertThat(error).hasMessage(ShouldOnlyHaveElementsOfTypes.shouldOnlyHaveElementsOfTypes(ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY, types, Lists.list(ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY)).create());
    }

    @Test
    public void should_fail_if_one_element_in_actual_does_not_belong_to_the_expected_types() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> arrays.assertHasOnlyElementsOfTypes(someInfo(), ARRAY, .class, .class));
        // THEN
        Assertions.assertThat(error).hasMessage(ShouldOnlyHaveElementsOfTypes.shouldOnlyHaveElementsOfTypes(ObjectArrays_assertHasOnlyElementsOfTypes_Test.ARRAY, Arrays.array(Long.class, String.class), Lists.list(6, 7.0)).create());
    }

    @Test
    public void should_throw_assertion_error_and_not_null_pointer_exception_on_null_elements() {
        // GIVEN
        Object[] array = Arrays.array(null, "notNull");
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> arrays.assertHasOnlyElementsOfTypes(someInfo(), array, .class));
        // THEN
        Assertions.assertThat(error).hasMessage(ShouldOnlyHaveElementsOfTypes.shouldOnlyHaveElementsOfTypes(array, Arrays.array(Long.class), Lists.list(null, "notNull")).create());
    }
}


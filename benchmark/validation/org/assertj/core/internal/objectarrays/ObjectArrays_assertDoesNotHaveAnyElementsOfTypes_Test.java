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


import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotHaveAnyElementsOfTypes;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class ObjectArrays_assertDoesNotHaveAnyElementsOfTypes_Test extends ObjectArraysBaseTest {
    private static final Object[] array = new Object[]{ 6, 7.0, 8L };

    @Test
    public void should_pass_if_actual_does_not_have_any_elements_of_the_unexpected_types() {
        arrays.assertDoesNotHaveAnyElementsOfTypes(TestData.someInfo(), ObjectArrays_assertDoesNotHaveAnyElementsOfTypes_Test.array, Arrays.array(Float.class, BigDecimal.class));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotHaveAnyElementsOfTypes(someInfo(), null, .class)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_one_element_is_one_of_the_unexpected_types() {
        // GIVEN
        Map<Class<?>, List<Object>> nonMatchingElementsByType = new LinkedHashMap<>();
        nonMatchingElementsByType.put(Long.class, Lists.newArrayList(8L));
        Class<?>[] unexpectedTypes = new Class<?>[]{ Long.class };
        // THEN
        String message = ShouldNotHaveAnyElementsOfTypes.shouldNotHaveAnyElementsOfTypes(ObjectArrays_assertDoesNotHaveAnyElementsOfTypes_Test.array, unexpectedTypes, nonMatchingElementsByType).create();
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            // WHEN;
            arrays.assertDoesNotHaveAnyElementsOfTypes(someInfo(), array, .class);
        }).withMessage(message);
    }

    @Test
    public void should_fail_if_one_element_type_is_a_subclass_one_of_the_unexpected_types() {
        // GIVEN
        Map<Class<?>, List<Object>> nonMatchingElementsByType = new LinkedHashMap<>();
        nonMatchingElementsByType.put(Number.class, Lists.newArrayList(6, 7.0, 8L));
        Class<?>[] unexpectedTypes = new Class<?>[]{ Number.class };
        // THEN
        String message = ShouldNotHaveAnyElementsOfTypes.shouldNotHaveAnyElementsOfTypes(ObjectArrays_assertDoesNotHaveAnyElementsOfTypes_Test.array, unexpectedTypes, nonMatchingElementsByType).create();
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            // WHEN;
            arrays.assertDoesNotHaveAnyElementsOfTypes(someInfo(), array, .class);
        }).withMessage(message);
    }
}


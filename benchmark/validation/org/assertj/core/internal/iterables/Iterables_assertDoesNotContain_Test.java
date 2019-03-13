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
package org.assertj.core.internal.iterables;


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertDoesNotContain(AssertionInfo, Collection, Object[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Iterables_assertDoesNotContain_Test extends IterablesBaseTest {
    private static List<String> actual = Lists.newArrayList("Luke", "Yoda", "Leia");

    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        iterables.assertDoesNotContain(TestData.someInfo(), Iterables_assertDoesNotContain_Test.actual, Arrays.array("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated() {
        iterables.assertDoesNotContain(TestData.someInfo(), Iterables_assertDoesNotContain_Test.actual, Arrays.array("Han", "Han", "Anakin"));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> iterables.assertDoesNotContain(someInfo(), Iterables_assertDoesNotContain_Test.actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertDoesNotContain(someInfo(), emptyList(), null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertDoesNotContain(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            iterables.assertDoesNotContain(info, Iterables_assertDoesNotContain_Test.actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(Iterables_assertDoesNotContain_Test.actual, expected, Sets.newLinkedHashSet("Luke", "Yoda")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_does_not_contain_given_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(TestData.someInfo(), Iterables_assertDoesNotContain_Test.actual, Arrays.array("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(TestData.someInfo(), Iterables_assertDoesNotContain_Test.actual, Arrays.array("Han", "Han", "Anakin"));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "LuKe", "YODA", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(info, Iterables_assertDoesNotContain_Test.actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(Iterables_assertDoesNotContain_Test.actual, expected, Sets.newLinkedHashSet("LuKe", "YODA"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


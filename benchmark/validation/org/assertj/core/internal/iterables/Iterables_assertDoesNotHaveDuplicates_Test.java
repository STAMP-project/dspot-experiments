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


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotHaveDuplicates;
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
 * Tests for <code>{@link Iterables#assertDoesNotHaveDuplicates(AssertionInfo, Collection)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Iterables_assertDoesNotHaveDuplicates_Test extends IterablesBaseTest {
    private static final int GENERATED_OBJECTS_NUMBER = 50000;

    private final List<String> actual = Lists.newArrayList("Luke", "Yoda", "Leia");

    @Test
    public void should_pass_if_actual_does_not_have_duplicates() {
        iterables.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        iterables.assertDoesNotHaveDuplicates(TestData.someInfo(), Collections.emptyList());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> duplicates = Sets.newLinkedHashSet("Luke", "Yoda");
        actual.addAll(duplicates);
        try {
            iterables.assertDoesNotHaveDuplicates(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, duplicates));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_within_time_constraints() {
        List<UUID> generated = Stream.generate(UUID::randomUUID).limit(Iterables_assertDoesNotHaveDuplicates_Test.GENERATED_OBJECTS_NUMBER).collect(Collectors.toList());
        long time = System.currentTimeMillis();
        iterables.assertDoesNotHaveDuplicates(TestData.someInfo(), generated);
        // check that it takes less than 2 seconds, usually it takes 100ms on an average computer
        // with the previous implementation, it would take minutes ...
        System.out.println(("Time elapsed in ms for assertDoesNotHaveDuplicates : " + ((System.currentTimeMillis()) - time)));
        Assertions.assertThat(((System.currentTimeMillis()) - time)).isLessThan(2000);
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_array() {
        Collection<String[]> actual = Lists.newArrayList(Arrays.array("Luke", "Yoda"), Arrays.array("Luke", "Yoda"));
        // duplicates is commented, because mockito is not smart enough to compare arrays contents
        // Collection<String[]> duplicates = newLinkedHashSet();
        // duplicates.add(array("Luke", "Yoda"));
        try {
            iterables.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
        } catch (AssertionError e) {
            // can't use verify since mockito not smart enough to compare arrays contents
            // verify(failures).failure(info, shouldNotHaveDuplicates(actual, duplicates));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_does_not_have_duplicates_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> duplicates = Sets.newLinkedHashSet("LUKE", "yoda");
        actual.addAll(duplicates);
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotHaveDuplicates(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, duplicates, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_within_time_constraints_with_custom_comparison_strategy() {
        List<String> generated = Stream.generate(() -> UUID.randomUUID().toString()).limit(Iterables_assertDoesNotHaveDuplicates_Test.GENERATED_OBJECTS_NUMBER).collect(Collectors.toList());
        long time = System.currentTimeMillis();
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotHaveDuplicates(TestData.someInfo(), generated);
        // check that it takes less than 10 seconds, usually it takes 1000ms on an average computer
        // with the previous implementation, it would take minutes ...
        System.out.println(("Time elapsed in ms for assertDoesNotHaveDuplicates with custom comparison strategy : " + ((System.currentTimeMillis()) - time)));
        Assertions.assertThat(((System.currentTimeMillis()) - time)).isLessThan(10000);
    }
}


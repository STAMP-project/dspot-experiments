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
package org.assertj.core.internal.doublearrays;


import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeSorted;
import org.assertj.core.internal.DoubleArraysBaseTest;
import org.assertj.core.test.DoubleArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link DoubleArrays#assertIsSortedAccordingToComparator(AssertionInfo, double[], Comparator)}</code>
 *
 * @author Joel Costigliola
 */
public class DoubleArrays_assertIsSortedAccordingToComparator_Test extends DoubleArraysBaseTest {
    private Comparator<Double> doubleDescendingOrderComparator;

    private Comparator<Double> doubleSquareComparator;

    @Test
    public void should_pass_if_actual_is_sorted_according_to_given_comparator() {
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), actual, doubleDescendingOrderComparator);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_given_comparator_is() {
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), DoubleArrays.emptyArray(), doubleDescendingOrderComparator);
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), DoubleArrays.emptyArray(), doubleSquareComparator);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSortedAccordingToComparator(someInfo(), null, doubleDescendingOrderComparator)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_comparator_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertIsSortedAccordingToComparator(someInfo(), emptyArray(), null));
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_according_to_given_comparator() {
        actual = new double[]{ 3.0, 2.0, 1.0, 9.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSortedAccordingToComparator(someInfo(), actual, doubleDescendingOrderComparator)).withMessage(ShouldBeSorted.shouldBeSortedAccordingToGivenComparator(2, actual, doubleDescendingOrderComparator).create());
    }
}


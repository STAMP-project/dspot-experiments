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
package org.assertj.core.internal.bytearrays;


import java.util.Comparator;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeSorted;
import org.assertj.core.internal.ByteArraysBaseTest;
import org.assertj.core.test.ByteArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ByteArrays#assertIsSortedAccordingToComparator(AssertionInfo, byte[], Comparator)}</code>
 *
 * @author Joel Costigliola
 */
public class ByteArrays_assertIsSortedAccordingToComparator_Test extends ByteArraysBaseTest {
    private Comparator<Byte> byteDescendingOrderComparator;

    private Comparator<Byte> byteAscendingOrderComparator;

    @Test
    public void should_pass_if_actual_is_sorted_according_to_given_comparator() {
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), actual, byteDescendingOrderComparator);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_given_comparator_is() {
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), ByteArrays.emptyArray(), byteDescendingOrderComparator);
        arrays.assertIsSortedAccordingToComparator(TestData.someInfo(), ByteArrays.emptyArray(), byteAscendingOrderComparator);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertIsSortedAccordingToComparator(someInfo(), null, byteDescendingOrderComparator)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_comparator_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertIsSortedAccordingToComparator(someInfo(), emptyArray(), null));
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_according_to_given_comparator() {
        AssertionInfo info = TestData.someInfo();
        actual = new byte[]{ 3, 2, 1, 9 };
        try {
            arrays.assertIsSortedAccordingToComparator(info, actual, byteDescendingOrderComparator);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeSorted.shouldBeSortedAccordingToGivenComparator(2, actual, byteDescendingOrderComparator));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


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
package org.assertj.core.internal.comparables;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBeEqual;
import org.assertj.core.internal.ComparablesBaseTest;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Comparables#assertNotEqualByComparison(AssertionInfo, Comparable, Comparable)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Comparables_assertNotEqualByComparison_Test extends ComparablesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> comparables.assertNotEqualByComparison(someInfo(), null, 8)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_objects_are_not_equal() {
        Person a = Mockito.spy(new Person("Han"));
        Person o = new Person("Yoda");
        comparables.assertNotEqualByComparison(TestData.someInfo(), a, o);
        Mockito.verify(a).compareTo(o);
    }

    @Test
    public void should_fail_if_objects_are_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparables.assertNotEqualByComparison(info, "Yoda", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual("Yoda", "Yoda"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> comparablesWithCustomComparisonStrategy.assertNotEqualByComparison(someInfo(), null, new Person("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_objects_are_not_equal_whatever_custom_comparison_strategy_is() {
        Person actual = Mockito.spy(new Person("YODA"));
        Person other = new Person("Yoda");
        comparablesWithCustomComparisonStrategy.assertNotEqualByComparison(TestData.someInfo(), actual, other);
        Mockito.verify(actual).compareTo(other);
    }

    @Test
    public void should_fail_if_objects_are_equal_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparablesWithCustomComparisonStrategy.assertNotEqualByComparison(info, new Person("Yoda"), new Person("Yoda"));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeEqual.shouldNotBeEqual(new Person("Yoda"), new Person("Yoda")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


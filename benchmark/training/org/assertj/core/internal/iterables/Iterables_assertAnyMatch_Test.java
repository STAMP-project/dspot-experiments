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


import PredicateDescription.GIVEN;
import java.util.List;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.AnyElementShouldMatch;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.presentation.PredicateDescription;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Iterables_assertAnyMatch_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_an_element_satisfies_predicate() {
        List<String> actual = Lists.newArrayList("123", "1234", "12345");
        iterables.assertAnyMatch(TestData.someInfo(), actual, ( s) -> (s.length()) >= 5, GIVEN);
    }

    @Test
    public void should_fail_if_predicate_is_not_met_by_any_elements() {
        List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda");
        Predicate<String> startsWithM = ( s) -> s.startsWith("M");
        try {
            iterables.assertAnyMatch(info, actual, startsWithM, GIVEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, AnyElementShouldMatch.anyElementShouldMatch(actual, GIVEN));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_with_custom_description_if_predicate_is_met_by_no_element() {
        List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda");
        Predicate<String> startsWithM = ( s) -> s.startsWith("M");
        try {
            iterables.assertAnyMatch(info, actual, startsWithM, new PredicateDescription("custom"));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, AnyElementShouldMatch.anyElementShouldMatch(actual, new PredicateDescription("custom")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            actual = null;
            iterables.assertAnyMatch(someInfo(), actual, String::isEmpty, PredicateDescription.GIVEN);
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_predicate_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertAnyMatch(someInfo(), actual, null, PredicateDescription.GIVEN)).withMessage("The predicate to evaluate should not be null");
    }
}


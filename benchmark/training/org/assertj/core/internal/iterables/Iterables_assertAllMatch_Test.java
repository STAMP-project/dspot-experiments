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
import org.assertj.core.error.ElementsShouldMatch;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.presentation.PredicateDescription;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Iterables_assertAllMatch_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_each_element_satisfies_predicate() {
        List<String> actual = Lists.newArrayList("123", "1234", "12345");
        iterables.assertAllMatch(TestData.someInfo(), actual, ( s) -> (s.length()) >= 3, GIVEN);
    }

    @Test
    public void should_throw_error_if_predicate_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertAllMatch(someInfo(), actual, null, PredicateDescription.GIVEN)).withMessage("The predicate to evaluate should not be null");
    }

    @Test
    public void should_fail_if_predicate_is_not_met() {
        List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda");
        Predicate<? super String> predicate = ( s) -> s.startsWith("L");
        try {
            iterables.assertAllMatch(info, actual, predicate, GIVEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ElementsShouldMatch.elementsShouldMatch(actual, "Yoda", GIVEN));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_with_custom_description_if_predicate_is_not_met() {
        List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda");
        Predicate<? super String> predicate = ( s) -> s.startsWith("L");
        try {
            iterables.assertAllMatch(info, actual, predicate, new PredicateDescription("custom"));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ElementsShouldMatch.elementsShouldMatch(actual, "Yoda", new PredicateDescription("custom")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_report_all_items_that_do_not_match() {
        List<String> actual = Lists.newArrayList("123", "1234", "12345");
        try {
            iterables.assertAllMatch(TestData.someInfo(), actual, ( s) -> (s.length()) <= 3, GIVEN);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ElementsShouldMatch.elementsShouldMatch(actual, Lists.newArrayList("1234", "12345"), GIVEN));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}


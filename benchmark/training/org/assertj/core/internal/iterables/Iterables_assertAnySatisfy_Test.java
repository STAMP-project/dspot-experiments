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


import ElementsShouldSatisfy.UnsatisfiedRequirement;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldSatisfy;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class Iterables_assertAnySatisfy_Test extends IterablesBaseTest {
    private List<String> actual = Lists.newArrayList("Luke", "Leia", "Yoda", "Obiwan");

    @Test
    public void must_not_check_all_elements() {
        // GIVEN
        Consumer<String> consumer = Mockito.mock(Consumer.class);
        // first element does not match -> assertion error, 2nd element does match -> doNothing()
        Mockito.doThrow(new AssertionError("some error message")).doNothing().when(consumer).accept(ArgumentMatchers.anyString());
        // WHEN
        iterables.assertAnySatisfy(TestData.someInfo(), actual, consumer);
        // THEN
        // make sure that we only evaluated 2 out of 4 elements
        Mockito.verify(consumer, Mockito.times(2)).accept(ArgumentMatchers.anyString());
    }

    @Test
    public void should_pass_when_one_element_satisfies_the_single_assertion_requirement() {
        iterables.<String>assertAnySatisfy(TestData.someInfo(), actual, ( s) -> assertThat(s).hasSize(6));
    }

    @Test
    public void should_pass_when_one_element_satisfies_all_the_assertion_requirements() {
        iterables.<String>assertAnySatisfy(TestData.someInfo(), actual, ( s) -> {
            assertThat(s).hasSize(4);
            assertThat(s).doesNotContain("L");
        });
    }

    @Test
    public void should_pass_when_several_elements_satisfy_all_the_assertion_requirements() {
        iterables.<String>assertAnySatisfy(TestData.someInfo(), actual, ( s) -> {
            assertThat(s).hasSize(4);
            assertThat(s).contains("L");
        });
    }

    @Test
    public void should_fail_if_no_elements_satisfy_the_assertions_requirements() {
        try {
            iterables.<String>assertAnySatisfy(TestData.someInfo(), actual, ( s) -> {
                assertThat(s).hasSize(4);
                assertThat(s).contains("W");
            });
        } catch (AssertionError e) {
            List<ElementsShouldSatisfy.UnsatisfiedRequirement> errors = Lists.list(ElementsShouldSatisfy.unsatisfiedRequirement("Luke", String.format(("%n" + ((("Expecting:%n" + " <\"Luke\">%n") + "to contain:%n") + " <\"W\"> ")))), ElementsShouldSatisfy.unsatisfiedRequirement("Leia", String.format(("%n" + ((("Expecting:%n" + " <\"Leia\">%n") + "to contain:%n") + " <\"W\"> ")))), ElementsShouldSatisfy.unsatisfiedRequirement("Yoda", String.format(("%n" + ((("Expecting:%n" + " <\"Yoda\">%n") + "to contain:%n") + " <\"W\"> ")))), ElementsShouldSatisfy.unsatisfiedRequirement("Obiwan", String.format(("%n" + ("Expected size:<4> but was:<6> in:%n" + "<\"Obiwan\">")))));
            Mockito.verify(failures).failure(info, ElementsShouldSatisfy.elementsShouldSatisfyAny(actual, errors, TestData.someInfo()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_the_iterable_under_test_is_empty_whatever_the_assertions_requirements_are() {
        actual.clear();
        try {
            iterables.<String>assertAnySatisfy(TestData.someInfo(), actual, ( $) -> assertThat(true).isTrue());
        } catch (AssertionError e) {
            List<ElementsShouldSatisfy.UnsatisfiedRequirement> errors = Lists.emptyList();
            Mockito.verify(failures).failure(info, ElementsShouldSatisfy.elementsShouldSatisfyAny(actual, errors, TestData.someInfo()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_consumer_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(actual).anySatisfy(null)).withMessage("The Consumer<T> expressing the assertions requirements must not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // WHEN
        AssertionError error = AssertionsUtil.expectAssertionError(() -> iterables.assertAnySatisfy(someInfo(), null, ( $) -> {
        }));
        // THEN
        Assertions.assertThat(error).hasMessage(FailureMessages.actualIsNull());
    }
}


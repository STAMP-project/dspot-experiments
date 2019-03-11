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
package org.assertj.core.api.predicate;


import PredicateDescription.GIVEN;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.PredicateAssertBaseTest;
import org.assertj.core.error.ElementsShouldMatch;
import org.assertj.core.error.ShouldAccept;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class PredicateAssert_accepts_Test extends PredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Predicate<String>) (null))).accepts("first", "second")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_values() {
        java.util.function.Predicate<String> ballSportPredicate = ( sport) -> sport.contains("ball");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ballSportPredicate).accepts("football", "basketball", "curling")).withMessage(ElementsShouldMatch.elementsShouldMatch(Lists.newArrayList("football", "basketball", "curling"), "curling", GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_all_values() {
        java.util.function.Predicate<String> ballSportPredicate = ( sport) -> sport.contains("ball");
        Assertions.assertThat(ballSportPredicate).accepts("football", "basketball", "handball");
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_value() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        String expectedValue = "something else";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).accepts(expectedValue)).withMessage(ShouldAccept.shouldAccept(predicate, expectedValue, GIVEN).create());
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_value_with_string_description() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        String expectedValue = "something else";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).as("test").accepts(expectedValue)).withMessage(("[test] " + (ShouldAccept.shouldAccept(predicate, expectedValue, GIVEN).create())));
    }

    @Test
    public void should_pass_when_predicate_accepts_value() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        Assertions.assertThat(predicate).accepts("something");
    }

    @Test
    public void should_pass_and_only_invoke_predicate_once_for_single_value() {
        // GIVEN
        java.util.function.Predicate<Object> predicate = Mockito.mock(java.util.function.Predicate.class);
        Mockito.when(predicate.test(ArgumentMatchers.any())).thenReturn(true);
        // WHEN
        Assertions.assertThat(predicate).accepts("something");
        // THEN
        Mockito.verify(predicate, Mockito.times(1)).test("something");
    }
}


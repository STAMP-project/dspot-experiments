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
import org.assertj.core.error.NoElementsShouldMatch;
import org.assertj.core.error.ShouldNotAccept;
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
public class PredicateAssert_rejects_Test extends PredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((Predicate<String>) (null))).rejects("first", "second")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_when_predicate_does_not_accept_value() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        Assertions.assertThat(predicate).rejects("something else");
    }

    @Test
    public void should_fail_when_predicate_accepts_value_with_no_description() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        String expectedValue = "something";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).rejects("something")).withMessage(ShouldNotAccept.shouldNotAccept(predicate, expectedValue, GIVEN).create());
    }

    @Test
    public void should_fail_when_predicate_accepts_value_with_given_string_description() {
        java.util.function.Predicate<String> predicate = ( val) -> val.equals("something");
        String expectedValue = "something";
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).as("test").rejects("something")).withMessage(("[test] " + (ShouldNotAccept.shouldNotAccept(predicate, expectedValue, GIVEN).create())));
    }

    @Test
    public void should_fail_when_predicate_accepts_some_value() {
        java.util.function.Predicate<String> ballSportPredicate = ( sport) -> sport.contains("ball");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(ballSportPredicate).rejects("curling", "judo", "football")).withMessage(NoElementsShouldMatch.noElementsShouldMatch(Lists.newArrayList("curling", "judo", "football"), "football", GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_no_value() {
        java.util.function.Predicate<String> ballSportPredicate = ( sport) -> sport.contains("ball");
        Assertions.assertThat(ballSportPredicate).rejects("curling", "judo", "marathon");
    }

    @Test
    public void should_pass_and_only_invoke_predicate_once_for_single_value() {
        // GIVEN
        java.util.function.Predicate<Object> predicate = Mockito.mock(java.util.function.Predicate.class);
        Mockito.when(predicate.test(ArgumentMatchers.any())).thenReturn(false);
        // WHEN
        Assertions.assertThat(predicate).rejects("something");
        // THEN
        Mockito.verify(predicate, Mockito.times(1)).test("something");
    }
}


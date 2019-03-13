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
package org.assertj.core.api.intpredicate;


import PredicateDescription.GIVEN;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IntPredicateAssertBaseTest;
import org.assertj.core.error.NoElementsShouldMatch;
import org.assertj.core.error.ShouldNotAccept;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class IntPredicateAssert_rejects_Test extends IntPredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((IntPredicate) (null))).rejects(3, 4, 5)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_when_predicate_does_not_accept_value() {
        IntPredicate predicate = ( val) -> val <= 2;
        Assertions.assertThat(predicate).rejects(3);
    }

    @Test
    public void should_fail_when_predicate_accepts_value() {
        IntPredicate predicate = ( val) -> val <= 2;
        Predicate<Integer> wrapPredicate = predicate::test;
        int expectedValue = 2;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).rejects(expectedValue)).withMessage(ShouldNotAccept.shouldNotAccept(wrapPredicate, expectedValue, GIVEN).create());
    }

    @Test
    public void should_fail_when_predicate_accepts_value_with_string_description() {
        IntPredicate predicate = ( val) -> val <= 2;
        Predicate<Integer> wrapPredicate = predicate::test;
        int expectedValue = 2;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).as("test").rejects(expectedValue)).withMessage(("[test] " + (ShouldNotAccept.shouldNotAccept(wrapPredicate, expectedValue, GIVEN).create())));
    }

    @Test
    public void should_fail_when_predicate_accepts_some_value() {
        IntPredicate predicate = ( num) -> num <= 2;
        int[] matchValues = new int[]{ 1, 2, 3 };
        List<Integer> matchValuesList = IntStream.of(matchValues).boxed().collect(Collectors.toList());
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).rejects(matchValues)).withMessage(NoElementsShouldMatch.noElementsShouldMatch(matchValuesList, 1, GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_no_value() {
        IntPredicate predicate = ( num) -> num <= 2;
        Assertions.assertThat(predicate).rejects(3, 4, 5);
    }
}


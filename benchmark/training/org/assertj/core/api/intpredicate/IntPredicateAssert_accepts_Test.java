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
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IntPredicateAssertBaseTest;
import org.assertj.core.error.ElementsShouldMatch;
import org.assertj.core.error.ShouldAccept;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class IntPredicateAssert_accepts_Test extends IntPredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((IntPredicate) (null))).accepts(1, 2, 3)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_value() {
        IntPredicate predicate = ( val) -> val <= 2;
        Predicate<Integer> wrapPredicate = predicate::test;
        int expectedValue = 3;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).accepts(expectedValue)).withMessage(ShouldAccept.shouldAccept(wrapPredicate, expectedValue, GIVEN).create());
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_value_with_string_description() {
        IntPredicate predicate = ( val) -> val <= 2;
        Predicate<Integer> wrapPredicate = predicate::test;
        int expectedValue = 3;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).as("test").accepts(expectedValue)).withMessage(("[test] " + (ShouldAccept.shouldAccept(wrapPredicate, expectedValue, GIVEN).create())));
    }

    @Test
    public void should_pass_when_predicate_accepts_value() {
        IntPredicate predicate = ( val) -> val <= 2;
        Assertions.assertThat(predicate).accepts(1);
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_values() {
        IntPredicate predicate = ( val) -> val <= 2;
        int[] matchValues = new int[]{ 1, 2, 3 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).accepts(matchValues)).withMessage(ElementsShouldMatch.elementsShouldMatch(matchValues, 3, GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_all_values() {
        IntPredicate predicate = ( val) -> val <= 2;
        Assertions.assertThat(predicate).accepts(1, 2);
    }
}


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
package org.assertj.core.api.longpredicate;


import PredicateDescription.GIVEN;
import java.util.List;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.LongPredicateAssertBaseTest;
import org.assertj.core.description.TextDescription;
import org.assertj.core.error.NoElementsShouldMatch;
import org.assertj.core.error.ShouldNotAccept;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class LongPredicateAssert_rejects_Test extends LongPredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((LongPredicate) (null))).rejects(1L, 2L, 3L)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_when_predicate_does_not_accept_value() {
        LongPredicate predicate = ( val) -> val <= 2;
        Assertions.assertThat(predicate).rejects(3);
    }

    @Test
    public void should_fail_when_predicate_accepts_value() {
        LongPredicate predicate = ( val) -> val <= 2;
        Predicate<Long> wrapPredicate = predicate::test;
        long expectedValue = 2;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).rejects(expectedValue)).withMessage(ShouldNotAccept.shouldNotAccept(wrapPredicate, expectedValue, GIVEN).create());
    }

    @Test
    public void should_fail_when_predicate_accepts_value_with_description() {
        LongPredicate predicate = ( val) -> val <= 2;
        Predicate<Long> wrapPredicate = predicate::test;
        long expectedValue = 2;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).as(new TextDescription("test")).rejects(expectedValue)).withMessage(("[test] " + (ShouldNotAccept.shouldNotAccept(wrapPredicate, expectedValue, GIVEN).create())));
    }

    @Test
    public void should_fail_when_predicate_accepts_some_value() {
        LongPredicate predicate = ( num) -> num <= 2;
        long[] matchValues = new long[]{ 1L, 2L, 3L };
        List<Long> matchValuesList = LongStream.of(matchValues).boxed().collect(Collectors.toList());
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).rejects(matchValues)).withMessage(NoElementsShouldMatch.noElementsShouldMatch(matchValuesList, 1L, GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_no_value() {
        LongPredicate predicate = ( num) -> num <= 2;
        Assertions.assertThat(predicate).rejects(3L, 4L, 5L);
    }
}


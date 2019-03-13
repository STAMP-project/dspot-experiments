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


import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.NoElementsShouldSatisfy;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Iterables_assertNoneSatisfy_Test extends IterablesBaseTest {
    private List<String> actual = Lists.list("Luke", "Leia", "Yoda");

    @Test
    public void should_pass_when_no_elements_satisfy_the_given_single_restriction() {
        // GIVEN
        Consumer<String> restriction = ( name) -> Assertions.assertThat(name).hasSize(5);
        // THEN
        iterables.assertNoneSatisfy(TestData.someInfo(), actual, restriction);
    }

    @Test
    public void should_pass_when_no_elements_satisfy_the_given_restrictions() {
        // GIVEN
        Consumer<String> restrictions = ( name) -> {
            Assertions.assertThat(name).hasSize(5);
            Assertions.assertThat(name).contains("V");
        };
        // THEN
        iterables.assertNoneSatisfy(TestData.someInfo(), actual, restrictions);
    }

    @Test
    public void should_pass_for_empty_whatever_the_given_restrictions_are() {
        // GIVEN
        Consumer<String> restriction = ( name) -> Assertions.assertThat(name).hasSize(5);
        actual.clear();
        // THEN
        iterables.assertNoneSatisfy(TestData.someInfo(), actual, restriction);
    }

    @Test
    public void should_fail_when_one_elements_satisfy_the_given_restrictions() {
        // GIVEN
        Consumer<String> restrictions = ( name) -> Assertions.assertThat(name).startsWith("Y");
        // WHEN
        Throwable assertionError = AssertionsUtil.expectAssertionError(() -> iterables.assertNoneSatisfy(someInfo(), actual, restrictions));
        // THEN
        Mockito.verify(failures).failure(info, NoElementsShouldSatisfy.noElementsShouldSatisfy(actual, Lists.list("Yoda")));
        Assertions.assertThat(assertionError).isNotNull();
    }

    @Test
    public void should_fail_when_two_elements_satisfy_the_given_restrictions() {
        // GIVEN
        Consumer<String> restrictions = ( name) -> Assertions.assertThat(name).startsWith("L");
        // WHEN
        AssertionsUtil.expectAssertionError(() -> iterables.assertNoneSatisfy(someInfo(), actual, restrictions));
        // THEN
        Mockito.verify(failures).failure(info, NoElementsShouldSatisfy.noElementsShouldSatisfy(actual, Lists.list("Luke", "Leia")));
    }

    @Test
    public void should_throw_error_if_consumer_restrictions_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertNoneSatisfy(someInfo(), actual, null)).withMessage("The Consumer<T> expressing the restrictions must not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            List<String> nullActual = null;
            iterables.assertNoneSatisfy(someInfo(), nullActual, ( name) -> assertThat(name).startsWith("Y"));
        }).withMessage(FailureMessages.actualIsNull());
    }
}


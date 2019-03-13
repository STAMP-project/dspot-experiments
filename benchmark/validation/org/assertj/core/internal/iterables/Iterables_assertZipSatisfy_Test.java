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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.error.ShouldStartWith;
import org.assertj.core.error.ZippedElementsShouldSatisfy;
import org.assertj.core.error.ZippedElementsShouldSatisfy.ZipSatisfyError;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Iterables_assertZipSatisfy_Test extends IterablesBaseTest {
    private List<String> other = Lists.newArrayList("LUKE", "YODA", "LEIA");

    @Test
    public void should_satisfy_single_zip_requirement() {
        iterables.assertZipSatisfy(TestData.someInfo(), actual, other, ( s1, s2) -> assertThat(s1).isEqualToIgnoringCase(s2));
    }

    @Test
    public void should_satisfy_compound_zip_requirements() {
        iterables.assertZipSatisfy(TestData.someInfo(), actual, other, ( s1, s2) -> {
            assertThat(s1).isEqualToIgnoringCase(s2);
            assertThat(s1).startsWith(firstChar(s2));
        });
    }

    @Test
    public void should_pass_if_both_iterables_are_empty() {
        actual.clear();
        other.clear();
        iterables.assertZipSatisfy(TestData.someInfo(), actual, other, ( s1, s2) -> assertThat(s1).isEqualToIgnoringCase(s2));
    }

    @Test
    public void should_fail_according_to_requirements() {
        // GIVEN
        ThrowingCallable assertion = () -> iterables.assertZipSatisfy(someInfo(), actual, other, ( s1, s2) -> assertThat(s1).startsWith(s2));
        // WHEN
        AssertionError assertionError = Assertions.catchThrowableOfType(assertion, AssertionError.class);
        // THEN
        Assertions.assertThat(assertionError).isNotNull();
        List<ZipSatisfyError> errors = Lists.list(new ZipSatisfyError("Luke", "LUKE", ShouldStartWith.shouldStartWith("Luke", "LUKE").create()), new ZipSatisfyError("Yoda", "YODA", ShouldStartWith.shouldStartWith("Yoda", "YODA").create()), new ZipSatisfyError("Leia", "LEIA", ShouldStartWith.shouldStartWith("Leia", "LEIA").create()));
        Mockito.verify(failures).failure(info, ZippedElementsShouldSatisfy.zippedElementsShouldSatisfy(info, actual, other, errors));
    }

    @Test
    public void should_fail_when_compared_iterables_have_different_sizes() {
        other.add("Vader");
        try {
            iterables.assertZipSatisfy(TestData.someInfo(), actual, other, ( s1, s2) -> assertThat(s1).startsWith(s2));
        } catch (AssertionError e) {
            Assertions.assertThat(e).hasMessageContaining(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, actual.size(), other.size()).create());
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_consumer_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> assertThat(actual).zipSatisfy(other, null)).withMessage("The BiConsumer expressing the assertions requirements must not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            actual = null;
            iterables.assertZipSatisfy(someInfo(), actual, other, ( s1, s2) -> assertThat(s1).isEqualToIgnoringCase(s2));
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            other = null;
            iterables.assertZipSatisfy(someInfo(), actual, other, ( s1, s2) -> assertThat(s1).isEqualToIgnoringCase(s2));
        }).withMessage("The iterable to zip actual with must not be null");
    }
}


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
package org.assertj.core.internal.throwables;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveMessageMatchingRegex;
import org.assertj.core.internal.ThrowablesBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ThrowableAssert#hasMessageMatching(String)}</code>.
 *
 * @author Libor Ondrusek
 */
public class Throwables_assertHasMessageMatching_Test extends ThrowablesBaseTest {
    public static final String REGEX = "Given id=\'\\d{2,4}\' not exists";

    @Test
    public void should_pass_if_throwable_message_matches_given_regex() {
        ThrowablesBaseTest.actual = new RuntimeException("Given id='259' not exists");
        throwables.assertHasMessageMatching(TestData.someInfo(), ThrowablesBaseTest.actual, Throwables_assertHasMessageMatching_Test.REGEX);
    }

    @Test
    public void should_pass_if_throwable_message_is_empty_and_regex_is_too() {
        ThrowablesBaseTest.actual = new RuntimeException("");
        throwables.assertHasMessageMatching(TestData.someInfo(), ThrowablesBaseTest.actual, "");
    }

    @Test
    public void should_fail_if_throwable_message_does_not_match_given_regex() {
        AssertionInfo info = TestData.someInfo();
        try {
            throwables.assertHasMessageMatching(info, ThrowablesBaseTest.actual, Throwables_assertHasMessageMatching_Test.REGEX);
            Assertions.fail("AssertionError expected");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveMessageMatchingRegex.shouldHaveMessageMatchingRegex(ThrowablesBaseTest.actual, Throwables_assertHasMessageMatching_Test.REGEX));
        }
    }

    @Test
    public void should_fail_if_given_regex_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> throwables.assertHasMessageMatching(someInfo(), ThrowablesBaseTest.actual, null)).withMessage("regex must not be null");
    }

    @Test
    public void should_fail_if_throwable_does_not_have_a_message() {
        ThrowablesBaseTest.actual = new RuntimeException();
        AssertionInfo info = TestData.someInfo();
        try {
            throwables.assertHasMessageMatching(info, ThrowablesBaseTest.actual, Throwables_assertHasMessageMatching_Test.REGEX);
            Assertions.fail("AssertionError expected");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveMessageMatchingRegex.shouldHaveMessageMatchingRegex(ThrowablesBaseTest.actual, Throwables_assertHasMessageMatching_Test.REGEX));
        }
    }
}


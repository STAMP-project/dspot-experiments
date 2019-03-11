/**
 * Copyright (c) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truth;


import java.util.OptionalDouble;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.util.OptionalDouble.empty;
import static java.util.OptionalDouble.of;


/**
 * Tests for Java 8 {@link OptionalDouble} Subjects.
 *
 * @author Ben Douglass
 */
@RunWith(JUnit4.class)
public class OptionalDoubleSubjectTest {
    @Test
    public void failOnNullSubject() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(null).isEmpty());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected empty optional", "but was").inOrder();
    }

    @Test
    public void isPresent() {
        ExpectFailure.assertThat(of(1337.0)).isPresent();
    }

    @Test
    public void isPresentFailing() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.empty()).isPresent());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected to be present");
    }

    @Test
    public void isPresentFailingWithNamed() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.empty()).named("name").isPresent());
        ExpectFailure.assertThat(expected).factKeys().contains("name");
    }

    @Test
    public void isEmpty() {
        ExpectFailure.assertThat(empty()).isEmpty();
    }

    @Test
    public void isEmptyFailing() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.of(1337.0)).isEmpty());
        ExpectFailure.assertThat(expected).factKeys().contains("expected to be empty");
        ExpectFailure.assertThat(expected).factValue("but was present with value").isEqualTo("1337.0");
    }

    @Test
    public void isEmptyFailingNull() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(null).isEmpty());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected empty optional", "but was").inOrder();
    }

    @Test
    public void hasValue() {
        ExpectFailure.assertThat(of(1337.0)).hasValue(1337.0);
    }

    @Test
    public void hasValue_FailingWithEmpty() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.empty()).hasValue(1337.0));
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected to have value", "but was absent").inOrder();
        ExpectFailure.assertThat(expected).factValue("expected to have value").isEqualTo("1337.0");
    }

    @Test
    public void hasValue_FailingWithWrongValue() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.of(1337.0)).hasValue(42.0));
        ExpectFailure.assertThat(expected).factValue("value of").isEqualTo("optionalDouble.getAsDouble()");
    }

    @Test
    public void hasValueThat_FailingWithEmpty() {
        AssertionError expected = expectFailure(( whenTesting) -> {
            DoubleSubject unused = whenTesting.that(OptionalDouble.empty()).hasValueThat();
        });
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected to be present");
    }

    @Test
    public void hasValueThat_FailingWithComparison() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(OptionalDouble.of(1337.0)).hasValueThat().isLessThan(42.0));
        // TODO(cpovirk): Assert that "value of" is present once we set it:
        // assertThat(expected).fieldValue("value of").isEqualTo("optionalDouble.getAsDouble()");
    }

    @Test
    public void hasValueThat_SuccessWithComparison() {
        ExpectFailure.assertThat(of(1337.0)).hasValueThat().isGreaterThan(42.0);
    }
}


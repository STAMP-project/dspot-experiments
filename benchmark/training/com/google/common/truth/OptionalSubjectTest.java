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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.util.Optional.empty;
import static java.util.Optional.of;


/**
 * Tests for Java 8 {@link Optional} Subject.
 *
 * @author Christian Gruber (cgruber@israfil.net)
 */
@RunWith(JUnit4.class)
public class OptionalSubjectTest {
    @Test
    public void isPresent() {
        ExpectFailure.assertThat(of("foo")).isPresent();
    }

    @Test
    public void isPresentFailing() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.empty()).isPresent());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected to be present");
    }

    @Test
    public void isPresentFailing_named() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.empty()).named("name").isPresent());
        ExpectFailure.assertThat(expected).factKeys().contains("name");
    }

    @Test
    public void isPresentFailingNull() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(null).isPresent());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected present optional", "but was").inOrder();
    }

    @Test
    public void isEmpty() {
        ExpectFailure.assertThat(empty()).isEmpty();
    }

    @Test
    public void isEmptyFailing() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.of("foo")).isEmpty());
        ExpectFailure.assertThat(expected).factKeys().contains("expected to be empty");
        ExpectFailure.assertThat(expected).factValue("but was present with value").isEqualTo("foo");
    }

    @Test
    public void isEmptyFailingNull() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(null).isEmpty());
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected empty optional", "but was").inOrder();
    }

    @Test
    public void hasValue() {
        ExpectFailure.assertThat(of("foo")).hasValue("foo");
    }

    @Test
    public void hasValue_failingWithEmpty() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.empty()).hasValue("foo"));
        ExpectFailure.assertThat(expected).factKeys().containsExactly("expected to have value", "but was empty").inOrder();
        ExpectFailure.assertThat(expected).factValue("expected to have value").isEqualTo("foo");
    }

    @Test
    public void hasValue_npeWithNullParameter() {
        try {
            ExpectFailure.assertThat(of("foo")).hasValue(null);
            Assert.fail("Expected NPE");
        } catch (NullPointerException expected) {
            ExpectFailure.assertThat(expected).hasMessageThat().isEqualTo("Optional cannot have a null value.");
        }
    }

    @Test
    public void hasValue_failingWithWrongValue() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.of("foo")).hasValue("boo"));
        ExpectFailure.assertThat(expected).factValue("value of").isEqualTo("optional.get()");
    }

    @Test
    public void hasValue_failingWithWrongValue_named() {
        AssertionError expected = expectFailure(( whenTesting) -> whenTesting.that(java.util.Optional.of("foo")).named("bar").hasValue("boo"));
        ExpectFailure.assertThat(expected).factValue("value of").isEqualTo("bar.get()");
    }
}


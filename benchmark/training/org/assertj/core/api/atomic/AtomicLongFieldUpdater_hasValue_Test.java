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
package org.assertj.core.api.atomic;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveValue;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.atomic.AtomicLongFieldUpdater.newUpdater;


public class AtomicLongFieldUpdater_hasValue_Test {
    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        volatile long age;
    }

    private AtomicLongFieldUpdater_hasValue_Test.Person person = new AtomicLongFieldUpdater_hasValue_Test.Person();

    @Test
    public void should_fail_when_atomicLongFieldUpdater_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((AtomicLongFieldUpdater<org.assertj.core.api.atomic.Person>) (null))).hasValue(25L, person)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null_and_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicLongFieldUpdater<AtomicLongFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicLongFieldUpdater_hasValue_Test.Person.class, "age");
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(fieldUpdater).hasValue(null, person)).withMessage("The expected value should not be <null>.");
    }

    @Test
    public void should_fail_if_atomicLongFieldUpdater_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicLongFieldUpdater<AtomicLongFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicLongFieldUpdater_hasValue_Test.Person.class, "age");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(fieldUpdater).hasValue(25L, person)).withMessage(ShouldHaveValue.shouldHaveValue(fieldUpdater, person.age, 25L, person).create());
    }

    @Test
    public void should_pass_if_atomicLongFieldUpdater_contains_expected_value() {
        java.util.concurrent.atomic.AtomicLongFieldUpdater<AtomicLongFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicLongFieldUpdater_hasValue_Test.Person.class, "age");
        fieldUpdater.set(person, 25);
        Assertions.assertThat(fieldUpdater).hasValue(25L, person);
    }
}


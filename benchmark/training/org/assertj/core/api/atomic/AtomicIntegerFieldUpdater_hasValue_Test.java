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

import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;


public class AtomicIntegerFieldUpdater_hasValue_Test {
    @SuppressWarnings("unused")
    private static class Person {
        private String name;

        volatile int age;
    }

    private AtomicIntegerFieldUpdater_hasValue_Test.Person person = new AtomicIntegerFieldUpdater_hasValue_Test.Person();

    @Test
    public void should_fail_when_atomicIntegerFieldUpdater_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((AtomicIntegerFieldUpdater<org.assertj.core.api.atomic.Person>) (null))).hasValue(25, person)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null_and_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicIntegerFieldUpdater_hasValue_Test.Person.class, "age");
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(fieldUpdater).hasValue(null, person)).withMessage("The expected value should not be <null>.");
    }

    @Test
    public void should_fail_if_atomicIntegerFieldUpdater_does_not_contain_expected_value() {
        java.util.concurrent.atomic.AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicIntegerFieldUpdater_hasValue_Test.Person.class, "age");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(fieldUpdater).hasValue(25, person)).withMessage(ShouldHaveValue.shouldHaveValue(fieldUpdater, person.age, 25, person).create());
    }

    @Test
    public void should_pass_if_atomicIntegerFieldUpdater_contains_expected_value() {
        java.util.concurrent.atomic.AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdater_hasValue_Test.Person> fieldUpdater = newUpdater(AtomicIntegerFieldUpdater_hasValue_Test.Person.class, "age");
        fieldUpdater.set(person, 25);
        Assertions.assertThat(fieldUpdater).hasValue(25, person);
    }
}


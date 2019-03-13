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
package org.assertj.core.internal.classes;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveMethods;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Classes#assertHasDeclaredMethods(AssertionInfo, Class, String...)}</code>
 */
public class Classes_assertHasDeclaredMethods_Test extends ClassesBaseTest {
    @SuppressWarnings("unused")
    private static final class AnotherMethodsClass {
        private String string;

        public void publicMethod() {
        }

        protected void protectedMethod() {
        }

        private void privateMethod() {
        }
    }

    @Test
    public void should_pass_if_actual_has_the_expected_declared_methods() {
        classes.assertHasDeclaredMethods(TestData.someInfo(), actual, "publicMethod", "protectedMethod", "privateMethod");
    }

    @Test
    public void should_pass_if_actual_has_no_declared_methods_and_no_expected_methods_are_given() {
        actual = ClassesBaseTest.Jedi.class;
        classes.assertHasDeclaredMethods(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasDeclaredMethods(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_some_declared_methods_and_no_expected_methods_are_given() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasDeclaredMethods(someInfo(), actual)).withMessage(String.format(ShouldHaveMethods.shouldNotHaveMethods(actual, true, Sets.newTreeSet("publicMethod", "privateMethod", "protectedMethod")).create()));
    }

    @Test
    public void should_fail_if_actual_does_not_have_the_expected_declared_methods() {
        String[] expected = new String[]{ "missingMethod", "publicMethod" };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasDeclaredMethods(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveMethods.shouldHaveMethods(actual, true, Sets.newTreeSet(expected), Sets.newTreeSet("missingMethod")).create()));
    }
}


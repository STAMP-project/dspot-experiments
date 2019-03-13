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
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


public class Classes_assertHasMethods_Test extends ClassesBaseTest {
    private static class AnotherMethodsClass extends ClassesBaseTest.MethodsClass {
        private String string;
    }

    @Test
    public void should_pass_if_actual_has_expected_accessible_public_methods() {
        classes.assertHasMethods(TestData.someInfo(), actual, "publicMethod", "protectedMethod", "privateMethod");
    }

    @Test
    public void should_fail_if_no_methods_are_expected_and_methods_are_available() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasMethods(someInfo(), actual)).withMessage(String.format(ShouldHaveMethods.shouldNotHaveMethods(actual, false, Sets.newTreeSet("publicMethod", "protectedMethod", "privateMethod", "finalize", "wait", "equals", "toString", "hashCode", "getClass", "clone", "registerNatives", "notify", "notifyAll")).create()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasMethods(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_methods_are_inherited() {
        String[] expected = Arrays.array("notify", "notifyAll");
        classes.assertHasMethods(TestData.someInfo(), actual, expected);
    }

    @Test
    public void should_fail_if_expected_methods_are_missing() {
        String[] expected = Arrays.array("missingMethod", "publicMethod");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasMethods(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveMethods.shouldHaveMethods(actual, false, Sets.newTreeSet(expected), Sets.newTreeSet("missingMethod")).create()));
    }
}


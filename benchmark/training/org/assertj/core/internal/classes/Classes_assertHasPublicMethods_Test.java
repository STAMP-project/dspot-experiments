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


import java.lang.reflect.Modifier;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveMethods;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


public class Classes_assertHasPublicMethods_Test extends ClassesBaseTest {
    @Test
    public void should_pass_if_actual_has_the_expected_public_methods() {
        classes.assertHasPublicMethods(TestData.someInfo(), actual, "publicMethod");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicMethods(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_no_methods_are_expected_but_public_methods_are_available() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicMethods(someInfo(), actual)).withMessage(String.format(ShouldHaveMethods.shouldNotHaveMethods(actual, Modifier.toString(Modifier.PUBLIC), false, Sets.newTreeSet("publicMethod", "wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll")).create()));
    }

    @Test
    public void should_fail_if_methods_are_protected_or_private() {
        String[] expected = Arrays.array("publicMethod", "protectedMethod", "privateMethod");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicMethods(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveMethods.shouldHaveMethods(actual, false, Sets.newTreeSet(expected), Sets.newTreeSet("protectedMethod", "privateMethod")).create()));
    }

    @Test
    public void should_fail_if_expected_public_methods_are_missing() {
        String[] expected = Arrays.array("missingMethod", "publicMethod");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicMethods(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveMethods.shouldHaveMethods(actual, false, Sets.newTreeSet(expected), Sets.newTreeSet("missingMethod")).create()));
    }
}


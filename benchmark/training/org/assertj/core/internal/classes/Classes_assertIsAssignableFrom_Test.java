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
import org.assertj.core.error.ShouldBeAssignableFrom;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Classes#assertIsAssignableFrom(org.assertj.core.api.AssertionInfo, Class, Class[])}</code>
 * .
 *
 * @author William Delanoue
 */
public class Classes_assertIsAssignableFrom_Test extends ClassesBaseTest {
    @Test
    public void should_pass_if_actual_is_assignable_from() {
        actual = ClassesBaseTest.Jedi.class;
        classes.assertIsAssignableFrom(TestData.someInfo(), actual, ClassesBaseTest.HumanJedi.class);
    }

    @Test
    public void should_fail_if_given_classes_are_empty() {
        actual = ClassesBaseTest.Jedi.class;
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> classes.assertIsAssignableFrom(someInfo(), actual)).withMessage("Expecting at least one Class to be specified");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertIsAssignableFrom(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_assignable_from_int() {
        actual = int.class;
        classes.assertIsAssignableFrom(TestData.someInfo(), actual, int.class);
    }

    @Test
    public void should_fail_if_actual_is_not_assignable_from() {
        actual = ClassesBaseTest.HumanJedi.class;
        Class<?>[] expected = new Class[]{ ClassesBaseTest.HumanJedi.class, ClassesBaseTest.Jedi.class };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertIsAssignableFrom(someInfo(), actual, expected)).withMessage(String.format(ShouldBeAssignableFrom.shouldBeAssignableFrom(actual, Sets.<Class<?>>newLinkedHashSet(expected), Sets.<Class<?>>newLinkedHashSet(ClassesBaseTest.Jedi.class)).create()));
    }
}


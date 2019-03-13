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
import org.assertj.core.error.ShouldHaveFields;
import org.assertj.core.error.ShouldHaveNoFields;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Classes#assertHasPublicFields(org.assertj.core.api.AssertionInfo, Class, String...)}</code>
 * .
 *
 * @author William Delanoue
 */
public class Classes_assertHasPublicFields_Test extends ClassesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicFields(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_class_has_expected_public_fields() {
        classes.assertHasPublicFields(TestData.someInfo(), actual, "publicField");
        classes.assertHasPublicFields(TestData.someInfo(), actual, "publicField", "publicField2");
    }

    @Test
    public void should_pass_if_class_has_no_public_fields_and_none_are_expected() {
        classes.assertHasPublicFields(TestData.someInfo(), ClassesBaseTest.NoField.class);
    }

    @Test
    public void should_fail_if_expected_fields_are_protected_or_private() {
        String[] expected = Arrays.array("publicField", "protectedField", "privateField");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicFields(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveFields.shouldHaveFields(actual, Sets.newLinkedHashSet(expected), Sets.newLinkedHashSet("protectedField", "privateField")).create()));
    }

    @Test
    public void should_fail_if_actual_does_not_have_all_expected_fields() {
        String[] expected = Arrays.array("missingField", "publicField");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicFields(someInfo(), actual, expected)).withMessage(String.format(ShouldHaveFields.shouldHaveFields(actual, Sets.newLinkedHashSet(expected), Sets.newLinkedHashSet("missingField")).create()));
    }

    @Test
    public void should_fail_if_no_public_fields_are_expected_and_class_has_some() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasPublicFields(someInfo(), actual)).withMessage(ShouldHaveNoFields.shouldHaveNoPublicFields(actual, Sets.newLinkedHashSet("publicField", "publicField2")).create());
    }
}


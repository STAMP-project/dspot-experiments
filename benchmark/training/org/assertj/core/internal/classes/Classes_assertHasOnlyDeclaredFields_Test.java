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


import java.util.LinkedHashSet;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveNoFields;
import org.assertj.core.error.ShouldOnlyHaveFields;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for
 * <code
 * >{@link org.assertj.core.internal.Classes#assertHasOnlyDeclaredFields(org.assertj.core.api.AssertionInfo, Class, String...)}</code>
 * .
 *
 * @author Filip Hrisafov
 */
public class Classes_assertHasOnlyDeclaredFields_Test extends ClassesBaseTest {
    private static final LinkedHashSet<String> EMPTY_STRING_SET = Sets.<String>newLinkedHashSet();

    @Test
    public void should_pass_if_class_has_all_the_expected_declared_fields() {
        classes.assertHasOnlyDeclaredFields(TestData.someInfo(), actual, "publicField", "publicField2", "protectedField", "privateField");
    }

    @Test
    public void should_pass_if_class_has_all_the_expected_declared_fields_whatever_the_order_is() {
        classes.assertHasOnlyDeclaredFields(TestData.someInfo(), actual, "protectedField", "privateField", "publicField2", "publicField");
    }

    @Test
    public void should_pass_if_class_has_no_declared_fields_and_none_are_expected() {
        classes.assertHasOnlyDeclaredFields(TestData.someInfo(), ClassesBaseTest.NoField.class);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasOnlyDeclaredFields(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_not_all_fields_are_expected() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasOnlyDeclaredFields(someInfo(), actual, "publicField", "protectedField", "privateField")).withMessage(String.format(ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(actual, Sets.newLinkedHashSet("publicField", "protectedField", "privateField"), Classes_assertHasOnlyDeclaredFields_Test.EMPTY_STRING_SET, Sets.newLinkedHashSet("publicField2")).create()));
    }

    @Test
    public void should_fail_if_fields_are_missing() {
        String[] expected = Arrays.array("missingField", "publicField", "publicField2", "protectedField", "privateField");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasOnlyDeclaredFields(someInfo(), actual, expected)).withMessage(String.format(ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(actual, Sets.newLinkedHashSet(expected), Sets.newLinkedHashSet("missingField"), Classes_assertHasOnlyDeclaredFields_Test.EMPTY_STRING_SET).create()));
    }

    @Test
    public void should_fail_if_fields_are_not_expected_and_not_found() {
        String[] expected = Arrays.array("publicField", "publicField2", "missing", "privateField");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasOnlyDeclaredFields(someInfo(), actual, expected)).withMessage(String.format(ShouldOnlyHaveFields.shouldOnlyHaveDeclaredFields(actual, Sets.newLinkedHashSet(expected), Sets.newLinkedHashSet("missing"), Sets.newLinkedHashSet("protectedField")).create()));
    }

    @Test
    public void should_fail_if_no_declared_fields_are_expected_and_class_has_some() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertHasOnlyDeclaredFields(someInfo(), actual)).withMessage(String.format(ShouldHaveNoFields.shouldHaveNoDeclaredFields(actual, Sets.newLinkedHashSet("publicField", "publicField2", "protectedField", "privateField")).create()));
    }
}


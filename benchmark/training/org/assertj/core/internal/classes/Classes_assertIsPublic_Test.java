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
import org.assertj.core.error.ClassModifierShouldBe;
import org.assertj.core.internal.ClassesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class Classes_assertIsPublic_Test extends ClassesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertIsPublic(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_a_public_class() {
        classes.assertIsPublic(TestData.someInfo(), Math.class);
    }

    @Test
    public void should_fail_if_actual_is_not_a_public_class() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> classes.assertIsPublic(someInfo(), .class)).withMessage(ClassModifierShouldBe.shouldBePublic(ClassesBaseTest.MethodsClass.class).create());
    }
}


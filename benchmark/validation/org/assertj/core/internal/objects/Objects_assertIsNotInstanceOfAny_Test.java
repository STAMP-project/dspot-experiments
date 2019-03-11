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
package org.assertj.core.internal.objects;


import java.io.File;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBeInstanceOfAny;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertIsNotInstanceOfAny(AssertionInfo, Object, Class[])}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class Objects_assertIsNotInstanceOfAny_Test extends ObjectsBaseTest {
    private static Person actual;

    @Test
    public void should_pass_if_actual_is_not_instance_of_any_type() {
        Class<?>[] types = new Class<?>[]{ String.class, File.class };
        objects.assertIsNotInstanceOfAny(TestData.someInfo(), Objects_assertIsNotInstanceOfAny_Test.actual, types);
    }

    @Test
    public void should_throw_error_if_array_of_types_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> objects.assertIsNotInstanceOfAny(someInfo(), Objects_assertIsNotInstanceOfAny_Test.actual, null)).withMessage("The given array of types should not be null");
    }

    @Test
    public void should_throw_error_if_array_of_types_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> objects.assertIsNotInstanceOfAny(someInfo(), Objects_assertIsNotInstanceOfAny_Test.actual, new Class<?>[0])).withMessage("The given array of types should not be empty");
    }

    @Test
    public void should_throw_error_if_array_of_types_has_null_elements() {
        Class<?>[] types = new Class<?>[]{ null, String.class };
        Assertions.assertThatNullPointerException().isThrownBy(() -> objects.assertIsNotInstanceOfAny(someInfo(), Objects_assertIsNotInstanceOfAny_Test.actual, types)).withMessage("The given array of types:<[null, java.lang.String]> should not have null elements");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Class<?>[] types = new Class<?>[]{ Object.class };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertIsNotInstanceOfAny(someInfo(), null, types)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_instance_of_any_type() {
        AssertionInfo info = TestData.someInfo();
        Class<?>[] types = new Class<?>[]{ String.class, Person.class };
        try {
            objects.assertIsNotInstanceOfAny(info, Objects_assertIsNotInstanceOfAny_Test.actual, types);
            TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
        } catch (AssertionError err) {
        }
        Mockito.verify(failures).failure(info, ShouldNotBeInstanceOfAny.shouldNotBeInstanceOfAny(Objects_assertIsNotInstanceOfAny_Test.actual, types));
    }
}


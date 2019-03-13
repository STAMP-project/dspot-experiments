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
import org.assertj.core.error.ShouldNotBeOfClassIn;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertIsNotOfAnyClassIn(AssertionInfo, Object, Class[])}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class Objects_assertIsNotOfClassIn_Test extends ObjectsBaseTest {
    private static Person actual;

    @Test
    public void should_pass_if_actual_is_not_of_class_in_types() {
        Class<?>[] types = new Class[]{ File.class, String.class };
        objects.assertIsNotOfAnyClassIn(TestData.someInfo(), Objects_assertIsNotOfClassIn_Test.actual, types);
    }

    @Test
    public void should_pass_if_actual_is__of_class_in_empty_types() {
        Class<?>[] types = new Class[]{  };
        objects.assertIsNotOfAnyClassIn(TestData.someInfo(), Objects_assertIsNotOfClassIn_Test.actual, types);
    }

    @Test
    public void should_throw_error_if_type_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> objects.assertIsNotOfAnyClassIn(someInfo(), Objects_assertIsNotOfClassIn_Test.actual, null)).withMessage("The given types should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Class<?>[] types = new Class[]{ File.class, Person.class, String.class };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertIsNotOfAnyClassIn(someInfo(), null, types)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_of_class_in_types() {
        AssertionInfo info = TestData.someInfo();
        Class<?>[] types = new Class[]{ File.class, Person.class, String.class };
        try {
            objects.assertIsNotOfAnyClassIn(info, Objects_assertIsNotOfClassIn_Test.actual, types);
            TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldNotBeOfClassIn.shouldNotBeOfClassIn(Objects_assertIsNotOfClassIn_Test.actual, types));
        }
    }
}


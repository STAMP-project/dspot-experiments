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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotHaveSameClass;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertDoesNotHaveSameClassAs(AssertionInfo, Object, Object)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class Objects_assertDoesNotHaveNotSameClassAs_Test extends ObjectsBaseTest {
    private static Person actual;

    @Test
    public void should_pass_if_actual_does_not_have_not_same_class_as_other() {
        objects.assertDoesNotHaveSameClassAs(TestData.someInfo(), Objects_assertDoesNotHaveNotSameClassAs_Test.actual, "Luke");
    }

    @Test
    public void should_throw_error_if_type_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> objects.assertDoesNotHaveSameClassAs(someInfo(), Objects_assertDoesNotHaveNotSameClassAs_Test.actual, null)).withMessage("The given object should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertDoesNotHaveSameClassAs(someInfo(), null, .class)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_same_type_as_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertDoesNotHaveSameClassAs(info, Objects_assertDoesNotHaveNotSameClassAs_Test.actual, new Person("Luke"));
            TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldNotHaveSameClass.shouldNotHaveSameClass(Objects_assertDoesNotHaveNotSameClassAs_Test.actual, new Person("Luke")));
        }
    }
}


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
import org.assertj.core.error.ShouldBeEqualToIgnoringFields;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.internal.TypeComparators;
import org.assertj.core.test.CartoonCharacter;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Jedi;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestClassWithRandomId;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertIsEqualToIgnoringNullFields(AssertionInfo, Object, Object, Map, TypeComparators)} </code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class Objects_assertIsEqualToIgnoringNullFields_Test extends ObjectsBaseTest {
    @Test
    public void should_pass_when_fields_are_equal() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Green");
        objects.assertIsEqualToIgnoringNullFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_pass_when_some_other_field_is_null_but_not_actual() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", null);
        objects.assertIsEqualToIgnoringNullFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_pass_when_fields_are_equal_even_if_objects_types_differ() {
        Person actual = new Person("Homer Simpson");
        CartoonCharacter other = new CartoonCharacter("Homer Simpson");
        objects.assertIsEqualToIgnoringNullFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_pass_when_private_fields_differ_but_are_not_compared() {
        Assertions.setAllowComparingPrivateFields(false);
        TestClassWithRandomId actual = new TestClassWithRandomId("1", 1);
        TestClassWithRandomId other = new TestClassWithRandomId(null, 1);
        // s field is ignored because null in other, and id also because it is private without public getter
        objects.assertIsEqualToIgnoringNullFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        // reset
        Assertions.setAllowComparingPrivateFields(true);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            Jedi other = new Jedi("Yoda", "Green");
            objects.assertIsEqualToIgnoringNullFields(someInfo(), null, other, noFieldComparators(), defaultTypeComparators());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_some_actual_field_is_null_but_not_other() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", null);
        Jedi other = new Jedi("Yoda", "Green");
        try {
            objects.assertIsEqualToIgnoringNullFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("lightSaberColor"), Lists.newArrayList(((Object) (null))), Lists.newArrayList(((Object) ("Green"))), Lists.newArrayList("strangeNotReadablePrivateField")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_a_field_differ() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Soda", "Green");
        try {
            objects.assertIsEqualToIgnoringNullFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("name"), Lists.newArrayList(((Object) ("Yoda"))), Lists.newArrayList(((Object) ("Soda"))), Lists.newArrayList("strangeNotReadablePrivateField")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_one_of_actual_field_to_compare_can_not_be_found_in_the_other_object() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            Jedi actual = new Jedi("Yoda", "Green");
            Employee other = new Employee();
            objects.assertIsEqualToIgnoringNullFields(someInfo(), actual, other, noFieldComparators(), defaultTypeComparators());
        }).withMessageContaining("Can't find any field or property with name 'lightSaberColor'");
    }

    @Test
    public void should_pass_when_class_has_synthetic_field() {
        Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass.InnerClass actual = new Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass().createInnerClass();
        Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass.InnerClass other = new Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass().createInnerClass();
        // ensure that the compiler has generated at one synthetic field for the comparison
        Assertions.assertThat(Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass.InnerClass.class.getDeclaredFields()).extracting("synthetic").contains(Boolean.TRUE);
        objects.assertIsEqualToIgnoringNullFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    // example taken from
    // http://stackoverflow.com/questions/8540768/when-is-the-jvm-bytecode-access-modifier-flag-0x1000-hex-synthetic-set
    class OuterClass {
        private String outerField;

        class InnerClass {
            // synthetic field this$1 generated in inner class to provider reference to outer
            private InnerClass() {
            }

            String getOuterField() {
                return outerField;
            }
        }

        Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass.InnerClass createInnerClass() {
            return new Objects_assertIsEqualToIgnoringNullFields_Test.OuterClass.InnerClass();
        }
    }
}


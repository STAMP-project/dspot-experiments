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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
 * Tests for <code>{@link Objects#assertIsEqualToIgnoringGivenFields(AssertionInfo, Object, Object, Map, TypeComparators, String...)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 */
public class Objects_assertIsEqualToIgnoringGivenFields_Test extends ObjectsBaseTest {
    @Test
    public void should_pass_when_fields_are_equal() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Green");
        // strangeNotReadablePrivateField fields are compared and are null in both actual and other
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_pass_when_not_ignored_fields_are_equal() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        // strangeNotReadablePrivateField fields are compared and are null in both actual and other
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "lightSaberColor");
    }

    @Test
    public void should_pass_when_not_ignored_inherited_fields_are_equal() {
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Luke", "Green");
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
    }

    @Test
    public void should_pass_when_not_ignored_fields_are_equal_even_if_one_ignored_field_is_not_defined() {
        Person actual = new Person("Yoda");
        Jedi other = new Jedi("Yoda", "Green");
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "lightSaberColor");
    }

    @Test
    public void should_pass_when_field_values_are_null() {
        Jedi actual = new Jedi("Yoda", null);
        Jedi other = new Jedi("Yoda", null);
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
    }

    @Test
    public void should_pass_when_fields_are_equal_even_if_objects_types_differ() {
        CartoonCharacter actual = new CartoonCharacter("Homer Simpson");
        Person other = new Person("Homer Simpson");
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "children");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Jedi other = new Jedi("Yoda", "Green");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertIsEqualToIgnoringGivenFields(someInfo(), null, other, noFieldComparators(), defaultTypeComparators(), "name")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_some_field_values_differ() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        try {
            objects.assertIsEqualToIgnoringGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("lightSaberColor"), Lists.newArrayList(((Object) ("Green"))), Lists.newArrayList(((Object) ("Blue"))), Lists.newArrayList("name")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_some_field_values_differ_and_no_fields_are_ignored() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Yoda", "Blue");
        try {
            objects.assertIsEqualToIgnoringGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("lightSaberColor"), Lists.newArrayList("Green"), Lists.newArrayList("Blue"), new ArrayList()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_some_inherited_field_values_differ() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Luke", "Green");
        try {
            objects.assertIsEqualToIgnoringGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "lightSaberColor");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("name"), Lists.newArrayList("Yoda"), Lists.newArrayList("Luke"), Lists.newArrayList("lightSaberColor")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_one_of_actual_field_to_compare_can_not_be_found_in_the_other_object() {
        Jedi actual = new Jedi("Yoda", "Green");
        Employee other = new Employee();
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            objects.assertIsEqualToIgnoringGivenFields(someInfo(), actual, other, noFieldComparators(), defaultTypeComparators(), "name");
        }).withMessageContaining("Can't find any field or property with name 'lightSaberColor'");
    }

    @Test
    public void should_fail_when_some_field_value_is_null_on_one_object_only() {
        AssertionInfo info = TestData.someInfo();
        Jedi actual = new Jedi("Yoda", null);
        Jedi other = new Jedi("Yoda", "Green");
        try {
            objects.assertIsEqualToIgnoringGivenFields(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "name");
        } catch (AssertionError err) {
            List<Object> expected = Lists.newArrayList(((Object) ("Green")));
            Mockito.verify(failures).failure(info, ShouldBeEqualToIgnoringFields.shouldBeEqualToIgnoringGivenFields(actual, Lists.newArrayList("lightSaberColor"), Lists.newArrayList(((Object) (null))), expected, Lists.newArrayList("name")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_when_private_fields_differ_but_are_not_compared_or_are_ignored() {
        Assertions.setAllowComparingPrivateFields(false);
        TestClassWithRandomId actual = new TestClassWithRandomId("1", 1);
        TestClassWithRandomId other = new TestClassWithRandomId("1", 2);
        // 
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators(), "n");
        // reset
        Assertions.setAllowComparingPrivateFields(true);
    }

    @Test
    public void should_be_able_to_compare_objects_of_different_types() {
        Objects_assertIsEqualToIgnoringGivenFields_Test.Dude person = new Objects_assertIsEqualToIgnoringGivenFields_Test.Dude("John", "Doe");
        Objects_assertIsEqualToIgnoringGivenFields_Test.DudeDAO personDAO = new Objects_assertIsEqualToIgnoringGivenFields_Test.DudeDAO("John", "Doe", 1L);
        Assertions.assertThat(person).isEqualToComparingFieldByField(personDAO);
        Assertions.assertThat(personDAO).isEqualToIgnoringGivenFields(person, "id");
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields() {
        Comparator<String> alwaysEqual = ( s1, s2) -> 0;
        Jedi actual = new Jedi("Yoda", "Green");
        Jedi other = new Jedi("Luke", "Green");
        Assertions.assertThat(actual).usingComparatorForFields(alwaysEqual, "name").isEqualToComparingFieldByField(other);
    }

    @Test
    public void should_pass_when_class_has_synthetic_field() {
        Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass.InnerClass actual = new Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass().createInnerClass();
        Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass.InnerClass other = new Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass().createInnerClass();
        // ensure that the compiler has generated at one synthetic field for the comparison
        Assertions.assertThat(Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass.InnerClass.class.getDeclaredFields()).extracting("synthetic").contains(Boolean.TRUE);
        objects.assertIsEqualToIgnoringGivenFields(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    private static class Dude {
        @SuppressWarnings("unused")
        String firstname;

        @SuppressWarnings("unused")
        String lastname;

        public Dude(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }
    }

    private static class DudeDAO {
        @SuppressWarnings("unused")
        String firstname;

        @SuppressWarnings("unused")
        String lastname;

        @SuppressWarnings("unused")
        Long id;

        public DudeDAO(String firstname, String lastname, Long id) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.id = id;
        }
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

        Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass.InnerClass createInnerClass() {
            return new Objects_assertIsEqualToIgnoringGivenFields_Test.OuterClass.InnerClass();
        }
    }
}


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
package org.assertj.core.api.object;


import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssertBaseTest;
import org.assertj.core.internal.objects.SymmetricDateComparator;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.Jedi;
import org.assertj.core.test.NeverEqualComparator;
import org.assertj.core.test.Patient;
import org.assertj.core.test.Person;
import org.assertj.core.test.PersonCaseInsensitiveNameComparator;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectAssert#isEqualToComparingFieldByField(Object)}</code>.
 *
 * @author Nicolas Fran?ois
 */
public class ObjectAssert_isEqualsToComparingFields_Test extends ObjectAssertBaseTest {
    private Jedi other = new Jedi("Yoda", "Blue");

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(actual).usingComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").isEqualToComparingFieldByField(other);
    }

    @Test
    public void comparators_for_fields_should_have_precedence_over_comparators_for_types() {
        Comparator<String> comparator = ( o1, o2) -> o1.compareTo(o2);
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(actual).usingComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").usingComparatorForType(comparator, String.class).isEqualToComparingFieldByField(other);
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_type() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "blue");
        Assertions.assertThat(actual).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).isEqualToComparingFieldByField(other);
    }

    @Test
    public void should_be_able_to_use_a_type_comparator_for_any_of_the_type_subclasses() {
        ObjectAssert_isEqualsToComparingFields_Test.JediMaster yoda1 = new ObjectAssert_isEqualsToComparingFields_Test.JediMaster("Yoda", new Jedi("luke", "Green"));
        ObjectAssert_isEqualsToComparingFields_Test.JediMaster yoda2 = new ObjectAssert_isEqualsToComparingFields_Test.JediMaster("Yoda", new Jedi("LUKE", null));
        // Jedi is a subclass of Person
        Assertions.assertThat(yoda1).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).isEqualToComparingFieldByField(yoda2);
        Assertions.assertThat(yoda2).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).isEqualToComparingFieldByField(yoda1);
    }

    @Test
    public void should_be_able_to_use_a_date_comparator_for_timestamp() {
        ObjectAssert_isEqualsToComparingFields_Test.JediMaster yoda1 = new ObjectAssert_isEqualsToComparingFields_Test.JediMaster("Yoda", new Jedi("luke", "Green"));
        yoda1.dateOfBirth = new Timestamp(1000L);
        ObjectAssert_isEqualsToComparingFields_Test.JediMaster yoda2 = new ObjectAssert_isEqualsToComparingFields_Test.JediMaster("Yoda", new Jedi("LUKE", null));
        yoda2.dateOfBirth = new Date(1000L);
        // use a date comparator to compare either Date or Timestamp
        Assertions.assertThat(yoda1).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).usingComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Date.class).isEqualToComparingFieldByField(yoda2);
        Assertions.assertThat(yoda2).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).usingComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Date.class).isEqualToComparingFieldByField(yoda1);
        Assertions.assertThat(yoda1).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).usingComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Timestamp.class).isEqualToComparingFieldByField(yoda2);
        Assertions.assertThat(yoda2).usingComparatorForType(new PersonCaseInsensitiveNameComparator(), Person.class).usingComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Timestamp.class).isEqualToComparingFieldByField(yoda1);
    }

    static class JediMaster {
        private Jedi padawan;

        private String name;

        public Date dateOfBirth;

        JediMaster(String name, Jedi padawan) {
            this.name = name;
            this.padawan = padawan;
        }

        public Jedi getPadawan() {
            return padawan;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void should_handle_null_field_with_field_comparator() {
        // GIVEN
        Patient adam = new Patient(null);
        Patient eve = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(adam).usingComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS, "dateOfBirth").isEqualToComparingFieldByField(eve);
    }

    @Test
    public void should_not_bother_with_comparators_when_fields_are_the_same() {
        // GIVEN
        Timestamp dateOfBirth = new Timestamp(3L);
        Patient adam = new Patient(dateOfBirth);
        Patient eve = new Patient(dateOfBirth);
        // THEN
        Assertions.assertThat(adam).usingComparatorForFields(NeverEqualComparator.NEVER_EQUALS, "dateOfBirth").isEqualToComparingFieldByField(eve);
    }
}


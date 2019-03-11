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
package org.assertj.core.util.introspection;


import FieldSupport.EXTRACTION;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link FieldSupport#fieldValues(String, Class, Iterable)}</code>.
 *
 * @author Joel Costigliola
 */
public class FieldSupport_fieldValues_Test {
    private Employee yoda;

    private Employee luke;

    private List<Employee> employees;

    private FieldSupport fieldSupport = FieldSupport.extraction();

    @Test
    public void should_return_empty_List_if_given_Iterable_is_null() {
        Iterable<Long> ids = fieldSupport.fieldValues("ids", Long.class, ((Iterable<Long>) (null)));
        Assertions.assertThat(ids).isEqualTo(Collections.emptyList());
    }

    @Test
    public void should_return_empty_List_if_given_Iterable_is_empty() {
        Iterable<Long> ids = fieldSupport.fieldValues("ids", Long.class, Collections.emptySet());
        Assertions.assertThat(ids).isEqualTo(Collections.emptyList());
    }

    @Test
    public void should_return_null_elements_for_null_field_value() {
        List<Employee> list = Lists.newArrayList(null, null);
        Iterable<Long> ages = fieldSupport.fieldValues("id", Long.class, list);
        Assertions.assertThat(ages).containsExactly(null, null);
        luke.setName(null);
        list = Lists.newArrayList(yoda, luke, null, null);
        Iterable<Name> names = fieldSupport.fieldValues("name", Name.class, list);
        Assertions.assertThat(names).containsExactly(new Name("Yoda"), null, null, null);
        Iterable<String> firstNames = fieldSupport.fieldValues("name.first", String.class, list);
        Assertions.assertThat(firstNames).containsExactly("Yoda", null, null, null);
    }

    @Test
    public void should_return_values_of_simple_field() {
        Iterable<Long> ids = fieldSupport.fieldValues("id", Long.class, employees);
        Assertions.assertThat(ids).isEqualTo(Lists.newArrayList(1L, 2L));
    }

    @Test
    public void should_return_values_of_nested_field() {
        Iterable<String> firstNames = fieldSupport.fieldValues("name.first", String.class, employees);
        Assertions.assertThat(firstNames).isEqualTo(Lists.newArrayList("Yoda", "Luke"));
    }

    @Test
    public void should_throw_error_if_field_not_found() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> fieldSupport.fieldValues("id.", .class, employees)).withMessage("Unable to obtain the value of the field <'id.'> from <Employee[id=1, name=Name[first='Yoda', last='null'], age=800]>");
    }

    @Test
    public void should_return_values_of_private_field() {
        List<Integer> ages = fieldSupport.fieldValues("age", Integer.class, employees);
        Assertions.assertThat(ages).isEqualTo(Lists.newArrayList(800, 26));
    }

    @Test
    public void should_throw_error_if_field_is_not_public_and_allowExtractingPrivateFields_set_to_false() {
        EXTRACTION.setAllowUsingPrivateFields(false);
        try {
            Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> fieldSupport.fieldValues("age", .class, employees)).withMessage("Unable to obtain the value of the field <'age'> from <Employee[id=1, name=Name[first='Yoda', last='null'], age=800]>, check that field is public.");
        } finally {
            // back to default value
            EXTRACTION.setAllowUsingPrivateFields(true);
        }
    }

    @Test
    public void should_extract_field() {
        Long id = fieldSupport.fieldValue("id", Long.class, yoda);
        Assertions.assertThat(id).isEqualTo(1L);
        Object idObject = fieldSupport.fieldValue("id", Object.class, yoda);
        Assertions.assertThat(idObject).isInstanceOf(Long.class).isEqualTo(1L);
    }

    @Test
    public void should_extract_nested_field() {
        String firstName = fieldSupport.fieldValue("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isEqualTo("Yoda");
        yoda.name.first = null;
        firstName = fieldSupport.fieldValue("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isNull();
        yoda.name = null;
        firstName = fieldSupport.fieldValue("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isNull();
    }

    @Test
    public void should_handle_array_as_iterable() {
        List<Long> fieldValuesFromIterable = fieldSupport.fieldValues("id", Long.class, employees);
        List<Long> fieldValuesFromArray = fieldSupport.fieldValues("id", Long.class, employees.toArray(new Employee[0]));
        Assertions.assertThat(fieldValuesFromArray).isEqualTo(fieldValuesFromIterable);
    }

    @Test
    public void should_extract_primitive_field() {
        FieldSupport_fieldValues_Test.SampleObject object = new FieldSupport_fieldValues_Test.SampleObject();
        Assertions.assertThat(fieldSupport.fieldValue("sampleByte", byte.class, object)).isEqualTo(object.sampleByte);
        Assertions.assertThat(fieldSupport.fieldValue("sampleShort", short.class, object)).isEqualTo(object.sampleShort);
        Assertions.assertThat(fieldSupport.fieldValue("sampleInt", int.class, object)).isEqualTo(object.sampleInt);
        Assertions.assertThat(fieldSupport.fieldValue("sampleLong", long.class, object)).isEqualTo(object.sampleLong);
        Assertions.assertThat(fieldSupport.fieldValue("sampleFloat", float.class, object)).isEqualTo(object.sampleFloat);
        Assertions.assertThat(fieldSupport.fieldValue("sampleDouble", double.class, object)).isEqualTo(object.sampleDouble);
        Assertions.assertThat(fieldSupport.fieldValue("sampleBoolean", boolean.class, object)).isEqualTo(object.sampleBoolean);
        Assertions.assertThat(fieldSupport.fieldValue("sampleChar", char.class, object)).isEqualTo(object.sampleChar);
    }

    static class SampleObject {
        final byte sampleByte = 1;

        final short sampleShort = 1;

        final int sampleInt = 1;

        final long sampleLong = 1;

        final float sampleFloat = 1;

        final double sampleDouble = 1;

        final boolean sampleBoolean = true;

        final char sampleChar = 'a';
    }
}


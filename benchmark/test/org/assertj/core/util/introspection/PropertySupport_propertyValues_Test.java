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


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.test.VehicleFactory;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link PropertySupport#propertyValues(String, Collection)}</code>.
 *
 * @author Yvonne Wang
 * @author Nicolas Fran?ois
 * @author Mikhail Mazursky
 * @author Florent Biville
 */
public class PropertySupport_propertyValues_Test {
    private Employee yoda;

    private Employee luke;

    private Iterable<Employee> employees;

    @Test
    public void should_return_empty_List_if_given_Iterable_is_null() {
        Iterable<Integer> ages = PropertySupport.instance().propertyValues("ages", Integer.class, null);
        Assertions.assertThat(ages).isEmpty();
    }

    @Test
    public void should_return_empty_List_if_given_Iterable_is_empty() {
        Iterable<Integer> ages = PropertySupport.instance().propertyValues("ages", Integer.class, Collections.emptySet());
        Assertions.assertThat(ages).isEmpty();
    }

    @Test
    public void should_return_null_elements_for_null_property_value() {
        List<Employee> list = Lists.newArrayList(null, null);
        Iterable<Integer> ages = PropertySupport.instance().propertyValues("ages", Integer.class, list);
        Assertions.assertThat(ages).containsExactly(null, null);
        list = Lists.newArrayList(yoda, luke, null, null);
        ages = PropertySupport.instance().propertyValues("age", Integer.class, list);
        Assertions.assertThat(ages).containsExactly(800, 26, null, null);
    }

    @Test
    public void should_return_values_of_simple_property() {
        Iterable<Integer> ages = PropertySupport.instance().propertyValues("age", Integer.class, employees);
        Assertions.assertThat(ages).containsExactly(800, 26);
    }

    @Test
    public void should_return_values_of_simple_property_as_objects() {
        Iterable<Integer> ages = PropertySupport.instance().propertyValues("age", Integer.class, employees);
        Iterable<Object> agesAsObjects = PropertySupport.instance().propertyValues("age", employees);
        Assertions.assertThat(ages).isEqualTo(agesAsObjects);
        Iterable<String> firstNames = PropertySupport.instance().propertyValues("name.first", String.class, employees);
        Iterable<Object> firstNamesAsObjects = PropertySupport.instance().propertyValues("name.first", employees);
        Assertions.assertThat(firstNames).isEqualTo(firstNamesAsObjects);
    }

    @Test
    public void should_return_values_of_nested_property() {
        Iterable<String> firstNames = PropertySupport.instance().propertyValues("name.first", String.class, employees);
        Assertions.assertThat(firstNames).containsExactly("Yoda", "Luke");
    }

    @Test
    public void should_throw_error_if_property_not_found() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> PropertySupport.instance().propertyValues("foo", .class, employees));
    }

    @Test
    public void should_throw_error_if_property_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> PropertySupport.instance().propertyValueOf(null, .class, employees));
    }

    @Test
    public void should_extract_property() {
        Integer age = PropertySupport.instance().propertyValue("age", Integer.class, yoda);
        Assertions.assertThat(age).isEqualTo(800);
    }

    @Test
    public void should_extract_nested_property() {
        String firstName = PropertySupport.instance().propertyValueOf("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isEqualTo("Yoda");
        yoda.name.first = null;
        firstName = PropertySupport.instance().propertyValueOf("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isNull();
        yoda.name = null;
        firstName = PropertySupport.instance().propertyValueOf("name.first", String.class, yoda);
        Assertions.assertThat(firstName).isNull();
    }

    @Test
    public void should_return_properties_of_inner_class() {
        VehicleFactory vehicleFactory = new VehicleFactory();
        List<String> names = PropertySupport.instance().propertyValues("name", String.class, vehicleFactory.getVehicles());
        Assertions.assertThat(names).containsExactly("Toyota", "Honda", "Audi");
    }

    @Test
    public void should_return_property_from_superclass() {
        Assertions.assertThat(PropertySupport.instance().propertyValues("class", Class.class, employees)).containsExactly(Employee.class, Employee.class);
    }
}


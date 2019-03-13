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
package org.assertj.core.api;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Assertions#assertThat(List)}</code>.
 *
 * @author Yvonne Wang
 * @author Alex Ruiz
 * @author Mikhail Mazursky
 */
public class Assertions_assertThat_with_List_Test {
    private static class Person {
        @SuppressWarnings("unused")
        private String name;

        public Person(String name) {
            this.name = name;
        }
    }

    private static class Employee extends Assertions_assertThat_with_List_Test.Person {
        public Employee(String name) {
            super(name);
        }
    }

    @Test
    public void should_create_Assert() {
        AbstractListAssert<?, List<? extends Object>, Object, ObjectAssert<Object>> assertions = Assertions.assertThat(Collections.emptyList());
        Assertions.assertThat(assertions).isNotNull();
    }

    @Test
    public void should_create_Assert_generics() {
        Assertions_assertThat_with_List_Test.Employee bill = new Assertions_assertThat_with_List_Test.Employee("bill");
        Assertions_assertThat_with_List_Test.Person billou = bill;
        Assertions.assertThat(bill).isEqualTo(billou);
        Assertions.assertThat(billou).isEqualTo(bill);
    }

    @Test
    public void should_create_Assert_with_list_extended() {
        List<String> strings0 = new ArrayList<>();
        List<? extends String> strings1 = new ArrayList<>();
        Assertions.assertThat(strings0).isEqualTo(strings1);
        Assertions.assertThat(strings1).isEqualTo(strings0);
    }

    @Test
    public void should_create_Assert_with_extends() {
        Assertions_assertThat_with_List_Test.Employee bill = new Assertions_assertThat_with_List_Test.Employee("bill");
        Assertions_assertThat_with_List_Test.Person billou = bill;
        List<Assertions_assertThat_with_List_Test.Person> list1 = Lists.newArrayList(billou);
        List<Assertions_assertThat_with_List_Test.Employee> list2 = Lists.newArrayList(bill);
        Assertions.assertThat(list1).isEqualTo(list2);
        Assertions.assertThat(list2).isEqualTo(list1);
    }

    @Test
    public void should_pass_actual() {
        List<String> names = Collections.singletonList("Luke");
        Assertions.assertThat(Assertions.assertThat(names).actual).isSameAs(names);
    }
}


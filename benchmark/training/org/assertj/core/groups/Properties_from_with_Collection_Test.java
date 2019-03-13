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
package org.assertj.core.groups;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.util.Lists;
import org.assertj.core.util.introspection.PropertySupport;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Properties#from(Collection)}</code>.
 *
 * @author Yvonne Wang
 * @author Mikhail Mazursky
 */
public class Properties_from_with_Collection_Test {
    private Employee yoda;

    private List<Employee> employees;

    private PropertySupport propertySupport;

    private String propertyName;

    private Properties<Integer> properties;

    @Test
    public void should_return_values_of_property() {
        List<Integer> ages = Lists.newArrayList();
        ages.add(yoda.getAge());
        Mockito.when(propertySupport.propertyValues(propertyName, Integer.class, employees)).thenReturn(ages);
        Assertions.assertThat(properties.from(employees)).isSameAs(ages);
    }
}


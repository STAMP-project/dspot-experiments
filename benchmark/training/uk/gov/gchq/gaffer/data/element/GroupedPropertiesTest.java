/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.data.element;


import org.junit.Assert;
import org.junit.Test;


public class GroupedPropertiesTest {
    @Test
    public void shouldConstructGroupedProperties() {
        // Given
        final String group = "group1";
        // When
        final GroupedProperties groupedProperties = new GroupedProperties(group);
        // Then
        Assert.assertEquals(group, groupedProperties.getGroup());
    }

    @Test
    public void shouldSetAndGetFields() {
        // Given
        GroupedProperties groupedProperties = new GroupedProperties();
        final String group = "group1";
        // Verify set/get Group
        Assert.assertNull(groupedProperties.getGroup());
        groupedProperties.setGroup(group);
        Assert.assertEquals(group, groupedProperties.getGroup());
        // Verify set/get Property
        Assert.assertNull(groupedProperties.get("prop1"));
        groupedProperties.put("prop1", "value1");
        Assert.assertEquals("value1", groupedProperties.get("prop1"));
    }

    @Test
    public void shouldReturnTrueWhenGroupedPropertiesAreEqual() {
        // Given
        final String group = "group1";
        final GroupedProperties groupedProperties1 = new GroupedProperties(group);
        groupedProperties1.put("prop1", "value1");
        final GroupedProperties groupedProperties2 = new GroupedProperties(group);
        groupedProperties2.put("prop1", "value1");
        // When
        final boolean isEqual = groupedProperties1.equals(groupedProperties2);
        // Then
        Assert.assertTrue(isEqual);
        Assert.assertEquals(groupedProperties1.hashCode(), groupedProperties2.hashCode());
    }

    @Test
    public void shouldReturnFalseWhenGroupedPropertiesHaveADifferentGroup() {
        // Given
        final String group1 = "group1";
        final GroupedProperties groupedProperties1 = new GroupedProperties(group1);
        groupedProperties1.put("prop1", "value1");
        final String group2 = "group2";
        final GroupedProperties groupedProperties2 = new GroupedProperties(group2);
        groupedProperties2.put("prop1", "value1");
        // When
        final boolean isEqual = groupedProperties1.equals(groupedProperties2);
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertNotEquals(groupedProperties1.hashCode(), groupedProperties2.hashCode());
    }

    @Test
    public void shouldReturnFalseWhenGroupedPropertiesHaveADifferentProperty() {
        // Given
        final String group = "group1";
        final GroupedProperties groupedProperties1 = new GroupedProperties(group);
        groupedProperties1.put("prop1", "value1");
        final GroupedProperties groupedProperties2 = new GroupedProperties(group);
        groupedProperties2.put("a different property", "value2");
        // When
        final boolean isEqual = groupedProperties1.equals(groupedProperties2);
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertNotEquals(groupedProperties1.hashCode(), groupedProperties2.hashCode());
    }
}

